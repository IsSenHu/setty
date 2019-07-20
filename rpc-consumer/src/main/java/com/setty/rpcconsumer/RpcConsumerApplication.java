package com.setty.rpcconsumer;

import com.setty.discovery.annotation.EnableDiscoveryClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author HuSen
 */
@EnableDiscoveryClient
@SpringBootApplication
public class RpcConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcConsumerApplication.class, args);
    }
}
