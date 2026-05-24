package com.mallforge.framework.mybatis;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.mallforge.common.utils.TenantContext;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import java.util.Set;

public class MallTenantLineHandler implements TenantLineHandler {

    private static final Set<String> BYPASS_TABLES = Set.of("tenant", "permission");

    @Override
    public Expression getTenantId() {
        Long id = TenantContext.getTenantId();
        return id == null ? new NullValue() : new LongValue(id);
    }

    @Override
    public boolean ignoreTable(String tableName) {
        return TenantContext.isSuperAdmin() || BYPASS_TABLES.contains(tableName.toLowerCase());
    }

    @Override
    public String getTenantIdColumn() { return "tenant_id"; }
}
