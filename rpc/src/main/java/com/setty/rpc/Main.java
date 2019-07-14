package com.setty.rpc;

import com.setty.commons.vo.registry.AppVO;
import com.setty.rpc.select.SelectData;
import com.setty.rpc.select.ServiceSelector;
import com.setty.rpc.select.impl.AvgWeightRoundRobinSelector;
import com.setty.rpc.select.impl.ConsistentHashSelector;
import com.setty.rpc.select.impl.HashSelector;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HuSen
 * create on 2019/7/11 16:13
 */
@SuppressWarnings("Duplicates")
public class Main {
    public static void main(String[] args) {
        ServiceSelector selector = new ConsistentHashSelector();
        AppVO vo1 = new AppVO();
        vo1.setAppId(1L);
        vo1.setHost("127.0.0.1");
        vo1.setPort(10001);
        vo1.setInstanceName("S1");
        vo1.setLastDirtyTimestamp(System.currentTimeMillis());
        vo1.setRegion("");
        vo1.setZone("");
        vo1.setLeaseInfo(null);

        Map<String, Object> params1 = new HashMap<>(1);
        params1.put(SelectData.SIGN_COUNT, 10);
        selector.join(vo1, params1);

        AppVO vo2 = new AppVO();
        vo2.setAppId(1L);
        vo2.setHost("127.0.0.1");
        vo2.setPort(10002);
        vo2.setInstanceName("S2");
        vo2.setLastDirtyTimestamp(System.currentTimeMillis());
        vo2.setRegion("");
        vo2.setZone("");
        vo2.setLeaseInfo(null);

        Map<String, Object> params2 = new HashMap<>(1);
        params2.put(SelectData.SIGN_COUNT, 10);
        selector.join(vo2, params2);

        AppVO vo3 = new AppVO();
        vo3.setAppId(1L);
        vo3.setHost("127.0.0.1");
        vo3.setPort(10003);
        vo3.setInstanceName("S3");
        vo3.setLastDirtyTimestamp(System.currentTimeMillis());
        vo3.setRegion("");
        vo3.setZone("");
        vo3.setLeaseInfo(null);

        Map<String, Object> params3 = new HashMap<>(1);
        params3.put(SelectData.SIGN_COUNT, 10);
        selector.join(vo3, params3);

        selector.buildFinish();

        Map<String, Object> p = new HashMap<>(1);
        p.put(SelectData.SIGN_KEY, "12");
        System.out.println(selector.select(1L, p));

        AppVO vo4 = new AppVO();
        vo4.setAppId(1L);
        vo4.setHost("127.0.0.1");
        vo4.setPort(10004);
        vo4.setInstanceName("S4");
        vo4.setLastDirtyTimestamp(System.currentTimeMillis());
        vo4.setRegion("");
        vo4.setZone("");
        vo4.setLeaseInfo(null);
        Map<String, Object> params4 = new HashMap<>(1);
        params4.put(SelectData.SIGN_COUNT, 10);
        selector.join(vo4, params4);
        System.out.println(selector.select(1L, p));
    }
}
