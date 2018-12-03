package com.wavesplatform.generators;

import com.wavesplatform.steps.AmmoSteps;
import com.wavesplatform.steps.MatcherSteps;
import com.wavesplatform.steps.UtilsSteps;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.Transactions;
import com.wavesplatform.wavesj.Transfer;
import com.wavesplatform.wavesj.transactions.MassTransferTransaction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.wavesplatform.steps.UtilsSteps.getJson;

public class MassTransferAmmo {

    private PrivateKeyAccount richPk;
    private MatcherSteps matcher;
    private AmmoSteps ammoSteps;
    private UtilsSteps utils;


    public MassTransferAmmo(PrivateKeyAccount richPk, Node node) throws URISyntaxException {
        this.utils = new UtilsSteps(node);
        this.matcher = new MatcherSteps();
        this.ammoSteps = new AmmoSteps();
        this.richPk = richPk;

    }

    private MassTransferTransaction massTransfer(String assetId, List<Transfer> transfers, long timestamp) {
        MassTransferTransaction massTransfer = Transactions.makeMassTransferTx(richPk, assetId, transfers, 100000 + (transfers.size() + 1) * 50000, null, timestamp);
        return massTransfer;
    }


    private static <T> Collection<List<T>> partition(List<T> list, int size) {
        final AtomicInteger counter = new AtomicInteger(0);

        return list.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / size))
                .values();
    }

    private List<Transfer> transfersList(List<PrivateKeyAccount> pks, long amount) {
        List<Transfer> transfers = new ArrayList<>();
        for (PrivateKeyAccount pk : pks)
            transfers.add(new Transfer(pk.getAddress(), amount));
        return transfers;
    }

    private void deleteFile(String orderidsFile) {
        File f = new File(orderidsFile);
        if (f.exists())
            f.delete();
    }

    private void writeDistributeAssets(List<PrivateKeyAccount> pks, Map<String, Integer> assetMap, long amount, int recipientsNum, boolean norm, String fileName) throws
            IOException {

        Collection<List<PrivateKeyAccount>> pPk = partition(pks, recipientsNum);

        deleteFile(fileName);
        int allTxNum = 120000;
        long timestamp = System.currentTimeMillis();
        long j = 0l;
        int repeat = recipientsNum * allTxNum / (assetMap.size() * pks.size());
        for (String asset : assetMap.keySet()) {

            for (int i = 0; i < repeat; i++) {
                long normedAmount = 0L;
                if (norm)
                    normedAmount = matcher.normAmount(amount, assetMap.get(asset));
                else
                    normedAmount = amount;

                List<String> nextAmmo = new ArrayList<>();
                for (List<PrivateKeyAccount> x : pPk) {
                    j++;
                    if (j < 150)
                        timestamp += 1000;
                    else if (j % 3 == 0)
                        timestamp += 1000;
                    MassTransferTransaction tx = massTransfer(asset, transfersList(x, normedAmount), timestamp);


                    nextAmmo.add(ammoSteps.printPostWithDeafultHeaders(getJson(tx), "/transactions/broadcast", "MASS"));
                }
                Files.write(Paths.get(fileName), nextAmmo, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }

        }

    }

    private Map<String, Integer> prep(List<PrivateKeyAccount> pks, int assetsNum, String fileName) throws InterruptedException, IOException, TimeoutException {
        assert assetsNum >= 2;

        Random r = new Random();
        Map<String, Integer> assetMap = new LinkedHashMap<>();
        for (int i = 0; i < assetsNum; i++) {
            int decimals = r.nextInt(8);

            assetMap.put(utils.issueAsset(richPk, (byte) decimals), decimals);
        }
        long assetAmount = 1L;
        utils.waitForHeightArise();
        writeDistributeAssets(pks, assetMap, assetAmount, 100, true, fileName);
        return assetMap;
    }


    public void genMasstTx(String seedPart) throws InterruptedException, IOException, TimeoutException {
        genMassTx(seedPart, 5, "masstx.txt");
    }

    public void genMasstTx(String seedPart, String fileName) throws InterruptedException, IOException, TimeoutException {
        genMassTx(seedPart, 5, fileName);
    }


    public void genMassTx(String seedPart, int assetNum, String fileName) throws InterruptedException, TimeoutException, IOException {
        int accountsNum = 3000;
        int startNonce = 0;
        List<PrivateKeyAccount> pks = new ArrayList<>();
        pks.addAll(utils.getAccountsBySeed(seedPart + "x0", accountsNum, startNonce));
        pks.addAll(utils.getAccountsBySeed(seedPart + "x1", accountsNum, startNonce));
        prep(pks, assetNum, fileName);
    }
}
