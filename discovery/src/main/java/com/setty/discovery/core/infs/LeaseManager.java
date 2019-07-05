package com.setty.discovery.core.infs;

/**
 * @author HuSen
 * create on 2019/7/5 16:39
 */
public interface LeaseManager<R, S, T> {

    /**
     * 用于注册服务实例信息
     *
     * @param r             服务数据
     * @param leaseDuration 续约持续时间
     * @param isReplication 是否同步
     */
    void register(R r, int leaseDuration, boolean isReplication);

    /**
     * 用于删除实例信息
     *
     * @param id            ID
     * @param name          实例名称
     * @param isReplication 是否同步
     * @return 是否删除成功
     */
    boolean cancel(S id, T name, boolean isReplication);

    /**
     * 与服务中心进行心跳操作，维持租约
     *
     * @param id            ID
     * @param name          实例名称
     * @param isReplication 是否同步
     * @return 是否删除成功
     */
    boolean renewal(S id, T name, boolean isReplication);

    /**
     * evit 是 Server 端的一个方法，用于剔除租约过期的服务实例信息
     */
    void evit();
}
