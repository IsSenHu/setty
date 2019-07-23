package com.setty.commons.demo.multithreading.c1;

/**
 * 去掉 volatile 运行几次 会出现一次死循环
 *
 * @author HuSen
 * create on 2019/7/23 19:43
 */
public class VolatileDemo {

    static volatile boolean status;

    static int i;

    public static void main(String[] args) throws InterruptedException {
        new Thread(new TimeConsumingTask()).start();
        Thread.sleep(1000);
        new Thread(new SwapRunnable()).start();
    }

    static class TimeConsumingTask implements Runnable {

        @Override
        public void run() {
            long now = System.currentTimeMillis();
            while (true) {
                System.out.println(i++);
                if (status == !status) {
                    System.out.println("exit");
                    System.out.println("-------" + (System.currentTimeMillis() - now));
                    System.exit(0);
                }
            }
        }
    }

    static class SwapRunnable implements Runnable {

        @Override
        public void run() {
            while (true) {
                status = !status;
            }
        }
    }
}
