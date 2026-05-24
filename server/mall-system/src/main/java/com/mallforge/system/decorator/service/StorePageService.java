package com.mallforge.system.decorator.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mallforge.common.exception.BusinessException;
import com.mallforge.common.exception.ErrorCode;
import com.mallforge.common.utils.TenantContext;
import com.mallforge.system.decorator.entity.StorePage;
import com.mallforge.system.decorator.mapper.StorePageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorePageService {

    private final StorePageMapper pageMapper;

    public List<StorePage> list() {
        return pageMapper.selectList(new LambdaQueryWrapper<StorePage>()
                .orderByDesc(StorePage::getUpdatedAt));
        // pageJson 字段较大，列表不返回（由详情接口返回）
    }

    public StorePage getById(Long id) {
        StorePage page = pageMapper.selectById(id);
        if (page == null) throw new BusinessException(ErrorCode.DATA_NOT_FOUND);
        return page;
    }

    public StorePage create(StorePage page) {
        page.setTenantId(TenantContext.getTenantId());
        page.setStatus("draft");
        page.setVersion(1);
        page.setIsDefault(false);
        pageMapper.insert(page);
        return page;
    }

    public void save(Long id, StorePage page) {
        page.setId(id);
        pageMapper.updateById(page);
    }

    @Transactional
    public void publish(Long id) {
        StorePage page = getById(id);
        page.setStatus("published");
        page.setVersion(page.getVersion() + 1);
        pageMapper.updateById(page);
    }

    /**
     * 小程序端获取已发布的默认首页（无租户鉴权，由租户码查询）
     */
    public StorePage getPublished(Long tenantId) {
        StorePage page = pageMapper.selectOne(new LambdaQueryWrapper<StorePage>()
                .eq(StorePage::getTenantId, tenantId)
                .eq(StorePage::getStatus, "published")
                .eq(StorePage::getIsDefault, true)
                .orderByDesc(StorePage::getVersion)
                .last("LIMIT 1"));
        if (page == null) {
            // 返回兜底的默认页面 JSON
            page = new StorePage();
            page.setPageJson("{\"version\":1,\"name\":\"默认首页\",\"background\":\"#F5F5F5\",\"components\":[]}");
        }
        return page;
    }
}
