package com.mallforge.payment.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
public class PrepayRequest {
    private Long orderId;
    private String orderNo;
    /** 金额（分） */
    private Long amountFen;
    private String subject;
    /** 买家 openid（微信 JSAPI 支付必传） */
    private String openid;
    private String clientIp;
    private String channel;
}
