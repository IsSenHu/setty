package com.setty.rpc.pool.map;

import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;

/**
 * @author HuSen
 * create on 2019/7/3 14:32
 */
public class ProtoChannelPoolMap extends AbstractChannelPoolMap<String, FixedChannelPool> {

    @Override
    protected FixedChannelPool newPool(String key) {
        return null;
    }
}
