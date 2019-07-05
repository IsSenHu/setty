package com.setty.commons.vo.registry;

import com.setty.commons.cons.JsonResultCode;
import com.setty.commons.vo.BaseJsonResult;

/**
 * @author HuSen
 * create on 2019/7/5 14:43
 */
public class RegistryJsonResult<T> extends BaseJsonResult<T> {

    private static final long serialVersionUID = 7347407787068992133L;

    public RegistryJsonResult(Integer code, String message, T data) {
        super(code, message, data);
    }

    public RegistryJsonResult(T data) {
        super(data);
    }

    public RegistryJsonResult(Integer code, String message) {
        super(code, message);
    }

    public RegistryJsonResult(JsonResultCode jsonResultCode) {
        super(jsonResultCode);
    }

    @Override
    public Integer appCode() {
        return 10000;
    }
}
