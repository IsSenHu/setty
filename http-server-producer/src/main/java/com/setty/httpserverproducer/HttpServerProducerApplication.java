package com.setty.httpserverproducer;

import com.setty.rpc.http.annotation.EnableHttpServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author HuSen
 */
@EnableHttpServer
@SpringBootApplication
public class HttpServerProducerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(HttpServerProducerApplication.class, args);
    }

    @Override
    public void run(String... args) {

    }
}
