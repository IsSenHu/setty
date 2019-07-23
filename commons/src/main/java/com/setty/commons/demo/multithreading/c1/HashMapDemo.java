package com.setty.commons.demo.multithreading.c1;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author HuSen
 * create on 2019/7/22 20:53
 */
public class HashMapDemo {
    static void doIt() throws InterruptedException {
        final int count = 2000;
        final AtomicInteger checkNum = new AtomicInteger(0);
        ExecutorService executorService = Executors.newFixedThreadPool(200);
        final Map<Long, String> map = new HashMap<>(2);
        map.put(0L, "ssaw.com");
        for (int i = 0; i < count; i++) {
            executorService.submit(() -> {
                map.put(System.nanoTime() + new Random().nextLong(), "ssaw.com");
                String s = map.get(0L);
                if (s == null) {
                    checkNum.incrementAndGet();
                }
            });
        }
        executorService.awaitTermination(1, TimeUnit.SECONDS);
        executorService.shutdown();

        System.out.println(checkNum.get());
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            doIt();
            Thread.sleep(10L);
        }
    }
}
