package com.setty.rpc.select;

/**
 * @author HuSen
 * create on 2019/7/10 18:51
 */
public interface ServiceSelector {

    /**
     * 选择服务实例
     *
     * @param appId appId
     * @return 服务实例
     */
    String select(Long appId);
}
