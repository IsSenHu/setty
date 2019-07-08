package com.setty.discovery.config;

import com.setty.discovery.properties.DiscoveryProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author HuSen
 * create on 2019/7/5 18:33
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(DiscoveryProperties.class)
public class EnableDiscoveryConfiguration {

    private final DiscoveryProperties dp;

    private AtomicBoolean init = new AtomicBoolean(false);

    @Autowired
    public EnableDiscoveryConfiguration(DiscoveryProperties dp) {
        this.dp = dp;
    }

    public static final Queue<Runnable> REGISTER_QUEUE = new LinkedBlockingQueue<>(1000);

    public static final Queue<Runnable> RENEWAL_QUEUE = new LinkedBlockingQueue<>(1000);

    private static final ScheduledExecutorService SCHEDULED = new ScheduledThreadPoolExecutor(5, r -> new Thread(r, "注册中心线程"), new ScheduledThreadPoolExecutor.CallerRunsPolicy());

    @EventListener(ApplicationStartedEvent.class)
    public void init() {
        if (!init.getAndSet(true)) {
            // 结束前一个执行后延迟的时间
            SCHEDULED.scheduleWithFixedDelay(() -> {

            }, 10, 30, TimeUnit.SECONDS);
        }
    }
}
