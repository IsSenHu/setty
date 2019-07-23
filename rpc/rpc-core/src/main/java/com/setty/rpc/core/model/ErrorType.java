package com.setty.rpc.core.model;

import lombok.Getter;

/**
 * @author HuSen
 * create on 2019/7/23 16:40
 */
@Getter
public enum ErrorType {
    //
    PARSE_ERROR(-32700, "解析错误"),
    REQUEST_INVALID(-32600, "请求无效"),
    UNKNOWN_METHOD(-32601, "未知的方法"),
    PARAM_ERROR(-32602, "参数错误"),
    NETWORK_ERROR(-32603, "网络错误");

    // -32000--32099 其他服务器错误保留码

    private Integer code;
    private String message;

    ErrorType(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
