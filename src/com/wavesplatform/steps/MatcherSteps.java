package com.wavesplatform.steps;

import com.wavesplatform.*;
import com.wavesplatform.helpers.BalanceTypes;
import com.wavesplatform.helpers.KeyComparator;
import com.wavesplatform.helpers.MethodEnum;
import com.wavesplatform.helpers.NodeDefaults;
import com.wavesplatform.response.AssetDetails;
import com.wavesplatform.response.matcher.MatcherResponse;
import com.wavesplatform.response.matcher.Message;
import com.wavesplatform.response.matcher.OrderByStatus;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.matcher.CancelOrder;
import com.wavesplatform.wavesj.matcher.DeleteOrder;
import com.wavesplatform.wavesj.matcher.Order;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.wavesplatform.helpers.BalanceTypes.*;
import static com.wavesplatform.helpers.MethodEnum.ASSET_DETAILS;
import static com.wavesplatform.TestVariables.getChainId;

public class MatcherSteps extends NodeDefaults {

    public MatcherSteps() throws URISyntaxException {
        steps = new BackendSteps();
        matcherNode = new Node(TestVariables.getMatcherUrl(), getChainId());
        nodeUrl = TestVariables.getProtocol() + TestVariables.getHost();
        node = new Node(nodeUrl, getChainId());
    }


    public String placeOrder(PrivateKeyAccount pk, Order.Type orderType, AssetPair pair, Long amount, Long orderPrice, int orderLifeTimeInSeconds) throws IOException {
        Long orderLifeTimeInMillis = System.currentTimeMillis() + orderLifeTimeInSeconds * 1000;
        Order signedOrder = Transactions.makeOrder(pk, matcherNode.getMatcherKey(), orderType, pair,
                orderPrice, amount, orderLifeTimeInMillis, 300000);
        MatcherResponse placedOrder = steps.sendPost(UtilsSteps.getJson(signedOrder), MatcherResponse.class, MethodEnum.PLACE_ORDER);
        Message matcherMessage = steps.deserialize(placedOrder.getMessage(), Message.class);

        return matcherMessage.getId();
    }


    public com.wavesplatform.wavesj.matcher.Order prepareOrder(PrivateKeyAccount pk, Order.Type orderType, AssetPair pair, Long amount, Long orderPrice, int orderLifeTime) {
        long orderLifeTimeInMillis = System.currentTimeMillis() + orderLifeTime * 1000;

        return Transactions.makeOrder(pk, "BvfTcXu4d9Nsge8Woz42QW94Rf7MKcjtMYQz4L6MAPFX", orderType, pair,
                orderPrice, amount, orderLifeTimeInMillis, 300000);
    }



    public String placeOrder(PrivateKeyAccount pk, Order.Type orderType, AssetPair pair, int orderLifeTime, double amount, double price) throws IOException {
        return placeOrder(pk, orderType, pair, normAmount(amount, pair), normPrice(price, pair), orderLifeTime);
    }


    public Order prepareOrder(PrivateKeyAccount pk, Order.Type orderType, AssetPair pair, int orderLifeTime, double amount, double price) throws IOException {
        return prepareOrder(pk, orderType, pair, normAmount(amount, pair), normPrice(price, pair), orderLifeTime);
    }


    public String placeOrder(PrivateKeyAccount pk, Order.Type orderType, AssetPair pair, double amount, double price, boolean norm) throws InterruptedException, IOException {
        int orderLifeTime = 20 * 24 * 60 * 60;
        if (norm)
            return placeOrder(pk, orderType, pair, normAmount(amount, pair), normPrice(price, pair), orderLifeTime);
        else
            return placeOrder(pk, orderType, pair, (long) amount, (long) price, orderLifeTime);
    }


    public Order prepareOrder(PrivateKeyAccount pk, Order.Type orderType, AssetPair pair, double amount, double price, boolean norm) throws InterruptedException, IOException {
        int orderLifeTime = 20 * 24 * 60 * 60;
        if (norm)
            return prepareOrder(pk, orderType, pair, normAmount(amount, pair), normPrice(price, pair), orderLifeTime);
        else
            return prepareOrder(pk, orderType, pair, (long) amount, (long) price, orderLifeTime);
    }


