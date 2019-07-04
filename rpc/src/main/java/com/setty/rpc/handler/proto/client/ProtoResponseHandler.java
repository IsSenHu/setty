package com.setty.rpc.handler.proto.client;

import com.setty.commons.proto.RpcProto;
import com.setty.rpc.cache.proto.ProtoCache;
import com.setty.rpc.callback.proto.ProtoCallback;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HuSen
 * create on 2019/7/4 9:49
 */
@Slf4j
@ChannelHandler.Sharable
public class ProtoResponseHandler extends SimpleChannelInboundHandler<RpcProto.Response> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProto.Response response) {
        ProtoCallback callback = ProtoCache.getCallback(response.getId());
        if (callback != null) {
            callback.execute(response);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Proto请求异常:", cause);
        ctx.close();
    }
}
