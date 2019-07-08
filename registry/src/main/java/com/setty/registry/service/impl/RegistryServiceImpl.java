package com.setty.registry.service.impl;

import com.setty.commons.cons.JsonResultCode;
import com.setty.commons.cons.registry.Header;
import com.setty.commons.vo.registry.AppVO;
import com.setty.commons.vo.registry.LeaseInfoVO;
import com.setty.commons.vo.registry.RegistryJsonResult;
import com.setty.discovery.core.infs.LeaseManager;
import com.setty.registry.model.AppDao;
import com.setty.registry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/5 14:49
 */
@Slf4j
@Service
public class RegistryServiceImpl implements RegistryService {

    private final AppDao appDao;

    private final LeaseManager<AppVO, Long, String> leaseManager;

    @Autowired
    public RegistryServiceImpl(AppDao appDao, LeaseManager<AppVO, Long, String> leaseManager) {
        this.appDao = appDao;
        this.leaseManager = leaseManager;
    }

    @Override
    public RegistryJsonResult registry(AppVO vo, HttpServletRequest request) {
        LeaseInfoVO leaseInfo = vo.getLeaseInfo();
        long now = System.currentTimeMillis();
        // 服务第一次注册时间
        leaseInfo.setRegistrationTimestamp(now);
        // 服务标记为UP的时间
        leaseInfo.setServiceUpTimestamp(now);
        AppVO found = appDao.findByIdAndName(vo.getAppId(), vo.getInstanceName());
        if (found == null) {
            appDao.insert(vo);
        } else {
            appDao.update(vo);
        }
        // 如果带有请求头 REGISTRY_IS_REPLICATION 则说明是复制请求 不再复制给其实节点
        if (request.getHeader(Header.REGISTRY_IS_REPLICATION) == null) {
            leaseManager.register(vo, leaseInfo.getDurationInSecs(), true);
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
        LeaseInfoVO leaseInfo = found.getLeaseInfo();
        // 最后一次续约时间
        leaseInfo.setLastRenewalTimestamp(System.currentTimeMillis());
        // 该租约的剔除时间 = 最后一次续约时间 + 租约的有效时长
        leaseInfo.setEvictionTimestamp(leaseInfo.getLastRenewalTimestamp() + leaseInfo.getDurationInSecs());
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
