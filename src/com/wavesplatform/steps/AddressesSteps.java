package com.wavesplatform.steps;

import com.wavesplatform.helpers.MethodEnum;
import com.wavesplatform.response.addresses.Balance;
import com.wavesplatform.wavesj.Asset;
import com.wavesplatform.TestVariables;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class AddressesSteps {


    private BackendSteps steps;

    private String URL = TestVariables.getProtocol().concat(TestVariables.getHost());

    public AddressesSteps(){
        this.steps = new BackendSteps();

    }



    public Long getBalance(String address) throws UnsupportedEncodingException {
        Balance balance = steps.sendGet(Balance.class, MethodEnum.GET_BALANCE, address);
        return balance.getBalance();
    }


    public long getBalance(String address, String assetId) throws IOException {
        return Asset.WAVES.equals(assetId)
                ? getBalance(address)
                : steps.sendGet(Balance.class, MethodEnum.ASSET_BALANCE_BY_ID_AND_ADDRESS, address, assetId).getBalance();
    }
}
