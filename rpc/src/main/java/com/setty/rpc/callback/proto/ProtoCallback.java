package com.setty.rpc.callback.proto;

import com.setty.commons.proto.RpcProto;
import com.setty.rpc.callback.Callback;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HuSen
 * create on 2019/7/4 9:53
 */
@Slf4j
public class ProtoCallback implements Callback<RpcProto.Response> {

    @Override
    public void execute(RpcProto.Response response) {
        log.info("响应:{}", response);
    }
}
