package com.setty.commons.demo.multithreading.c1;

/**
 * @author HuSen
 * create on 2019/7/22 10:41
 */
public class WelcomeApp1 {
    public static void main(String[] args) {
        Thread thread = new Thread(new WelcomeTask());
        thread.start();
        System.out.printf("1.Welcome! I'm %s.%n", Thread.currentThread().getName());
    }
}

class WelcomeTask implements Runnable {

    @Override
    public void run() {
        System.out.printf("2.Welcome! I'm %s.%n", Thread.currentThread().getName());
    }
}
