package com.mallforge.framework.security;

import com.mallforge.common.utils.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 从 Gateway 注入的 Header 中恢复 TenantContext
 */
@Component
public class TenantContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        try {
            String tid = req.getHeader("X-Tenant-Id");
            String uid = req.getHeader("X-User-Id");
            if (tid != null && !"null".equals(tid)) TenantContext.setTenantId(Long.parseLong(tid));
            if (uid != null && !"null".equals(uid))  TenantContext.setUserId(Long.parseLong(uid));
            chain.doFilter(req, res);
        } finally {
            TenantContext.clear();
        }
    }
}
