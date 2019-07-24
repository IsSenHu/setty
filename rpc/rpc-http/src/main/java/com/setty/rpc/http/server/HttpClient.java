package com.setty.rpc.http.server;

import com.alibaba.fastjson.JSON;
import com.setty.rpc.core.model.Request;
import com.setty.rpc.http.handler.client.HttpResponseHandler;
import com.setty.rpc.http.handler.client.LifeCycleHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * 1，接下来思考如何实现重连机制
 *
 * @author HuSen
 * create on 2019/7/23 16:57
 */
@Slf4j
public class HttpClient {

    private final EventLoopGroup group;
    private final String host;
    private final int port;
    private final String name;
    private final int connectionTimeout;
    private Bootstrap bootstrap;
    private volatile boolean started;
    private volatile ChannelHandlerContext connection;

    /**
     * 它会在计数器达到 0 的时候唤醒相应实例上的所有等待线程
     *
     * 此时继续调用countDown()并不会导致异常的抛出
     *
     * 并且后续执行await()的线程也不会被暂停
     */
    private final CountDownLatch open = new CountDownLatch(1);

    public HttpClient(String host, int port, String name, int connectionTimeout) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.connectionTimeout = connectionTimeout;
        group = new NioEventLoopGroup();
    }

    public void init() {
        bootstrap = new Bootstrap();
        bootstrap
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout);
        bootstrap.handler(createChannelInitializer());
    }

    private void start() {
        synchronized (this) {
            if (!started && bootstrap != null) {
                log.info("HttpClient {} {} is starting", Thread.currentThread().getName(), name);
                bootstrap.connect(host, port).syncUninterruptibly();
                started = true;
            }
        }
    }

    private ChannelHandler createChannelInitializer() {
        LifeCycleHandler lifeCycleHandler = new LifeCycleHandler(host, port, name, this);
        return new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpClientCodec());
                pipeline.addLast(new HttpObjectAggregator(1024 * 60));
                pipeline.addLast(lifeCycleHandler);
                pipeline.addLast(new HttpResponseHandler());
            }
        };
    }

    public void request(Request request) {
        try {
            if (!started) {
                start();
            }
            if (connection == null) {
                open.await();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String body = JSON.toJSONString(request);
        byte[] bytes = body.getBytes(CharsetUtil.UTF_8);
        HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "");
        httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
        HttpContent httpContent = new DefaultHttpContent(connection.alloc().buffer(bytes.length).writeBytes(bytes));
        connection.write(httpRequest);
        connection.write(httpContent);
        connection.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
    }

    public void destroy() {
        group.shutdownGracefully();
    }

    public void setConnection(ChannelHandlerContext connection) {
        this.connection = connection;
    }

    public CountDownLatch getOpen() {
        return open;
    }
}
