package com.cargps.android.data;

/**
 * Created by jpj on 2017/2/7.
 */

public class PaymentRequest {
    String channel;
    int amount;
    String payType;

    public PaymentRequest(String channel, int amount, String payType) {
        this.channel = channel;
        this.amount = amount;
        this.payType = payType;
    }
}
