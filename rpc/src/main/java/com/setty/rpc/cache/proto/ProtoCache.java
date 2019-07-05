package com.setty.rpc.cache.proto;

import com.setty.rpc.annotation.proto.Client;
import com.setty.rpc.annotation.proto.ProtoClient;
import com.setty.rpc.callback.proto.ProtoCallback;
import io.netty.channel.Channel;
import io.netty.channel.pool.FixedChannelPool;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/3 17:01
 */
public class ProtoCache {

    /**
     * 方法缓存
     */
    private static final Map<Long, Method> METHOD_CACHE = new HashMap<>();

    public static Method getMethod(Long id) {
        return METHOD_CACHE.get(id);
    }

    public static void addMethod(Long id, Method method) {
        METHOD_CACHE.put(id, method);
    }

    public static boolean methodExist(Long id) {
        return METHOD_CACHE.containsKey(id);
    }

    /**
     * Controller 缓存
     */
    private static final Map<Long, Object> CONTROLLER_CACHE = new HashMap<>();

    public static Object getController(Long id) {
        return CONTROLLER_CACHE.get(id);
    }

    public static void addController(Long id, Object o) {
        CONTROLLER_CACHE.put(id, o);
    }

    public static boolean controllerExist(Long id) {
        return CONTROLLER_CACHE.containsKey(id);
    }

    /**
     * Callback 缓存
     */
    private static final Map<String, ProtoCallback> CALLBACK_CACHE = new HashMap<>();

    public static ProtoCallback getCallback(String key) {
        return CALLBACK_CACHE.get(key);
    }

    public static void addCallback(String key, ProtoCallback callback) {
        CALLBACK_CACHE.put(key, callback);
    }

    /**
     * ProtoClient 缓存
     */
    private static final Map<Class<?>, ProtoClient> PROTO_CLIENT_CACHE = new HashMap<>();

    public static ProtoClient getProtoClient(Class<?> c) {
        return PROTO_CLIENT_CACHE.get(c);
    }

    public static void addProtoClient(Class<?> c, ProtoClient client) {
        PROTO_CLIENT_CACHE.putIfAbsent(c, client);
    }

    /**
     * ClientP 缓存
     */
    private static final Map<Method, Client> CLIENT_CACHE = new HashMap<>();

    public static Client getClient(Method method) {
        return CLIENT_CACHE.get(method);
    }

    public static void addClient(Method method, Client client) {
        CLIENT_CACHE.putIfAbsent(method, client);
    }

    /**
     * appId 和 Pool 对应关系
     */
    private static final Map<Long, List<FixedChannelPool>> APP_ID_POOL_CACHE = new HashMap<>();

    public static void addPool(Long appId, FixedChannelPool pool) {
        addItem(APP_ID_POOL_CACHE, pool, appId);
    }

    public static List<FixedChannelPool> getPools(Long appId) {
        return APP_ID_POOL_CACHE.get(appId);
    }

    /**
     * appId 和 Channel 对应关系
     */
    private static final Map<Long, List<Channel>> APP_ID_CAHNNEL_CACHE = new HashMap<>();

    public static void addChannel(Long appId, Channel channel) {
        addItem(APP_ID_CAHNNEL_CACHE, channel, appId);
    }

    public static List<Channel> getChannels(Long appId) {
        return APP_ID_CAHNNEL_CACHE.get(appId);
    }

    private static <T, S> void addItem(Map<T, List<S>> map, S item, T key) {
        // 若key对应的value为空，会将第二个参数的返回值存入并返回
        List<S> list = map.computeIfAbsent(key, k -> new ArrayList<>());
        list.add(item);
    }
}
