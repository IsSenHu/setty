package com.setty.discovery.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.setty.commons.cons.JsonResultCode;
import com.setty.commons.util.http.OkHttpUtil;
import com.setty.commons.util.result.ResultEqualUtil;
import com.setty.commons.vo.JsonResult;
import com.setty.commons.vo.registry.AppVO;
import com.setty.discovery.core.infs.LookupService;
import com.setty.discovery.properties.DiscoveryProperties;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author HuSen
 * create on 2019/7/5 18:38
 */
public class DefaultLookupServiceImpl implements LookupService<AppVO, Long> {

    private static final String URL_SPLIT = "/";

    private final DiscoveryProperties dp;

    public DefaultLookupServiceImpl(DiscoveryProperties dp) {
        this.dp = dp;
    }

    @Override
    public List<AppVO> getApplication(Long id) {
        Map<String, String> serviceUrl = dp.getServiceUrl();
        if (MapUtils.isEmpty(serviceUrl)) {
            return Lists.newArrayList();
        }
        Map<String, AppVO> nameMap = new HashMap<>(16);
        serviceUrl.values().forEach(urls -> {
            String[] split = urls.split(",");
            for (String url : split) {
                if (url.endsWith(URL_SPLIT)) {
                    url = url + "apps/" + id;
                } else {
                    url = url + URL_SPLIT + "apps/" + id;
                }
                getApplication(nameMap, url);
            }
        });
        return new ArrayList<>(nameMap.values());
    }

    private void getApplication(Map<String, AppVO> nameMap, String url) {
        String resp = OkHttpUtil.getSync(url, null);
        JsonResult result;
        if (StringUtils.isNotBlank(resp) && ResultEqualUtil.equal((result = JSON.parseObject(resp, JsonResult.class)), JsonResultCode.SUCCESS)) {
            JSONArray jsonArray = (JSONArray) result.getData();
            List<AppVO> apps = jsonArray.toJavaList(AppVO.class);
            for (AppVO app : apps) {
                AppVO exist = nameMap.get(app.getInstanceName());
                // 如果为空 直接放入 否则 判断 选择最新的
                if (Objects.isNull(exist) || exist.getLastDirtyTimestamp() < app.getLastDirtyTimestamp()) {
                    nameMap.put(app.getInstanceName(), app);
                }
            }
        }
    }

    @Override
    public List<Map<Long, AppVO>> getApplications() {
        Map<String, String> serviceUrl = dp.getServiceUrl();
        if (MapUtils.isEmpty(serviceUrl)) {
            return Lists.newArrayList();
        }
        Map<String, AppVO> nameMap = new HashMap<>(16);
        List<Map<Long, AppVO>> ret = new ArrayList<>();
        serviceUrl.values().forEach(urls -> {
            String[] split = urls.split(",");
            for (String url : split) {
                if (url.endsWith(URL_SPLIT)) {
                    url = url + "apps";
                } else {
                    url = url + URL_SPLIT + "apps";
                }
                getApplication(nameMap, url);
            }
        });
        nameMap.forEach((name, app) -> {
            Map<Long, AppVO> map = new HashMap<>(1);
            map.put(app.getAppId(), app);
            ret.add(map);
        });
        return ret;
    }

    @Override
    public AppVO getNextServerFromEureka(String virtualHostname, boolean secure) {
        return null;
    }
}
