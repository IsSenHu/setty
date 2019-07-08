package com.setty.registry.controller;

import com.setty.commons.vo.registry.AppVO;
import com.setty.commons.vo.registry.RegistryJsonResult;
import com.setty.registry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/5 14:17
 */
@RestController
@RequestMapping("/api/registry")
public class RegistryController {

    private final RegistryService registryService;

    @Autowired
    public RegistryController(RegistryService registryService) {
        this.registryService = registryService;
    }

    /**
     * 注册新的应用实例
     *
     * @param vo      应用实例数据模型
     * @param request HttpRequest
     * @return 注册结果
     */
    @PostMapping
    public RegistryJsonResult registry(@RequestBody AppVO vo, HttpServletRequest request) {
        return registryService.registry(vo, request);
    }

    /**
     * 注销应用实例
     *
     * @param appId        appId
     * @param instanceName 实例名称
     * @return 注销结果
     */
    @DeleteMapping("/{appId}/{instanceName}")
    public RegistryJsonResult logout(@PathVariable("appId") Long appId, @PathVariable("instanceName") String instanceName) {
        return registryService.logout(appId, instanceName);
    }

    /**
     * 应用实例发送心跳 instance 不存在 返回404
     *
     * @param appId        appId
     * @param instanceName 实例名称
     * @return 注销结果
     */
    @PutMapping("/{appId}/{instanceName}")
    public RegistryJsonResult renewal(@PathVariable("appId") Long appId, @PathVariable("instanceName") String instanceName) {
        return registryService.renewal(appId, instanceName);
    }

    /**
     * 查询所有实例
     *
     * @return 所有实例
     */
    @GetMapping("/apps")
    public RegistryJsonResult<List<AppVO>> findAll() {
        return registryService.findAll();
    }

    /**
     * 查询指定appId的实例
     *
     * @param appId appId
     * @return 指定 appId 的实例
     */
    @GetMapping("/apps/{appId}")
    public RegistryJsonResult<List<AppVO>> findByAppId(@PathVariable("appId") Long appId) {
        return registryService.findByAppId(appId);
    }

    /**
     * 根据指定appId和instanceId查询
     *
     * @param appId        appId
     * @param instanceName 实例名称
     * @return 实例
     */
    @GetMapping("/apps/{appId}/{instanceName}")
    public RegistryJsonResult<AppVO> findByIdAndName(@PathVariable("appId") Long appId, @PathVariable("instanceName") String instanceName) {
        return registryService.findByIdAndName(appId, instanceName);
    }

    // 暂停应用实例
    // 恢复应用实例
    // 更新元数据
    // 根据vip地址查询
    // 根据svip地址查询
}
