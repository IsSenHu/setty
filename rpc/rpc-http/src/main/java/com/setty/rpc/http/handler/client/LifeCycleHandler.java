package com.setty.rpc.http.handler.client;

import com.setty.rpc.http.server.HttpClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * 生命周期 Handler
 *
 * @author HuSen
 * create on 2019/7/5 12:51
 */
@Slf4j
public class LifeCycleHandler extends ChannelInboundHandlerAdapter {

    private final String host;
    private final int port;
    private final String name;
    private final HttpClient client;

    public LifeCycleHandler(String host, int port, String name, HttpClient client) {
        this.host = host;
        this.port = port;
        this.name = name;
        this.client = client;
    }

    /**
     * 1
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {

    }

    /**
     * 2
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("连接就绪 {}", Thread.currentThread().getName());
        client.setConnection(ctx);
        client.getOpen().countDown();
    }

    /**
     * 3
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {

    }

    /**
     * 4
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {

    }
}
