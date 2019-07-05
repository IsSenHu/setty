package com.setty.commons.vo.registry;

import lombok.Data;

import java.io.Serializable;

/**
 * 实例租约信息
 *
 * @author HuSen
 * create on 2019/7/5 15:55
 */
@Data
public class LeaseInfoVO implements Serializable {
    private static final long serialVersionUID = 2431213935822395539L;

    /**
     * Client 端续约的间隔周期 renewalIntervalInSecs
     */
    private Integer renewalIntervalInSecs;

    /**
     * Client 端需要设定的租约的有效时长 durationInSecs
     */
    private Integer durationInSecs;

    /**
     * Server 端设置的该租约的第一次注册时间 registrationTimestamp
     */
    private Long registrationTimestamp;

    /**
     * Server 端设置的该租约的最后一次续约时间 lastRenewalTimestamp
     */
    private Long lastRenewalTimestamp;

    /**
     * Server 端设置的该租约被剔除的时间 evictionTimestamp
     */
    private Long evictionTimestamp;

    /**
     * Server 段设置的该服务实例标记为UP的时间 serviceUpTimestamp
     */
    private Long serviceUpTimestamp;
}
