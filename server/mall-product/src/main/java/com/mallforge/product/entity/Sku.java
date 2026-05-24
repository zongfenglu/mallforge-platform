package com.mallforge.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@TableName("sku")
public class Sku {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    /** JSON: {"颜色":"红色","尺码":"XL"} */
    private String specJson;
    private BigDecimal price;
    private Integer stock;
    private String code;
    private String image;
    private Integer status; // 1=正常 0=禁用
}
