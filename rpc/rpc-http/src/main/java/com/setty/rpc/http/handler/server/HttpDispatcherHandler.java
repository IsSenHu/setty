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
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;

/**
 * @author HuSen
 * create on 2019/7/23 14:52
 */
@Slf4j
public class HttpDispatcherHandler extends SimpleChannelInboundHandler<Request> {

    private static final TaskExecutor QUEUE;
    static {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(200);
        threadPoolTaskExecutor.setQueueCapacity(100000);
        threadPoolTaskExecutor.setMaxPoolSize(200);
        threadPoolTaskExecutor.initialize();
        QUEUE = threadPoolTaskExecutor;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (log.isDebugEnabled()) {
            log.debug("请求连接已建立:{}", ctx.channel());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Request request) {
        QUEUE.execute(() -> {
            if (log.isDebugEnabled()) {
                log.debug("receive message: {}", request);
            }
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
            response(ctx, objectResponse);
        });
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
