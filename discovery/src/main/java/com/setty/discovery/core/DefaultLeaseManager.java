package com.setty.discovery.core;

import com.setty.commons.vo.registry.AppVO;
import com.setty.discovery.core.infs.LeaseManager;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 默认的租约管理器
 *
 * @author HuSen
 * create on 2019/7/5 17:04
 */
@Slf4j
public class DefaultLeaseManager implements LeaseManager<AppVO, Long, String> {

    private final List<String> registryEndpoints;

    public DefaultLeaseManager(List<String> registryEndpoints) {
        this.registryEndpoints = registryEndpoints;
    }

    @Override
    public void register(AppVO vo, int leaseDuration, boolean isReplication) {

    }

    @Override
    public boolean cancel(Long id, String name, boolean isReplication) {
        return false;
    }

    @Override
    public boolean renewal(Long id, String name, boolean isReplication) {
        return false;
    }

    @Override
    public void evit() {

    }
}
