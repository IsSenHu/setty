package com.setty.commons.util.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/5 13:40
 */
@Slf4j
public class OkHttpUtil {

    private static final OkHttpClient CLIENT = new OkHttpClient();

    private static final String JSON = "application/json;charset=utf-8";

    /**
     * 异步Get请求
     *
     * @param url      请求地址
     * @param params   请求参数
     * @param callback 异步回调
     */
    public static void getAsync(String url, Map<String, String> params, Callback callback) {
        url = handleUrlParams(url, params);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        CLIENT.newCall(request).enqueue(callback);
    }

    /**
     * 同步Get请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 响应body
     */
    public static String getSync(String url, Map<String, String> params) {
        url = handleUrlParams(url, params);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = CLIENT.newCall(request);
        return execAndResp(call);
    }

    /**
     * 同步Post请求
     *
     * @param url     请求地址
     * @param body    请求体
     * @param headers 请求头
     * @return 响应body
     */
    public static String postSync(String url, String body, Headers headers) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .post(RequestBody.create(body, MediaType.get(JSON)));
        if (null != headers) {
            builder.headers(headers);
        }
        Request request = builder.build();
        Call call = CLIENT.newCall(request);
        return execAndResp(call);
    }

    /**
     * 同步Delete请求
     *
     * @param url     请求地址
     * @param headers 请求头
     * @return 响应body
     */
    public static String deleteSync(String url, Headers headers) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .delete();
        if (null != headers) {
            builder.headers(headers);
        }
        Request request = builder.build();
        Call call = CLIENT.newCall(request);
        return execAndResp(call);
    }

    /**
     * 同步Put请求
     *
     * @param url     请求地址
     * @param headers 请求头
     * @return 响应body
     */
    public static String putSync(String url, Headers headers) {
        Request.Builder builder = new Request.Builder()
                .url(url)
                .put(RequestBody.create(StringUtils.EMPTY.getBytes()));
        if (null != headers) {
            builder.headers(headers);
        }
        Request request = builder.build();
        Call call = CLIENT.newCall(request);
        return execAndResp(call);
    }

    private static String execAndResp(Call call) {
        // 注意资源的释放
        try (Response response = call.execute()) {
            ResponseBody body = response.body();
            if (body == null) {
                return null;
            }
            return body.string();
        } catch (Exception e) {
            log.error("请求异常:", e);
        }
        return null;
    }

    private static String handleUrlParams(String url, Map<String, String> params) {
        if (MapUtils.isNotEmpty(params)) {
            StringBuilder sb = new StringBuilder(url).append("?");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            url = sb.substring(0, sb.length() - 1);
        }
        return url;
    }
}
