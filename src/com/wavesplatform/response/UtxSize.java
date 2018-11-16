package com.wavesplatform.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UtxSize {

    @SerializedName("size")
    @Expose
    private Integer size;

    public Integer getSize() {
        return size;
    }
}
