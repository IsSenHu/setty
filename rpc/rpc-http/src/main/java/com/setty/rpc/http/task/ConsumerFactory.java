package com.setty.rpc.http.task;

import com.setty.rpc.core.model.Request;
import com.setty.rpc.http.task.impl.DispatcherConsumer;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author HuSen
 * create on 2019/7/25 14:41
 */
public class ConsumerFactory {

    public static AbstractConsumer create(Request request, ChannelHandlerContext connection) {
        return new DispatcherConsumer(request, connection);
    }
}
