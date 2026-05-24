package com.mallforge.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mallforge.common.result.Result;
import com.mallforge.product.entity.Product;
import com.mallforge.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long categoryId) {
        Page<Product> p = productService.page(page, size, keyword, status, categoryId);
        return Result.page(p.getRecords(), p.getCurrent(), p.getSize(), p.getTotal());
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.ok(productService.getById(id));
    }

    @PostMapping
    public Result<Product> create(@RequestBody Product product) {
        return Result.ok(productService.create(product, null));
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Product product) {
        productService.update(id, product, null);
        return Result.ok();
    }

    @PatchMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.ok();
    }

    @GetMapping("/{id}/skus")
    public Result<?> skus(@PathVariable Long id) {
        return Result.ok(productService.getSkus(id));
    }
}
