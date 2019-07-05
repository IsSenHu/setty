package com.setty.rpc.config.proto;

import com.setty.rpc.annotation.proto.EnableProtoClient;
import com.setty.rpc.pool.map.ProtoChannelPoolMap;
import com.setty.rpc.properties.proto.ClientProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

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

    @Autowired
    public EnableProtoClientAutoConfiguration(ClientProperties cp) {
        this.cp = cp;
    }

    @Bean
    public ProtoChannelPoolMap protoChannelPoolMap() {
        ProtoChannelPoolMap poolMap = new ProtoChannelPoolMap(cp.getClients());
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

    }
}
