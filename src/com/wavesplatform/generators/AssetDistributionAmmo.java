package com.wavesplatform.generators;

import com.wavesplatform.TestVariables;
import com.wavesplatform.steps.AmmoSteps;
import com.wavesplatform.steps.UtilsSteps;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class AssetDistributionAmmo {
    private Node node;
    private UtilsSteps utils;
    private AmmoSteps ammoSteps;
    private static byte chainId = TestVariables.getChainId();

    public AssetDistributionAmmo(Node node) throws URISyntaxException {
        this.node = node;
        this.utils = new UtilsSteps(node);
        this.ammoSteps = new AmmoSteps();
    }

    public void genAssetBalanceByAssetId(String assetIdsFileName, String fileName) throws IOException {
        PrivateKeyAccount pka;// = PrivateKeyAccount.fromSeed(seedPart, 0, chainId);
        List<String> assetIds = utils.parseAssetIdsFromFile(assetIdsFileName);
        String path;
        try (FileWriter ammoWriter = new FileWriter(fileName)) {
            for (String assetId: assetIds) {
                path = String.format("/assets/%s/distribution", assetId);
                ammoWriter.write(ammoSteps.printGet(path, "ASSET_DISTRIBUTION"));
            }
        }
    }
}
