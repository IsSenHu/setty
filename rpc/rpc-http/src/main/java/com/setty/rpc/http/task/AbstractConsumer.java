package com.setty.rpc.http.task;

import com.setty.rpc.core.model.Request;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author HuSen
 * create on 2019/7/25 14:40
 */
public abstract class AbstractConsumer implements Runnable {

    private Request request;
    private ChannelHandlerContext connection;

    public AbstractConsumer(Request request, ChannelHandlerContext connection) {
        this.request = request;
        this.connection = connection;
    }

    @Override
    public void run() {
        doLogic(request, connection);
    }

    protected abstract void doLogic(Request request, ChannelHandlerContext connection);
}
