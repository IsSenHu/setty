package com.setty.rpc.http.handler.server;

import com.setty.rpc.core.model.Request;
import com.setty.rpc.http.task.AbstractConsumer;
import com.setty.rpc.http.task.ConsumerFactory;
import com.setty.rpc.http.task.Publisher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HuSen
 * create on 2019/7/23 14:52
 */
@Slf4j
public class LogicHandler extends SimpleChannelInboundHandler<Request> {

    private final Publisher publisher;

    public LogicHandler(Publisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (log.isDebugEnabled()) {
            log.debug("请求连接已建立:{}", ctx.channel());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) {
        AbstractConsumer consumer = ConsumerFactory.create(request, ctx);
        publisher.on(consumer);
    }
}
