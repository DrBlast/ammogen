package com.wavesplatform.generators;

import com.wavesplatform.steps.AmmoSteps;
import com.wavesplatform.steps.BackendSteps;
import com.wavesplatform.steps.MatcherSteps;
import com.wavesplatform.steps.UtilsSteps;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.CancelOrder;
import com.wavesplatform.wavesj.matcher.Order;

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

import static com.wavesplatform.TestVariables.getChainId;
import static com.wavesplatform.TestVariables.getMatcherUrl;
import static com.wavesplatform.steps.UtilsSteps.getJson;
import static com.wavesplatform.wavesj.matcher.Order.Type.BUY;
import static com.wavesplatform.wavesj.matcher.Order.Type.SELL;

public class MatcherAmmo {

    private PrivateKeyAccount richPk;
    private MatcherSteps matcher;
    private AmmoSteps ammoSteps;
    private UtilsSteps utils;
    private Node  matcherNode;
    private BackendSteps steps;


    public MatcherAmmo(PrivateKeyAccount richPk, Node node) throws URISyntaxException {
        this.utils = new UtilsSteps(node);
        this.matcher = new MatcherSteps();
        this.ammoSteps = new AmmoSteps();
        this.richPk = richPk;
        this.steps = new BackendSteps();
        matcherNode = new Node(getMatcherUrl(), getChainId());
    }

    private Map<String, Integer> prep(List<PrivateKeyAccount> pks, int assetsNum, int accountsNum, int startNonce) throws InterruptedException, IOException, TimeoutException {
        Random r = new Random();
        Map<String, Integer> assetMap = new LinkedHashMap<>();
        for (int i = 0; i < assetsNum; i++) {
            int decimals = r.nextInt(8) + 1;
            assetMap.put(utils.issueAsset(richPk, (byte) decimals), decimals);
        }

        long assetAmount = 9999l;
        long wavesAmount = 100l;
        utils.waitForHeightArise();
        utils.distributeAssets(richPk, pks, assetMap, assetAmount, true);
        utils.distributeWaves(richPk, pks, wavesAmount, true);
        return assetMap;
    }


    private List<AssetPair> createAssetPairs(Map<String, Integer> assetMap) {
        List<AssetPair> pairs = new ArrayList<>();
        Map<String, Integer> sortedMap = utils.sortAssets(assetMap);
        assetMap.put(Asset.WAVES, 8);
        LinkedHashMap<String, Integer> x = new LinkedHashMap<>(sortedMap);
        List<String> assetList = new ArrayList<>(x.keySet());

        for (int i = 0; i < assetList.size() - 1; i++) {
            for (int j = i + 1; j < assetList.size(); j++) {
                pairs.add(new AssetPair(assetList.get(i), assetList.get(j)));
            }
        }
        return pairs;
    }

