package com.setty.rpc.http.cache;

import com.setty.rpc.http.callback.HttpCallback;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author HuSen
 * create on 2019/7/24 16:20
 */
public class CallbackCache {

    private static final ConcurrentMap<String, HttpCallback> CALLBACK_MAP = new ConcurrentHashMap<>();

    public static void add(String id, HttpCallback callback) {
        CALLBACK_MAP.put(id, callback);
    }

    public static HttpCallback getAndRemove(String id) {
        return CALLBACK_MAP.remove(id);
    }
}
