package com.mallforge.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mallforge.common.result.Result;
import com.mallforge.order.dto.CreateOrderRequest;
import com.mallforge.order.entity.Order;
import com.mallforge.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status) {
        Page<Order> p = orderService.page(page, size, status);
        return Result.page(p.getRecords(), p.getCurrent(), p.getSize(), p.getTotal());
    }

    @GetMapping("/{id}")
    public Result<?> detail(@PathVariable Long id) {
        Order order = orderService.getById(id);
        return Result.ok(Map.of("order", order, "items", orderService.getItems(id)));
    }

    @PostMapping
    public Result<Order> create(@RequestBody CreateOrderRequest req) {
        return Result.ok(orderService.createOrder(req));
    }

    @PatchMapping("/{id}/ship")
    public Result<?> ship(@PathVariable Long id,
                          @RequestParam String logisticsCompany,
                          @RequestParam String logisticsNo) {
        orderService.ship(id, logisticsCompany, logisticsNo);
        return Result.ok();
    }

    @PatchMapping("/{id}/cancel")
    public Result<?> cancel(@PathVariable Long id) {
        orderService.cancel(id);
        return Result.ok();
    }
}
