package com.wavesplatform.helpers;

import com.wavesplatform.steps.*;
import com.wavesplatform.steps.BackendSteps;
import com.wavesplatform.wavesj.AssetPair;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;
import com.wavesplatform.wavesj.json.WavesJsonMapper;

public class NodeDefaults {

    protected PrivateKeyAccount richPk;
    protected String customSeed;
    protected String nodeUrl;
    protected String matcherUrl;
    protected Node node;
    protected Node node1;
    protected Node node2;
    protected Node node3;
    protected Node node4;
    protected byte chainByte;
    protected MatcherSteps matcher;
    protected Node matcherNode;
    protected PrivateKeyAccount account;
    protected final static long FEE = 100_000;
    protected final static long ISSUE_FEE= 100000000;

    protected AssetPair assetPair;
    protected BackendSteps steps;
    protected UtilsSteps utils;
    protected AddressesSteps accSteps;
    protected AmmoSteps ammoSteps;

}
