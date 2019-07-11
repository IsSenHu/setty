package com.setty.rpc.select.impl;

import com.setty.commons.vo.registry.AppVO;
import com.setty.rpc.select.ServiceSelector;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 带权重的轮询算法 均匀
 *
 * @author HuSen
 * create on 2019/7/11 17:42
 */
public class AvgWeightRoundRobinSelector implements ServiceSelector {

    private final Map<Long, List<Server>> idServers = new HashMap<>();

    private final Map<Long, Integer> idTotal = new HashMap<>();

    private boolean finish = false;

    @Override
    public String select(Long appId) {
        Integer now = idTotal.get(appId);
        List<Server> servers = idServers.get(appId);
        long l = System.nanoTime();
        for (Server server : servers) {
            server.currentWeight += server.weight;
        }
        // 选择目前 currentWeight 最大的服务
        Server server = servers.stream().sorted((x, y) -> y.currentWeight - x.currentWeight).collect(Collectors.toList()).get(0);
        server.currentWeight -= now;
        return server.name;
    }

    @Override
    public void join(AppVO vo, Map<String, Object> params) {
        Integer weight = (Integer) params.get(SIGN_WEIGHT);
        Long appId = vo.getAppId();
        Server server = new Server(vo.getInstanceName(), weight, 0);
        List<Server> servers = idServers.computeIfAbsent(appId, k -> new ArrayList<>());
        servers.add(server);
        Integer integer = idTotal.computeIfAbsent(appId, k -> 0);
        integer += weight;
        idTotal.put(appId, integer);
    }

    @Override
    public void buildFinish() {
        finish = true;
    }

    @Override
    public boolean validate() {
        return finish;
    }

    @AllArgsConstructor
    private final static class Server {
        private String name;
        private Integer weight;
        private Integer currentWeight;
    }
}
