package com.mallforge.payment.channel;

import com.mallforge.payment.dto.PrepayRequest;
import com.mallforge.payment.dto.PrepayResponse;

import java.util.Map;

/**
 * 支付通道策略接口
 * 每个支付渠道（微信/支付宝/Stripe/PayPal/银联）实现此接口
 */
public interface PaymentChannel {

    /** 通道标识，与 payment_config.channel 一致 */
    String channel();

    /**
     * 发起预支付，返回前端调起支付所需参数
     */
    PrepayResponse prepay(PrepayRequest request, Map<String, Object> config);

    /**
     * 验证回调签名，返回 true 表示签名正确
     */
    boolean verifyNotify(Map<String, String> params, Map<String, Object> config);

    /**
     * 从回调参数中提取订单号和交易流水号
     */
    NotifyResult parseNotify(Map<String, String> params);

    /**
     * 退款
     */
    void refund(String transactionNo, String refundNo, long amountFen, Map<String, Object> config);

    /**
     * 测试连接（验证配置有效性）
     */
    boolean testConnection(Map<String, Object> config);

    record NotifyResult(String orderNo, String transactionNo, boolean success) {}
}
