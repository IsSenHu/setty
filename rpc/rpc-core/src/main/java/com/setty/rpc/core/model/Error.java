package com.setty.rpc.core.model;

import lombok.Data;

/**
 * @author HuSen
 * create on 2019/7/23 16:38
 */
@Data
public class Error {
    private Integer code;
    private String message;

    public Error(ErrorType errorType) {
        code = errorType.getCode();
        message = errorType.getMessage();
    }
}
