package com.wavesplatform.response.matcher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssetPair {

    @SerializedName("amountAsset")
    @Expose
    private Object amountAsset;
    @SerializedName("priceAsset")
    @Expose
    private String priceAsset;

    public Object getAmountAsset() {
        return amountAsset;
    }

    public void setAmountAsset(Object amountAsset) {
        this.amountAsset = amountAsset;
    }

    public String getPriceAsset() {
        return priceAsset;
    }

    public void setPriceAsset(String priceAsset) {
        this.priceAsset = priceAsset;
    }

}
