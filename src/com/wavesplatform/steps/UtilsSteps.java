package com.wavesplatform.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wavesplatform.helpers.KeyComparator;
import com.wavesplatform.helpers.MethodEnum;
import com.wavesplatform.helpers.NodeDefaults;
import com.wavesplatform.TestVariables;
import com.wavesplatform.response.TransactionInfo;
import com.wavesplatform.wavesj.*;
import com.wavesplatform.wavesj.json.WavesJsonMapper;
import com.wavesplatform.wavesj.transactions.IssueTransactionV2;
import com.wavesplatform.wavesj.transactions.MassTransferTransaction;
import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeoutException;

import static com.google.common.collect.Lists.partition;
import static com.wavesplatform.TestVariables.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class UtilsSteps extends NodeDefaults {

    private BackendSteps steps;
    private Node node;
    private static byte chainId = TestVariables.getChainId();

    public UtilsSteps(Node node) throws URISyntaxException {
        this.node = node;
        this.steps = new BackendSteps();
        this.matcher = new MatcherSteps();
    }
    
    public int waitForHeightArise() throws IOException, InterruptedException {
        int h = node.getHeight();
        while (node.getHeight() != h + 1) {
            Thread.sleep(5000);
        }
        return h + 1;
    }

    public void waitForTransaction(String txId) throws InterruptedException, UnsupportedEncodingException, TimeoutException {
        TransactionInfo txInfo = steps.sendGetAndWaitSuccess(TransactionInfo.class, MethodEnum.TRANSACTION_INFO, txId);
    }

    public static String getJson(ApiJson obj) throws JsonProcessingException {
        WavesJsonMapper wavesJsonMapper = new WavesJsonMapper(chainId);
        return wavesJsonMapper.writeValueAsString(obj);
    }

    public void checkThatListIsEmpty(List list) {
        assertThat("Список не пустой!", list.isEmpty());
    }

    
    public String issueAsset(PrivateKeyAccount pk, byte decimals, String script) throws IOException, TimeoutException, InterruptedException, URISyntaxException {
        PrivateKeyAccount sAccount = PrivateKeyAccount.fromSeed(getRichSeed(), 0, chainId);
        String assetName = RandomStringUtils.randomAlphabetic(5) + "_" + decimals;

        String encodedCompiledScript = (script == null) ? null : new Node(TestVariables.getProtocol() + getHost().replace("/", ""), getChainId()).compileScript(script);

        IssueTransactionV2 issueAsset = Transactions.makeIssueTx(
                sAccount,
                chainId,
                assetName + "" + "_" + decimals,
                assetName + System.currentTimeMillis(),
                Long.MAX_VALUE,
                decimals,
                true,
                encodedCompiledScript,
                ISSUE_FEE);

        TransactionInfo issueTxInfo = steps.sendPost(getJson(issueAsset), TransactionInfo.class, MethodEnum.ASSETS_ISSUE);
        waitForTransaction(issueTxInfo.getId());
        return issueAsset.getId().toString();
    }

    
    public String issueAsset(byte decimals, String script) throws IOException, TimeoutException, InterruptedException, URISyntaxException {
        PrivateKeyAccount richAccount = PrivateKeyAccount.fromSeed(getRichSeed(), 0, chainId);
        return issueAsset(richAccount, decimals, script);
    }

    
    public String massTransfer(PrivateKeyAccount fromAcc, String assetId, List<Transfer> transfers, long extraFee) throws IOException {
        MassTransferTransaction massTransfer = Transactions.makeMassTransferTx(fromAcc, assetId, transfers, 100000 + (transfers.size() + 1) * 50000 + extraFee, null);
        TransactionInfo massTransferInfo = steps.sendPost(getJson(massTransfer), TransactionInfo.class, MethodEnum.BROADCAST);
        return massTransfer.getId().toString();
    }

    
    private List<Transfer> transfersList(List<PrivateKeyAccount> pks, long amount) {
        List<Transfer> transfers = new ArrayList<>();
        for (PrivateKeyAccount pk : pks)
            transfers.add(new Transfer(pk.getAddress(), amount));
        return transfers;
    }

    
    public void distributeWaves(PrivateKeyAccount richAccount, List<PrivateKeyAccount> pks, long amount, boolean norm, long extraFee) throws IOException {
        long normedAmount = (norm) ? matcher.normAmount(amount, 8) : amount;

        Collection<List<PrivateKeyAccount>> pPk = partition(pks, 100);
        for (List<PrivateKeyAccount> x : pPk) {
            massTransfer(richAccount, Asset.WAVES, transfersList(x, normedAmount), extraFee);
        }
    }

    
    public void distributeAssets(PrivateKeyAccount richAccount, List<PrivateKeyAccount> pks, Map<String, Integer> assetMap, long amount, boolean norm, long extraFee) throws
            IOException {

        for (String asset : assetMap.keySet()) {
            long normedAmount = (norm) ? matcher.normAmount(amount, assetMap.get(asset)) : amount;

            Collection<List<PrivateKeyAccount>> pPk = partition(pks, 100);
            for (List<PrivateKeyAccount> x : pPk) {
                massTransfer(richAccount, asset, transfersList(x, normedAmount), extraFee);
            }
        }

    }

    
    public List<PrivateKeyAccount> getAccountsBySeed(String seedPart, int numberOfAccounts, int startNonce) {
        ArrayList<PrivateKeyAccount> pks = new ArrayList<>();
        for (int nonce = startNonce; nonce < startNonce + numberOfAccounts; nonce++) {
            pks.add(PrivateKeyAccount.fromSeed(seedPart, nonce, chainId));
        }
        return pks;
    }

    public void setScriptToAccounts(List<PrivateKeyAccount> accounts, String script) throws IOException, TimeoutException, InterruptedException {
        List<String> txIds = new ArrayList<>();
        for (PrivateKeyAccount account : accounts) {
            txIds.add(node.setScript(account, script, chainId, 1000000));
        }
        for (String tx : txIds)
            waitForTransaction(tx);
    }
    
    public Map<String, Integer> sortAssets(Map<String, Integer> unsorted) {
        KeyComparator bvc = new KeyComparator();
        Map<String, Integer> sortedAssets = new TreeMap<>(bvc);
        sortedAssets.putAll(unsorted);

        return sortedAssets;
    }


    public void deleteFile(String orderidsFile) throws IOException {
        Files.deleteIfExists(Paths.get(orderidsFile));
    }
}
