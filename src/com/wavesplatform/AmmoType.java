package com.wavesplatform;

public enum AmmoType {

    API("api.txt"),
    TRANSFER("transfertx.txt"),
    MASS_TRANSFER("masstx.txt");


    private String name;

    AmmoType(String name) {
        this.name = name;
    }


    public static AmmoType fileName(String text) {
        if (text != null) {
            for (AmmoType b : AmmoType.values()) {
                if (text.equalsIgnoreCase(b.name)) {
                    return b;
                }
            }
        }
        return null;
    }
}
