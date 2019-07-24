package com.setty.rpc.http.handler.client;

import com.alibaba.fastjson.JSON;
import com.setty.rpc.core.model.Response;
import com.setty.rpc.http.cache.CallbackCache;
import com.setty.rpc.http.callback.HttpCallback;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.timeout.ReadTimeoutException;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HuSen
 * create on 2019/7/23 17:26
 */
@Slf4j
public class HttpResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) {
        ByteBuf content = response.content();
        String resp = content.toString(CharsetUtil.UTF_8);
        Response r = JSON.parseObject(resp, Response.class);
        HttpCallback httpCallback = CallbackCache.getAndRemove(r.getId());
        if (httpCallback != null) {
            ctx.executor().submit(() -> httpCallback.execute(r));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof ReadTimeoutException) {
            log.error("readTimeout for {}", ctx);
        } else {
            log.error("Request error:", cause);
        }
    }
}
