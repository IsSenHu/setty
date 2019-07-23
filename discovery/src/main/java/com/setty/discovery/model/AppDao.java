package com.setty.discovery.model;

import com.google.common.collect.Lists;
import com.setty.commons.vo.registry.AppVO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * HashMap的线程安全问题 put 之后 get 获取不到
 *
 * @author HuSen
 * create on 2019/7/5 14:54
 */
public class AppDao {

    public AppVO findByIdAndName(Long appId, String instanceName) {
        ConcurrentMap<String, AppVO> map = Database.TB_APP.get(appId);
        return map != null ? map.get(instanceName) : null;
    }

    public void insert(AppVO vo) {
        ConcurrentMap<String, AppVO> map = Database.TB_APP.computeIfAbsent(vo.getAppId(), k -> new ConcurrentHashMap<>());
        map.put(vo.getInstanceName(), vo);
    }

    public void update(AppVO vo) {
        delete(vo.getAppId(), vo.getInstanceName());
        insert(vo);
    }

    public void delete(Long appId, String instanceName) {
        ConcurrentMap<String, AppVO> map = Database.TB_APP.get(appId);
        if (map != null) {
            map.remove(instanceName);
        }
    }

    public List<AppVO> findAll() {
        List<AppVO> ret = new ArrayList<>();
        Database.TB_APP.values().forEach(map -> ret.addAll(map.values()));
        return ret;
    }

    public List<AppVO> findByAppId(Long appId) {
        if (Database.TB_APP.containsKey(appId)) {
            return new ArrayList<>(Database.TB_APP.get(appId).values());
        }
        return Lists.newArrayList();
    }

    private static class Database {
        private static final ConcurrentMap<Long, ConcurrentMap<String, AppVO>> TB_APP = new ConcurrentHashMap<>();
    }
}
