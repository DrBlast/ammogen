package com.wavesplatform;

import net.sourceforge.argparse4j.inf.ArgumentParser;

public class ArgParse {

    protected static ArgumentParser setDefaults(ArgumentParser p) {
        p.addArgument("-net", "--network", "--chainId", "--chain_id")
                .type(String.class)
                .help("select chain id: D W T M");
        p.addArgument("-n", "--num")
                .type(Integer.class)
                .setDefault(500000)
                .help("set number of requests");
        p.addArgument("-a", "--account_number")
                .type(Integer.class)
                .setDefault(2000)
                .help("set number of accounts");
        p.addArgument("-t", "--type")
                .type(AmmoType.class)
                .help("set load type")
                .required(true);
        return p;
    }

    protected static ArgumentParser setDefaultsWithSeed(ArgumentParser p) {
        p.addArgument("-s", "--seed")
                .type(String.class)
                //   .required(true)
                .help("Specify seed");
        return setDefaults(p);
    }
}
