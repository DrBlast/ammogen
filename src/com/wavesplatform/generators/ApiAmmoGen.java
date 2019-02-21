package com.wavesplatform.generators;

import com.wavesplatform.steps.AmmoSteps;
import com.wavesplatform.steps.UtilsSteps;
import com.wavesplatform.wavesj.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static com.wavesplatform.steps.UtilsSteps.getJson;

public class ApiAmmoGen {


    private AmmoSteps ammoSteps;
    private UtilsSteps utils;
    private Node node;


    public ApiAmmoGen(PrivateKeyAccount richPk, Node node) throws URISyntaxException {
        this.node = node;
        this.utils = new UtilsSteps(node);
        this.ammoSteps = new AmmoSteps();

    }


    //3.32326% /transactions/info
    //31.4199% /transactions/address/
    //9.96979% /assets/details/
    //4.4713% /blocks/at/
    //5.92145% /alias/by-address/
    //0.543807% /addresses/effectiveBalance/
    //0.120846% /addresses/scriptInfo/
    //0.060423% /addresses/publicKey/
    //42.1148% /blocks/height
    //0.241692% blocks/seq
    //1.81269% /transactions/unconfirmed

    public void genApiLoadAmmo(String seed, int txByAddress, boolean reuse) throws IOException {

        int accountsNum = 2500;
        int startNonce = 0;


        int maxH = node.getHeight();

        List<PrivateKeyAccount> pks = new ArrayList<>();
        pks.addAll(utils.getAccountsBySeed(seed + "x0", accountsNum, startNonce));
        pks.addAll(utils.getAccountsBySeed(seed + "x1", accountsNum, startNonce));
//1542383360903

        List<String> t;
        List<String> ad;
        if (!reuse) {
            t = collectTransactions();
            ad = getIssueTx();
        } else {
            t = Files.readAllLines(Paths.get("transactionsD.txt"));
            ad = Files.readAllLines(Paths.get("assetsD.txt"));
        }
        
        String resultFileName = "tx" + txByAddress + ".txt";
        System.out.println("result filename is " + resultFileName);

        utils.deleteFile(resultFileName);
        
        Random r = new Random();
        System.out.println(t.size());
        for (int i = 0; i < t.size() * 2; i++) {
            double x = Math.random();
            List<String> nextAmmo = new ArrayList<>();

            if (x < 0.03) {
                nextAmmo.add(ammoSteps.printGet(
                        String.format("/transactions/info/%s", t.get(r.nextInt(t.size()))), "TX_INFO"));
            }
            if (x < 0.31) {
                nextAmmo.add(ammoSteps.printGet(
                        String.format("/transactions/address/%s/limit/%d", pks.get(r.nextInt(pks.size())).getAddress(), txByAddress), "TX_BY_ADDRESS"));
            }
            if (x < 0.0996) {
                nextAmmo.add(ammoSteps.printGet(
                        String.format("/assets/details/%s", ad.get(r.nextInt(ad.size()))), "ASSET_DETAILS"));
            }
            if (x < 0.0447) {
                nextAmmo.add(ammoSteps.printGet(
                        String.format("/blocks/at/%d", r.nextInt(maxH) + 1), "BLOCKS_AT"));
            }
            if (x < 0.0592) {
                nextAmmo.add(ammoSteps.printGet(
                        String.format("/alias/by-address/%s", pks.get(r.nextInt(pks.size())).getAddress()), "ALIAS"));

            }
            if (x < 0.0054) {
                nextAmmo.add(ammoSteps.printGet(
                        String.format("/addresses/effectiveBalance/%s", pks.get(r.nextInt(pks.size())).getAddress()), "EF_BALANCE"));
            }
            if (x < 0.0012) {
                nextAmmo.add(ammoSteps.printGet(
                        String.format("/addresses/scriptInfo/%s", pks.get(r.nextInt(pks.size())).getAddress()), "SCRIPT_INFO"));
            }
            if (x < 0.42) {
                nextAmmo.add(ammoSteps.printGet(
                        "/blocks/height", "HEIGHT"));

            }
            if (x < 0.0024) {
                int from = r.nextInt(maxH) + 1;
                int to = from + r.nextInt(10) + 1;
                nextAmmo.add(ammoSteps.printGet(
                        String.format("/blocks/seq/%d/%d", from, to), "BLOCK_SEQ"));

            }
            if (x < 0.01813) {
                nextAmmo.add(ammoSteps.printGet("/transactions/unconfirmed", "UTX"));
            }
            Files.write(Paths.get(resultFileName), nextAmmo, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    private List<String> getIssueTx() throws IOException {

        List<String> txId = new ArrayList<>();
        int max = node.getHeight();
        for (int i = max; i > 1; i--) {
            try {
                Block b = node.getBlock(i);
                if (b.getTransactions().size() != 0) {
                    for (Transaction tx : b.getTransactions()) {
                        byte type = tx.getType();
                        if (type == 3)
                            txId.add(tx.getId().toString());
                    }
                }
                if (txId.size() > 1000) {
                    break;
                }
            } catch (Exception ignored) {
                System.out.println(i);
            }
        }
        Files.write(Paths.get("assetsD.txt"), txId);
        return txId;
    }


    private List<String> collectTransactions() throws IOException {

        List<String> txId = new ArrayList<>();
        int i;
        int max = node.getHeight();
        for (i = max; i > 1; i--) {
            try {
                Block b = node.getBlock(i);
                if (b.getTransactions().size() != 0) {
                    for (Transaction t : b.getTransactions()) {
                        txId.add(t.getId().toString());
                    }
                    if (txId.size() > 20000) {
                        break;
                    }
                }
            } catch (Exception ignored) {
                System.out.println(i);
            }
        }
        System.out.println(i);
        for (String id : txId) {
            System.out.println(id);
        }
        Files.write(Paths.get("transactionsD.txt"), txId);
        return txId;
    }


    public void getStats() throws IOException {
        int min = 2;
        int max = node.getHeight();
        getStats(min, max);
    }


    public void getStats(int min, int max) throws IOException {

        HashMap<String, Integer> zeroBlocks = new HashMap<>();
        HashMap<String, Integer> minedBlocks = new HashMap<>();
        HashMap<String, Integer> bigZeroBlocks = new HashMap<>();
        HashMap<String, Long> blocksFee = new HashMap<>();


        int tx = 0;
        long ts1 = node.getBlockHeader(min).getTimestamp();

        for (int i = min; i <= max; i++) {
            BlockHeader b = node.getBlockHeader(i);
            int mined = minedBlocks.getOrDefault(b.getGenerator(), 0);
            minedBlocks.put(b.getGenerator(), ++mined);
            long fee = blocksFee.getOrDefault(b.getGenerator(), 0L);
            blocksFee.put(b.getGenerator(), fee + b.getFee());
            if (b.getTransactionCount() == 0) {
                int zero = zeroBlocks.getOrDefault(b.getGenerator(), 0);
                zeroBlocks.put(b.getGenerator(), ++zero);
                if (node.getBlockHeader(i + 1).getTimestamp() - b.getTimestamp() > 10000) {
                    System.out.println("h = " + i + " next block in  = " + (node.getBlockHeader(i + 1).getTimestamp() - b.getTimestamp()) / 1000);
                    int bigZero = bigZeroBlocks.getOrDefault(b.getGenerator(), 0);
                    bigZeroBlocks.put(b.getGenerator(), ++bigZero);
                }
            }
        }

        //int total = 1537 - 1163;
        int total = max - min;
        System.out.println("Mined Blocks");
        for (Map.Entry<String, Integer> e : minedBlocks.entrySet()) {
            double percent = (double) e.getValue() * 100 / (double) total;
            System.out.println(String.format("%s: %d %.2f", e.getKey(), e.getValue(), percent));
        }
        System.out.println("ZERO Blocks");
        for (Map.Entry<String, Integer> e : zeroBlocks.entrySet()) {
            double percent = (double) e.getValue() * 100 / (double) minedBlocks.get(e.getKey());
            System.out.println(String.format("%s: %d %.2f", e.getKey(), e.getValue(), percent));
        }
        System.out.println("Big ZERO Blocks");
        for (Map.Entry<String, Integer> e : bigZeroBlocks.entrySet()) {
            double percent = (double) e.getValue() * 100 / (double) minedBlocks.get(e.getKey());
            System.out.println(String.format("%s: %d %.2f", e.getKey(), e.getValue(), percent));
        }
        System.out.println("Fee");
        for (Map.Entry<String, Long> e : blocksFee.entrySet()) {
            System.out.println(String.format("%s: %d", e.getKey(), e.getValue()));
        }


    }

}
