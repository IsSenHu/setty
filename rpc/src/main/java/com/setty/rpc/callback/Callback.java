package com.setty.rpc.callback;

/**
 * 会调函数
 *
 * @author HuSen
 * create on 2019/7/4 9:51
 */
public interface Callback<T> {

    /**
     * 执行回调
     *
     * @param t 回调参数
     */
    void execute(T t);
}
