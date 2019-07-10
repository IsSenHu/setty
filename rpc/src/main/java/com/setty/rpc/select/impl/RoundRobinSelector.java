package com.setty.rpc.select.impl;

import com.setty.commons.vo.registry.AppVO;
import com.setty.rpc.select.ServiceSelector;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 轮询算法选择服务
 *
 * @author HuSen
 * create on 2019/7/10 18:53
 */
public class RoundRobinSelector implements ServiceSelector {

    private final Map<Long, Map<Integer, String>> ID_SEQ_NAME_MAPPING = new HashMap<>();

    private final Map<Long, AtomicLong> ID_CURRENT_SEQ_MAPPING = new HashMap<>();

    private boolean buildFinished = false;

    public void join(AppVO vo) {
        Map<Integer, String> map = ID_SEQ_NAME_MAPPING.computeIfAbsent(vo.getAppId(), k -> new TreeMap<>());
        map.put(map.size(), vo.getInstanceName());
    }

    public void buildFinish() {
        ID_SEQ_NAME_MAPPING.keySet().forEach(id -> ID_CURRENT_SEQ_MAPPING.put(id, new AtomicLong(0)));
        buildFinished = true;
    }

    public boolean validate() {
        return buildFinished;
    }

    @Override
    public String select(Long appId) {
        AtomicLong atomicLong = ID_CURRENT_SEQ_MAPPING.get(appId);
        Map<Integer, String> seqName = ID_SEQ_NAME_MAPPING.get(appId);
        long current = atomicLong.getAndIncrement();
        int size = seqName.size();
        long l = current % size;
        return seqName.get((int) l);
    }
}