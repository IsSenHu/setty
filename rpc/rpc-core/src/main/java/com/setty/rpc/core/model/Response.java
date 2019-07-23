package com.setty.rpc.core.model;

import lombok.Data;

/**
 * error 和 result 不能同时存在
 *
 * @author HuSen
 * create on 2019/7/23 16:36
 */
@Data
public class Response<T> {
    private String jsonrpc;
    private T result;
    private Error error;
    private String id;
}
