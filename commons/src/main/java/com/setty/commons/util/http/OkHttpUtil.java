package com.setty.commons.util.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/5 13:40
 */
@Slf4j
public class OkHttpUtil {

    private static final OkHttpClient CLIENT = new OkHttpClient();

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
        // 注意资源的释放
        try(Response response = call.execute()) {
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
