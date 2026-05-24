package com.mallforge.system.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiresIn;
    private UserInfo userInfo;

    @Data
    @Builder
    public static class UserInfo {
        private Long userId;
        private Long tenantId;
        private String username;
        private String realName;
        private String avatar;
    }
}
