package com.mallforge.payment.service;

import cn.hutool.crypto.symmetric.AES;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mallforge.common.exception.BusinessException;
import com.mallforge.common.exception.ErrorCode;
import com.mallforge.common.utils.TenantContext;
import com.mallforge.payment.channel.PaymentChannel;
import com.mallforge.payment.dto.PrepayRequest;
import com.mallforge.payment.dto.PrepayResponse;
import com.mallforge.payment.entity.PaymentConfig;
import com.mallforge.payment.entity.PaymentLog;
import com.mallforge.payment.mapper.PaymentConfigMapper;
import com.mallforge.payment.mapper.PaymentLogMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentConfigMapper configMapper;
    private final PaymentLogMapper logMapper;
    private final List<PaymentChannel> channels;
    private final ObjectMapper objectMapper;

    @Value("${mall.payment.aes-key:mallforge-payment-aes-key-16byte}")
    private String aesKey;

    private Map<String, PaymentChannel> channelMap;
    private AES aes;

    @PostConstruct
    public void init() {
        channelMap = channels.stream()
                .collect(Collectors.toMap(PaymentChannel::channel, Function.identity()));
        aes = new AES(aesKey.getBytes(StandardCharsets.UTF_8));
        log.info("Payment channels registered: {}", channelMap.keySet());
    }

    // ── 配置管理 ────────────────────────────────────────────

    public List<PaymentConfig> listConfigs() {
        List<PaymentConfig> list = configMapper.selectList(
                new LambdaQueryWrapper<PaymentConfig>()
                        .eq(PaymentConfig::getTenantId, TenantContext.getTenantId()));
        // 脱敏：清空 configJson，由前端按需请求
        list.forEach(c -> c.setConfigJson(null));
        return list;
    }

    public void saveConfig(PaymentConfig config) {
        config.setTenantId(TenantContext.getTenantId());
        // AES-256 加密 configJson
        if (config.getConfigJson() != null) {
            config.setConfigJson(aes.encryptHex(config.getConfigJson()));
        }
        if (config.getId() == null) {
            configMapper.insert(config);
        } else {
            configMapper.updateById(config);
        }
    }

    public Map<String, Object> testConnection(Long configId) {
        PaymentConfig config = getConfig(configId);
        PaymentChannel ch = requireChannel(config.getChannel());
        Map<String, Object> parsed = decryptConfig(config);
        boolean ok = ch.testConnection(parsed);

        // 更新测试时间和错误信息
        config.setTestedAt(LocalDateTime.now());
        config.setLastError(ok ? null : "连接测试失败");
        configMapper.updateById(config);

        return Map.of("success", ok, "channel", config.getChannel());
    }

    // ── 支付发起 ────────────────────────────────────────────

    public PrepayResponse prepay(PrepayRequest request) {
        PaymentConfig config = getActiveConfig(request.getChannel());
        PaymentChannel ch = requireChannel(request.getChannel());
        Map<String, Object> parsed = decryptConfig(config);

        PrepayResponse resp = ch.prepay(request, parsed);

        // 记录支付日志
        PaymentLog log = new PaymentLog();
        log.setTenantId(TenantContext.getTenantId());
        log.setOrderId(request.getOrderId());
        log.setChannel(request.getChannel());
        log.setAmount(java.math.BigDecimal.valueOf(request.getAmountFen(), 2));
        log.setStatus("pending");
        logMapper.insert(log);

        return resp;
    }

    // ── 回调处理 ────────────────────────────────────────────

    /**
     * 通用回调入口，由各通道 Controller 调用
     * 返回 orderNo 供上层更新订单状态
     */
    public PaymentChannel.NotifyResult handleNotify(String channel, Map<String, String> params, Long tenantId) {
        // 查找该租户的通道配置
        PaymentConfig config = configMapper.selectOne(
                new LambdaQueryWrapper<PaymentConfig>()
                        .eq(PaymentConfig::getTenantId, tenantId)
                        .eq(PaymentConfig::getChannel, channel)
                        .eq(PaymentConfig::getStatus, "enabled"));
        if (config == null) throw new BusinessException(ErrorCode.PAYMENT_CONFIG_NOT_FOUND);

        PaymentChannel ch = requireChannel(channel);
        Map<String, Object> parsed = decryptConfig(config);

        if (!ch.verifyNotify(params, parsed)) {
            throw new BusinessException(ErrorCode.PAYMENT_VERIFY_FAILED);
        }

        PaymentChannel.NotifyResult result = ch.parseNotify(params);

        // 更新支付日志
        if (result.success()) {
            logMapper.updateStatusByOrderNo(result.orderNo(), "success", result.transactionNo());
        }

        return result;
    }

    // ── 私有工具 ────────────────────────────────────────────

    private PaymentConfig getConfig(Long id) {
        PaymentConfig c = configMapper.selectById(id);
        if (c == null) throw new BusinessException(ErrorCode.PAYMENT_CONFIG_NOT_FOUND);
        return c;
    }

    private PaymentConfig getActiveConfig(String channel) {
        PaymentConfig c = configMapper.selectOne(
                new LambdaQueryWrapper<PaymentConfig>()
                        .eq(PaymentConfig::getTenantId, TenantContext.getTenantId())
                        .eq(PaymentConfig::getChannel, channel)
                        .eq(PaymentConfig::getStatus, "enabled"));
        if (c == null) throw new BusinessException(ErrorCode.PAYMENT_CONFIG_NOT_FOUND);
        return c;
    }

    private PaymentChannel requireChannel(String channel) {
        PaymentChannel ch = channelMap.get(channel);
        if (ch == null) throw new BusinessException(500, "不支持的支付通道: " + channel);
        return ch;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> decryptConfig(PaymentConfig config) {
        try {
            String json = aes.decryptStr(config.getConfigJson());
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("Failed to decrypt payment config: {}", config.getId(), e);
            throw new BusinessException(500, "支付配置解密失败");
        }
    }
}
