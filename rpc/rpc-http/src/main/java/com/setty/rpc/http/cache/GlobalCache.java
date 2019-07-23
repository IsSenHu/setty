package com.setty.rpc.http.cache;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/23 15:03
 */
public final class GlobalCache {

    /**
     * Method 缓存
     * 这个只会在服务启动的时候进行写入 所以 可以使用HashMap
     */
    private static final Map<String, Method> METHOD_MAP = new HashMap<>(1000);

    /**
     * Controller 缓存
     */
    private static final Map<String, Object> CONTROLLER_MAP = new HashMap<>(100);

    public static Method getMethod(String methodMark) {
        return METHOD_MAP.get(methodMark);
    }

    public static void addMethod(String methodMark, Method method) {
        METHOD_MAP.put(methodMark, method);
    }

    public static Object getController(String name) {
        return CONTROLLER_MAP.get(name);
    }

    public static void addController(String name, Object o) {
        CONTROLLER_MAP.put(name, o);
    }
}
