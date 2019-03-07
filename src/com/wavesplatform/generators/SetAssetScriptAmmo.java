package com.wavesplatform.generators;

import com.wavesplatform.TestVariables;
import com.wavesplatform.response.AssetBalance;
import com.wavesplatform.steps.AmmoSteps;
import com.wavesplatform.steps.CommonSteps;
import com.wavesplatform.steps.UtilsSteps;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.Transactions;
import com.wavesplatform.wavesj.transactions.IssueTransactionV2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.wavesplatform.steps.UtilsSteps.getJson;

public class SetAssetScriptAmmo {

    private Node node;
    private PrivateKeyAccount senderPk;
    private UtilsSteps utils;

    public SetAssetScriptAmmo(PrivateKeyAccount senderPk, Node node) throws URISyntaxException {
        this.node = node;
        this.senderPk = senderPk;
        this.utils = new UtilsSteps(node);
    }

    public void genSetAssetScriptTxs(String assetIdsFileName, PrivateKeyAccount senderPk, String fileName) throws IOException {
        AmmoSteps ammoSteps = new AmmoSteps();
        String script = "base64:AgMGBgYWjJ7Z";
        try (FileWriter ammoWriter = new FileWriter(fileName)) {
            long timestamp = System.currentTimeMillis();
            int txcount = 0;
            for (String assetId : utils.parseAssetIdsFromFile(assetIdsFileName)) {
                /**TODO: change IssueTransaction to SetAssetScriptTransaction (remove quantity, reissuable
                 *  and decimal, replace name with assetId)*/
                IssueTransactionV2 tx = Transactions.makeIssueTx(senderPk, TestVariables.getChainId(), assetId, "desciption",
                        0, (byte) 0, true, script, 100400000, timestamp);
                ammoWriter.write(ammoSteps.printPostWithDefaultHeaders(getJson(tx), "/transactions/broadcast", "setassetscript"));
                timestamp += 1000;
            }
        }
    }

    private List<String> getScriptedAssetIds(String fileName) throws IOException {
        List<String> scriptedAssetIds = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            while (br.ready()) {
                scriptedAssetIds.add(br.readLine());
            }
        }
        return scriptedAssetIds;
    }
}
