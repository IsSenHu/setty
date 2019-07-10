package com.setty.rpc.client;

import com.setty.rpc.cache.proto.ProtoCache;
import com.setty.rpc.handler.proto.client.LifeCycleHandler;
import com.setty.rpc.handler.proto.client.ProtoCodec;
import com.setty.rpc.handler.proto.client.ProtoResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Getter;

/**
 * @author HuSen
 * create on 2019/7/5 11:55
 */
@Getter
public class ProtoClientServer {
    private final NioEventLoopGroup group;
    private final String host;
    private final int port;
    private final String key;
    private final long appId;
    private final int connectionTimeout;

    public ProtoClientServer(String host, int port, String key, long appId, int connectionTimeout) {
        this.host = host;
        this.port = port;
        this.key = key;
        this.appId = appId;
        this.connectionTimeout = connectionTimeout;
        group = new NioEventLoopGroup();
    }

    public void start() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout);
        bootstrap.handler(createChannelInitializer());
        bootstrap.connect(host, port).syncUninterruptibly();
    }

    private ChannelHandler createChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LifeCycleHandler(host, port, key, appId));
                pipeline.addLast(new ProtoCodec());
                pipeline.addLast(new ProtoResponseHandler());
            }
        };
    }
}
