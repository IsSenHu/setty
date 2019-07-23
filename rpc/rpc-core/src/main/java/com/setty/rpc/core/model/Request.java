package com.setty.rpc.core.model;

import lombok.Data;

/**
 * JSON-RPC 请求对象
 *
 * @author HuSen
 * create on 2019/7/23 14:39
 */
@Data
public class Request<T> {
    private String jsonrpc;
    private String id;
    private String method;
    private T params;
}
