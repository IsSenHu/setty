package com.setty.rpc.http.callback;

import com.setty.rpc.core.callback.Callback;
import com.setty.rpc.core.model.Response;

/**
 * @author HuSen
 * create on 2019/7/24 16:24
 */
@FunctionalInterface
public interface HttpCallback extends Callback<Response> {
}
