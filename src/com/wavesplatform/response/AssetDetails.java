package com.wavesplatform.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AssetDetails {
    @SerializedName("assetId")
    @Expose
    private String assetId;
    @SerializedName("issueHeight")
    @Expose
    private Long issueHeight;
    @SerializedName("issueTimestamp")
    @Expose
    private Long issueTimestamp;
    @SerializedName("issuer")
    @Expose
    private String issuer;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("decimals")
    @Expose
    private Integer decimals;
    @SerializedName("reissuable")
    @Expose
    private Boolean reissuable;
    @SerializedName("quantity")
    @Expose
    private Long quantity;
    @SerializedName("script")
    @Expose
    private String script;
    @SerializedName("scriptText")
    @Expose
    private String scriptText;
    @SerializedName("complexity")
    @Expose
    private Long complexity;
    @SerializedName("extraFee")
    @Expose
    private Long extraFee;
    @SerializedName("minSponsoredAssetFee")
    @Expose
    private Long minSponsoredAssetFee;

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public Long getIssueHeight() {
        return issueHeight;
    }

    public void setIssueHeight(Long issueHeight) {
        this.issueHeight = issueHeight;
    }

    public Long getIssueTimestamp() {
        return issueTimestamp;
    }

    public void setIssueTimestamp(Long issueTimestamp) {
        this.issueTimestamp = issueTimestamp;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public Boolean getReissuable() {
        return reissuable;
    }

    public void setReissuable(Boolean reissuable) {
        this.reissuable = reissuable;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScriptText() {
        return scriptText;
    }

    public void setScriptText(String scriptText) {
        this.scriptText = scriptText;
    }

    public Long getComplexity() {
        return complexity;
    }

    public void setComplexity(Long complexity) {
        this.complexity = complexity;
    }

    public Long getExtraFee() {
        return extraFee;
    }

    public void setExtraFee(Long extraFee) {
        this.extraFee = extraFee;
    }

    public Long getMinSponsoredAssetFee() {
        return minSponsoredAssetFee;
    }

    public void setMinSponsoredAssetFee(Long minSponsoredAssetFee) {
        this.minSponsoredAssetFee = minSponsoredAssetFee;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("assetId", assetId).append("issueHeight", issueHeight).append("issueTimestamp", issueTimestamp).append("issuer", issuer).append("name", name).append("description", description).append("decimals", decimals).append("reissuable", reissuable).append("quantity", quantity).append("script", script).append("scriptText", scriptText).append("complexity", complexity).append("extraFee", extraFee).append("minSponsoredAssetFee", minSponsoredAssetFee).toString();
    }

}

