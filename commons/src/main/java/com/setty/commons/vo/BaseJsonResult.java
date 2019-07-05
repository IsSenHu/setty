package com.setty.commons.vo;

import com.setty.commons.cons.JsonResultCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author HuSen
 * create on 2019/7/5 14:30
 */
@Data
public abstract class BaseJsonResult<T> implements Serializable {
    private static final long serialVersionUID = -7886007263670793179L;

    private Integer code;

    private String message;

    private Long time;

    private T data;

    public BaseJsonResult(Integer code, String message, T data) {
        this.code = appCode() + code;
        this.message = message;
        this.data = data;
        this.time = System.currentTimeMillis();
    }

    public BaseJsonResult(T data) {
        this.data = data;
        this.code = appCode() + JsonResultCode.SUCCESS.getCode();
        this.message = JsonResultCode.SUCCESS.getMessage();
        this.time = System.currentTimeMillis();
    }

    public BaseJsonResult(Integer code, String message) {
        this.code = appCode() + code;
        this.message = message;
        this.time = System.currentTimeMillis();
    }

    public BaseJsonResult(JsonResultCode jsonResultCode) {
        this.code = appCode() + jsonResultCode.getCode();
        this.message = jsonResultCode.getMessage();
        this.time = System.currentTimeMillis();
    }

    /**
     * 应用Code
     *
     * @return appCode
     */
    protected abstract Integer appCode();
}
