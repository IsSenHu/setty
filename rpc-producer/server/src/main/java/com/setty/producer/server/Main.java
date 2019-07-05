package com.setty.producer.server;

import com.setty.rpc.annotation.proto.EnableProtoServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author HuSen
 * create on 2019/7/5 10:52
 */
@Slf4j
@EnableProtoServer
@SpringBootApplication
public class Main implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("app is started");
    }
}
