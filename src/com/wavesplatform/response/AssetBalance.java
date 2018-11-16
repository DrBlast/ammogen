package com.wavesplatform.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AssetBalance {

    @SerializedName("assetId")
    @Expose
    private String assetId;
    @SerializedName("balance")
    @Expose
    private Long balance;
    @SerializedName("reissuable")
    @Expose
    private Boolean reissuable;
    @SerializedName("minSponsoredAssetFee")
    @Expose
    private Long minSponsoredAssetFee;
    @SerializedName("sponsorBalance")
    @Expose
    private Long sponsorBalance;
    @SerializedName("quantity")
    @Expose
    private Long quantity;
    @SerializedName("issueTransaction")
    @Expose
    private AssetDetails assetDetails;

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public Boolean getReissuable() {
        return reissuable;
    }

    public void setReissuable(Boolean reissuable) {
        this.reissuable = reissuable;
    }

    public Long getMinSponsoredAssetFee() {
        return minSponsoredAssetFee;
    }

    public void setMinSponsoredAssetFee(Long minSponsoredAssetFee) {
        this.minSponsoredAssetFee = minSponsoredAssetFee;
    }

    public Long getSponsorBalance() {
        return sponsorBalance;
    }

    public void setSponsorBalance(Long sponsorBalance) {
        this.sponsorBalance = sponsorBalance;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public AssetDetails getAssetDetails() {
        return assetDetails;
    }

    public void setIssueTransaction(AssetDetails assetDetails) {
        this.assetDetails = assetDetails;
    }


}
