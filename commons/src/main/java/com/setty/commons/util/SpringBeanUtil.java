package com.setty.commons.util;

import org.springframework.context.ApplicationContext;

/**
 * @author HuSen
 * create on 2019/7/4 10:53
 */
public class SpringBeanUtil {
    private static ApplicationContext context;

    public static <T> T getBean(Class<T> tClass) {
        return context.getBean(tClass);
    }

    public static void setContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }
}
