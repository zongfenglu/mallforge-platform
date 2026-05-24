package com.mallforge.system.service;

import com.mallforge.common.exception.BusinessException;
import com.mallforge.common.exception.ErrorCode;
import com.mallforge.framework.security.JwtUtil;
import com.mallforge.system.dto.LoginRequest;
import com.mallforge.system.dto.LoginResponse;
import com.mallforge.system.entity.User;
import com.mallforge.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest req) {
        User user = userMapper.findByUsername(req.getUsername());
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(10001, "用户名或密码错误");
        }
        if (user.getStatus() != 1) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        // 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        String accessToken  = jwtUtil.generateToken(user.getId(), user.getTenantId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(86400L)
                .userInfo(LoginResponse.UserInfo.builder()
                        .userId(user.getId())
                        .tenantId(user.getTenantId())
                        .username(user.getUsername())
                        .realName(user.getRealName())
                        .avatar(user.getAvatar())
                        .build())
                .build();
    }
}
