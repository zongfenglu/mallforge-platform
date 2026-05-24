package com.mallforge.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private Long parentId;
    private String name;
    private String icon;
    private Integer sort;
    private Integer status; // 1=正常 0=禁用
}
