package com.setty.discovery;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
 * 1支持 preferSameZoneEureka，即有多个分区的话，优先选择与应用实例所在分区一样的其他服务的实例(AvailabilityZone)，如果没有找到则默认使用 defaultZone
 *
 * 2客户端使用 quarantineSet 维护了一个不可用的 Eureka Server 列表，进行请求的时候，优先从可用的列表中进行选择，如果请求失败则切换到下一个 Eureka Server 进行重试，重试次数默认为3
 *
 * 3为了防止每个Client都按配置文件指定的顺序进行请求造成 Eureka Server 节点请求分布不均衡的情况，Client端有个定时任务 (默认5分钟执行一次)来刷新并随机化 Eureka Server 的列表
 *
 * @author HuSen
 * create on 2019/7/5 13:25
 */
@Slf4j
public class Main {

    public static void main(String[] args) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url("")
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    log.info("{}", Objects.requireNonNull(response.body()).string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
