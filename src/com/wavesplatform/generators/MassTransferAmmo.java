package com.wavesplatform.generators;

import com.wavesplatform.steps.AmmoSteps;
import com.wavesplatform.steps.MatcherSteps;
import com.wavesplatform.steps.UtilsSteps;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.Transactions;
import com.wavesplatform.wavesj.Transfer;
import com.wavesplatform.wavesj.transactions.MassTransferTransaction;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
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
    private Node node;

    public MassTransferAmmo(PrivateKeyAccount richPk, Node node) throws URISyntaxException {
        this.node = node;
        this.utils = new UtilsSteps(this.node);
        this.matcher = new MatcherSteps();
        this.ammoSteps = new AmmoSteps();
        this.richPk = richPk;
    }

    private MassTransferTransaction massTransfer(String assetId, List<Transfer> transfers, long timestamp) {
        return Transactions.makeMassTransferTx(richPk, assetId, transfers, 100000 + (transfers.size() + 1) * 50000, null, timestamp);
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

    private void writeDistributeAssets(List<PrivateKeyAccount> pks, Map<String, Integer> assetMap, long amount, int recipientsNum, boolean norm, String fileName) throws
            IOException {

        Collection<List<PrivateKeyAccount>> pPk = partition(pks, recipientsNum);

        utils.deleteFile(fileName);
        int allTxNum = 120000;
        long timestamp = System.currentTimeMillis();
        long j = 0L;
        int repeat = recipientsNum * allTxNum / (assetMap.size() * pks.size());

        try(FileWriter ammoWriter = new FileWriter(fileName)) {
            for (String asset : assetMap.keySet()) {
                for (int i = 0; i < repeat; i++) {
                    long normedAmount = (norm) ? matcher.normAmount(amount, assetMap.get(asset)) : amount;

                    for (List<PrivateKeyAccount> x : pPk) {
                        j++;
                        if (j < 150)
                            timestamp += 1000;
                        else if (j % 3 == 0)
                            timestamp += 1000;
                        MassTransferTransaction tx = massTransfer(asset, transfersList(x, normedAmount), timestamp);

                        ammoWriter.write(ammoSteps.printPostWithDefaultHeaders(getJson(tx), "/transactions/broadcast", "MASS"));
                    }
                }
            }
        }

    }

    private Map<String, Integer> prep(List<PrivateKeyAccount> pks, int assetsNum, String fileName) throws InterruptedException, IOException, TimeoutException, URISyntaxException {
        assert assetsNum >= 2;

        Random r = new Random();
        Map<String, Integer> assetMap = new LinkedHashMap<>();
        for (int i = 0; i < assetsNum; i++) {
            int decimals = r.nextInt(8);

            assetMap.put(utils.issueAsset(richPk, (byte) decimals, null), decimals);
        }
        long assetAmount = 1L;
        utils.waitForHeightArise();
        writeDistributeAssets(pks, assetMap, assetAmount, 100, true, fileName);
        return assetMap;
    }

    public void genMasstTx(String seedPart, String fileName) throws InterruptedException, IOException, TimeoutException, URISyntaxException {
        genMassTx(seedPart, 5, fileName);
    }

    public void genMassTx(String seedPart, int assetNum, String fileName) throws InterruptedException, TimeoutException, IOException, URISyntaxException {
        int accountsNum = 3000;
        int startNonce = 0;
        List<PrivateKeyAccount> pks = new ArrayList<>();
        pks.addAll(utils.getAccountsBySeed(seedPart + "x0", accountsNum, startNonce));
        pks.addAll(utils.getAccountsBySeed(seedPart + "x1", accountsNum, startNonce));
        prep(pks, assetNum, fileName);
    }
}
