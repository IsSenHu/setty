package com.setty.rpc.http.server;

import com.setty.rpc.http.demo.AbstractServer;
import com.setty.rpc.http.handler.server.LogicHandler;
import com.setty.rpc.http.handler.server.RequestToMessengerHandler;
import com.setty.rpc.http.task.Publisher;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * @author HuSen
 * create on 2019/7/23 14:24
 */
@Slf4j
public class HttpServer extends AbstractServer {

    private ServerBootstrap bootstrap;
    private int bossThreads;
    private volatile boolean running = false;
    private Publisher publisher;

    public HttpServer(int port, String name, Publisher publisher) {
        super(name, port);
        this.publisher = publisher;
    }

    @Override
    protected void initBootstrap() {
        if (bootstrap == null) {
            bootstrap = new ServerBootstrap();
        }
        EventLoopGroup bossGroup = getUseEpoll() ? new EpollEventLoopGroup(getBossThreads(), newThreadFactory(getBossThreadName()))
                : new NioEventLoopGroup(getBossThreads(), newThreadFactory(getBossThreadName()));

        EventLoopGroup workerGroup = getUseEpoll() ? new EpollEventLoopGroup(getWorkerThreads(), newThreadFactory(getWorkerThreadName())) :
                new NioEventLoopGroup(getWorkerThreads(), newThreadFactory(getWorkerThreadName()));

        bootstrap
                .group(bossGroup, workerGroup)
                // Java NIO根据操作系统不同，比如
                // macosx 是KQueueSelectorProvider、
                // windows有WindowsSelectorProvider、
                // Linux有EPollSelectorProvider （Linux kernels >= 2.6，是epoll模式）或PollSelectorProvider（selector模式）
                // Oracle jdk会自动选择合适的Selector，Oracle JDK在Linux已经默认使用epoll方式，为什么netty还要提供一个基于epoll的实现呢?
                // 1.Netty的 epoll transport使用 epoll edge-triggered 而 java的 nio 使用 level-triggered
                // 2.Netty的 epoll transport暴露了更多的nio没有的配置参数，如 TCP_CORK, SO_REUSEADDR等
                // 3.更少GC，更少synchronized
                .channel(getUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                // TCP Keep-alive 检测连接是否可用
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // 是否开启 Nagle 算法 | false 开启 | true 关闭
                // HTTP协议请求响应的模型的场景不太需要考虑禁用这个算法
                .childOption(ChannelOption.TCP_NODELAY, false)
                // 在默认情况下,当调用close关闭socket的使用,close会立即返回,但是,如果send buffer中还有数据,系统会试着先把send buffer中的数据发送出去,然后close才返回.
                // 执行Socket的close方法，该方法不会立即返回，而进入阻塞状态，同时，底层的Socket也会尝试发送剩余的数据，只有满足下面的两个条件之一，close方法才会返回：
                // (1) 底层的Socket已经发送完所有的剩余数据
                // (2) 尽管底层的Socket还没有发送完所有的剩余数据，但已经阻塞了x秒，close()方法的阻塞时间超过x秒，也会返回，剩余未发送的数据被丢弃。
                // 设为0 该方法也会立即返回，但底层的Socket也会立即关闭，所有未发送完的剩余数据被丢弃
                .childOption(ChannelOption.SO_LINGER, 0)
                .childHandler(createChannelInitializer());

        if (getUseEpoll()) {
            // TFO(TCP fast open)是TCP协议的experimental update，它允许服务器和客户端在连接建立握手阶段交换数据，从而使应用节省了一个RTT的时延。
            // 但是TFO会引起一些问题，因此协议要求TCP实现必须默认禁止TFO。需要在某个服务端口上启用TFO功能的时候需要应用程序显示启用。
            bootstrap.option(EpollChannelOption.TCP_FASTOPEN, 1);
        }
    }

    @Override
    protected void doStart() {
        if (running) {
            return;
        }
        running = true;
        ChannelFuture channelFuture = bootstrap.bind(getPort());
        channelFuture.addListener(
                (GenericFutureListener<Future<Void>>) future -> {
                    boolean status = future.isSuccess();
                    log.info("server {} status : {}, listening at : {}", getName(), status, getPort());
                    if (!status) {
                        HttpServer.this.stop(null);
                    }
                }
        );
    }

    @Override
    protected void doInitTimeout(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new WriteTimeoutHandler(getWriteTimeout()));
    }

    @Override
    protected void doInitLogic(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1024 * 64));
        pipeline.addLast(new RequestToMessengerHandler());
        pipeline.addLast(new LogicHandler(publisher));
    }

    @Override
    protected void exceptionCaught0(ChannelHandlerContext ctx, Throwable cause) {
        log.error("unknown error:", cause);
        ctx.close();
    }

    @Override
    protected void writeTimeout(ChannelHandlerContext ctx, Throwable cause) {
        log.error("writeTimeout to {}", ctx);
        ctx.close();
    }

    @Override
    public void stop(final Runnable runnable) {
        if (!running) {
            return;
        }
        final CountDownLatch latch = new CountDownLatch(2);
        bootstrap.config().group().shutdownGracefully().addListener((GenericFutureListener<Future<? super Object>>) future -> {
            latch.countDown();
            log.info("boss thread status : {}", future.isSuccess() ? "shutdown" : "running");
        });

        bootstrap.config().childGroup().shutdownGracefully().addListener((GenericFutureListener<Future<? super Object>>) future -> {
            latch.countDown();
            log.info("worker thread status : {}", future.isSuccess() ? "shutdown" : "running");
        });

        try {
            latch.await();
        } catch (InterruptedException ignored) {}

        super.stop(runnable);
        log.info("{} server stopped.", getName());
    }
}
