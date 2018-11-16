package com.wavesplatform.response.matcher.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Market {


    @SerializedName("amountAsset")
    @Expose
    private String amountAsset;
    @SerializedName("amountAssetName")
    @Expose
    private String amountAssetName;
    @SerializedName("amountAssetInfo")
    @Expose
    private AssetDecimalsInfo amountAssetInfo;
    @SerializedName("priceAsset")
    @Expose
    private String priceAsset;
    @SerializedName("priceAssetName")
    @Expose
    private String priceAssetName;
    @SerializedName("priceAssetInfo")
    @Expose
    private AssetDecimalsInfo priceAssetInfo;
    @SerializedName("created")
    @Expose
    private Long created;

    public String getAmountAsset() {
        return amountAsset;
    }

    public void setAmountAsset(String amountAsset) {
        this.amountAsset = amountAsset;
    }

    public String getAmountAssetName() {
        return amountAssetName;
    }

    public void setAmountAssetName(String amountAssetName) {
        this.amountAssetName = amountAssetName;
    }

    public AssetDecimalsInfo getAmountAssetInfo() {
        return amountAssetInfo;
    }

    public void setAmountAssetInfo(AssetDecimalsInfo amountAssetInfo) {
        this.amountAssetInfo = amountAssetInfo;
    }

    public String getPriceAsset() {
        return priceAsset;
    }

    public void setPriceAsset(String priceAsset) {
        this.priceAsset = priceAsset;
    }

    public String getPriceAssetName() {
        return priceAssetName;
    }

    public void setPriceAssetName(String priceAssetName) {
        this.priceAssetName = priceAssetName;
    }

    public AssetDecimalsInfo getPriceAssetInfo() {
        return priceAssetInfo;
    }

    public void setPriceAssetInfo(AssetDecimalsInfo assetDecimalsInfo) {
        this.priceAssetInfo = assetDecimalsInfo;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("amountAsset", amountAsset).append("amountAssetName", amountAssetName).append("amountAssetInfo", amountAssetInfo).append("priceAsset", priceAsset).append("priceAssetName", priceAssetName).append("priceAssetInfo", priceAssetInfo).append("created", created).toString();
    }
}
