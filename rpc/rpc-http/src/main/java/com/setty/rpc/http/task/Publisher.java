package com.setty.rpc.http.task;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;

/**
 * @author HuSen
 * create on 2019/7/25 14:34
 */
public class Publisher {

    private static final ThreadPoolTaskExecutor TASK_EXECUTOR = new ThreadPoolTaskExecutor();

    @PostConstruct
    public void init() {
        TASK_EXECUTOR.setCorePoolSize(200);
        TASK_EXECUTOR.setMaxPoolSize(1000);
        TASK_EXECUTOR.setQueueCapacity(20000);
        TASK_EXECUTOR.setAllowCoreThreadTimeOut(false);
        TASK_EXECUTOR.setKeepAliveSeconds(30);
        TASK_EXECUTOR.initialize();
    }

    public void on(AbstractConsumer consumer) {
        TASK_EXECUTOR.submit(consumer);
    }
}
