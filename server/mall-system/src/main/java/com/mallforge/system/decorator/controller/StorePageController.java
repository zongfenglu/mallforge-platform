package com.mallforge.system.decorator.controller;

import com.mallforge.common.result.Result;
import com.mallforge.system.decorator.entity.StorePage;
import com.mallforge.system.decorator.service.StorePageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pages")
@RequiredArgsConstructor
public class StorePageController {

    private final StorePageService pageService;

    @GetMapping
    public Result<List<StorePage>> list() {
        return Result.ok(pageService.list());
    }

    @GetMapping("/{id}")
    public Result<StorePage> detail(@PathVariable Long id) {
        return Result.ok(pageService.getById(id));
    }

    @PostMapping
    public Result<StorePage> create(@RequestBody StorePage page) {
        return Result.ok(pageService.create(page));
    }

    @PutMapping("/{id}")
    public Result<?> save(@PathVariable Long id, @RequestBody StorePage page) {
        pageService.save(id, page);
        return Result.ok();
    }

    @PostMapping("/{id}/publish")
    public Result<?> publish(@PathVariable Long id) {
        pageService.publish(id);
        return Result.ok();
    }

    /** 小程序端：获取已发布页面（无鉴权，tenantId 由小程序传入） */
    @GetMapping("/published")
    public Result<StorePage> published(@RequestParam Long tenantId) {
        return Result.ok(pageService.getPublished(tenantId));
    }
}
