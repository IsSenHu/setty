package com.setty.rpc.pool.map;

import com.setty.rpc.cache.proto.ProtoCache;
import com.setty.rpc.pool.proto.ProtoChannelPoolHandler;
import com.setty.rpc.properties.proto.ClientP;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author HuSen
 * create on 2019/7/3 14:32
 */
public class ProtoChannelPoolMap extends AbstractChannelPoolMap<String, FixedChannelPool> {

    private final Map<String, ClientP> clientMap;

    private final NioEventLoopGroup group;

    public ProtoChannelPoolMap(Map<String, ClientP> clientMap) {
        this.clientMap = clientMap;
        group = new NioEventLoopGroup();
    }

    @Override
    protected FixedChannelPool newPool(String key) {
        ClientP client = clientMap.get(key);
        Assert.notNull(client, "client: " + key + "is not found");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, client.getConnectionTimeout())
                .remoteAddress(client.getHost(), client.getPort());
        return new FixedChannelPool(
                // 引导
                bootstrap,
                // ChannelPoolHandler
                new ProtoChannelPoolHandler(),
                // Channel检查
                ChannelHealthChecker.ACTIVE,
                // 获取连接超时处理
                FixedChannelPool.AcquireTimeoutAction.FAIL,
                // 获取连接超时时间
                5000,
                // 最大连接数
                10,
                // 最大等待获取连接数
                10,
                // 释放的时候是否进行健康检查
                false,
                // true Channel selection will be LIFO, if false FIFO
                // true 选择 Channel 时后进先出 false 先进后出
                false
        );
    }

    public void start() {
        clientMap.keySet().forEach(this::newPool);
        clientMap.forEach((k, c) -> {
            FixedChannelPool fixedChannelPool = this.get(k);
            ProtoCache.addPool(c.getAppId(), fixedChannelPool);
            Future<Channel> acquire = fixedChannelPool.acquire();
            acquire.addListener((GenericFutureListener<Future<Channel>>) future -> {
                Channel channel = future.get(5, TimeUnit.SECONDS);
                ProtoCache.addChannel(c.getAppId(), channel);
            });
        });
    }
}
