package com.setty.discovery.core;

import com.setty.commons.vo.registry.AppVO;
import com.setty.discovery.core.infs.LookupService;

import java.util.List;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/5 18:38
 */
public class DefaultLookupServiceImpl implements LookupService<AppVO, Long> {

    @Override
    public List<AppVO> getApplication(Long id) {
        return null;
    }

    @Override
    public List<Map<Long, AppVO>> getApplications() {
        return null;
    }

    @Override
    public AppVO getNextServerFromEureka(String virtualHostname, boolean secure) {
        return null;
    }
}
