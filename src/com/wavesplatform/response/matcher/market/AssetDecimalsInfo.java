package com.wavesplatform.response.matcher.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class AssetDecimalsInfo {

    @SerializedName("decimals")
    @Expose
    private Integer decimals;

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("decimals", decimals).toString();
    }

}
