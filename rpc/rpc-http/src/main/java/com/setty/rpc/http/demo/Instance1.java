package com.setty.rpc.http.demo;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.SystemPropertyUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.ThreadFactory;

/**
 * @author HuSen
 * create on 2019/7/24 9:38
 */
@Slf4j
@Getter
@Setter
public abstract class Instance1 {
    protected static final boolean DEFAULT_USE_EPOLL;
    protected static final int DEFAULT_WORKER_THREADS;

    // todo

    private volatile boolean initialized = false;
    private String bossThreadName;
    private String workerThreadName;
    private int workerThreads;
    private int readTimeout;
    private int writeTimeout;
    private int pingTime;
    private String name;

    static {
        DEFAULT_USE_EPOLL = SystemPropertyUtil.getBoolean("io.epoll", false);
        DEFAULT_WORKER_THREADS = Math.max(1, SystemPropertyUtil.getInt("thread.worker", Runtime.getRuntime().availableProcessors() * 2));

        if (log.isDebugEnabled()) {
            log.debug("Service is use e poll [{}]", DEFAULT_USE_EPOLL);
            log.debug("Service io worker thread number [{}]", DEFAULT_WORKER_THREADS);
        }
    }

    public Instance1(String name) {
        Assert.hasText(name, "not set name of instance");
        this.name = name;
        this.bossThreadName = String.format("[%s] %s", name, "boss-thread");
        this.workerThreadName = String.format("[%s] %s", name, "io-worker-thread");
    }

    private void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        initBootstrap();
    }

    /**
     * 初始化Bootstrap
     */
    protected abstract void initBootstrap();

    protected void doInitChannelTimeout(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        if(Math.max(readTimeout, pingTime) > 0) {
            pipeline.addLast("idle", new IdleStateHandler(Math.max(0, readTimeout), Math.max(0, pingTime), 0));
        }
        if (writeTimeout > 0) {
            pipeline.addLast("write", new WriteTimeoutHandler(Math.max(0, writeTimeout)));
        }
    }

    protected abstract ChannelHandler createChannelInitializer();

    protected boolean trafficLog() {
        return false;
    }

    public void start() {
        if (!initialized) {
            initialize();
        }
        doStart();
    }

    /**
     * start server real logic
     */
    protected abstract void doStart();

    public void stop(Runnable callback) {
        if (callback != null) {
            callback.run();
        }
    }

    protected ThreadFactory newThreadFactory(String poolName) {
        return new DefaultThreadFactory(poolName);
    }
}
