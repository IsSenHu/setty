package com.setty.commons.cons;

import lombok.Getter;

/**
 * @author HuSen
 * create on 2019/7/5 14:33
 */
@Getter
public enum JsonResultCode {
    //
    SUCCESS(0, "success"),
    NOT_FOUND(404, "not found");

    private Integer code;
    private String message;

    JsonResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
