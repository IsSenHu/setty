package com.setty.rpc.select.impl;

import com.setty.commons.util.math.MathUtil;
import com.setty.commons.vo.registry.AppVO;
import com.setty.rpc.select.ServiceSelector;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 带权重的轮询算法 不均匀
 *
 * @author HuSen
 * create on 2019/7/11 14:47
 */
public class WeightRoundRobinSelector implements ServiceSelector {

    private final Map<Long, AtomicInteger> ID_INDEX = new HashMap<>();

    private final Map<Long, Integer[]> ID_WEIGHTS = new HashMap<>();

    private final Map<Long, List<Map<String, Integer>>> ID_NAME_WEIGHT_LIST = new HashMap<>();

    private final Map<Long, AtomicInteger> ID_CUR_WEIGHT = new HashMap<>();

    private final Map<Long, Integer> ID_MAX_WEIGHT = new HashMap<>();

    private boolean finish = false;

    /**
     * 最大公约数
     */
    private final Map<Long, Integer> ID_GCD = new HashMap<>();

    @Override
    public String select(Long appId) {
        List<Map<String, Integer>> list = ID_NAME_WEIGHT_LIST.get(appId);
        AtomicInteger index = ID_INDEX.get(appId);
        if (index.get() == -1) {
            index.incrementAndGet();
        }
        Integer[] weights = ID_WEIGHTS.get(appId);
        Integer gcd = ID_GCD.get(appId);
        AtomicInteger currentWeight = ID_CUR_WEIGHT.get(appId);
        if (index.get() == list.size()) {
            index.set(0);
            int ncw = currentWeight.addAndGet(-gcd);
            if (ncw == 0) {
                currentWeight.set(ID_MAX_WEIGHT.get(appId));
            }
        }
        int i;
        while ((i = index.getAndIncrement()) < list.size()) {
            Map<String, Integer> map = list.get(i);
            Integer weight = map.values().iterator().next();
            if (weight >= currentWeight.get()) {
                return map.keySet().iterator().next();
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
        List<Map<String, Integer>> list = ID_NAME_WEIGHT_LIST.computeIfAbsent(appId, k -> new ArrayList<>());
        Map<String, Integer> map = new HashMap<>(1);
        map.put(vo.getInstanceName(), weight);
        list.add(map);
    }

    @Override
    public void buildFinish() {
        ID_NAME_WEIGHT_LIST.forEach((id, wn) -> {
            ID_CUR_WEIGHT.put(id, new AtomicInteger(ID_MAX_WEIGHT.get(id)));
            // 返回的集合是排好序的 源集合不排序
            wn = wn.stream().sorted(Comparator.comparingInt(x -> x.values().iterator().next())).collect(Collectors.toList());
            ID_NAME_WEIGHT_LIST.replace(id, wn);
            Integer[] integers = wn.stream().map(m -> m.values().iterator().next()).toArray(Integer[]::new);
            ID_WEIGHTS.put(id, integers);
        });
        calGcd();
        finish = true;
    }

    @Override
    public boolean validate() {
        return finish;
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