    public void cancelOrder(PrivateKeyAccount pk, AssetPair assetPair, String orderId) throws InterruptedException {
        boolean orderCanceled = false;
        while (!orderCanceled) try {
            CancelOrder cancelOrder = Transactions.makeOrderCancel(pk, assetPair, orderId);
            MatcherResponse cancelledOrder = steps.sendPost(UtilsSteps.getJson(cancelOrder), MatcherResponse.class, MethodEnum.CANCEL_ORDER, TestVariables.getBtcAssetId());
            if (cancelledOrder.getStatus().equals("OrderCanceled")) {
                orderCanceled = true;
            } else {
                System.err.println("PROBLEM. Order cannot be cancelled");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.err.println("PROBLEM. Order cannot be cancelled");
            Thread.sleep(1000);
        }
    }


    public void deleteOrder(PrivateKeyAccount pk, AssetPair assetPair, String orderId) throws InterruptedException {
        boolean orderCanceled = false;
        while (!orderCanceled) {
            try {
                DeleteOrder cancelOrder = Transactions.makeDeleteOrder(pk, assetPair, orderId);
                MatcherResponse cancelledOrder = steps.sendPost(UtilsSteps.getJson(cancelOrder), MatcherResponse.class, MethodEnum.DELETE_ORDER, TestVariables.getBtcAssetId());
                if (cancelledOrder.getStatus().equals("OrderCanceled")) {
                    orderCanceled = true;
                } else {
                    System.err.println("PROBLEM. Order cannot be cancelled");
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                System.err.println("PROBLEM. Order cannot be cancelled");
                Thread.sleep(1000);
            }
        }
    }


    public long normAmount(double amount, Integer decimals) {
        return (long) Math.ceil(amount * (long) Math.pow(10, decimals));
    }


    public long normPrice(double price, Integer amountAssetDecimals, Integer priceAssetDecimals) {
        return (long) Math.ceil(price * (long) Math.pow(10, (8 + priceAssetDecimals - amountAssetDecimals)));
    }


    public long normAmount(double amount, AssetPair assetPair) throws UnsupportedEncodingException {
        AssetDetails assetDetails = steps.sendGet(AssetDetails.class, ASSET_DETAILS, assetPair.getAmountAsset());
        return normAmount(amount, assetDetails.getDecimals());
    }


    public long normPrice(double price, AssetPair assetPair) throws UnsupportedEncodingException {
        AssetDetails amountAssetDetails = steps.sendGet(AssetDetails.class, ASSET_DETAILS, assetPair.getAmountAsset());
        AssetDetails priceAssetDetails = steps.sendGet(AssetDetails.class, ASSET_DETAILS, assetPair.getPriceAsset());
        return (long) Math.ceil(price * (long) Math.pow(10, (8 + priceAssetDetails.getDecimals() - amountAssetDetails.getDecimals())));
    }


    public AssetPair setAssetPair(String a1, String a2) {
        KeyComparator bvc = new KeyComparator();
        if (a1.equals(Asset.WAVES))
            return
                    new AssetPair(a2, a1);
        if (a2.equals(Asset.WAVES))
            return
                    new AssetPair(a1, a2);
        if (bvc.compare(a1, a2) < 0)
            return
                    new AssetPair(a1, a2);
        else
            return
                    new AssetPair(a2, a1);
    }


    public OrderByStatus getOrderCnt(PrivateKeyAccount pk) throws IOException {
        List<Order> orderList = matcherNode.getOrders(pk);

        int totalOrders = orderList.size();
        int active = 0;
        int closed = 0;
        int partial = 0;
        int accepted = 0;
        int calcelled = 0;
        int filled = 0;
        boolean hasActive = false;
        if (!orderList.isEmpty()) {
            for (Order o : orderList) {
                if (o.isActive()) {
                    if (o.getStatus().equals(Order.Status.ACCEPTED))
                        accepted++;
                    if (o.getStatus().equals(Order.Status.PARTIALLY_FILLED))
                        partial++;
                    active++;
                    hasActive = true;
                } else {
                    if (o.getStatus().equals(Order.Status.FILLED))
                        filled++;
                    if (o.getStatus().equals(Order.Status.CANCELED))
                        calcelled++;
                    closed++;
                }
            }
        }
        OrderByStatus ost = new OrderByStatus();
        ost.accepted = accepted;
        ost.active = active;
        ost.closed = closed;
        ost.partial = partial;
        ost.filled = filled;
        ost.total = totalOrders;
        ost.calcelled = calcelled;
        ost.hasActive = hasActive;
        System.out.println("\nActive orders: " + active);
        System.out.println("Closed orders: " + closed);
        System.out.println("Total: " + totalOrders);

        return ost;
    }

    public Map<String, Long> putBalances(String asset, Long amount) {
        Map<String, Long> m = new HashMap<>();
        m.put(asset, amount);
        return m;
    }


    public Map<BalanceTypes, Map<String, Long>> getTradableAndReservedBalances(AssetPair pair, PrivateKeyAccount pk) throws IOException {
        Map<String, Map<Object, Object>> m = new HashMap<>();
        System.out.println("====================");
        System.out.println(String.format("Address: %s\n", pk.getAddress()));
        System.out.println("Balances");
        long wavesBalance = node.getBalance(pk.getAddress());
        long amountBalance = node.getBalance(pk.getAddress(), pair.getAmountAsset());
        long priceBalance = node.getBalance(pk.getAddress(), pair.getPriceAsset());

        if (!(pair.getAmountAsset().equals(Asset.WAVES) || (pair.getPriceAsset().equals(Asset.WAVES)))) {
            System.out.println("Waves:" + wavesBalance);
        }
        Map<BalanceTypes, Map<String, Long>> typedBalances = new HashMap<>();
        typedBalances.put(WAVES, putBalances("WAVES", wavesBalance));
        typedBalances.put(AMOUNT, putBalances(pair.getAmountAsset(), amountBalance));
        typedBalances.put(PRICE, putBalances(pair.getPriceAsset(), priceBalance));

        typedBalances.put(RESERVED, matcherNode.getReservedBalance(pk));
        typedBalances.put(TRADABLE, matcherNode.getTradableBalance(pair, pk.getAddress()));

        System.out.println(pair.getAmountAsset() + ": " + amountBalance);
        System.out.println(pair.getPriceAsset() + ": " + priceBalance);
        System.out.println("\nTradable balance:");
        System.out.println(matcherNode.getTradableBalance(pair, pk.getAddress()));
        System.out.println("\nReserved balance:");
        System.out.println(matcherNode.getReservedBalance(pk));

        return typedBalances;
    }


    public void cancelSomeOrders(PrivateKeyAccount pk) throws IOException {
        List<Order> orderList = matcherNode.getOrders(pk);
        int i = 0;
        for (Order o : orderList) {
            try {
                if (o.getStatus().isActive()) {
                    if (i % 2 == 0)
                        System.out.println(o.getId().toString() + " " + matcherNode.cancelOrder(pk, o.getAssetPair(), o.getId().toString()));
                    i++;
                }
            } catch (Exception ignore) {
            }
        }

    }


    public void cancelAllOrders(PrivateKeyAccount pk) throws IOException {

        boolean allOrders = false;
        while (!allOrders) {
            List<Order> orderList = matcherNode.getOrders(pk);
            int i = 0;
            for (Order o : orderList) {
                try {
                    if (o.getStatus().isActive()) {
                        System.out.println(o.getId().toString() + " " + matcherNode.cancelOrder(pk, o.getAssetPair(), o.getId().toString()));
                        i++;
                    }
                } catch (Exception ignore) {
                    System.out.println(o.getId().toString());
                }
            }
            if (i == 0)
                allOrders = true;
        }

    }


    public void cancelAndDeleteSomeOrders(PrivateKeyAccount pk) throws IOException {
        List<Order> orderList = matcherNode.getOrders(pk);
        System.out.println(pk.getAddress());
        int i = 0;
        if (orderList.isEmpty())
            System.out.println("empty");
        else
            for (Order o : orderList) {
                try {
                    if (o.getStatus().isActive()) {
                        System.out.println(o.getId().toString() + " " + matcherNode.cancelOrder(pk, o.getAssetPair(), o.getId().toString()));
                    } else if (i % 4 == 0) {
                        System.out.println(o.getId().toString() + " " + matcherNode.deleteOrder(pk, o.getAssetPair(), o.getId().toString()));
                    }
                    i++;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
    }


    public void cancelAndDeleteAllOrders(PrivateKeyAccount pk) throws IOException {
        boolean allOrders = false;
        while (!allOrders) {
            List<Order> orderList = matcherNode.getOrders(pk);
            int i = 0;
            for (Order o : orderList) {
                try {
                    if (o.getStatus().isActive()) {
                        System.out.println(o.getId().toString() + " " + matcherNode.cancelOrder(pk, o.getAssetPair(), o.getId().toString()));
                        i++;
                    }
                    System.out.println(o.getId().toString() + " " + matcherNode.deleteOrder(pk, o.getAssetPair(), o.getId().toString()));
                } catch (Exception ignore) {
                    System.out.println(o.getId().toString());
                }
            }
            if (i == 0)
                allOrders = true;
        }

    }


    public void deleteNonActiveOrders(PrivateKeyAccount pk) throws IOException {
        List<Order> orderList = matcherNode.getOrders(pk);
        System.out.println(pk.getAddress());
        if (orderList.isEmpty())
            System.out.println("empty");
        else
            for (Order o : orderList) {
                if (!o.getStatus().isActive())
                    matcherNode.deleteOrder(pk, o.getAssetPair(), o.getId().toString());
            }
    }
}
