package com.setty.producer.server.controller;

import com.google.protobuf.InvalidProtocolBufferException;
import com.setty.commons.proto.RpcProto;
import com.setty.producer.sdk.proto.RpcProducer;
import com.setty.rpc.core.cons.Code;
import com.setty.rpc.proto.annotation.ProtoConsumer;
import com.setty.rpc.proto.annotation.ProtoController;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HuSen
 * create on 2019/7/5 10:55
 */
@Slf4j
@ProtoController(moduleId = 10000)
public class CatController {

    @ProtoConsumer(1)
    public RpcProto.Response add(RpcProto.Request request) throws InvalidProtocolBufferException {
        if (request.getParams().is(RpcProducer.Cat.class)) {
            RpcProducer.Cat cat = request.getParams().unpack(RpcProducer.Cat.class);
            log.info("新增小猫咪:{}", cat);
        }
        RpcProto.Response.Builder builder = RpcProto.Response.newBuilder();
        builder.setId(request.getId());
        builder.setTime(System.currentTimeMillis());
        builder.setCode(Code.SUCCESS.getCode());
        builder.setMsg(Code.SUCCESS.getMsg());
        return builder.build();
    }
}
