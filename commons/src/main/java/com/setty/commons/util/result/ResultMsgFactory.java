package com.setty.commons.util.result;

import com.setty.commons.cons.JsonResultCode;
import com.setty.commons.vo.JsonResult;

/**
 * @author HuSen
 * create on 2019/7/8
 */
public class ResultMsgFactory {
    private static Integer appCode;

    public static void setAppCode(Integer appCode) {
        ResultMsgFactory.appCode = appCode;
    }

    public static <T> JsonResult<T> create(T t) {
        return new JsonResult<>(t);
    }

    public static <T> JsonResult<T> create(Integer code, String message, T data) {
        return new JsonResult<>(appCode + code, message, data);
    }

    public static <T> JsonResult<T> create(Integer code, String message) {
        return new JsonResult<>(code, message);
    }

    public static <T> JsonResult<T> create(JsonResultCode code) {
        return new JsonResult<>(code);
    }
}
