package com.mallforge.common.utils;

public class TenantContext {
    private static final ThreadLocal<Long> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<Long> USER_ID   = new ThreadLocal<>();

    public static void setTenantId(Long id) { TENANT_ID.set(id); }
    public static Long getTenantId() { return TENANT_ID.get(); }
    public static void setUserId(Long id) { USER_ID.set(id); }
    public static Long getUserId() { return USER_ID.get(); }
    public static boolean isSuperAdmin() { return TENANT_ID.get() == null; }

    public static void clear() { TENANT_ID.remove(); USER_ID.remove(); }
}
