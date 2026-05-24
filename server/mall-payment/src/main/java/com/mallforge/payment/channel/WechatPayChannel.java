package com.mallforge.payment.channel;

import com.mallforge.payment.dto.PrepayRequest;
import com.mallforge.payment.dto.PrepayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 微信支付通道（JSAPI / 小程序支付）
 *
 * config_json 字段:
 *   appId, mchId, apiKey, apiV3Key, privateKeyPath,
 *   certSerialNo, notifyUrl, useV3(boolean)
 *
 * 生产实现：引入 wechatpay-java SDK
 *   com.github.wechatpay-apiv3:wechatpay-java
 */
@Slf4j
@Component
public class WechatPayChannel implements PaymentChannel {

    @Override
    public String channel() { return "wechat"; }

    @Override
    public PrepayResponse prepay(PrepayRequest request, Map<String, Object> config) {
        String appId      = str(config, "appId");
        String mchId      = str(config, "mchId");
        String notifyUrl  = str(config, "notifyUrl");
        boolean useV3     = Boolean.TRUE.equals(config.get("useV3"));

        log.info("[WechatPay] prepay: mchId={}, orderNo={}, amount={}",
                mchId, request.getOrderNo(), request.getAmountFen());

        // TODO: 调用微信支付 V3 API
        // RSAAutoCertificateConfig cfg = new RSAAutoCertificateConfig.Builder()
        //     .merchantId(mchId)
        //     .privateKeyFromPath(str(config,"privateKeyPath"))
        //     .merchantSerialNumber(str(config,"certSerialNo"))
        //     .apiV3Key(str(config,"apiV3Key"))
        //     .build();
        // JsapiServiceExtension service = new JsapiServiceExtension.Builder().config(cfg).build();
        // PrepayWithRequestPaymentResponse resp = service.prepayWithRequestPayment(prepayRequest);

        // 返回 wx.requestPayment 所需参数（mock）
        return PrepayResponse.builder()
                .channel("wechat")
                .params(Map.of(
                        "appId",    appId,
                        "timeStamp", String.valueOf(System.currentTimeMillis() / 1000),
                        "nonceStr", java.util.UUID.randomUUID().toString().replace("-", ""),
                        "package",  "prepay_id=mock_prepay_id",
                        "signType", "RSA",
                        "paySign",  "mock_sign"
                ))
                .build();
    }

    @Override
    public boolean verifyNotify(Map<String, String> params, Map<String, Object> config) {
        // TODO: 使用微信 SDK 验证签名
        // NotificationParser parser = new NotificationParser(cfg);
        // Transaction transaction = parser.parse(requestParam, Transaction.class);
        log.info("[WechatPay] verifyNotify called");
        return true; // mock
    }

    @Override
    public NotifyResult parseNotify(Map<String, String> params) {
        String orderNo       = params.get("out_trade_no");
        String transactionNo = params.get("transaction_id");
        String resultCode    = params.get("result_code");
        return new NotifyResult(orderNo, transactionNo, "SUCCESS".equals(resultCode));
    }

    @Override
    public void refund(String transactionNo, String refundNo, long amountFen, Map<String, Object> config) {
        log.info("[WechatPay] refund: transactionNo={}, refundNo={}, amount={}", transactionNo, refundNo, amountFen);
        // TODO: 调用微信退款 API
    }

    @Override
    public boolean testConnection(Map<String, Object> config) {
        try {
            // TODO: 调用微信查询接口验证配置
            log.info("[WechatPay] testConnection: mchId={}", config.get("mchId"));
            return true;
        } catch (Exception e) {
            log.error("[WechatPay] testConnection failed", e);
            return false;
        }
    }

    private String str(Map<String, Object> config, String key) {
        Object v = config.get(key);
        return v == null ? "" : v.toString();
    }
}
