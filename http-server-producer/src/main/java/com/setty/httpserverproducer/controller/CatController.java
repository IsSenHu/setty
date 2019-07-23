package com.setty.httpserverproducer.controller;

import com.setty.commons.vo.JsonResult;
import com.setty.rpc.core.model.Request;
import com.setty.rpc.http.annotation.HttpConsumer;
import com.setty.rpc.http.annotation.HttpController;
import lombok.extern.slf4j.Slf4j;

/**
 * @author HuSen
 * create on 2019/7/23 16:14
 */
@Slf4j
@HttpController("cat")
public class CatController {

    @HttpConsumer("add")
    public JsonResult<String> add(Request request) {
        log.info("请求:{}", request);
        return new JsonResult<>("好的");
    }
}
