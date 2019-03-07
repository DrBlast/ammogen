package com.wavesplatform.generators;

import com.wavesplatform.TestVariables;
import com.wavesplatform.steps.AmmoSteps;
import com.wavesplatform.steps.BackendSteps;
import com.wavesplatform.steps.MatcherSteps;
import com.wavesplatform.steps.UtilsSteps;
import com.wavesplatform.wavesj.AssetBalance;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.Transactions;
import com.wavesplatform.wavesj.transactions.TransferTransaction;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.wavesplatform.steps.UtilsSteps.getJson;

public class AssetTransferAmmo {

    private MatcherSteps matcher;
    private BackendSteps backendSteps;
    private AmmoSteps ammoSteps;
    private UtilsSteps utils;
    private Node node;
    private Random r;
    private static byte chainId = TestVariables.getChainId();


    public AssetTransferAmmo(Node node) throws URISyntaxException {
        this.node = node;
        this.utils = new UtilsSteps(this.node);
        this.matcher = new MatcherSteps();
        this.backendSteps = new BackendSteps();
        this.ammoSteps = new AmmoSteps();
        this.r = new Random();
    }

    private void writeDistributeAssets(PrivateKeyAccount pk, List<PrivateKeyAccount> pks, List<String> assetList, boolean isScripted, String fileName) throws IOException {
        try (FileWriter ammoWriter = new FileWriter(fileName, true)) {
            long timestamp = System.currentTimeMillis();
            int txCount = 0;
            long fee = 100000l;
            if (isScripted)
                fee = 500000;

            for (String asset : assetList) {
                txCount++;
                if (txCount % 20 == 0) {
                    timestamp += 1000;
                }

                TransferTransaction tx = Transactions.makeTransferTx(pk,
                        pks.get(r.nextInt(pks.size())).getAddress(),
                        1, asset, fee, null, (String) null, timestamp);
                ammoWriter.append(ammoSteps.printPostWithDefaultHeaders(getJson(tx), "/transactions/broadcast", "ASSET_TRANSFER"));
            }

        }

    }

    public void genAssetTransferTxs(String seedPart, String assetIdsFileName, boolean isScripted, String fileName) throws IOException {
        int accountsNum = 10;
        List<PrivateKeyAccount> pks = new ArrayList<>();
        pks.addAll(utils.getAccountsBySeed(seedPart + "x0", accountsNum, 1));
        pks.addAll(utils.getAccountsBySeed(seedPart + "x1", accountsNum, 1));
        PrivateKeyAccount senderPk;

        for (int i = 0; i < 40; i++) {
            senderPk = PrivateKeyAccount.fromSeed(seedPart + i, 0, chainId);
            List<String> distributedAssetIds = prepare(seedPart + i);//utils.parseAssetIdsFromFile(assetIdsFileName);
            writeDistributeAssets(senderPk, pks, distributedAssetIds, isScripted, fileName);
            System.out.println(i);
        }

    }

    private List<String> prepare(String seedPart) throws IOException {

        List<String> ids = new ArrayList<>();
        //
        PrivateKeyAccount senderPk = PrivateKeyAccount.fromSeed(seedPart, 0, chainId);
        List<AssetBalance> assetBalanceList = node.getAssetsBalance(senderPk.getAddress());

        for (int i = 0; i < assetBalanceList.size() / 2; i++) {
//            for (AssetBalance ab : assetBalanceList) {
            if (assetBalanceList.get(i).balance == 1l)
                ids.add(assetBalanceList.get(i).getPriceAsset());
        }
        //  System.out.println(i);
        //}
        return ids;
    }
}
