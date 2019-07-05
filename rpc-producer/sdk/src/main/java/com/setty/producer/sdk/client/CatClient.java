package com.setty.producer.sdk.client;

import com.setty.producer.sdk.proto.RpcProducer;
import com.setty.rpc.annotation.proto.Client;
import com.setty.rpc.annotation.proto.ProtoClient;
import com.setty.rpc.callback.proto.ProtoCallback;

/**
 * @author HuSen
 * create on 2019/7/5 11:16
 */
@ProtoClient(appId = 100000000)
public interface CatClient {

    /**
     * 新增猫咪
     *
     * @param cat      猫咪
     * @param callback 结果回调
     */
    @Client(moduleId = 10000, methodId = 1)
    void add(RpcProducer.Cat cat, ProtoCallback callback);
}
