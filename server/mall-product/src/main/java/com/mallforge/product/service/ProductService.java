package com.mallforge.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mallforge.common.exception.BusinessException;
import com.mallforge.common.exception.ErrorCode;
import com.mallforge.product.entity.Product;
import com.mallforge.product.entity.Sku;
import com.mallforge.product.mapper.ProductMapper;
import com.mallforge.product.mapper.SkuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;
    private final SkuMapper skuMapper;

    public Page<Product> page(int pageNum, int pageSize, String keyword, Integer status, Long categoryId) {
        LambdaQueryWrapper<Product> qw = new LambdaQueryWrapper<Product>()
                .like(keyword != null, Product::getName, keyword)
                .eq(status != null, Product::getStatus, status)
                .eq(categoryId != null, Product::getCategoryId, categoryId)
                .orderByDesc(Product::getCreatedAt);
        return productMapper.selectPage(new Page<>(pageNum, pageSize), qw);
    }

    public Product getById(Long id) {
        Product p = productMapper.selectById(id);
        if (p == null) throw new BusinessException(ErrorCode.PRODUCT_NOT_FOUND);
        return p;
    }

    @Transactional
    public Product create(Product product, List<Sku> skus) {
        productMapper.insert(product);
        skus.forEach(sku -> {
            sku.setProductId(product.getId());
            skuMapper.insert(sku);
        });
        return product;
    }

    @Transactional
    public void update(Long id, Product product, List<Sku> skus) {
        product.setId(id);
        productMapper.updateById(product);
        if (skus != null && !skus.isEmpty()) {
            skuMapper.delete(new LambdaQueryWrapper<Sku>().eq(Sku::getProductId, id));
            skus.forEach(sku -> { sku.setProductId(id); skuMapper.insert(sku); });
        }
    }

    public void updateStatus(Long id, Integer status) {
        productMapper.update(null, new LambdaUpdateWrapper<Product>()
                .eq(Product::getId, id).set(Product::getStatus, status));
    }

    public List<Sku> getSkus(Long productId) {
        return skuMapper.selectList(
                new LambdaQueryWrapper<Sku>().eq(Sku::getProductId, productId));
    }

    /**
     * 扣减库存（乐观锁 CAS，避免超卖）
     */
    @Transactional
    public void deductStock(Long skuId, int quantity) {
        int rows = skuMapper.deductStock(skuId, quantity);
        if (rows == 0) throw new BusinessException(ErrorCode.SKU_STOCK_INSUFFICIENT);
    }
}
