package com.wavesplatform.response.matcher;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("senderPublicKey")
    @Expose
    private String senderPublicKey;
    @SerializedName("matcherPublicKey")
    @Expose
    private String matcherPublicKey;
    @SerializedName("assetPair")
    @Expose
    private AssetPair assetPair;
    @SerializedName("orderType")
    @Expose
    private String orderType;
    @SerializedName("price")
    @Expose
    private Long price;
    @SerializedName("amount")
    @Expose
    private Long amount;
    @SerializedName("timestamp")
    @Expose
    private Long timestamp;
    @SerializedName("expiration")
    @Expose
    private Long expiration;
    @SerializedName("matcherFee")
    @Expose
    private Long matcherFee;
    @SerializedName("signature")
    @Expose
    private String signature;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderPublicKey() {
        return senderPublicKey;
    }

    public void setSenderPublicKey(String senderPublicKey) {
        this.senderPublicKey = senderPublicKey;
    }

    public String getMatcherPublicKey() {
        return matcherPublicKey;
    }

    public void setMatcherPublicKey(String matcherPublicKey) {
        this.matcherPublicKey = matcherPublicKey;
    }

    public AssetPair getAssetPair() {
        return assetPair;
    }

    public void setAssetPair(AssetPair assetPair) {
        this.assetPair = assetPair;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public Long getMatcherFee() {
        return matcherFee;
    }

    public void setMatcherFee(Long matcherFee) {
        this.matcherFee = matcherFee;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

}