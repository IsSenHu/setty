package com.setty.discovery.vo;

import com.setty.commons.vo.registry.AppVO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author HuSen
 * create on 2019/7/20
 */
@Getter
@Setter
@AllArgsConstructor
public class AppInstance {
    private Long appId;
    private AppVO appVO;
}
