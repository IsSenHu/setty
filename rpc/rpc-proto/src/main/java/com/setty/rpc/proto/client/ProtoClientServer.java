package com.setty.rpc.proto.client;

import com.setty.rpc.proto.handler.client.LifeCycleHandler;
import com.setty.rpc.proto.handler.client.ProtoCodec;
import com.setty.rpc.proto.handler.client.ProtoResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

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
    private volatile ChannelHandlerContext connection;
    private volatile AtomicBoolean started = new AtomicBoolean(false);

    public ProtoClientServer(String host, int port, String key, long appId, int connectionTimeout) {
        this.host = host;
        this.port = port;
        this.key = key;
        this.appId = appId;
        this.connectionTimeout = connectionTimeout;
        group = new NioEventLoopGroup();
    }

    public void start() {
        if (!started.getAndSet(true)) {
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
    }

    private ChannelHandler createChannelInitializer() {
        LifeCycleHandler lifeCycleHandler = new LifeCycleHandler(host, port, key, appId, this);
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(lifeCycleHandler);
                pipeline.addLast(new ProtoCodec());
                pipeline.addLast(new ProtoResponseHandler());
            }
        };
    }

    public boolean started() {
        return started.get();
    }

    public void setConnection(ChannelHandlerContext connection) {
        this.connection = connection;
    }
}
