package com.setty.rpc.callback.proto;

import com.setty.commons.proto.RpcProto;
import com.setty.rpc.callback.Callback;

/**
 * @author HuSen
 * create on 2019/7/5 9:37
 */
@FunctionalInterface
public interface ProtoCallback extends Callback<RpcProto.Response> {
}
