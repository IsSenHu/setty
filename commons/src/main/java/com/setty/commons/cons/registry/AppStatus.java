package com.setty.commons.cons.registry;

import org.apache.commons.lang3.StringUtils;

/**
 * 标识服务实例的状态
 *
 * @author HuSen
 * create on 2019/7/5 16:21
 */
public enum AppStatus {
    // 准备接收流量
    UP,
    // 不发送流量 健康监测失败
    DOWN,
    // 正在启动中
    STARTING,
    // 关闭流量 标识停止服务 即停止接收请求 处于这个状态的实例不会被路由到 经常用于升级部署的场景
    OUT_OF_SERVICE,
    // 未知状态
    UNKNOWN;

    public static AppStatus toEnum(String name) {
        if (StringUtils.isNotBlank(name)) {
            try {
                return AppStatus.valueOf(name.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
        }
        return UNKNOWN;
    }
}
