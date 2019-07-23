package com.setty.rpc.http.handler.server;

import com.alibaba.fastjson.JSON;
import com.setty.rpc.core.cons.Sign;
import com.setty.rpc.core.model.Error;
import com.setty.rpc.core.model.ErrorType;
import com.setty.rpc.core.model.Request;
import com.setty.rpc.core.model.Response;
import com.setty.rpc.http.cache.GlobalCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * @author HuSen
 * create on 2019/7/23 14:52
 */
@Slf4j
public class HttpDispatcherHandler extends SimpleChannelInboundHandler<Request> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (log.isDebugEnabled()) {
            log.debug("请求连接已建立:{}", ctx.channel());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("receive message: {}", request);
        }
        String methodMark = request.getMethod();
        // instanceName controller method
        String[] icm = StringUtils.split(methodMark, Sign.POINT);
        String controllerName = icm[1];
        Object controller = GlobalCache.getController(controllerName);
        Method method = GlobalCache.getMethod(methodMark);

        Response<Object> objectResponse = new Response<>();
        objectResponse.setId(request.getId());
        objectResponse.setJsonrpc(request.getJsonrpc());

        if (controller == null || method == null) {
            objectResponse.setError(new Error(ErrorType.UNKNOWN_METHOD));
        } else {
            Object ret = method.invoke(controller, request);
            objectResponse.setResult(ret);
        }

        response(ctx, objectResponse);
    }

    private void response(ChannelHandlerContext ctx, Response<Object> objectResponse) {
        String body = JSON.toJSONString(objectResponse);
        byte[] bytes = body.getBytes(CharsetUtil.UTF_8);
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json;charset=utf-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
        ctx.write(response);
        HttpContent content = new DefaultHttpContent(ctx.alloc().buffer(bytes.length).writeBytes(bytes));
        ctx.write(content);
        ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("分发参数到方法异常:", cause);
        ctx.channel().close();
    }
}
