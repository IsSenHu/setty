package com.setty.commons.vo.registry;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author HuSen
 * create on 2019/7/5 14:23
 */
@Data
public class AppVO implements Serializable {

    private static final long serialVersionUID = -824871707854206406L;

    // appId
    // host
    // port
    // instanceName

    private Long appId;
    private String host;
    private Integer port;
    private String instanceName;
    private Long lastDirtyTimestamp;

    /**
     * 租约信息
     * */
    private LeaseInfoVO leaseInfo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        AppVO appVO = (AppVO) o;
        return Objects.equals(appId, appVO.appId) &&
                Objects.equals(host, appVO.host) &&
                Objects.equals(port, appVO.port) &&
                Objects.equals(instanceName, appVO.instanceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, host, port, instanceName);
    }
}
