package com.wavesplatform;

import com.wavesplatform.helpers.LogFormatter;
import com.wavesplatform.steps.BackendSteps;
import com.wavesplatform.wavesj.AssetPair;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import static com.wavesplatform.TestVariables.*;


public class Defaults {


    static String defaultTestSeed;
    static Node matcherNode;

    static String nodeUrl;
    static String matcherUrl;
    static Byte chainByte;
    static Byte chainId;
    static Node node;


    static PrivateKeyAccount richAkk;
    final static long FEE = 100_000;
    final static long ISSUE_FEE = 100000000;




}
