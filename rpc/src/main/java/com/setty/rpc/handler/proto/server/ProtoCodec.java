package com.setty.rpc.handler.proto.server;

import com.setty.commons.proto.RpcProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/3 14:51
 */
@Slf4j
public class ProtoCodec extends ByteToMessageCodec<RpcProto.Response> {

    /**
     * 解码
     *
     * @param ctx ChannelHandlerContext
     * @param in  输入
     * @param out 解码后的数据集合
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        try {
            int i = in.readableBytes();
            byte[] bytes = new byte[i];
            in.readBytes(bytes);
            RpcProto.Request request = RpcProto.Request.parseFrom(bytes);
            out.add(request);
        } catch (Exception e) {
            log.error("解析数据异常:", e);
            // TODO write error
        }
    }

    /**
     * 编码
     *
     * @param ctx      ChannelHandlerContext
     * @param response Response
     * @param out      输出
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProto.Response response, ByteBuf out) {
        byte[] bytes = response.toByteArray();
        ByteBuf buffer = ctx.alloc().buffer(bytes.length);
        buffer.writeBytes(bytes);
        ctx.writeAndFlush(buffer);
    }
}
