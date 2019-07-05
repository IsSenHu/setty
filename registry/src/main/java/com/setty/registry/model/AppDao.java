package com.setty.registry.model;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.setty.commons.vo.registry.AppVO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/5 14:54
 */
@Repository
public class AppDao {

    public AppVO findByIdAndName(Long appId, String instanceName) {
        return Database.TB_APP.get(appId, instanceName);
    }

    public void insert(AppVO vo) {
        Database.TB_APP.put(vo.getAppId(), vo.getInstanceName(), vo);
    }

    public void update(AppVO vo) {
        delete(vo.getAppId(), vo.getInstanceName());
        insert(vo);
    }

    public void delete(Long appId, String instanceName) {
        Database.TB_APP.remove(appId, instanceName);
    }

    public List<AppVO> findAll() {
        return new ArrayList<>(Database.TB_APP.values());
    }

    public List<AppVO> findByAppId(Long appId) {
        if (Database.TB_APP.containsRow(appId)) {
            Map<String, AppVO> row = Database.TB_APP.row(appId);
            return new ArrayList<>(row.values());
        } else {
            return Lists.newArrayList();
        }
    }

    private static class Database {
        private static final Table<Long, String, AppVO> TB_APP = HashBasedTable.create();
    }
}
