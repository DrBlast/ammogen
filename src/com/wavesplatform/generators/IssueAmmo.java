package com.wavesplatform.generators;

import com.wavesplatform.TestVariables;
import com.wavesplatform.steps.AmmoSteps;
import com.wavesplatform.steps.UtilsSteps;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.Transactions;
import com.wavesplatform.wavesj.transactions.IssueTransactionV1;
import com.wavesplatform.wavesj.transactions.IssueTransactionV2;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.wavesplatform.steps.UtilsSteps.getJson;

public class IssueAmmo {
    private Node node;
    private PrivateKeyAccount richPk;
    private UtilsSteps utils;
    private AmmoSteps ammoSteps;

    public IssueAmmo(PrivateKeyAccount richPk, Node node) throws URISyntaxException {
        this.utils = new UtilsSteps(node);
        this.ammoSteps = new AmmoSteps();
        this.node = node;
        this.richPk = richPk;
    }

    private void writeAssets(List<PrivateKeyAccount> issuerPks, int txsQuantity, long quantity, byte decimals, boolean isScripted, String fileName) throws IOException {
        AmmoSteps ammoSteps = new AmmoSteps();
        ArrayList<String> issuedAssetIds = new ArrayList<>();
        String script = "base64:AgZ7TN8j";

        try (FileWriter ammoWriter = new FileWriter(fileName)) {
            long timestamp = System.currentTimeMillis();

            for (PrivateKeyAccount pk : issuerPks) {
                for (int i = 0; i < txsQuantity; i++) {
                    if (i % 10 == 0) {
                        timestamp += 1000;
                    }

                    if (isScripted) {
                        IssueTransactionV2 tx = Transactions.makeIssueTx(pk, TestVariables.getChainId(), RandomStringUtils.randomAlphabetic(4), "desciption",
                                quantity, decimals, true, script, 100000000, timestamp);
                        issuedAssetIds.add(tx.getId().toString());
                        ammoWriter.write(ammoSteps.printPostWithDefaultHeaders(getJson(tx), "/transactions/broadcast", "Issue"));

                    } else {
                        IssueTransactionV2 tx = Transactions.makeIssueTx(pk, TestVariables.getChainId(), RandomStringUtils.randomAlphabetic(4), "description",
                                quantity, decimals, true, null, 100000000, timestamp);
                        issuedAssetIds.add(tx.getId().toString());
                        ammoWriter.write(ammoSteps.printPostWithDefaultHeaders(getJson(tx), "/transactions/broadcast", "Issue"));
                    }
                }
            }
        }

        String idsWriterFileName;
        if (isScripted) {
            idsWriterFileName = "scriptedAssetIds2.txt";
        } else {
            idsWriterFileName = "nonScriptedAssetIds2.txt";
        }

        try (FileWriter idsWriter = new FileWriter(idsWriterFileName)) {
            for (String id : issuedAssetIds) {
                idsWriter.write(id + "\r\n");
            }
        }
    }

    public void genIssueTxs(int accountsQuantity, int txsQuantity, boolean isScripted, String fileName, String seed) throws IOException, InterruptedException {
        int assetsQuantity = 1;
        int decimals = 0;
//        long feeAmmountSum = (long) Math.ceil(txsQuantity * 0.005);

        List<PrivateKeyAccount> pks = new ArrayList<>();
        for (int i = 0; i < accountsQuantity; i++) {
            pks.addAll(utils.getAccountsBySeed(seed + i + "accquantity" + accountsQuantity, 1, 0));
        }
        for (PrivateKeyAccount pk: pks){
            System.out.println(pk.getAddress() + "\r\n");
        }
//        pks.addAll(utils.getAccountsBySeed(seed, accountsQuantity, 0));

        utils.distributeWaves(richPk, pks, txsQuantity, true, 0);
//        utils.waitForHeightArise();

        writeAssets(pks, txsQuantity, assetsQuantity, (byte) decimals, isScripted, fileName);
    }

}
