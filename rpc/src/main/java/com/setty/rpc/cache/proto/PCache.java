package com.setty.rpc.cache.proto;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/3 17:01
 */
public class PCache {

    /**
     * 方法缓存
     */
    private static final Map<Long, Method> METHOD_CACHE = new HashMap<>();

    /**
     * Controller 缓存
     */
    private static final Map<Long, Object> CONTROLLER_CACHE = new HashMap<>();

    public static Method getMethod(Long id) {
        return METHOD_CACHE.get(id);
    }

    public static Object getController(Long id) {
        return CONTROLLER_CACHE.get(id);
    }

    public static void addMethod(Long id, Method method) {
        METHOD_CACHE.put(id, method);
    }

    public static void addController(Long id, Object o) {
        CONTROLLER_CACHE.put(id, o);
    }

    public static boolean methodExist(Long id) {
        return METHOD_CACHE.containsKey(id);
    }

    public static boolean controllerExist(Long id) {
        return CONTROLLER_CACHE.containsKey(id);
    }
}
