package com.wavesplatform.helpers;

/**
 * Created by DrBlast on 28.11.2016.
 */
public enum MethodEnum {

    GET_BALANCE("addresses/balance/%s"),
    ASSET_BALANCE_BY_ID_AND_ADDRESS("assets/balance/%s/%s"),
    ASSET_BALANCE_BY_ADDRESS("assets/balance/%s"),
    TRANSACTION_INFO("transactions/info/%s"),
    ADDRESSES("addresses"),
    LEASE("leasing/lease"),
    ASSETS_ISSUE("transactions/broadcast"),
    ASSETS_BURN("assets/broadcast/burn"),
    ASSET_TRANSFER("assets/broadcast/transfer"),
    ALIAS_CREATE("alias/create"),
    SIGN("transactions/sign"),
    UTX_SIZE("transactions/unconfirmed/size"),
    SEND_TX("transactions/broadcast"),
    ASSET_DETAILS("assets/details/%s"),
    REISSUE("assets/reissue"),
    TX_ADDRESS("transactions/address/%s/limit/%s"),
    PLACE_ORDER("http://devnet-aws-fr-4.wavesnodes.com:6886/matcher/orderbook"),
    CANCEL_ORDER("http://devnet-aws-fr-4.wavesnodes.com:6886/matcher/orderbook/%s/%s/cancel"),
    DELETE_ORDER("http://devnet-aws-fr-4.wavesnodes.com:6886/matcher/orderbook/%s/%s/delete"),
    HEIGHT("http://%s.%s.wavesnodes.com:6869/blocks/height"),
    NODE_VERSION("http://%s.%s.wavesnodes.com:6869/node/version"),
    NODE_STATUS("http://%s.%s.wavesnodes.com:6869/node/status"),
    BROADCAST("transactions/broadcast"),
    TB("http://devnet-aws-fr-4.wavesnodes.com:6886/matcher/orderbook/%s/%s/tradableBalance/%s"),
    MARKETS("http://devnet-aws-fr-4.wavesnodes.com:6886/matcher/orderbook"),
    ASSET_DISTRIBUTION("assets/%s/distribution");

    private String name;

    MethodEnum(String name) {
        this.name = name;
    }

    public String getMethodName() {
        return this.name;
    }

    public static MethodEnum fromString(String text) {
        if (text != null) {
            for (MethodEnum b : MethodEnum.values()) {
                if (text.equalsIgnoreCase(b.name)) {
                    return b;
                }
            }
        }
        return null;
    }
}
