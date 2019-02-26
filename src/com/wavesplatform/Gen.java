package com.wavesplatform;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class Gen {

    private static ArgumentParser massTransferArgs(ArgumentParser parser) {

        parser = ArgParse.setDefaultsWithSeed(parser);
        parser.addArgument("-f", "--file")
                .type(String.class)
                .setDefault("masstx.txt")
                .help("specify file name for ammos");
        return parser;
    }

    private static ArgumentParser loadType( ArgumentParser parser) {

        parser.addArgument("-f", "--file")
                .type(String.class)
                .setDefault("transfertx.txt")
                .help("specify file name for ammos");
        return parser;
    }

    private static ArgumentParser commonApiArgs(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor(String.valueOf(AmmoType.TRANSFER)).build()
                .defaultHelp(true)
                .description("Generate api ammo");
        parser = ArgParse.setDefaults(parser);
        parser.addArgument("-f", "--file")
                .type(String.class)
                .setDefault("api.txt")
                .help("specify file name for ammos");
        return parser;
    }


    public static void main(String[] args) throws ArgumentParserException {
        ArgumentParser parser = ArgumentParsers.newFor(String.valueOf(AmmoType.MASS_TRANSFER)).build()
                .defaultHelp(true)
                .description("Generate MassTrasfer ammo");

        ArgumentParser p = loadType(parser);
        System.out.println(p.parseArgs(args));
    }

    @Test
    public void t() throws ArgumentParserException {
       // main("-t -s fdasfdfdasfdsaf -n 100 -a 10 -net W".split(" "));
        main("--help".split(" "));
    }

}
