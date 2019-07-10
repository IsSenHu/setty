package com.setty.rpc.config.proto;

import com.setty.commons.vo.registry.AppVO;
import com.setty.discovery.core.infs.LookupService;
import com.setty.rpc.annotation.proto.EnableProtoClient;
import com.setty.rpc.client.ProtoClientServer;
import com.setty.rpc.pool.map.ProtoChannelPoolMap;
import com.setty.rpc.properties.proto.ClientP;
import com.setty.rpc.properties.proto.ClientProperties;
import com.setty.rpc.select.impl.RoundRobinSelector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author HuSen
 * create on 2019/7/4 17:03
 */
@Slf4j
@ConditionalOnBean(annotation = EnableProtoClient.class)
@EnableConfigurationProperties(ClientProperties.class)
public class EnableProtoClientAutoConfiguration {

    private AtomicBoolean init = new AtomicBoolean(false);

    private final ClientProperties cp;

    private final LookupService<AppVO, Long> lookupService;

    @Autowired
    public EnableProtoClientAutoConfiguration(ClientProperties cp, LookupService<AppVO, Long> lookupService) {
        this.cp = cp;
        this.lookupService = lookupService;
    }

    @Bean
    public ProtoChannelPoolMap protoChannelPoolMap() {
        // 初始化时 拉取所有的服务
        List<Map<Long, AppVO>> applications = lookupService.getApplications();
        // 根据目前有的服务 生成Client
        applications.stream().map(app -> app.values().iterator()).forEach(iterator -> {
            if (iterator.hasNext()) {
                AppVO next = iterator.next();
                ProtoClientServer client = new ProtoClientServer(next.getHost(), next.getPort(), next.getInstanceName(), next.getAppId(), cp.getConnectionTimeout());

            }
        });

        Map<String, ClientP> clients = new HashMap<>(applications.size());
        // 轮询选择
        RoundRobinSelector selector = new RoundRobinSelector();
        applications.forEach(app -> {
            AppVO vo = app.values().iterator().next();
            ClientP p = new ClientP();
            p.setAppId(vo.getAppId());
            p.setHost(vo.getHost());
            p.setPort(vo.getPort());
            p.setConnectionTimeout(cp.getConnectionTimeout());
            clients.put(vo.getInstanceName(), p);
            selector.join(vo);
        });
        selector.buildFinish();
        ProtoChannelPoolMap poolMap = new ProtoChannelPoolMap(clients);
        poolMap.start();
        return poolMap;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void init() {
        if (!init.getAndSet(true)) {
            doInit();
        }
    }

    private void doInit() {
        // 拉取可用的服务列表

    }
}
