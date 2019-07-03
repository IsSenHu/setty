package com.setty.rpc.handler.proto;

import com.setty.commons.proto.RpcProto;
import com.setty.rpc.cache.proto.PCache;
import com.setty.rpc.cons.proto.Code;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author HuSen
 * create on 2019/7/3 15:03
 */
@Slf4j
@ChannelHandler.Sharable
public class ProtoDispatcherHandler extends SimpleChannelInboundHandler<RpcProto.Request> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (log.isDebugEnabled()) {
            log.debug("请求连接已建立:{}", ctx.channel());
        }
    }

    /**
     * 0000_0000_0000 9999个应用 9999个模块/应用 9999方法/模块
     * 应用ID取值范围: [100_000_000,999_900_000_000]
     * 模块ID取值范围: [10_000, 99990000]
     * 方法ID取值范围: [1,9999]
     *
     * @param ctx     ChannelHandlerContext
     * @param request 请求参数
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProto.Request request) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("请求参数:{}", request.toString());
        }
        Method method = PCache.getMethod(request.getMethod());
        Object o = PCache.getController(request.getMethod());
        if (method != null && o != null) {
            method.invoke(o, ctx, request);
        } else {
            do404(ctx, request);
        }
    }

    private void do404(ChannelHandlerContext ctx, RpcProto.Request request) {
        if (log.isDebugEnabled()) {
            log.debug("not found {} to go", request.getMethod());
        }
        RpcProto.Response.Builder builder = RpcProto.Response.newBuilder();
        builder.setId(request.getId());
        builder.setCode(Code.NOT_FOUND.getCode());
        builder.setMsg(Code.NOT_FOUND.getMsg());
        builder.setTime(System.currentTimeMillis());
        byte[] resp = builder.build().toByteArray();
        ByteBuf byteBuf = ctx.alloc().buffer(resp.length).writeBytes(resp);
        ctx.writeAndFlush(byteBuf);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("分发参数到方法异常:", cause);
        ctx.channel().close();
    }
}
