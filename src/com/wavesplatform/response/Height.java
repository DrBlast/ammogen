package com.wavesplatform.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Height {

    @SerializedName("height")
    @Expose
    private Integer height;

    public Integer getHeight() {
        return height;
    }

}
