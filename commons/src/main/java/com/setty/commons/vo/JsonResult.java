package com.setty.commons.vo;

import com.setty.commons.cons.JsonResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author HuSen
 * create on 2019/7/5 14:30
 */
@Data
@AllArgsConstructor
public class JsonResult<T> implements Serializable {
    private static final long serialVersionUID = -7886007263670793179L;

    private Integer appCode;

    private Integer code;

    private String message;

    private Long time;

    private T data;

    public JsonResult<T> appCode(Integer appCode) {
        this.appCode = appCode;
        return this;
    }

    public JsonResult(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.time = System.currentTimeMillis();
    }

    public JsonResult(T data) {
        this.data = data;
        this.code = JsonResultCode.SUCCESS.getCode();
        this.message = JsonResultCode.SUCCESS.getMessage();
        this.time = System.currentTimeMillis();
    }

    public JsonResult(Integer code, String message) {
        this.code = code;
        this.message = message;
        this.time = System.currentTimeMillis();
    }
}
