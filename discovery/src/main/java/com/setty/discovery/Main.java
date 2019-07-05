package com.setty.discovery;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

/**
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
