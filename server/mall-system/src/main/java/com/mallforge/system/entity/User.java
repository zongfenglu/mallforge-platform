package com.mallforge.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("`user`")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long tenantId;
    private String username;
    @TableField(select = false)
    private String passwordHash;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Integer status; // 0=禁用 1=正常
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
}
