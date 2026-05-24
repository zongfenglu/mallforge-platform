package com.mallforge.order.service;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mallforge.common.exception.BusinessException;
import com.mallforge.common.exception.ErrorCode;
import com.mallforge.common.utils.TenantContext;
import com.mallforge.order.dto.CreateOrderRequest;
import com.mallforge.order.entity.Order;
import com.mallforge.order.entity.OrderItem;
import com.mallforge.order.mapper.OrderItemMapper;
import com.mallforge.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public Page<Order> page(int pageNum, int pageSize, String status) {
        LambdaQueryWrapper<Order> qw = new LambdaQueryWrapper<Order>()
                .eq(status != null, Order::getStatus, status)
                .orderByDesc(Order::getCreatedAt);
        return orderMapper.selectPage(new Page<>(pageNum, pageSize), qw);
    }

    public Order getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        return order;
    }

    public List<OrderItem> getItems(Long orderId) {
        return orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
    }

    @Transactional
    public Order createOrder(CreateOrderRequest req) {
        Order order = new Order();
        order.setTenantId(TenantContext.getTenantId());
        order.setUserId(TenantContext.getUserId());
        order.setOrderNo("MF" + IdUtil.getSnowflakeNextIdStr());
        order.setStatus("PENDING_PAYMENT");
        order.setReceiverName(req.getReceiverName());
        order.setReceiverPhone(req.getReceiverPhone());
        order.setReceiverAddress(req.getReceiverAddress());
        order.setFreight(req.getFreight() != null ? req.getFreight() : BigDecimal.ZERO);
        order.setDiscountAmount(req.getDiscountAmount() != null ? req.getDiscountAmount() : BigDecimal.ZERO);

        BigDecimal total = req.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        order.setPayAmount(total.add(order.getFreight()).subtract(order.getDiscountAmount()));
        order.setRemark(req.getRemark());

        orderMapper.insert(order);

        // 插入订单明细
        final Long orderId = order.getId();
        req.getItems().forEach(item -> {
            OrderItem oi = new OrderItem();
            oi.setOrderId(orderId);
            oi.setProductId(item.getProductId());
            oi.setSkuId(item.getSkuId());
            oi.setProductName(item.getProductName());
            oi.setProductImage(item.getProductImage());
            oi.setSpecDesc(item.getSpecDesc());
            oi.setPrice(item.getPrice());
            oi.setQuantity(item.getQuantity());
            orderItemMapper.insert(oi);
        });

        return order;
    }

    @Transactional
    public void ship(Long id, String logisticsCompany, String logisticsNo) {
        Order order = getById(id);
        if (!"PENDING_SHIPMENT".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID);
        }
        order.setStatus("SHIPPED");
        order.setLogisticsCompany(logisticsCompany);
        order.setLogisticsNo(logisticsNo);
        orderMapper.updateById(order);
    }

    @Transactional
    public void cancel(Long id) {
        Order order = getById(id);
        if (!"PENDING_PAYMENT".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_CANCEL_FAILED);
        }
        order.setStatus("CANCELLED");
        orderMapper.updateById(order);
    }

    /** 支付回调成功后调用 */
    @Transactional
    public void markPaid(String orderNo, String transactionNo, String payMethod) {
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>().eq(Order::getOrderNo, orderNo));
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        if ("PENDING_PAYMENT".equals(order.getStatus())) {
            order.setStatus("PENDING_SHIPMENT");
            order.setPayTransactionNo(transactionNo);
            order.setPayMethod(payMethod);
            order.setPayTime(java.time.LocalDateTime.now());
            orderMapper.updateById(order);
        }
    }
}
