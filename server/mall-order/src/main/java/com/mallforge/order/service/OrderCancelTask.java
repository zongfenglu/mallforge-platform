package com.mallforge.order.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mallforge.order.entity.Order;
import com.mallforge.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时取消超时未付款订单
 * 每分钟扫描一次，取消 30 分钟前仍是 PENDING_PAYMENT 的订单
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelTask {

    private final OrderMapper orderMapper;

    @Value("${mall.order.cancel-timeout-minutes:30}")
    private int timeoutMinutes;

    @Scheduled(fixedDelay = 60_000)
    public void cancelTimeoutOrders() {
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(timeoutMinutes);

        List<Order> timeoutOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getStatus, "PENDING_PAYMENT")
                        .lt(Order::getCreatedAt, deadline));

        if (timeoutOrders.isEmpty()) return;

        log.info("[OrderCancelTask] Cancelling {} timeout orders", timeoutOrders.size());
        for (Order order : timeoutOrders) {
            orderMapper.update(null, new LambdaUpdateWrapper<Order>()
                    .eq(Order::getId, order.getId())
                    .eq(Order::getStatus, "PENDING_PAYMENT") // 乐观锁：防止并发
                    .set(Order::getStatus, "CANCELLED"));
        }
    }
}
