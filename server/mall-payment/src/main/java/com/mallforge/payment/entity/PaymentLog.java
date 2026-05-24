package com.mallforge.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_log")
public class PaymentLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long orderId;
    private String channel;
    private BigDecimal amount;
    private String transactionNo;
    /** pending / success / failed */
    private String status;
    /** 原始回调报文 */
    private String rawNotify;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
