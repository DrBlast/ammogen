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

    private PrivateKeyAccount richPk;
    private MatcherSteps matcher;
    private BackendSteps backendSteps;
    private AmmoSteps ammoSteps;
    private UtilsSteps utils;
    private Node node;
    private Random r;
    private static byte chainId = TestVariables.getChainId();


    public AssetTransferAmmo(PrivateKeyAccount richPk, Node node) throws URISyntaxException {
        this.node = node;
        this.utils = new UtilsSteps(this.node);
        this.matcher = new MatcherSteps();
        this.backendSteps = new BackendSteps();
        this.ammoSteps = new AmmoSteps();
        this.richPk = richPk;
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
                if (txCount % 60 == 0) {
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
        //utils.deleteFile(fileName);

        int accountsNum = 10;
        List<PrivateKeyAccount> pks = new ArrayList<>();
        pks.addAll(utils.getAccountsBySeed(seedPart + "x0", accountsNum, 1));
        pks.addAll(utils.getAccountsBySeed(seedPart + "x1", accountsNum, 1));
        PrivateKeyAccount senderPk;
        List<PrivateKeyAccount> senderPks = new ArrayList<>();
        List<String> distributedAssetIds = new ArrayList<>();
//        for (int i = 0; i <= 14; i++) {
//            senderPk = PrivateKeyAccount.fromSeed(seedPart + i, 0, chainId);
//            senderPks.add(senderPk);
//            distributedAssetIds = prepare(i);//prepare(seedPart + i);
//            if (distributedAssetIds.size() != 0)
//                writeDistributeAssets(senderPk, pks, distributedAssetIds, isScripted, fileName);
//            System.out.println("iter:"+i);
//        }
        for (int i = 22; i <= 29; i++) {
            senderPk = PrivateKeyAccount.fromSeed(seedPart + i, 0, chainId);
            senderPks.add(senderPk);
            distributedAssetIds = prepare(i);//prepare(seedPart + i);
            if (distributedAssetIds.size() != 0)
                writeDistributeAssets(senderPk, pks, distributedAssetIds, isScripted, fileName);
            System.out.println("iter:"+i);
        }
        long feeAmount = (long) (Math.ceil(distributedAssetIds.size() * 0.005));
        utils.distributeWaves(richPk, senderPks, feeAmount, true, 0);

    }
//utils.getAccountsBySeed(seed + txsQuantity + "i2" + i, 1, 0)
private List<String> prepare(int i) throws IOException {
    List<String> ids = new ArrayList<>();

    ids = utils.parseAssetIdsFromFile("idsi"+i+".txt");
//    if(i < 10){
//        List<String> idss = new ArrayList<>();
//        for (int j = i * 8000 + 1; j <= (i+1)*8000; j++) {
//            System.out.println(j);
//            idss = utils.parseAssetIdsFromFile("IDisstx80k0to9.txt");
//            if (j % 2 == 0){
//            ids.add(idss.get(j));
//            }
//        }
//    }
//    else if (i < 20 && i > 9) {
//        List<String> idss = new ArrayList<>();
//        for (int j = i * 8000 + 1; j <= (i+1)*8000; j++) {
//            System.out.println(j);
//            idss = utils.parseAssetIdsFromFile("IDisstx80k10to19.txt");
//            if (j % 2 == 0){
//                ids.add(idss.get(j));
//            }
//        }
//    } else {
//        List<String> idss = new ArrayList<>();
//        for (int j = i * 8000 + 1; j <= (i+1)*8000; j++) {
//            System.out.println(j);
//            idss = utils.parseAssetIdsFromFile("nonScriptedAssetIds2.txt");
//            if (j % 3 == 0){
//                ids.add(idss.get(j));
//            }
//    }
//    }
    return ids;

}
    private List<String> prepare(String seedPart) throws IOException {
        List<String> ids = new ArrayList<>();
        System.out.println(seedPart);
        PrivateKeyAccount senderPk = PrivateKeyAccount.fromSeed(seedPart, 0, chainId);
        List<AssetBalance> assetBalanceList = node.getAssetsBalance(senderPk.getAddress());

//        for (int i = 0; i < assetBalanceList.size() / 2; i++) {
//            if (assetBalanceList.get(i).balance == 1l)
//                ids.add(assetBalanceList.get(i).getPriceAsset());
//        }
        for (int i = 0; i < 3448; i++) {
            if (assetBalanceList.get(i).balance == 1l)
                ids.add(assetBalanceList.get(i).getPriceAsset());
        }
        return ids;
    }
}
