package com.mallforge.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("`order`")
public class Order {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long userId;
    private String orderNo;
    /**
     * 订单状态:
     * PENDING_PAYMENT=待付款
     * PENDING_SHIPMENT=待发货
     * SHIPPED=已发货
     * COMPLETED=已完成
     * CANCELLED=已取消
     * REFUNDING=退款中
     * REFUNDED=已退款
     */
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private BigDecimal freight;
    private BigDecimal payAmount;
    private String payMethod;
    private LocalDateTime payTime;
    private String payTransactionNo;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String logisticsCompany;
    private String logisticsNo;
    private String remark;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