    public void prepareOHTest(String seed, String fileName) throws InterruptedException, TimeoutException, IOException {
        int accountsNum = 100;
        int startNonce = 0;

//        PrivateKeyAccount pks = utils.getAccountsBySeed(customSeed, accountsNum, startNonce)
        List<PrivateKeyAccount> pks = utils.getAccountsBySeed(seed, accountsNum, startNonce);
        Map<String, Integer> assetMap = prep(pks, 2, accountsNum, startNonce);
        AssetPair pair = createAssetPairs(assetMap).get(0);
        int amountDecimals = assetMap.get(pair.getAmountAsset());
        int priceDecimals = assetMap.get(pair.getPriceAsset());

        utils.deleteFile(fileName);
        long amount = matcher.normAmount(1, amountDecimals);
        long price = matcher.normPrice(2, amountDecimals, priceDecimals);
        long nonMatchablePrice = matcher.normPrice(10, amountDecimals, priceDecimals);
        long cancelPrice = matcher.normPrice(9, amountDecimals, priceDecimals);
        long autocancelPrice = matcher.normPrice(1, amountDecimals, priceDecimals);
        for (int i = 0; i < 99; i++) {

            List<String> nextAmmo = new ArrayList<>();
            
            
            int orderlifetime = 20 * 24 * 60 * 60;

            for (PrivateKeyAccount pk : pks) {
                orderlifetime--;
                //     Thread.sleep(1);
                Order oInfoX = matcher.prepareOrder(
                        pk,
                        SELL,
                        pair,
                        amount,
                        cancelPrice,
                        orderlifetime--);
                nextAmmo.add(ammoSteps.printPostWithDeafultHeaders(getJson(oInfoX), "/matcher/orderbook", "TO_CANCEL"));
                // Thread.sleep(1);
                Order oInfo0 = matcher.prepareOrder(
                        pk,
                        SELL,
                        pair,
                        amount,
                        nonMatchablePrice,
                        orderlifetime);
                nextAmmo.add(ammoSteps.printPostWithDeafultHeaders(getJson(oInfo0), "/matcher/orderbook", "ACTIVE"));
                for (int k = 0; k < 20; k++) {
                    orderlifetime--;
                    Order oInfo1 = matcher.prepareOrder(
                            pk,
                            BUY,
                            pair,
                            amount,
                            price,
                            orderlifetime);
                    Order oInfo2 = matcher.prepareOrder(
                            pk,
                            SELL,
                            pair,
                            amount,
                            price,
                            orderlifetime);
                    nextAmmo.add(ammoSteps.printPostWithDeafultHeaders(getJson(oInfo1), "/matcher/orderbook", "TO_FILL"));
                    nextAmmo.add(ammoSteps.printPostWithDeafultHeaders(getJson(oInfo2), "/matcher/orderbook", "FILL"));

                }
                CancelOrder cancel = Transactions.makeOrderCancelTx(pk, pair, oInfoX.getId().toString());
                nextAmmo.add(ammoSteps.printPostWithDeafultHeaders(getJson(cancel),
                        String.format("/matcher/orderbook/%s/%s/cancel", pair.getAmountAsset(), pair.getPriceAsset()), "CANCEL"));
            }
            Files.write(Paths.get(fileName), nextAmmo, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println(pks.size());
        }
    }


    public void orderHistory(String seed, String fullHistoryFile) throws IOException {

        int startNonce = 0;
        int accountsNum = 100;
        long currentTs = System.currentTimeMillis();
        List<PrivateKeyAccount> pks = utils.getAccountsBySeed(seed, accountsNum, startNonce);
        utils.deleteFile(fullHistoryFile);

        for (int j = 0; j < 10000; j++) {
            List<String> nextAmmo = new ArrayList<>();
            for (PrivateKeyAccount pk : pks) {
                String signature = matcherNode.getOrderHistorySignature(pk, currentTs);
                nextAmmo.add(ammoSteps.printGetWithTimeStampAndSignaure("/matcher/orderbook/" + Base58.encode(pk.getPublicKey()) + "", currentTs, signature));
            }
            Files.write(Paths.get(fullHistoryFile), nextAmmo, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    public void orderHistoryPair(String seed, String historyFile, String amountAsset, String priceAsset ) throws IOException {

        int startNonce = 0;
        int accountsNum = 100;
        long currentTs = System.currentTimeMillis();
        utils.deleteFile(historyFile);
        //5*100*2*1000
        List<AssetPair> assetPairs = Arrays.asList(
                new AssetPair(amountAsset, priceAsset)
        );
        List<PrivateKeyAccount> pks = utils.getAccountsBySeed(seed, accountsNum, startNonce);
        for (int j = 0; j < 10000; j++) {
            List<String> nextAmmo = new ArrayList<>();

            for (PrivateKeyAccount pk : pks) {
                for (AssetPair pair : assetPairs) {
                    String signature = matcherNode.getOrderHistorySignature(pk, currentTs);
                    nextAmmo.add(ammoSteps.printGetWithTimeStampAndSignaure(
                            String.format("/matcher/orderbook/%s/%s/publicKey/%s", pair.getAmountAsset(), pair.getPriceAsset(), Base58.encode(pk.getPublicKey())),
                            currentTs, signature));
                }
            }
            Files.write(Paths.get(historyFile), nextAmmo, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }
    }

    public void generateOrdersManyAsset(String seed, String fileName) throws InterruptedException, TimeoutException, IOException {
        int accountsNum = 2500;
        int startNonce = 0;
        int assetNum = 20;
        utils.deleteFile(fileName);

        List<PrivateKeyAccount> pks = utils.getAccountsBySeed(seed, accountsNum, startNonce);
        Map<String, Integer> assetMap = prep(pks, assetNum, accountsNum, startNonce);
        List<AssetPair> pairsList = createAssetPairs(assetMap);


        List<String> remAssets = new ArrayList<>();
        for (int i = 0; i < pairsList.size(); i++) {
            String amountAsset = pairsList.get(i).getAmountAsset();
            String priceAsset = pairsList.get(i).getPriceAsset();
            StringBuffer sb = new StringBuffer();
            sb.append(amountAsset);
            sb.append(";");
            sb.append(assetMap.get(amountAsset));
            sb.append(";");
            sb.append(priceAsset);
            sb.append(";");
            sb.append(assetMap.get(priceAsset));
            remAssets.add(sb.toString());
        }
        Files.write(Paths.get("asset-pairs.txt"), remAssets);

        Map<PrivateKeyAccount, Integer> ordersForAccount = new HashMap<>();
        for (PrivateKeyAccount pk1 : pks) {
            ordersForAccount.put(pk1, 0);
        }

        int i = 0;

        while (!pks.isEmpty() && i < 600000) {
            List<String> nextAmmo = new ArrayList<>();
            i++;
            Random r = new Random();
            PrivateKeyAccount pk = pks.get(r.nextInt(pks.size()));

            AssetPair pair = pairsList.get(r.nextInt(pairsList.size()));

            int amountDecimals = assetMap.get(pair.getAmountAsset());
            int priceDecimals = assetMap.get(pair.getPriceAsset());
            Order.Type oType = BUY;
            long amount = matcher.normAmount(1, amountDecimals);
            long price = matcher.normPrice(1, amountDecimals, priceDecimals);

            Order oInfo = matcher.prepareOrder(pk, oType, pair, amount, price, false);

            nextAmmo.add(ammoSteps.printPostWithDeafultHeaders(getJson(oInfo), "/matcher/orderbook", "PLACE"));

            int currentCount = ordersForAccount.get(pk);
            currentCount++;
            if (currentCount > 199) {
                pks.remove(pk);
                ordersForAccount.remove(pk);
            } else
                ordersForAccount.put(pk, currentCount);
            Files.write(Paths.get(fileName), nextAmmo, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

    }


    public void generateOrdersManyAssetWithFilling(String seed, String fileName) throws InterruptedException, TimeoutException, IOException {
        utils.deleteFile(fileName);

        int accountsNum = 3500;
        int startNonce = 0;
        int assetNum = 20;
        List<PrivateKeyAccount> pks = utils.getAccountsBySeed(seed, accountsNum, startNonce);
        Map<String, Integer> assetMap = prep(pks, assetNum, accountsNum, startNonce);
        List<AssetPair> pairsList = createAssetPairs(assetMap);


        List<String> remAssets = new ArrayList<>();
        for (int i = 0; i < pairsList.size(); i++) {
            String amountAsset = pairsList.get(i).getAmountAsset();
            String priceAsset = pairsList.get(i).getPriceAsset();
            StringBuffer sb = new StringBuffer();
            sb.append(amountAsset);
            sb.append(";");
            sb.append(assetMap.get(amountAsset));
            sb.append(";");
            sb.append(priceAsset);
            sb.append(";");
            sb.append(assetMap.get(priceAsset));
            remAssets.add(sb.toString());
        }
        Files.write(Paths.get("asset-pairs.txt"), remAssets);

        Map<PrivateKeyAccount, Integer> ordersForAccount = new HashMap<>();
        for (PrivateKeyAccount pk1 : pks) {
            ordersForAccount.put(pk1, 0);
        }

        int i = 0;

        Random r = new Random();
        Order.Type prevOrderType = SELL;
        for (AssetPair pair : pairsList) {
            List<String> nextAmmo = new ArrayList<>();
            PrivateKeyAccount pk = pks.get(r.nextInt(pks.size()));

            int amountDecimals = assetMap.get(pair.getAmountAsset());
            int priceDecimals = assetMap.get(pair.getPriceAsset());
            long amount = matcher.normAmount(1, amountDecimals);
            long price = matcher.normPrice(2, amountDecimals, priceDecimals);
            Order oInfo = matcher.prepareOrder(pk, prevOrderType, pair, amount, price, false);
            nextAmmo.add(ammoSteps.printPostWithDeafultHeaders(getJson(oInfo), "/matcher/orderbook", "PLACE"));

            int currentCount = ordersForAccount.get(pk);
            currentCount++;
            ordersForAccount.put(pk, currentCount);
            Files.write(Paths.get(fileName), nextAmmo, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

        while (!pks.isEmpty() && i < 600000) {
            List<String> nextAmmo = new ArrayList<>();
            Order.Type oType = prevOrderType.equals(SELL) ? BUY : SELL;
            prevOrderType = oType;

            for (AssetPair pair : pairsList) {
                if (pks.isEmpty())
                    break;
                i++;
                PrivateKeyAccount pk = pks.get(r.nextInt(pks.size()));
                int amountDecimals = assetMap.get(pair.getAmountAsset());
                int priceDecimals = assetMap.get(pair.getPriceAsset());

                long amount = matcher.normAmount(2, amountDecimals);
                long price = matcher.normPrice(2, amountDecimals, priceDecimals);

                Order oInfo = matcher.prepareOrder(pk, oType, pair, amount, price, false);
                nextAmmo.add(ammoSteps.printPostWithDeafultHeaders(getJson(oInfo), "/matcher/orderbook", "MATCH"));

                int currentCount = ordersForAccount.get(pk);
                currentCount++;
                if (currentCount > 199) {
                    pks.remove(pk);
                    ordersForAccount.remove(pk);
                } else
                    ordersForAccount.put(pk, currentCount);
            }
            Files.write(Paths.get(fileName), nextAmmo, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

    }

    public void genCancel(String seed, String fileName) throws InterruptedException, TimeoutException, IOException {
        utils.deleteFile(fileName);
        int accountsNum = 2000;
        int startNonce = 0;
        List<PrivateKeyAccount> pks = new ArrayList<>();
        pks.addAll(utils.getAccountsBySeed(seed, accountsNum, startNonce));


        Random r = new Random();

        while (!pks.isEmpty()) {
            List<String> nextAmmo = new ArrayList<>();
            int i = r.nextInt(pks.size());
            List<Order> oh = matcherNode.getOrders(pks.get(i));
            for (Order o : oh) {
                if (!o.isActive())
                    continue;
                CancelOrder cancel = Transactions.makeOrderCancelTx(pks.get(i), o.getAssetPair(), o.getId().toString());
                nextAmmo.add(ammoSteps.printPostWithDeafultHeaders(getJson(cancel),
                        String.format("/matcher/orderbook/%s/%s/cancel", o.getAssetPair().getAmountAsset(), o.getAssetPair().getPriceAsset()), "CANCEL"));
            }
            pks.remove(pks.get(i));
            Files.write(Paths.get(fileName), nextAmmo, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        }

    }

}
