package com.cargps.android.data;

import java.io.Serializable;

public class OrderInfo implements Serializable {
//	 "type": 0,
//     "startTime": "2016-12-05T04:45:00.000+0000",
//     "endTime": "2016-12-05T06:02:00.000+0000",
//     "orderAmount": 53.78,
//     "price": null,
//     "countTime": 47

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String type;
    public String startTime;
    public String endTime;
    public double orderAmount;
    public double price;
    public int countTime;
    public String orderId;
}
