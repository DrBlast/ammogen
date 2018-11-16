package com.wavesplatform.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NodeVersion {

    @SerializedName("version")
    @Expose
    private String version;

    public String getVersion() {
        return version;
    }

}
