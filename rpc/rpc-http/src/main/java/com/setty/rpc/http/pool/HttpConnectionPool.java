package com.setty.rpc.http.pool;

import com.setty.rpc.http.handler.client.HttpResponseHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author HuSen
 * create on 2019/7/24 15:48
 */
@Slf4j
public class HttpConnectionPool extends AbstractChannelPoolMap<InetSocketAddress, FixedChannelPool> {

    private boolean useEpoll;
    private int connectTimeout;
    private int readTimeout;
    private int maxConnections;
    private int maxPendingAcquires;

    public HttpConnectionPool(boolean useEpoll, int connectTimeout, int readTimeout, int maxConnections, int maxPendingAcquires) {
        this.useEpoll = useEpoll;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.maxConnections = maxConnections;
        this.maxPendingAcquires = maxPendingAcquires;
    }

    @Override
    protected FixedChannelPool newPool(InetSocketAddress address) {
        return new FixedChannelPool(
                // 引导
                bootstrap(address),
                // ChannelPoolHandler
                channelPoolHandler(),
                // Channel检查
                ChannelHealthChecker.ACTIVE,
                // 获取连接超时处理
                FixedChannelPool.AcquireTimeoutAction.FAIL,
                // 获取连接超时时间
                connectTimeout,
                // 最大连接数
                maxConnections,
                // 最大等待获取连接数
                maxPendingAcquires,
                // 释放的时候是否进行健康检查
                false,
                // true Channel selection will be LIFO, if false FIFO
                // true 选择 Channel 时后进先出 false 先进后出
                false
        );
    }

    private Bootstrap bootstrap(InetSocketAddress address) {
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup group = useEpoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        bootstrap
                .group(group)
                .channel(useEpoll ? EpollSocketChannel.class : NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
                .option(ChannelOption.SO_LINGER, 0)
                .remoteAddress(address);

        if (useEpoll) {
            // TFO(TCP fast open)是TCP协议的experimental update，它允许服务器和客户端在连接建立握手阶段交换数据，从而使应用节省了一个RTT的时延。
            // 但是TFO会引起一些问题，因此协议要求TCP实现必须默认禁止TFO。需要在某个服务端口上启用TFO功能的时候需要应用程序显示启用。
            bootstrap.option(EpollChannelOption.TCP_FASTOPEN, 1);
        }

        return bootstrap;
    }

    private ChannelPoolHandler channelPoolHandler() {
        return new ChannelPoolHandler() {

            @Override
            public void channelReleased(Channel ch) {
                // flush掉所有写回的数据
                ch.writeAndFlush(Unpooled.EMPTY_BUFFER);
                if (log.isDebugEnabled()) {
                    log.debug("{} channelReleased......", ch);
                }
            }

            @Override
            public void channelAcquired(Channel ch) {
                if (log.isDebugEnabled()) {
                    log.debug("{} channelAcquired......", ch);
                }
            }

            @Override
            public void channelCreated(Channel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new ReadTimeoutHandler(readTimeout));

                pipeline.addLast(new HttpClientCodec());
                pipeline.addLast(new HttpObjectAggregator(1024 * 64));
                pipeline.addLast(new HttpResponseHandler());
            }
        };
    }
}
