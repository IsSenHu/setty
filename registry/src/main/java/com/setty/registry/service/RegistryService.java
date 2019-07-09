package com.setty.registry.service;

import com.setty.commons.vo.JsonResult;
import com.setty.commons.vo.registry.AppVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author HuSen
 * create on 2019/7/5 14:49
 */
public interface RegistryService {

    /**
     * 注册新的应用实例
     *
     * @param vo      应用实例数据模型
     * @param request HttpServletRequest
     * @return 注册结果
     */
    JsonResult registry(AppVO vo, HttpServletRequest request);

    /**
     * 注销应用实例
     *
     * @param appId        appId
     * @param instanceName 实例名称
     * @return 注销结果
     */
    JsonResult logout(Long appId, String instanceName);

    /**
     * 应用实例发送心跳 instance 不存在 返回404
     *
     * @param appId        appId
     * @param instanceName 实例名称
     * @param request      HttpRequest
     * @return 注销结果
     */
    JsonResult renewal(Long appId, String instanceName, HttpServletRequest request);

    /**
     * 查询所有实例
     *
     * @return 所有实例
     */
    JsonResult<List<AppVO>> findAll();

    /**
     * 查询指定appId的实例
     *
     * @param appId appId
     * @return 指定 appId 的实例
     */
    JsonResult<List<AppVO>> findByAppId(Long appId);

    /**
     * 根据指定appId和instanceId查询
     *
     * @param appId        appId
     * @param instanceName 实例名称
     * @return 实例
     */
    JsonResult<AppVO> findByIdAndName(Long appId, String instanceName);
}
