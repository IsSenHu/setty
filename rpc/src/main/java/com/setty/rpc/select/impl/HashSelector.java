package com.setty.rpc.select.impl;

import com.setty.commons.vo.registry.AppVO;
import com.setty.rpc.select.ServiceSelector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 普通Hash算法
 *
 * @author HuSen
 * create on 2019/7/12 9:49
 */
public class HashSelector implements ServiceSelector {

    private final Map<Long, List<AppVO>> idApps = new HashMap<>();

    private boolean finish = false;

    @Override
    public String select(Long appId, Map<String, Object> params) {
        String key = (String) params.get(SIGN_KEY);
        int hashCode = key.hashCode();
        List<AppVO> vos = idApps.get(appId);
        int index = hashCode % vos.size();
        return vos.get(index).getInstanceName();
    }

    @Override
    public void join(AppVO vo, Map<String, Object> params) {
        List<AppVO> vos = idApps.computeIfAbsent(vo.getAppId(), k -> new ArrayList<>());
        vos.add(vo);
    }

    @Override
    public void buildFinish() {
        idApps.forEach((id, apps) -> {
            apps = apps.stream().sorted(Comparator.comparingInt(app -> app.getHost().concat(":").concat(app.getPort().toString()).hashCode())).collect(Collectors.toList());
            idApps.replace(id, apps);
        });
        finish = true;
    }

    @Override
    public boolean validate() {
        return finish;
    }
}
