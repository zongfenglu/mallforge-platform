package com.mallforge.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("payment_config")
public class PaymentConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    /** wechat / alipay / unionpay / stripe / paypal */
    private String channel;
    private String channelName;
    /** AES-256 加密存储的 JSON 配置 */
    private String configJson;
    private Boolean enableTest;
    /** enabled / disabled */
    private String status;
    private LocalDateTime testedAt;
    private String lastError;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
