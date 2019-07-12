package com.setty.security.core.context;

import org.springframework.util.Assert;

/**
 * 当前线程及其子线程策略
 *
 * @author HuSen
 * create on 2019/7/12 14:22
 */
final class InheritableThreadLocalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {

    private static final ThreadLocal<SecurityContext> CONTEXT_HOLDER = new InheritableThreadLocal<>();

    @Override
    public void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    @Override
    public SecurityContext getContext() {
        SecurityContext ctx = CONTEXT_HOLDER.get();
        if (ctx == null) {
            ctx = createEmptyContext();
            CONTEXT_HOLDER.set(ctx);
        }
        return ctx;
    }

    @Override
    public void setContext(SecurityContext context) {
        Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
        CONTEXT_HOLDER.set(context);
    }

    @Override
    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }
}
