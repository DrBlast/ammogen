package com.wavesplatform.generators;

import com.wavesplatform.TestVariables;
import com.wavesplatform.steps.AmmoSteps;
import com.wavesplatform.steps.CommonSteps;
import com.wavesplatform.steps.UtilsSteps;
import com.wavesplatform.wavesj.AssetBalance;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class AssetBalanceAmmo {
    private Node node;
    private UtilsSteps utils;
    private AmmoSteps ammoSteps;
    private static byte chainId = TestVariables.getChainId();


    public AssetBalanceAmmo(Node node) throws URISyntaxException {
        this.node = node;
        this.utils = new UtilsSteps(node);
        this.ammoSteps = new AmmoSteps();
    }

    public void genAssetBalanceByAddress(String seedPart, String fileName) throws IOException {
        String path;
        try (FileWriter ammoWriter = new FileWriter(fileName)) {
            for (int i = 0; i < 40; i++) {
                PrivateKeyAccount pka = PrivateKeyAccount.fromSeed(seedPart + i, 0, chainId);
                List<AssetBalance> assetBalanceList = node.getAssetsBalance(pka.getAddress());
                if (assetBalanceList.size() < 501) {
                    path = String.format("/assets/balance/%s", pka.getAddress());
                    ammoWriter.write(ammoSteps.printGet(path, "ADDRESS_ASSET_BALANCE"));
                }
            }
//            for (int i = 0; i < 40; i++) {
//                PrivateKeyAccount pka = PrivateKeyAccount.fromSeed(seedPart + i, 0, chainId);
//                path = String.format("/assets/balance/%s", pka.getAddress());
//                ammoWriter.write(ammoSteps.printGet(path, "ADDRESS_ASSET_BALANCE"));
//            }
        }

    }

    public void genAssetBalanceByAssetId(String seedPart, String assetIdsFileName, String fileName) throws IOException {
        PrivateKeyAccount pka;// = PrivateKeyAccount.fromSeed(seedPart, 0, chainId);
        List<String> assetIds = utils.parseAssetIdsFromFile(assetIdsFileName);
        String path;
        int assetCount = 0;
        try (FileWriter ammoWriter = new FileWriter(fileName)) {
            for (int i = 0; i <= 200; i++) {
                pka = PrivateKeyAccount.fromSeed(seedPart, i, chainId);
                if (i == 0) {
                    for (int j = assetCount; assetCount < 100; assetCount++) {
                        path = String.format("/assets/balance/%s/%s", pka.getAddress(), assetIds.get(assetCount));
                        ammoWriter.write(ammoSteps.printGet(path, "ASSETID_BALANCE"));
                    }
                } else {
                    for (int j = assetCount; assetCount < i * 100; assetCount++) {
                        path = String.format("/assets/balance/%s/%s", pka.getAddress(), assetIds.get(assetCount));
                        ammoWriter.write(ammoSteps.printGet(path, "ASSETID_BALANCE"));
                    }

                }
            }
//        try (FileWriter ammoWriter = new FileWriter(fileName)) {
//            for (int i = 0; i <= 40; i++) {
//                pka = PrivateKeyAccount.fromSeed(seedPart + i, 0, chainId);
//                if (i == 0) {
//                    for (int j = assetCount; assetCount < 1000; assetCount++) {
//                        path = String.format("/assets/balance/%s/%s", pka.getAddress(), assetIds.get(assetCount));
//                        ammoWriter.write(ammoSteps.printGet(path, "ASSETID_BALANCE"));
//                    }
//                } else {
//                    for (int j = assetCount; assetCount < i * 1000; assetCount++) {
//                        path = String.format("/assets/balance/%s/%s", pka.getAddress(), assetIds.get(assetCount));
//                        ammoWriter.write(ammoSteps.printGet(path, "ASSETID_BALANCE"));
//                    }
//                }
//            }

//        try (FileWriter ammoWriter = new FileWriter(fileName)) {
//            for (String assetId : utils.parseAssetIdsFromFile(assetIdsFileName)) {
//                path = String.format("/assets/balance/%s/%s", pka.getAddress(), assetId);
//                ammoWriter.write(ammoSteps.printGet(path, "ASSETBALANCE"));
//            }
//        }

        }
    }
}
