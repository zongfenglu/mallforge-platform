package com.mallforge.marketing.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mallforge.common.exception.BusinessException;
import com.mallforge.common.exception.ErrorCode;
import com.mallforge.common.utils.TenantContext;
import com.mallforge.marketing.entity.Coupon;
import com.mallforge.marketing.entity.UserCoupon;
import com.mallforge.marketing.mapper.CouponMapper;
import com.mallforge.marketing.mapper.UserCouponMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    private final StringRedisTemplate redis;

    public List<Coupon> listActiveCoupons() {
        return couponMapper.selectList(new LambdaQueryWrapper<Coupon>()
                .eq(Coupon::getStatus, "active")
                .le(Coupon::getStartTime, LocalDateTime.now())
                .ge(Coupon::getEndTime, LocalDateTime.now()));
    }

    /**
     * 领取优惠券（分布式锁防并发超领）
     */
    @Transactional
    public UserCoupon claim(Long couponId) {
        Long userId = TenantContext.getUserId();
        String lockKey = "coupon:claim:" + couponId;

        // 尝试获取分布式锁（超时 5s）
        Boolean locked = redis.opsForValue().setIfAbsent(lockKey, "1", 5, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(locked)) {
            throw new BusinessException(40006, "领取请求过于频繁，请稍后重试");
        }
        try {
            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon == null) throw new BusinessException(ErrorCode.COUPON_NOT_FOUND);
            if (!"active".equals(coupon.getStatus())) throw new BusinessException(ErrorCode.COUPON_EXPIRED);
            if (LocalDateTime.now().isAfter(coupon.getEndTime())) throw new BusinessException(ErrorCode.COUPON_EXPIRED);

            // 检查剩余数量
            long claimed = userCouponMapper.selectCount(
                    new LambdaQueryWrapper<UserCoupon>().eq(UserCoupon::getCouponId, couponId));
            if (claimed >= coupon.getTotalQty()) throw new BusinessException(ErrorCode.COUPON_STOCK_EMPTY);

            // 检查用户已领数量
            long userClaimed = userCouponMapper.selectCount(
                    new LambdaQueryWrapper<UserCoupon>()
                            .eq(UserCoupon::getCouponId, couponId)
                            .eq(UserCoupon::getUserId, userId));
            if (userClaimed >= coupon.getUserLimit()) {
                throw new BusinessException(40007, "已超过领取上限");
            }

            // 创建用户优惠券
            UserCoupon uc = new UserCoupon();
            uc.setUserId(userId);
            uc.setCouponId(couponId);
            uc.setStatus("unused");
            uc.setExpireAt(LocalDateTime.now().plus(coupon.getValidDays(), ChronoUnit.DAYS));
            userCouponMapper.insert(uc);
            return uc;
        } finally {
            redis.delete(lockKey);
        }
    }

    public List<UserCoupon> myUsableCoupons() {
        return userCouponMapper.selectList(new LambdaQueryWrapper<UserCoupon>()
                .eq(UserCoupon::getUserId, TenantContext.getUserId())
                .eq(UserCoupon::getStatus, "unused")
                .ge(UserCoupon::getExpireAt, LocalDateTime.now()));
    }
}
