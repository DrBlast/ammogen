package com.wavesplatform.response.matcher;

public class TradableBalance {


    private String asset;

    private Long amount;


    public String getAsset() {
        return asset;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }
}
