package com.mallforge.system.decorator.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("store_page")
public class StorePage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String name;
    /** home / activity / custom */
    private String type;
    /** 完整的页面 JSON 结构 */
    private String pageJson;
    /** draft / published */
    private String status;
    private Integer version;
    private Boolean isDefault;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
