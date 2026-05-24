package com.mallforge.payment.controller;

import com.mallforge.common.result.Result;
import com.mallforge.payment.dto.PrepayRequest;
import com.mallforge.payment.dto.PrepayResponse;
import com.mallforge.payment.entity.PaymentConfig;
import com.mallforge.payment.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /** 支付通道配置列表（脱敏，不含密钥） */
    @GetMapping("/configs")
    public Result<List<PaymentConfig>> configs() {
        return Result.ok(paymentService.listConfigs());
    }

    /** 新增或更新支付通道配置 */
    @PostMapping("/configs")
    public Result<?> saveConfig(@RequestBody PaymentConfig config) {
        paymentService.saveConfig(config);
        return Result.ok();
    }

    /** 测试通道连接 */
    @PostMapping("/configs/{id}/test")
    public Result<?> testConnection(@PathVariable Long id) {
        return Result.ok(paymentService.testConnection(id));
    }

    /** 发起预支付 */
    @PostMapping("/prepay")
    public Result<PrepayResponse> prepay(@RequestBody PrepayRequest request) {
        return Result.ok(paymentService.prepay(request));
    }

    /** 微信支付回调（无 JWT 鉴权，由 Gateway 白名单放行） */
    @PostMapping("/notify/wechat")
    public String wechatNotify(HttpServletRequest req,
                               @RequestParam(required = false) Long tenantId) {
        Map<String, String> params = extractParams(req);
        try {
            paymentService.handleNotify("wechat", params, tenantId);
            return "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
        } catch (Exception e) {
            return "<xml><return_code><![CDATA[FAIL]]></return_code></xml>";
        }
    }

    /** 支付宝回调 */
    @PostMapping("/notify/alipay")
    public String alipayNotify(HttpServletRequest req,
                               @RequestParam(required = false) Long tenantId) {
        Map<String, String> params = extractParams(req);
        try {
            paymentService.handleNotify("alipay", params, tenantId);
            return "success";
        } catch (Exception e) {
            return "fail";
        }
    }

    private Map<String, String> extractParams(HttpServletRequest req) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            params.put(name, req.getParameter(name));
        }
        return params;
    }
}
