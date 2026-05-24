package com.mallforge.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mallforge.product.entity.Sku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SkuMapper extends BaseMapper<Sku> {

    /**
     * CAS 扣减库存，防止超卖
     * WHERE id = #{skuId} AND stock >= #{quantity}
     */
    @Update("UPDATE sku SET stock = stock - #{quantity} " +
            "WHERE id = #{skuId} AND stock >= #{quantity}")
    int deductStock(Long skuId, int quantity);
}
