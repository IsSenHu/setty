package com.setty.rpc.core.select.impl;

import com.setty.commons.vo.registry.AppVO;
import com.setty.rpc.core.select.ServiceSelector;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * 一致性Hash算法
 * 单调性
 * 分散性
 * 平衡性
 *
 * @author HuSen
 * create on 2019/7/12 10:17
 */
public class ConsistentHashSelector implements ServiceSelector {

    private final Map<Long, NavigableMap<Integer, AppVO>> idApps = new HashMap<>();

    private boolean finish = false;

    @Override
    public String select(Long appId, Map<String, Object> params) {
        String key = (String) params.get(SIGN_KEY);
        int target = myHashCode(key);
        NavigableMap<Integer, AppVO> apps = idApps.get(appId);
        NavigableMap<Integer, AppVO> tailMap = apps.tailMap(target, true);
        if (tailMap.size() == 0) {
            return apps.firstEntry().getValue().getInstanceName();
        } else {
            return tailMap.firstEntry().getValue().getInstanceName();
        }
    }

    @Override
    public void join(AppVO vo, Map<String, Object> params) {
        Integer duplications = (Integer) params.get(SIGN_COUNT);
        NavigableMap<Integer, AppVO> map = idApps.computeIfAbsent(vo.getAppId(), k -> new TreeMap<>());
        String baseRouteKey = vo.getInstanceName();
        for (Integer i = 1; i <= duplications; i++) {
            String routeKey = baseRouteKey.concat("#").concat(String.valueOf(i));
            int hashCode =  myHashCode(routeKey);
            map.put(hashCode, vo);
        }
    }

    @Override
    public void buildFinish() {
        finish = true;
    }

    @Override
    public boolean validate() {
        return finish;
    }

    private static Integer myHashCode(String str) {
        return DigestUtils.md5Hex(str).hashCode() & Integer.MAX_VALUE;
    }
}
