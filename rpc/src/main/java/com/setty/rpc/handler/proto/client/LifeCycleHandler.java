package com.setty.rpc.handler.proto.client;

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
    private final String key;
    private final long appId;

    public LifeCycleHandler(String host, int port, String key, long appId) {
        this.host = host;
        this.port = port;
        this.key = key;
        this.appId = appId;
    }

    /**
     * 1
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        if (log.isInfoEnabled()) {
            log.info("App [{}] Key [{}] Host [{}] Port [{}] 连接已注册.", appId, key, host, port);
        }
    }

    /**
     * 2
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (log.isInfoEnabled()) {
            log.info("App [{}] Key [{}] Host [{}] Port [{}] 已经连接就绪.", appId, key, host, port);
        }
    }

    /**
     * 3
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (log.isInfoEnabled()) {
            log.info("App [{}] Key [{}] Host [{}] Port [{}] 连接已失效.", appId, key, host, port);
        }
    }

    /**
     * 4
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        if (log.isInfoEnabled()) {
            log.info("App [{}] Key [{}] Host [{}] Port [{}] 连接已销毁.", appId, key, host, port);
        }
    }
}
