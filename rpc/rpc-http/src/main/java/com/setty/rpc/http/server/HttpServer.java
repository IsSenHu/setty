package com.setty.rpc.http.server;

import com.setty.rpc.http.handler.server.HttpDispatcherHandler;
import com.setty.rpc.http.handler.server.RequestToMessengerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author HuSen
 * create on 2019/7/23 14:24
 */
public class HttpServer {

    private final EventLoopGroup group;
    private final int port;

    public HttpServer(int port) {
        this.port = port;
        group = new NioEventLoopGroup();
    }

    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(createChannelInitializer());
        bootstrap.bind().syncUninterruptibly();
    }

    /**
     * 创建 Channel处理链
     *
     * @return ChannelHandler
     */
    private ChannelHandler createChannelInitializer() {
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpServerCodec());
                pipeline.addLast(new HttpObjectAggregator(1024 * 64));
                pipeline.addLast(new RequestToMessengerHandler());
                pipeline.addLast(new HttpDispatcherHandler());
            }
        };
    }

    public void close() {
        group.shutdownGracefully();
    }
}
