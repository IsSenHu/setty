package com.setty.discovery.core.infs;

import java.util.List;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/5 16:52
 */
public interface LookupService<R, S> {

    /**
     * 根据ID获取所有应用信息
     *
     * @param id id
     * @return 对应ID的所有应用信息
     */
    List<R> getApplication(S id);

    /**
     * 获取所有的应用信息
     *
     * @return 所有应用信息
     */
    List<Map<S, R>> getApplications();

    /**
     * 根据 virtualHostname 使用 round-robin 方式获取下一个服务实例的方法
     *
     * @param virtualHostname virtualHostname
     * @param secure          安全
     * @return 一个服务实例
     */
    R getNextServerFromEureka(String virtualHostname, boolean secure);
}
