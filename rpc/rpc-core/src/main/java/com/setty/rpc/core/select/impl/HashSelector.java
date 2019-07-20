package com.setty.rpc.core.select.impl;

import com.setty.commons.vo.registry.AppVO;
import com.setty.rpc.core.select.ServiceSelector;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 普通Hash算法
 *
 * 优点:简单易懂
 * 缺点:如果一个节点L宕机，那么新数据会落在 K = (M - 1) mod (N-1) 节点上，大量的数据将无法命中
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
