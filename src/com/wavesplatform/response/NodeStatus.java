package com.wavesplatform.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NodeStatus {


    @SerializedName("blockchainHeight")
    @Expose
    private Integer blockchainHeight;
    @SerializedName("stateHeight")
    @Expose
    private Integer stateHeight;
    @SerializedName("updatedTimestamp")
    @Expose
    private Long updatedTimestamp;
    @SerializedName("updatedDate")
    @Expose
    private String updatedDate;

    public Integer getBlockchainHeight() {
        return blockchainHeight;
    }

    public void setBlockchainHeight(Integer blockchainHeight) {
        this.blockchainHeight = blockchainHeight;
    }

    public Integer getStateHeight() {
        return stateHeight;
    }

    public void setStateHeight(Integer stateHeight) {
        this.stateHeight = stateHeight;
    }

    public Long getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(Long updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }
}
