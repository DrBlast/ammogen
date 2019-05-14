package com.wavesplatform.generators;

import com.wavesplatform.TestVariables;
import com.wavesplatform.steps.AmmoSteps;
import com.wavesplatform.steps.UtilsSteps;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.Transactions;
import com.wavesplatform.wavesj.transactions.SponsorTransaction;
import com.wavesplatform.wavesj.transactions.TransferTransaction;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.wavesplatform.steps.UtilsSteps.getJson;

public class SetSponsorshipAmmo {

    private PrivateKeyAccount richPk;
    private Node node;
    private AmmoSteps ammoSteps;
    private UtilsSteps utils;
    private static byte chainId = TestVariables.getChainId();

    public SetSponsorshipAmmo(Node node, PrivateKeyAccount richPk) throws URISyntaxException {
        this.node = node;
        this.ammoSteps = new AmmoSteps();
        this.utils = new UtilsSteps(this.node);
        this.richPk = richPk;

    }


    public void genSponsorTxs(String seedPart, int txsQuantity, int issuersQuantity, String assetIdsFilename, String fileName) throws IOException {
        List<String> assetIds = utils.parseAssetIdsFromFile(assetIdsFilename);
        PrivateKeyAccount senderPk;
        List<PrivateKeyAccount> senderPks = new ArrayList<>();
        int assetsCount = 0;
        long timestamp = System.currentTimeMillis();
        try (FileWriter ammoWriter = new FileWriter(fileName)) {

            for (int i = 0; i < issuersQuantity; i++) {
                senderPk = PrivateKeyAccount.fromSeed(seedPart + i, 0, chainId);
                ammoWriter.write(senderPk.getAddress() + "\r\n");
                for (int j = i * txsQuantity; j < (i * txsQuantity + (txsQuantity - 1)); j++) {
                    assetsCount++;
                    if (assetsCount % 20 == 0) {
                        timestamp += 1000;
                    }
                    SponsorTransaction tx = Transactions.makeSponsorTx(senderPk, assetIds.get(assetsCount),
                            1, 100000L, timestamp);
                    ammoWriter.write(ammoSteps.printPostWithDefaultHeaders(getJson(tx), "/transactions/broadcast", "SET_SPONSORSHIP"));
                }
            }
            utils.distributeWaves(richPk, senderPks, 4, true, 0);
        }
    }
}

