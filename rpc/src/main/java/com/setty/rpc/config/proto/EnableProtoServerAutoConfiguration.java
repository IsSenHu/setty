package com.setty.rpc.config.proto;

import com.setty.rpc.annotation.proto.EnableProtoServer;
import com.setty.rpc.annotation.proto.ProtoConsumer;
import com.setty.rpc.annotation.proto.ProtoController;
import com.setty.rpc.cache.proto.ProtoCache;
import com.setty.rpc.cons.proto.Id;
import com.setty.rpc.handler.proto.server.ProtoCodec;
import com.setty.rpc.handler.proto.server.ProtoDispatcherHandler;
import com.setty.rpc.properties.proto.ServerProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Proto 服务器自动化配置
 *
 * @author HuSen
 * create on 2019/7/3 15:22
 */
@ConditionalOnBean(annotation = EnableProtoServer.class)
@EnableConfigurationProperties(ServerProperties.class)
public class EnableProtoServerAutoConfiguration {

    private final ServerProperties sp;
    private final ApplicationContext context;

    private AtomicBoolean init = new AtomicBoolean(false);

    @Autowired
    public EnableProtoServerAutoConfiguration(ServerProperties sp, ApplicationContext context) {
        this.sp = sp;
        this.context = context;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void init() {
        if (!init.getAndSet(true)) {
            doInit();
        }
    }

    private void doInit() {
        // 初始化模块和方法
        initModuleAndMethod();
        // 启动服务器
        startServer();
    }

    /**
     * 初始化模块和方法
     */
    private void initModuleAndMethod() {
        long appId = sp.getAppId();
        Assert.isTrue(appId >= Id.MIN_APP_ID && appId <= Id.MAX_APP_ID, "appId 的取值不合法");

        Map<String, Object> controllerMap = context.getBeansWithAnnotation(ProtoController.class);
        controllerMap.values().forEach(c -> {
            ProtoController pc = AnnotationUtils.findAnnotation(c.getClass(), ProtoController.class);
            Assert.notNull(pc, c.getClass().getName() + " 没有注解 " + ProtoController.class.getName());

            // moduleId取值范围[10000, 99990000]
            int moduleId = pc.moduleId();
            Assert.isTrue(moduleId >= Id.MIN_MODULE_ID && moduleId <= Id.MAX_MODULE_ID, "moduleId 的取值范围不合法");

            Method[] methods = c.getClass().getDeclaredMethods();
            Method.setAccessible(methods, true);
            Arrays.stream(methods).forEach(method -> {
                ProtoConsumer consumer = AnnotationUtils.findAnnotation(method, ProtoConsumer.class);
                if (consumer != null) {
                    int methodId = consumer.value();
                    Assert.isTrue(methodId >= Id.MIN_CONSUMER_ID || methodId <= Id.MAX_CONSUMER_ID, "methodId 的取值范围不合法");
                    long id = appId + moduleId + methodId;
                    Assert.isTrue(!ProtoCache.controllerExist(id) && !ProtoCache.methodExist(id), "ID" + id + "重复了，请检查");
                    ProtoCache.addController(id, c);
                    ProtoCache.addMethod(id, method);
                }
            });
        });
    }

    /**
     * 启动服务器
     */
    private void startServer() {
        int port = sp.getPort();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(new NioEventLoopGroup())
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(createChannel());
        bootstrap.bind().syncUninterruptibly();
    }

    /**
     * 创建 Channel处理链
     *
     * @return ChannelHandler
     */
    private ChannelHandler createChannel() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ProtoCodec());
                pipeline.addLast(new ProtoDispatcherHandler());
            }
        };
    }
}
