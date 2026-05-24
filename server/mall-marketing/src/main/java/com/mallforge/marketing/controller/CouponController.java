package com.mallforge.marketing.controller;

import com.mallforge.common.result.Result;
import com.mallforge.marketing.entity.Coupon;
import com.mallforge.marketing.entity.UserCoupon;
import com.mallforge.marketing.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /** 管理后台：优惠券模板列表 */
    @GetMapping
    public Result<List<Coupon>> list() {
        return Result.ok(couponService.listActiveCoupons());
    }

    /** 小程序：领取优惠券 */
    @PostMapping("/{id}/claim")
    public Result<UserCoupon> claim(@PathVariable Long id) {
        return Result.ok(couponService.claim(id));
    }

    /** 小程序：我的可用优惠券 */
    @GetMapping("/my")
    public Result<List<UserCoupon>> myCoupons() {
        return Result.ok(couponService.myUsableCoupons());
    }
}
