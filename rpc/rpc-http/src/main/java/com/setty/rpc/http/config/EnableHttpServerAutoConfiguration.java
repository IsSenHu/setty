package com.setty.rpc.http.config;

import com.setty.rpc.core.cons.Sign;
import com.setty.rpc.core.properties.ServerProperties;
import com.setty.rpc.http.annotation.EnableHttpServer;
import com.setty.rpc.http.annotation.HttpConsumer;
import com.setty.rpc.http.annotation.HttpController;
import com.setty.rpc.http.cache.GlobalCache;
import com.setty.rpc.http.server.HttpServer;
import com.setty.rpc.http.task.Publisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/23 15:32
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ServerProperties.class)
@ConditionalOnBean(annotation = EnableHttpServer.class)
public class EnableHttpServerAutoConfiguration {

    private final ServerProperties serverProperties;

    private boolean init = false;

    private HttpServer httpServer;

    @Autowired
    public EnableHttpServerAutoConfiguration(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void init(ApplicationStartedEvent event) {
        if (!init) {
            doInit(event);
            init = true;
        }
    }

    @Bean
    public Publisher publisher() {
        return new Publisher();
    }

    private void doInit(ApplicationStartedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        String instanceName = serverProperties.getInstanceName();
        Assert.hasLength(instanceName, "instanceName is not allow empty");

        // 1.加载所有的Controller和Method
        Map<String, Object> controllerMap = context.getBeansWithAnnotation(HttpController.class);
        log.info("发现 Controller:{}", controllerMap.keySet());

        for (Object controller : controllerMap.values()) {
            HttpController httpController = AnnotationUtils.findAnnotation(controller.getClass(), HttpController.class);
            Assert.notNull(httpController, "@HttpController is need for " + controller.getClass().getName());
            String name = httpController.value();
            GlobalCache.addController(name, controller);

            Method[] methods = controller.getClass().getDeclaredMethods();
            Method.setAccessible(methods, true);

            for (Method method : methods) {
                HttpConsumer httpConsumer = AnnotationUtils.findAnnotation(method, HttpConsumer.class);
                if (httpConsumer == null) {
                    continue;
                }
                String m = httpConsumer.value();
                String methodName = instanceName.concat(Sign.POINT).concat(name).concat(Sign.POINT).concat(m);
                log.info("发现 method:{}", methodName);
                GlobalCache.addMethod(methodName, method);
            }
        }

        // 2.启动HttpServer
        httpServer = new HttpServer(serverProperties.getPort(), serverProperties.getInstanceName(), publisher());
        httpServer.setBossThreads(serverProperties.getBossThreads());
        httpServer.setWorkerThreads(serverProperties.getWorkerThreads());
        httpServer.setUseEpoll(serverProperties.isUseEpoll());
        httpServer.setWriteTimeout(serverProperties.getWriteTimeout());
        httpServer.start();
    }

    @PreDestroy
    public void destroy() {
        httpServer.stop(() -> log.warn("Http server is closed."));
    }
}
