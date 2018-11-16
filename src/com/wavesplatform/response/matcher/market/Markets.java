package com.wavesplatform.response.matcher.market;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

public class Markets {
    @SerializedName("matcherPublicKey")
    @Expose
    private String matcherPublicKey;
    @SerializedName("markets")
    @Expose
    private List<Market> market = null;

    public String getMatcherPublicKey() {
        return matcherPublicKey;
    }

    public void setMatcherPublicKey(String matcherPublicKey) {
        this.matcherPublicKey = matcherPublicKey;
    }

    public List<Market> getMarkets() {
        return market;
    }

    public void setMarkets(List<Market> market) {
        this.market = market;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("matcherPublicKey", matcherPublicKey).append("markets", market).toString();
    }
}
