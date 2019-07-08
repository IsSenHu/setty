package com.setty.discovery.config;

import com.setty.commons.vo.registry.AppVO;
import com.setty.discovery.core.DefaultLeaseManager;
import com.setty.discovery.core.infs.LeaseManager;
import com.setty.discovery.properties.DiscoveryProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.*;
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

    public static final Queue<Runnable> RUN_QUEUE = new LinkedBlockingQueue<>(1000);

    private static final ScheduledExecutorService SCHEDULED = new ScheduledThreadPoolExecutor(5, r -> new Thread(r, "注册中心线程"), new ScheduledThreadPoolExecutor.CallerRunsPolicy());

    @EventListener(ApplicationStartedEvent.class)
    public void init() {
        if (!init.getAndSet(true)) {
            // 将自己信息注册到注册中心
            AppVO vo = new AppVO();
            vo.setAppId(dp.getAppId());
            vo.setHost(dp.getHost());
            vo.setPort(dp.getPort());
            vo.setInstanceName(dp.getInstanceName());
            leaseManager().register(vo, dp.getLeaseDuration(), false);

            // 结束前一个执行后延迟的时间
            SCHEDULED.scheduleWithFixedDelay(() -> {
                int size = RUN_QUEUE.size();
                for (int i = 0; i < size; i++) {
                    Objects.requireNonNull(RUN_QUEUE.poll()).run();
                }
            }, 10, dp.getRenewalIntervalInSecs(), TimeUnit.SECONDS);
        }
    }

    @Bean
    public LeaseManager<AppVO, Long, String> leaseManager() {
        return new DefaultLeaseManager(dp);
    }
}
