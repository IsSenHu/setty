package com.setty.rpc.proxy.proto;

import com.google.protobuf.Any;
import com.google.protobuf.Message;
import com.setty.commons.proto.RpcProto;
import com.setty.commons.util.SpringBeanUtil;
import com.setty.rpc.annotation.proto.Client;
import com.setty.rpc.annotation.proto.ProtoClient;
import com.setty.rpc.cache.proto.ProtoCache;
import com.setty.rpc.callback.proto.ProtoCallback;
import com.setty.rpc.pool.map.ProtoChannelPoolMap;
import io.netty.channel.Channel;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.List;
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
        log.info("invoke:{}", interfaceClass);
        ProtoClient protoClient = ProtoCache.getProtoClient(interfaceClass);
        Client client = ProtoCache.getClient(method);
        log.info("{} - {}", protoClient, client);
        Message message = (Message) objects[0];
        Any any = Any.pack(message);
        log.info("params:{}", any);
        RpcProto.Request.Builder builder = RpcProto.Request.newBuilder();
        String id = UUID.randomUUID().toString();
        builder.setId(id);
        builder.setMethod(protoClient.appId() + client.methodId() + client.methodId());
        builder.setParams(any);
        RpcProto.Request request = builder.build();

        List<String> keys = ProtoCache.getKeys(protoClient.appId());
        if (keys != null && keys.size() > 0) {
            String key = keys.get(0);
            ProtoChannelPoolMap poolMap = SpringBeanUtil.getBean(ProtoChannelPoolMap.class);
            FixedChannelPool pool = poolMap.get(key);
            Future<Channel> acquire = pool.acquire();
            acquire.addListener((GenericFutureListener<Future<Channel>>) future -> {
                Channel channel = future.getNow();
                channel.writeAndFlush(request);
            });
        }
        ProtoCallback protoCallback = new ProtoCallback();
        return null;
    }
}
