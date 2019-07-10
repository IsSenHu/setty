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
        return new JsonResult<>(t).appCode(appCode);
    }

    public static <T> JsonResult<T> create(Integer code, String message, T data) {
        return new JsonResult<>(code.equals(JsonResultCode.SUCCESS.getCode()) ? code : appCode + code, message, data).appCode(appCode);
    }

    public static JsonResult<Object> create(Integer code, String message) {
        return new JsonResult<>(code.equals(JsonResultCode.SUCCESS.getCode()) ? code : appCode + code, message).appCode(appCode);
    }

    public static JsonResult<Object> create(JsonResultCode code) {
        return new JsonResult<>(code == JsonResultCode.SUCCESS ? code.getCode() : appCode + code.getCode(), code.getMessage()).appCode(appCode);
    }
}
