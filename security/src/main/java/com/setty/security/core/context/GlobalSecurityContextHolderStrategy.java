package com.setty.security.core.context;

import org.springframework.util.Assert;

/**
 * 全局策略
 *
 * @author HuSen
 * create on 2019/7/12 14:24
 */
final class GlobalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {

    private static SecurityContext contextHolder;

    @Override
    public void clearContext() {
        contextHolder = null;
    }

    @Override
    public SecurityContext getContext() {
        if (contextHolder == null) {
            contextHolder = new SecurityContextImpl();
        }
        return contextHolder;
    }

    @Override
    public void setContext(SecurityContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        contextHolder = context;
    }

    @Override
    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }
}
