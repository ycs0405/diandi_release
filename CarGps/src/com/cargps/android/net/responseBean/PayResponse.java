package com.cargps.android.net.responseBean;

public class PayResponse {
    public int statusCode;
    public String message;
    public PayData data;

    public class PayData {
        public String id;
        public String object;
        public long created;
        public boolean livemode;
        public boolean paid;
        public boolean refunded;
        public String app;
        public String channel;
        public String orderNo;
        public String clientIp;
        public int amount;
        public int amountSettle;
        public String currency;
        public String subject;
        public String body;
        public String timePaid;
        public long timeExpire;
        public String timeSettle;
        public String transactionNo;
        public Refunds refunds;
        public int amountRefunded;
        public String failureCode;
        public String failureMsg;
        //		metadata//不确定是什么JSON对象
//		extra//不确定是什么JSON对象
        public Credential credential;
        public String description;

    }

    public class Refunds {
        public String object;
        public String url;
        public String hasMore;
//		public Object data 不确定是什么类型的数组
    }

    public class Credential {
        public String object;
        public String alipay;
    }
}


//{
//	"statusCode": 200,
//	"message": "",
//	"data": {
//		"id": "ch_y9anPG04ePiPzvff98C48WzL",
//		"object": "charge",
//		"created": 1477711236,
//		"livemode": false,
//		"paid": false,
//		"refunded": false,
//		"app": "app_OqDG8KyTWD8O9eH4",
//		"channel": "alipay",
//		"orderNo": "1477711235972gob140f",
//		"clientIp": "10.27.213.11",
//		"amount": 1,
//		"amountSettle": 1,
//		"currency": "cny",
//		"subject": "Your Subject",
//		"body": "Your Body",
//		"timePaid": null,
//		"timeExpire": 1477797636,
//		"timeSettle": null,
//		"transactionNo": null,
//		"refunds": {
//			"object": "list",
//			"url": "/v1/charges/ch_y9anPG04ePiPzvff98C48WzL/refunds",
//			"hasMore": false,
//			"data": []
//		},
//		"amountRefunded": 0,
//		"failureCode": null,
//		"failureMsg": null,
//		"metadata": {
//			
//		},
//		"credential": {
//			"object": "credential",
//			"alipay": {
//				"orderInfo": "_input_charset=\"utf-8\"&body=\"Your Body\"&it_b_pay=\"2016-10-30 11:20:36\"&notify_url=\"https%3A%2F%2Fnotify.pingxx.com%2Fnotify%2Fcharges%2Fch_y9anPG04ePiPzvff98C48WzL\"&out_trade_no=\"1477711235972gob140f\"&partner=\"2008101606730584\"&payment_type=\"1\"&seller_id=\"2008101606730584\"&service=\"mobile.securitypay.pay\"&subject=\"Your Subject\"&total_fee=\"0.01\"&sign=\"YnJUQzg4WDEwYWZMRFNlYjU4RENPNHlQ\"&sign_type=\"RSA\""
//      }
//    },
//    "extra": {},
//    "description": null
//  }
//}
