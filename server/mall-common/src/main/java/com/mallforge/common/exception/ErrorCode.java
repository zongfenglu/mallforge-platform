package com.mallforge.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    UNAUTHORIZED(10001, "未登录或 token 已过期"),
    FORBIDDEN(10002, "无权限访问"),
    TENANT_NOT_FOUND(10003, "租户不存在或已禁用"),
    PARAM_ERROR(10004, "请求参数错误"),
    DATA_NOT_FOUND(10005, "数据不存在"),

    PRODUCT_NOT_FOUND(20001, "商品不存在"),
    PRODUCT_OFF_SHELF(20002, "商品已下架"),
    SKU_NOT_FOUND(20003, "SKU不存在"),
    SKU_STOCK_INSUFFICIENT(20004, "库存不足"),
    CATEGORY_NOT_FOUND(20005, "分类不存在"),

    ORDER_NOT_FOUND(30001, "订单不存在"),
    ORDER_STATUS_INVALID(30002, "订单状态不合法"),
    ORDER_CANCEL_FAILED(30003, "订单取消失败"),
    ORDER_ALREADY_PAID(30004, "订单已支付"),

    COUPON_NOT_FOUND(40001, "优惠券不存在"),
    COUPON_EXPIRED(40002, "优惠券已过期"),
    COUPON_USED(40003, "优惠券已使用"),
    COUPON_STOCK_EMPTY(40004, "优惠券已领完"),
    ACTIVITY_NOT_FOUND(40005, "活动不存在"),

    PAYMENT_CONFIG_NOT_FOUND(50001, "支付通道未配置"),
    PAYMENT_FAILED(50002, "支付发起失败"),
    PAYMENT_VERIFY_FAILED(50003, "支付签名验证失败"),
    REFUND_FAILED(50004, "退款失败");

    private final int code;
    private final String message;
}
