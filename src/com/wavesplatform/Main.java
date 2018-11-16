package com.wavesplatform;

import com.wavesplatform.generators.ApiAmmoGen;
import com.wavesplatform.generators.MassTransferAmmo;
import com.wavesplatform.generators.MatcherAmmo;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static com.wavesplatform.TestVariables.*;


public class Main extends Defaults {

    public static int getChoice() {

        int selection;
        Scanner input = new Scanner(System.in);

        /***************************************************/

        System.out.println("Choose from these choices");
        System.out.println("-------------------------\n");
        System.out.println("1 - MassTransfer");
        System.out.println("2 - Api load");
        System.out.println("3 - Get blocks stats");
        System.out.println("-------------------------\n");
        System.out.println("4 - Prepare order history test");
        System.out.println("5 - Full order history test");
        System.out.println("6 - Order history by pair");
        System.out.println("7 - Place orders");
        System.out.println("8 - Cancel orders");
        System.out.println("9 - Matching");

        selection = input.nextInt();
        return selection;
    }


    private static void masstransfer(Scanner in, String seed) throws URISyntaxException, InterruptedException, TimeoutException, IOException {

        System.out.println("Enter results file name. default: `masstx.txt`");
        String val = in.nextLine();
        String fileName = val.equals("") ? "ammo/masstx.txt" : val;
        MassTransferAmmo massTransferAmmo = new MassTransferAmmo(richAkk, node);
        massTransferAmmo.genMasstTx(seed, fileName);
    }

    private static void apiLoad(Scanner in, String seed) throws URISyntaxException, InterruptedException, TimeoutException, IOException {
        System.out.println("Enter number of tx, default `30` (recomend): ");
        String val = in.nextLine();
        int txNum = val.equals("") ? 30 : Integer.parseInt(val);
        System.out.println("Reuse file with transactions? Y/N: ");
        val = in.nextLine();
        boolean reuse = val.toLowerCase().equals("y");
        ApiAmmoGen apiAmmoGen = new ApiAmmoGen(richAkk, node);
        apiAmmoGen.genApiLoadAmmo(seed, txNum, reuse);
    }

    private static void getStats(Scanner in) throws URISyntaxException, IOException {
        System.out.println("Enter min-max block height default `2-maxHeight`. Format: x-y: ");
        String val = in.nextLine();
        ApiAmmoGen apiAmmoGen = new ApiAmmoGen(richAkk, node);
        if (val.equals(""))
            apiAmmoGen.getStats();
        else {
            int min = Integer.parseInt(val.split("-")[0]);
            int max = Integer.parseInt(val.split("-")[1]);
            apiAmmoGen.getStats(min, max);
        }
    }

    private static void prepareOrderHistory(Scanner in, String seed) throws InterruptedException, IOException, TimeoutException, URISyntaxException {
        System.out.println("Enter results file name. default: `orders.txt`");
        String val = in.nextLine();
        String fileName = val.equals("") ? "ammo/orders.txt" : val;

        MatcherAmmo matcherAmmo = new MatcherAmmo(richAkk, node);
        matcherAmmo.prepareOHTest(seed, fileName);
    }

    private static void orderHistory(Scanner in, String seed) throws InterruptedException, IOException, TimeoutException, URISyntaxException {
        System.out.println("Enter results file name. default: `full-history.txt`");
        String val = in.nextLine();
        String fileName = val.equals("") ? "ammo/full-history.txt" : val;

        MatcherAmmo matcherAmmo = new MatcherAmmo(richAkk, node);
        matcherAmmo.orderHistory(seed, fileName);
    }

    private static void orderHistoryPair(Scanner in, String seed) throws InterruptedException, IOException, TimeoutException, URISyntaxException {
        System.out.println("Enter results file name. default: `history-by-pair.txt`");
        String val = in.nextLine();
        String fileName = val.equals("") ? "ammo/history-by-pair.txt" : val;
        System.out.println("Enter amount asset: ");
        String amountAsset = in.nextLine();
        System.out.println("Enter price asset: ");
        String priceAsset = in.nextLine();

        MatcherAmmo matcherAmmo = new MatcherAmmo(richAkk, node);
        matcherAmmo.orderHistoryPair(seed, fileName, amountAsset, priceAsset);
    }

    private static void placeOrders(Scanner in, String seed) throws URISyntaxException, InterruptedException, IOException, TimeoutException {
        System.out.println("Enter results file name. default: `orders.txt`");
        String val = in.nextLine();
        String fileName = val.equals("") ? "ammo/orders.txt" : val;

        MatcherAmmo matcherAmmo = new MatcherAmmo(richAkk, node);
        matcherAmmo.generateOrdersManyAsset(seed, fileName);
    }

    private static void cancelOrders(Scanner in, String seed) throws URISyntaxException, InterruptedException, IOException, TimeoutException {
        System.out.println("Enter results file name. default: `cancel-orders.txt`");
        String val = in.nextLine();
        String fileName = val.equals("") ? "ammo/cancel-orders.txt" : val;

        MatcherAmmo matcherAmmo = new MatcherAmmo(richAkk, node);
        matcherAmmo.genCancel(seed, fileName);
    }

    private static void matching(Scanner in, String seed) throws URISyntaxException, InterruptedException, IOException, TimeoutException {
        System.out.println("Enter results file name. default: `matching.txt`");
        String val = in.nextLine();
        String fileName = val.equals("") ? "ammo/matching.txt" : val;

        MatcherAmmo matcherAmmo = new MatcherAmmo(richAkk, node);
        matcherAmmo.generateOrdersManyAssetWithFilling(seed, fileName);
    }



    public static void main(String[] args) throws InterruptedException, IOException, URISyntaxException, TimeoutException {
        System.setProperty("env", args[0]);

        fillDefaultValues();
        ApiAmmoGen apiAmmoGen = new ApiAmmoGen(richAkk, node);


        int choice = getChoice();


        Scanner in = new Scanner(System.in);
        System.out.print(String.format("Enter default seed for the test. Default `%s`:", defaultTestSeed));
        String val = in.nextLine().trim();
        String seed = val.equals("") ? defaultTestSeed : val;
        String fileName;
        switch (choice) {
            //MASS TRANSFER
            case 1:
                masstransfer(in, val);
                break;

            //API LOAD
            case 2:
                apiLoad(in, seed);
                break;
            case 3:
                getStats(in);
                break;
            //prepare order history test
            case 4:
               prepareOrderHistory(in, seed);
                break;
            case 5:
                orderHistory(in, seed);
                break;
            case 6:
                orderHistoryPair(in, seed);
                break;
            case 7:
                placeOrders(in, seed);
                break;
            case 8:
                cancelOrders(in,seed);
                break;
            case 9:
                matching(in,seed);
                break;
            default:
                // The user input an unexpected choice.
        }
        System.out.println();

    }




    private static void fillDefaultValues() throws URISyntaxException {
        defaultTestSeed = TestVariables.getTestSeed();
        nodeUrl = getProtocol() + getHost();
        matcherUrl = getMatcherUrl();
        chainByte = getChainId();
        chainId = chainByte;
        node = new Node(nodeUrl, getChainId());
        richAkk = PrivateKeyAccount.fromSeed(getRichSeed(), 0, getChainId());
        matcherNode = new Node(matcherUrl, getChainId());
    }

}
