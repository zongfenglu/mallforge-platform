package com.mallforge.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long categoryId;
    private String name;
    private String brand;
    private String coverImage;
    /** JSON 数组，多张图片 */
    private String images;
    private String description;
    private BigDecimal salePrice;
    private BigDecimal marketPrice;
    private Integer totalStock;
    private String unit;
    private Long salesCount;
    private Integer sort;
    /** 0=草稿 1=上架 2=下架 */
    private Integer status;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
