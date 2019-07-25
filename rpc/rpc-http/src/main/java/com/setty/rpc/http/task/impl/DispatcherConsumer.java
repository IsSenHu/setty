package com.setty.rpc.http.task.impl;

import com.alibaba.fastjson.JSON;
import com.setty.rpc.core.cons.Sign;
import com.setty.rpc.core.model.Error;
import com.setty.rpc.core.model.ErrorType;
import com.setty.rpc.core.model.Request;
import com.setty.rpc.core.model.Response;
import com.setty.rpc.http.cache.GlobalCache;
import com.setty.rpc.http.task.AbstractConsumer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;

/**
 * @author HuSen
 * create on 2019/7/25 14:44
 */
public class DispatcherConsumer extends AbstractConsumer {

    public DispatcherConsumer(Request request, ChannelHandlerContext connection) {
        super(request, connection);
    }

    @Override
    protected void doLogic(Request request, ChannelHandlerContext connection) {
        Response<Object> objectResponse = new Response<>();
        objectResponse.setId(request.getId());
        objectResponse.setJsonrpc(request.getJsonrpc());
        try {
            String methodMark = request.getMethod();
            // instanceName controller method
            String[] icm = StringUtils.split(methodMark, Sign.POINT);
            String controllerName = icm[1];
            Object controller = GlobalCache.getController(controllerName);
            Method method = GlobalCache.getMethod(methodMark);

            if (controller == null || method == null) {
                objectResponse.setError(new Error(ErrorType.UNKNOWN_METHOD));
            } else {
                Object ret = method.invoke(controller, request);
                objectResponse.setResult(ret);
            }
        } catch (Exception e) {
            objectResponse.setError(new Error(ErrorType.NETWORK_ERROR));
        }
        response(connection, objectResponse);
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
}
