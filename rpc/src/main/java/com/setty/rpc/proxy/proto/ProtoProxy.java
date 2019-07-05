package com.setty.rpc.proxy.proto;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.setty.commons.proto.RpcProto;
import com.setty.rpc.annotation.proto.Client;
import com.setty.rpc.annotation.proto.ProtoClient;
import com.setty.rpc.cache.proto.ProtoCache;
import com.setty.rpc.callback.proto.ProtoCallback;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author HuSen
 * create on 2019/7/4 13:24
 */
@Slf4j
public class ProtoProxy implements InvocationHandler {

    private Class<?> interfaceClass;

    Object bind(Class<?> cls) {
        this.interfaceClass = cls;
        // 保存 ProtoClient 和 class 的绑定关系
        ProtoClient protoClient = AnnotationUtils.findAnnotation(cls, ProtoClient.class);
        Assert.notNull(protoClient, "@ProtoClient is need");
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

        // 获取到目标服务器集合 这里可以写策略算法
        List<Channel> channelList = ProtoCache.getChannels(appId);
        if (CollectionUtils.isEmpty(channelList)) {
            // 没有找到目标服务
            if (log.isWarnEnabled()) {
                log.warn("没有找到目标服务:{}", appId);
            }
        } else {
            Channel channel = channelList.get(0);
            if (channel != null) {
                channel.writeAndFlush(request);
            }
        }
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
