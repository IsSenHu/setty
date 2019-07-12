package com.setty.rpc.select;

import com.setty.commons.vo.registry.AppVO;

import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/10 18:51
 */
public interface ServiceSelector extends SelectData {

    /**
     * 选择服务实例
     *
     * @param appId  appId
     * @param params 自定义参数
     * @return 服务实例
     */
    String select(Long appId, Map<String, Object> params);

    /**
     * 加入服务实例
     *
     * @param vo     服务实例
     * @param params 自定义参数
     */
    void join(AppVO vo, Map<String, Object> params);

    /**
     * 构建完成触发
     */
    void buildFinish();

    /**
     * 是否初始化完成
     *
     * @return 是否可用
     */
    boolean validate();
}
