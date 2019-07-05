package com.setty.registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 1满足AP
 *
 * 2对等复制 Peer to Peer
 *
 * 3在单个 Eureka Server 启动的时候，会有一个 syncUp 的操作，通过 Eureka Client 请求其他 Eureka Server 节点中的一个节点获取注册的应用实例信息，然后复制到其他的peer节点
 *
 * 4lastDirtyTimestamp 标识 当 lastDirtyTimestamp 不为空时就会进行相应的处理 大于本地 表示 Eureka Server 之间的数据出现冲突，返回404，要求应用实例重新进行 register 操作
 * 小于本地 如果是 peer 节点的复制请求，则表示数据出现冲突，返回 409 给 peer 节点，要求同步自己最新的数据信息
 *
 * 5peer 节点之间的相互复制并不能保证所有操作都能够成功，因此 Eureka 还通过应用实例与 Server 之间的heartbeat也就是 renewalLease操作来进行数据的最终修复，即如果发现应用实例数据与某个 Server 的数据出现不一致
 * 则 Server 返回404，应用实例重新进行 register 操作
 *
 * 6Region AvailabilityZone 一对多 每个 Region 之间相互独立及隔离，默认情况下资源只在单个 Region 之间的 AvailabilityZone 进行复制
 * AvailabilityZone 可以看做 Region下的一个个机房，各个机房相对独立 主要是为了 Region 下的高可用
 * 一个 AvailabilityZone 可以设置多个 Eureka Server 实例，它们之间构成 peer 节点，然后采用 Peer to Peer 的复制模式
 *
 * 7Self Preservation Client 与 Server 端之间有个租约，Client 要定时发送心跳来维持这个续约，表示自己还存活着。Eureka 通过 当前注册的实例数，去计算每分钟应该从应用实例接收到的心跳数
 * 如果小于指定的阀值，则关闭租约失效剔除，禁止定时任务提示失效的实例，从而保护注册信息
 *
 * @author HuSen
 */
@SpringBootApplication
public class RegistryApplication {
    public static void main(String[] args) {
        SpringApplication.run(RegistryApplication.class, args);
    }
}
