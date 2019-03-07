package com.wavesplatform.generators;

import com.wavesplatform.steps.AmmoSteps;
import com.wavesplatform.steps.BackendSteps;
import com.wavesplatform.steps.MatcherSteps;
import com.wavesplatform.steps.UtilsSteps;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.Transactions;
import com.wavesplatform.wavesj.transactions.TransferTransaction;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static com.wavesplatform.steps.UtilsSteps.getJson;

public class TransferAmmo {

    private PrivateKeyAccount richPk;
    private MatcherSteps matcher;
    private BackendSteps backendSteps;
    private AmmoSteps ammoSteps;
    private UtilsSteps utils;
    private Node node;
    private Random r;

    public TransferAmmo(PrivateKeyAccount richPk, Node node) throws URISyntaxException {
        this.node = node;
        this.utils = new UtilsSteps(this.node);
        this.matcher = new MatcherSteps();
        this.backendSteps = new BackendSteps();
        this.ammoSteps = new AmmoSteps();
        this.richPk = richPk;
        this.r = new Random();
    }

    private void writeDistributeAssets(List<PrivateKeyAccount> pks, Map<String, Integer> assetMap, long amount, boolean norm, String fileName) throws
            IOException {
        utils.deleteFile(fileName);
        try(FileWriter ammoWriter = new FileWriter(fileName)) {
            int allTxNum = 20000;
            long timestamp = System.currentTimeMillis();
            long txCount = 0;

            while (txCount < allTxNum) {
                for (String asset : assetMap.keySet()) {
                    long normedAmount = norm ? matcher.normAmount(amount, assetMap.get(asset)) : amount;

                    for (PrivateKeyAccount pk : pks) {
                        txCount++;
                        if (txCount < 150)
                            timestamp += 1000;
                        else if (txCount % 3 == 0)
                            timestamp += 1000;

                        TransferTransaction tx = Transactions.makeTransferTx(pk, pks.get(r.nextInt(pks.size())).getAddress(), normedAmount, asset, 1300000, null, "transfer", timestamp);
                        ammoWriter.write(ammoSteps.printPostWithDefaultHeaders(getJson(tx), "/transactions/broadcast", "TRANSFER"));
                    }
                }
            }
        }
    }

    public void genTransferTxs(String seedPart, String fileName) throws InterruptedException, IOException, TimeoutException, URISyntaxException {
        int accountsNum = 10;
        int assetsNum = 3;

        String script = null;

        List<PrivateKeyAccount> pks = new ArrayList<>();
        pks.addAll(utils.getAccountsBySeed(seedPart + "x0", accountsNum, 0));
        pks.addAll(utils.getAccountsBySeed(seedPart + "x1", accountsNum, 0));

        Map<String, Integer> assetMap = new LinkedHashMap<>();
        for (int i = 0; i < assetsNum; i++) {
            int decimals = r.nextInt(9);
            assetMap.put(utils.issueAsset(richPk, (byte) decimals, script), decimals);
        }

        utils.distributeWaves(richPk, pks, 100, true, 0);

        long assetAmount = 100L;
        utils.waitForHeightArise();

        utils.distributeAssets(richPk, pks, assetMap, 10000, true, 400000);

        writeDistributeAssets(pks, assetMap, assetAmount, true, fileName);
    }

}
