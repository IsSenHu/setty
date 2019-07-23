package com.setty.rpc.http.handler.server;

import com.alibaba.fastjson.JSON;
import com.setty.rpc.core.model.Request;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/23 14:34
 */
public class RequestToMessengerHandler extends MessageToMessageDecoder<FullHttpRequest> {

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest request, List<Object> out) {
        String jsonRpc = request.content().toString(CharsetUtil.UTF_8);
        Request req = JSON.parseObject(jsonRpc, Request.class);
        out.add(req);
    }
}
