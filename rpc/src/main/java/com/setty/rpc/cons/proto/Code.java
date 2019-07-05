package com.setty.rpc.cons.proto;

/**
 * @author HuSen
 * create on 2019/7/3 17:55
 */
public enum Code {
    // 成功
    SUCCESS(0, "success"),
    // 没有找到请求的资源
    NOT_FOUND(4004, "not found");
    private int code;
    private String msg;

    Code(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
