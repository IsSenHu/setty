package com.setty.rpc.proto.handler.client;

import com.setty.commons.proto.RpcProto;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/4 9:40
 */
public class ProtoCodec extends ByteToMessageCodec<RpcProto.Request> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProto.Request request, ByteBuf out) {
        byte[] bytes = request.toByteArray();
        ByteBuf byteBuf = ctx.alloc().buffer(bytes.length).writeBytes(bytes);
        ctx.writeAndFlush(byteBuf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int i = in.readableBytes();
        byte[] bytes = new byte[i];
        in.readBytes(bytes);
        RpcProto.Response response = RpcProto.Response.parseFrom(bytes);
        out.add(response);
    }
}
