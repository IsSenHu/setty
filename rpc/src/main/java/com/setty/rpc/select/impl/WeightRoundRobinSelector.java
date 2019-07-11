package com.setty.rpc.select.impl;

import com.setty.commons.util.math.MathUtil;
import com.setty.commons.vo.registry.AppVO;
import com.setty.rpc.select.ServiceSelector;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 带权重的轮询算法
 *
 * @author HuSen
 * create on 2019/7/11 14:47
 */
public class WeightRoundRobinSelector implements ServiceSelector {

    private final Map<Long, AtomicInteger> ID_INDEX = new HashMap<>();

    private final Map<Long, Integer[]> ID_WEIGHTS = new HashMap<>();

    private final Map<Long, Map<Integer, String>> ID_WEIGHT_NAME = new HashMap<>();

    private final Map<Long, AtomicInteger> ID_CUR_WEIGHT = new HashMap<>();

    private final Map<Long, Integer> ID_MAX_WEIGHT = new HashMap<>();

    /**
     * 最大公约数
     */
    private final Map<Long, Integer> ID_GCD = new HashMap<>();

    @Override
    public String select(Long appId) {
        AtomicInteger index = ID_INDEX.get(appId);
        if (index.get() == -1) {
            index.incrementAndGet();
        }
        Integer[] weights = ID_WEIGHTS.get(appId);
        Integer gcd = ID_GCD.get(appId);
        AtomicInteger currentWeight = ID_CUR_WEIGHT.get(appId);
        if (index.get() == weights.length) {
            index.set(0);
            int ncw = currentWeight.addAndGet(-gcd);
            if (ncw == 0) {
                currentWeight.set(ID_MAX_WEIGHT.get(appId));
            }
        }
        int i;
        while ((i = index.getAndIncrement()) < weights.length) {
            Integer weight = weights[i];
            if (weight >= currentWeight.get()) {
                return ID_WEIGHT_NAME.get(appId).get(weight);
            }
        }
        return null;
    }

    @Override
    public void join(AppVO vo, Map<String, Object> params) {
        Integer weight = (Integer) params.get(SIGN_WEIGHT);
        Long appId = vo.getAppId();
        ID_INDEX.put(appId, new AtomicInteger(-1));
        Integer oldVal = ID_MAX_WEIGHT.computeIfAbsent(appId, k -> 0);
        if (weight > oldVal) {
            ID_MAX_WEIGHT.put(appId, weight);
        }
        Map<Integer, String> map = ID_WEIGHT_NAME.computeIfAbsent(appId, k -> new TreeMap<>());
        map.put(weight, vo.getInstanceName());
    }

    @Override
    public void buildFinish() {
        ID_WEIGHT_NAME.forEach((id, wn) -> {
            ID_CUR_WEIGHT.put(id, new AtomicInteger(ID_MAX_WEIGHT.get(id)));
            Integer[] integers = wn.keySet().toArray(new Integer[0]);
            ID_WEIGHTS.put(id, integers);
        });
        calGcd();
    }

    /**
     * 计算最大公约数
     */
    private void calGcd() {
        ID_WEIGHTS.forEach((id, weights) -> {
            if (weights.length > 1) {
                TreeSet<Integer> treeSet = new TreeSet<>();
                for (int i = 0; i < weights.length - 1; i++) {
                    int gcd = MathUtil.gcd(weights[i], weights[i + 1]);
                    treeSet.add(gcd);
                }
                ID_GCD.put(id, treeSet.first());
            }
        });
    }
}
