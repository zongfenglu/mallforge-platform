package com.mallforge.system.controller;

import com.mallforge.common.result.Result;
import com.mallforge.system.dto.LoginRequest;
import com.mallforge.system.dto.LoginResponse;
import com.mallforge.system.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return Result.ok(authService.login(req));
    }
}
