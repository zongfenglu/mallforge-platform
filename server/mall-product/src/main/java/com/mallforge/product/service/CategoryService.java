package com.mallforge.product.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mallforge.common.exception.BusinessException;
import com.mallforge.common.exception.ErrorCode;
import com.mallforge.product.entity.Category;
import com.mallforge.product.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public List<Category> list(Long parentId, Integer status) {
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<Category>()
                .eq(parentId != null, Category::getParentId, parentId)
                .eq(status != null, Category::getStatus, status)
                .orderByAsc(Category::getSort);
        return categoryMapper.selectList(qw);
    }

    public Category getById(Long id) {
        Category c = categoryMapper.selectById(id);
        if (c == null) throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        return c;
    }

    public Category create(Category category) {
        categoryMapper.insert(category);
        return category;
    }

    public void update(Long id, Category category) {
        category.setId(id);
        categoryMapper.updateById(category);
    }

    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
