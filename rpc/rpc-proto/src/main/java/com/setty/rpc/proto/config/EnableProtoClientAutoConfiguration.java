package com.setty.rpc.proto.config;

import com.setty.commons.vo.registry.AppVO;
import com.setty.discovery.core.infs.LookupService;
import com.setty.discovery.vo.AppInstance;
import com.setty.rpc.core.properties.ClientProperties;
import com.setty.rpc.core.select.ServiceSelector;
import com.setty.rpc.core.select.impl.RoundRobinSelector;
import com.setty.rpc.proto.annotation.EnableProtoClient;
import com.setty.rpc.proto.cache.ProtoCache;
import com.setty.rpc.proto.client.ProtoClientServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author HuSen
 * create on 2019/7/4 17:03
 */
@Slf4j
@Configuration
@ConditionalOnBean(annotation = EnableProtoClient.class)
@EnableConfigurationProperties(ClientProperties.class)
public class EnableProtoClientAutoConfiguration {

    private AtomicBoolean init = new AtomicBoolean(false);

    private final ClientProperties cp;

    private final LookupService<AppVO, Long> lookupService;

    @Autowired
    public EnableProtoClientAutoConfiguration(ClientProperties cp, @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") LookupService<AppVO, Long> lookupService) {
        this.cp = cp;
        this.lookupService = lookupService;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void init() {
        if (!init.getAndSet(true)) {
            doInit();
        }
    }

    @Bean
    public ServiceSelector serviceSelector() {
        return new RoundRobinSelector();
    }

    private void doInit() {
        // 拉取可用的服务列表
        List<AppInstance> instances = lookupService.getApplications();
        // 根据appId分组
        Map<Long, List<AppVO>> appGroups = new HashMap<>(instances.size());
        for (AppInstance instance : instances) {
            List<AppVO> apps = appGroups.computeIfAbsent(instance.getAppId(), k -> new ArrayList<>());
            apps.add(instance.getAppVO());
        }
        // 轮询选择
        ServiceSelector selector = serviceSelector();
        // 生成 rpc client
        for (Map.Entry<Long, List<AppVO>> entry : appGroups.entrySet()) {
            List<AppVO> apps = entry.getValue();
            for (AppVO app : apps) {
                ProtoClientServer client = new ProtoClientServer(app.getHost(), app.getPort(), app.getInstanceName(), app.getAppId(), cp.getConnectionTimeout());
                client.start();
                ProtoCache.setProtoClientServer(app.getInstanceName(), client);
                selector.join(app, null);
            }
        }
        selector.buildFinish();
    }
}
