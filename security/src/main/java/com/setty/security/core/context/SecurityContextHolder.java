package com.setty.security.core.context;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;

/**
 * 安全上下文持有者 主要负责上下文的初始化和操作
 *
 * @author HuSen
 * create on 2019/7/12 14:07
 */
public class SecurityContextHolder {
    private static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
    private static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";
    private static final String MODE_GLOBAL = "MODE_GLOBAL";
    private static final String SYSTEM_PROPERTY = "spring.security.strategy";
    private static String strategyName = System.getProperty(SYSTEM_PROPERTY);
    private static SecurityContextHolderStrategy strategy;
    private static int initializeCount = 0;

    static {
        initialize();
    }

    /**
     * Explicitly clears the context value from the current thread.
     */
    public static void clearContext() {
        strategy.clearContext();
    }

    /**
     * Obtain the current <code>SecurityContext</code>.
     *
     * @return the security context (never <code>null</code>)
     */
    public static SecurityContext getContext() {
        return strategy.getContext();
    }

    private static void initialize() {
        // 默认使用ThreadLocal存储
        if (!StringUtils.hasText(strategyName)) {
            strategyName = MODE_THREADLOCAL;
        }

        switch (strategyName) {
            case MODE_THREADLOCAL:
                strategy = new ThreadLocalSecurityContextHolderStrategy();
                break;
            case MODE_INHERITABLETHREADLOCAL:
                strategy = new InheritableThreadLocalSecurityContextHolderStrategy();
                break;
            case MODE_GLOBAL:
                strategy = new GlobalSecurityContextHolderStrategy();
                break;
            default:
                // 尝试加载自定义的策略
                try {
                    Class<?> clazz = Class.forName(strategyName);
                    Constructor<?> customStrategy = clazz.getConstructor();
                    strategy = (SecurityContextHolderStrategy) customStrategy.newInstance();
                } catch (Exception ex) {
                    ReflectionUtils.handleReflectionException(ex);
                }
                break;
        }
    }

    /**
     * Primarily for troubleshooting purposes, this method shows how many times the class
     * has re-initialized its <code>SecurityContextHolderStrategy</code>.
     *
     * @return the count (should be one unless you've called
     * {@link #setStrategyName(String)} to switch to an alternate strategy.
     */
    public static int getInitializeCount() {
        return initializeCount;
    }

    /**
     * Associates a new <code>SecurityContext</code> with the current thread of execution.
     *
     * @param context the new <code>SecurityContext</code> (may not be <code>null</code>)
     */
    public static void setContext(SecurityContext context) {
        strategy.setContext(context);
    }

    /**
     * Changes the preferred strategy. Do <em>NOT</em> call this method more than once for
     * a given JVM, as it will re-initialize the strategy and adversely affect any
     * existing threads using the old strategy.
     *
     * @param strategyName the fully qualified class name of the strategy that should be
     *                     used.
     */
    @SuppressWarnings("WeakerAccess")
    public static void setStrategyName(String strategyName) {
        SecurityContextHolder.strategyName = strategyName;
        initialize();
    }

    /**
     * Allows retrieval of the context strategy. See SEC-1188.
     *
     * @return the configured strategy for storing the security context.
     */
    public static SecurityContextHolderStrategy getContextHolderStrategy() {
        return strategy;
    }

    /**
     * Delegates the creation of a new, empty context to the configured strategy.
     */
    public static SecurityContext createEmptyContext() {
        return strategy.createEmptyContext();
    }

    @Override
    public String toString() {
        return "SecurityContextHolder[strategy='" + strategyName + "'; initializeCount="
                + initializeCount + "]";
    }
}
