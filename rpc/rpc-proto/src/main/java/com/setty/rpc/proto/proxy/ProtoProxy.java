package com.setty.rpc.proto.proxy;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.setty.commons.proto.RpcProto;
import com.setty.commons.util.spring.SpringBeanUtil;
import com.setty.rpc.core.select.ServiceSelector;
import com.setty.rpc.proto.annotation.Client;
import com.setty.rpc.proto.annotation.ProtoClient;
import com.setty.rpc.proto.cache.ProtoCache;
import com.setty.rpc.proto.callback.ProtoCallback;
import com.setty.rpc.proto.client.ProtoClientServer;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author HuSen
 * create on 2019/7/4 13:24
 */
@Slf4j
public class ProtoProxy implements InvocationHandler {

    private Class<?> interfaceClass;

    Object bind(Class<?> cls) {
        this.interfaceClass = cls;
        // 保存 ProtoClientServer 和 class 的绑定关系
        ProtoClient protoClient = AnnotationUtils.findAnnotation(cls, ProtoClient.class);
        Assert.notNull(protoClient, "@ProtoClientServer is need");
        ProtoCache.addProtoClient(cls, protoClient);

        // 保存 ClientP 和 Method 的绑定关系
        Method[] methods = cls.getDeclaredMethods();
        Method.setAccessible(methods, true);
        for (Method method : methods) {
            Client client = AnnotationUtils.findAnnotation(method, Client.class);
            if (client != null) {
                ProtoCache.addClient(method, client);
            }
        }
        return Proxy.newProxyInstance(cls.getClassLoader(), new Class[]{interfaceClass}, this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) {
        ProtoClient protoClient = ProtoCache.getProtoClient(interfaceClass);
        Client client = ProtoCache.getClient(method);
        long appId = protoClient.appId();

        RpcProto.Request.Builder builder = RpcProto.Request.newBuilder();
        String id = UUID.randomUUID().toString();
        // 请求ID
        builder.setId(id);
        // 请求方法
        builder.setMethod(appId + client.moduleId() + client.methodId());
        for (Object object : objects) {
            judgeParam(builder, id, object);
        }
        RpcProto.Request request = builder.build();

        ServiceSelector selector = SpringBeanUtil.getBean(ServiceSelector.class);
        Assert.notNull(selector, "Service selector is empty!");
        Assert.isTrue(selector.validate(), "Service selector is not available!");

        String instanceName = selector.select(appId, null);
        ProtoClientServer clientServer = ProtoCache.getProtoClientServer(instanceName);
        if (!clientServer.started()) {
            clientServer.start();
        }

        ChannelHandlerContext connection = clientServer.getConnection();
        Assert.isTrue(connection.channel().isActive(), "Connection is not available!");

        connection.channel().writeAndFlush(request);
        return null;
    }

    private void judgeParam(RpcProto.Request.Builder builder, String id, Object param) {
        if (param instanceof Message) {
            Message message = (Message) param;
            Any any = Any.pack(message);
            builder.setParams(any);
        } else if (param instanceof ProtoCallback) {
            ProtoCallback callback = (ProtoCallback) param;
            ProtoCache.addCallback(id, callback);
        } else {
            if (log.isWarnEnabled()) {
                log.warn("方法 {} 没有使用的参数:{}", builder.getMethod(), param);
            }
        }
    }
}
