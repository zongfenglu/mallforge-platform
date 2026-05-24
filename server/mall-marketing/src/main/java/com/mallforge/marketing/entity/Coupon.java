package com.mallforge.marketing.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon")
public class Coupon {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String name;
    /** full_reduce=满减, discount=折扣, direct=直减 */
    private String type;
    private BigDecimal thresholdAmount;
    private BigDecimal reduceAmount;
    /** 折扣率 0.0~1.0，type=discount 时有效 */
    private BigDecimal discountRate;
    private Integer totalQty;
    /** 每人限领次数 */
    private Integer userLimit;
    /** 领取后有效天数 */
    private Integer validDays;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    /** active / inactive / exhausted */
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
