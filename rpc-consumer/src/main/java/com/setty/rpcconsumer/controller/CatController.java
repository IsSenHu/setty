package com.setty.rpcconsumer.controller;

import com.setty.producer.sdk.client.CatClient;
import com.setty.producer.sdk.proto.RpcProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HuSen
 * create on 2019/7/5 11:25
 */
@Slf4j
@RestController
public class CatController {

    private final CatClient catClient;

    @Autowired
    public CatController(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") CatClient catClient) {
        this.catClient = catClient;
    }

    @GetMapping("/add")
    public void add() {
        RpcProducer.Cat.Builder builder = RpcProducer.Cat.newBuilder();
        builder.setName("tom");
        builder.setAge(12);
        builder.setSex("man");
        builder.setColor("green");
        catClient.add(builder.build(), m -> log.info("响应:{}", m));
    }
}
