package com.setty.commons.util.result;

import com.setty.commons.cons.JsonResultCode;
import com.setty.commons.vo.JsonResult;

/**
 * @author HuSen
 * create on 2019/7/10 10:49
 */
public class ResultEqualUtil {

    public static boolean equal(JsonResult result, JsonResultCode code) {
        return code == JsonResultCode.SUCCESS ? result.getCode().equals(code.getCode()) : (result.getCode() - result.getAppCode()) == code.getCode();
    }
}
