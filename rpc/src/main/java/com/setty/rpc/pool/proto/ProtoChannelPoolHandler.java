package com.setty.rpc.pool.proto;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HuSen
 * create on 2019/7/3 14:41
 */
@Slf4j
public class ProtoChannelPoolHandler implements ChannelPoolHandler {

    @Override
    public void channelReleased(Channel ch) {
        // flush掉所有写回的数据
        ch.writeAndFlush(Unpooled.EMPTY_BUFFER);
        if (log.isDebugEnabled()) {
            log.debug("释放 channel:{}", ch);
        }
    }

    @Override
    public void channelAcquired(Channel ch) {
        if (log.isDebugEnabled()) {
            log.debug("获取连接池中的 channel:{}", ch);
        }
    }

    @Override
    public void channelCreated(Channel ch) {

    }
}
