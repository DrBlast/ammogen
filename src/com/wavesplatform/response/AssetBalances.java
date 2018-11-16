package com.wavesplatform.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AssetBalances {


    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("balances")
    @Expose
    private List<AssetBalance> balances = null;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<AssetBalance> getBalances() {
        return balances;
    }

    public void setBalances(List<AssetBalance> balances) {
        this.balances = balances;
    }
}
