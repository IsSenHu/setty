package com.setty.discovery.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Sets;
import com.setty.commons.cons.JsonResultCode;
import com.setty.commons.util.http.OkHttpUtil;
import com.setty.commons.util.result.ResultEqualUtil;
import com.setty.commons.vo.JsonResult;
import com.setty.commons.vo.registry.AppVO;
import com.setty.discovery.core.infs.LookupService;
import com.setty.discovery.properties.DiscoveryProperties;
import com.setty.discovery.vo.AppInstance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

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

    private Set<String> getTargetUrls() {
        // 优先获取自己所属的zone的服务
        String zone = dp.getZone();
        Assert.isTrue(StringUtils.isNotBlank(zone), "region is empty");
        Map<String, String> serviceUrl = dp.getServiceUrl();
        String urlStr = serviceUrl.get(zone);
        Assert.isTrue(StringUtils.isNotBlank(urlStr), zone + "'s urls is empty");
        String[] urls = urlStr.split(",");
        return new HashSet<>(Sets.newHashSet(urls));
    }

    @Override
    public List<AppVO> getApplication(Long id) {
        Map<String, AppVO> nameMap = new HashMap<>(16);
        getTargetUrls().forEach(url -> {
            if (url.endsWith(URL_SPLIT)) {
                url = url + "apps/" + id;
            } else {
                url = url + URL_SPLIT + "apps/" + id;
            }
            getApplication(nameMap, url);
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
    public List<AppInstance> getApplications() {
        Map<String, AppVO> nameMap = new HashMap<>(16);
        List<AppInstance> ret = new ArrayList<>();
        getTargetUrls().forEach(url -> {
            if (url.endsWith(URL_SPLIT)) {
                url = url + "apps";
            } else {
                url = url + URL_SPLIT + "apps";
            }
            getApplication(nameMap, url);
        });
        nameMap.forEach((name, app) -> ret.add(new AppInstance(app.getAppId(), app)));
        return ret;
    }

    @Override
    public AppVO getNextServerFromEureka(String virtualHostname, boolean secure) {
        return null;
    }
}
