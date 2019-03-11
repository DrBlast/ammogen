package com.wavesplatform;

import com.wavesplatform.generators.*;
import com.wavesplatform.wavesj.Node;
import com.wavesplatform.wavesj.PrivateKeyAccount;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import static com.wavesplatform.TestVariables.*;


public class Main extends Defaults {

    private static int getChoice() {

        int selection;
        Scanner input = new Scanner(System.in);

        System.out.println("Choose from these choices");
        System.out.println("-------------------------\n");
        System.out.println("0 - Transfer");
        System.out.println("1 - MassTransfer");
        System.out.println("2 - Api load");
        System.out.println("3 - Get blocks stats");
        System.out.println("-------------------------\n");
        System.out.println("4 - Prepare order history test");
        System.out.println("5 - Full order history test");
        System.out.println("6 - Order history by pair");
        System.out.println("-------------------------\n");
        System.out.println("7 - Place orders");
        System.out.println("8 - Cancel orders");
        System.out.println("-------------------------\n");
        System.out.println("9 - Matching");
        System.out.println("-------------------------\n");
        System.out.println("10 - Issue");
        System.out.println("11 - SetAssetScript");
        System.out.println("12 - Asset Balance");
        System.out.println("13 - Asset Transfer");

        selection = input.nextInt();
        return selection;
    }

    private static void issue(Scanner in, String seed) throws IOException, URISyntaxException, InterruptedException {
        System.out.println("Enter number of tx, default `100`");
        String val = in.nextLine();
        int txNum = val.equals("") ? 100 : Integer.parseInt(val);
        System.out.println("Enter results file name. default: `isstx.txt`");
        val = in.nextLine();
        String fileName = val.equals("") ? "ammo/isstx.txt" : val;
        System.out.println("Scripted asset? Y/N");
        val = in.nextLine();
        boolean isScripted = val.toLowerCase().equals("y");
        System.out.println("Enter number of issuers. default: `1`");
        val = in.nextLine();
        int issuersNum = val.equals("") ? 1 : Integer.parseInt(val);
        IssueAmmo issueAmmo = new IssueAmmo(richAkk, node);
        issueAmmo.genIssueTxs(issuersNum, txNum, isScripted, fileName, seed);
    }

    private static void setAssetScript(Scanner in) throws IOException, URISyntaxException {
        System.out.println("Enter results file name. default: `setassetscrtx.txt`");
        String val = in.nextLine();
        String filename = val.equals("") ? "ammo/setassetscrtx.txt" : val;
        SetAssetScriptAmmo setAssetScriptAmmo = new SetAssetScriptAmmo(richAkk, node);
        setAssetScriptAmmo.genSetAssetScriptTxs("scriptedAssetIds.txt", richAkk, filename);
    }

    private static void assetTransfer(Scanner in, String seed) throws URISyntaxException, IOException {
        System.out.println("Enter results file name. default: `assettransfertx.txt`");
        String val = in.nextLine();
        String filename = val.equals("") ? "ammo/assettransfertx.txt" : val;
        System.out.println("Enter assetIds file name. default: `scriptedAssetIds.txt`");
        val = in.nextLine();
        String assetIdsfileName = val.equals("") ? "scriptedAssetIds.txt" : val;
        AssetTransferAmmo assetTransferAmmo = new AssetTransferAmmo(node);
        assetTransferAmmo.genAssetTransferTxs(seed, assetIdsfileName, true, filename);
    }

    private static void assetBalance(Scanner in, String seed) throws URISyntaxException, IOException {
        System.out.println("Enter results file name for AssetBalanceByAddress requests. default: `assetbalancebyaddress.txt`");
        String val = in.nextLine();
        String filename = val.equals("") ? "ammo/assetbalancebyaddress.txt" : val;
        System.out.println("Enter results file name for AssetBalanceByAssetId requests. default: `assetbalancebyassetid.txt`");
        val = in.nextLine();
        String filename2 = val.equals("") ? "ammo/assetbalancebyassetid.txt" : val;
        AssetBalanceAmmo assetBalanceAmmo = new AssetBalanceAmmo(node);
        assetBalanceAmmo.genAssetBalanceByAddress(seed,filename);
        assetBalanceAmmo.genAssetBalanceByAssetId(seed,"scriptedAssetIds2.txt", filename2);

    }

    private static void transfer(Scanner in, String seed) throws URISyntaxException, InterruptedException, TimeoutException, IOException {

        System.out.println("Enter results file name. default: `trtx.txt`");
        String val = in.nextLine();
        String fileName = val.equals("") ? "ammo/trtx.txt" : val;
        TransferAmmo transferAmmo = new TransferAmmo(richAkk, node);
        transferAmmo.genTransferTxs(seed, fileName);
    }

    private static void massTransfer(Scanner in, String seed) throws URISyntaxException, InterruptedException, TimeoutException, IOException {

        System.out.println("Enter results file name. default: `masstx.txt`");
        String val = in.nextLine();
        String fileName = val.equals("") ? "ammo/masstx.txt" : val;
        MassTransferAmmo massTransferAmmo = new MassTransferAmmo(richAkk, node);
        massTransferAmmo.genMasstTx(seed, fileName);
    }

    private static void apiLoad(Scanner in, String seed) throws URISyntaxException, IOException {
        System.out.println("Enter number of tx, default `30` (recommend): ");
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

    private static void orderHistory(Scanner in, String seed) throws IOException, URISyntaxException {
        System.out.println("Enter results file name. default: `full-history.txt`");
        String val = in.nextLine();
        String fileName = val.equals("") ? "ammo/full-history.txt" : val;

        MatcherAmmo matcherAmmo = new MatcherAmmo(richAkk, node);
        matcherAmmo.orderHistory(seed, fileName);
    }

    private static void orderHistoryPair(Scanner in, String seed) throws IOException, URISyntaxException {
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

    private static void cancelOrders(Scanner in, String seed) throws URISyntaxException, IOException {
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
            //TRANSFER
            case 0:
                transfer(in, val);
                break;
            //MASS TRANSFER
            case 1:
                massTransfer(in, val);
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
                cancelOrders(in, seed);
                break;
            case 9:
                matching(in, seed);
                break;
            case 10:
                issue(in, seed);
                break;
            case 11:
                setAssetScript(in);
                break;
            case 12:
                assetBalance(in, seed);
                break;
            case 13:
                assetTransfer(in, seed);
                break;
            default:
                // The user input an unexpected choice.
        }
        System.out.println();

    }

    public static void fillDefaultValues() throws URISyntaxException {
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
