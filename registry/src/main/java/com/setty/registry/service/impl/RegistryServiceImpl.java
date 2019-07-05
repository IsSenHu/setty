package com.setty.registry.service.impl;

import com.setty.commons.cons.JsonResultCode;
import com.setty.commons.vo.registry.AppVO;
import com.setty.commons.vo.registry.RegistryJsonResult;
import com.setty.registry.model.AppDao;
import com.setty.registry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/5 14:49
 */
@Service
public class RegistryServiceImpl implements RegistryService {

    private final AppDao appDao;

    @Autowired
    public RegistryServiceImpl(AppDao appDao) {
        this.appDao = appDao;
    }

    @Override
    public RegistryJsonResult registry(AppVO vo) {
        AppVO found = appDao.findByIdAndName(vo.getAppId(), vo.getInstanceName());
        if (found == null) {
            appDao.insert(vo);
        } else {
            appDao.update(vo);
        }
        return new RegistryJsonResult(JsonResultCode.SUCCESS);
    }

    @Override
    public RegistryJsonResult logout(Long appId, String instanceName) {
        appDao.delete(appId, instanceName);
        return new RegistryJsonResult(JsonResultCode.SUCCESS);
    }

    @Override
    public RegistryJsonResult renewal(Long appId, String instanceName) {
        AppVO found = appDao.findByIdAndName(appId, instanceName);
        if (found == null) {
            return new RegistryJsonResult(JsonResultCode.NOT_FOUND);
        }
        // TODO 心跳监测逻辑
        return new RegistryJsonResult(JsonResultCode.SUCCESS);
    }

    @Override
    public RegistryJsonResult<List<AppVO>> findAll() {
        return new RegistryJsonResult<>(appDao.findAll());
    }

    @Override
    public RegistryJsonResult<List<AppVO>> findByAppId(Long appId) {
        return new RegistryJsonResult<>(appDao.findByAppId(appId));
    }

    @Override
    public RegistryJsonResult<AppVO> findByIdAndName(Long appId, String instanceName) {
        return new RegistryJsonResult<>(appDao.findByIdAndName(appId, instanceName));
    }
}
