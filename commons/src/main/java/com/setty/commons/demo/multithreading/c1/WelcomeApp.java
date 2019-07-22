package com.setty.commons.demo.multithreading.c1;

/**
 * 线程优先级 默认与父类相同
 * 线程类别 默认与父类相同
 *
 * NEW
 * RUNNABLE
 * BLOCKED
 * WAITING
 * TIMED_WAITING
 * TERMINATED
 *
 * 竞态
 *
 * 原子性
 * <p>
 * 不可分割：访问某个共享变量的操作从其执行线程以外的任何线程来看，该操作要么已经执行结束要么尚未发生，即其他线程不会看到该操作执行了部分的中间效果
 * 访问同一组共享变量的原子操作时不能被交错的 例如加和减属于一组操作
 * </p>
 * Java语言对变量的写操作原子性 读操作原子性
 *
 * 可见性
 * 缓存一致性协议
 * Java语言规范保证，父线程在启动子线程之前对共享变量的更新对于子线程来说是可见的
 * Java语言规范保证，一个线程终止后该线程对共享变量的更新对于调用该线程的join方法而言是可见的
 * 查看JIT编译器动态生成的汇编代码
 *
 * 有序性
 * 貌似串行语义
 * 保证感知顺序与源代码顺序一致
 *
 * 上下文切换的开销和测量
 * 1.保存和恢复上下文
 * 2.线程调度器进行线程调度的开销
 *
 * 线程活性故障
 * 1.死锁 Deadlock
 * 2.锁死 Lockout
 * 3.活锁 Livelock
 * 4.饥饿 Starvation
 *
 * 6.锁泄漏 Lock Leak 无法释放锁
 *
 * 资源争用于调度
 * 一次只能被一个线程占用的资源称为排他性(Exclusive)资源
 * 理想是高并发、低争用
 *
 * 非公平策略 申请者申请资源时间偏差可能较大 可能导致饥饿现象 吞吐率较大
 * 公平策略 时间偏差小 不会导致饥饿现象 吞吐率小
 *
 * 内部锁 非公平锁 不会导致锁泄漏
 * 外部锁 都支持
 *
 * 在Java平台中，锁的获得隐含着刷新处理器缓存这个动作，而锁的释放隐含着冲刷处理器缓存这个动作
 *
 * 读锁对于读线程来说起到保护其访问的共享变量在其访问期间不被修改的作用，并使多个线程可以同时读取这些变量从而提高了并发性；
 * 写锁保障了写线程能够以独占的方式安全地更新共享变量
 *
 * 读写锁适用场景
 * 只读操作比写操作要频繁得多
 * 读线程持有锁的时间比较长
 *
 * volatile关键字
 *  不会被编译器分配到寄存器进行存储，对volatile变量的读写操作都是内存访问
 *  保证可见性和有序性 保证long/double型变量读写操作的原子性
 *  没有锁的排他性
 *  不会引起上下文切换
 *
 *  volatile关键字在可见性方面仅仅是保证读线程能够读取到共享变量的相对新值
 *  对于引用型变量和数组变量，volatile关键字并不能保证读线程能够读取到相应对象的字段、元素的相对新值
 *
 *  避免在构造器中将this作为方法参数传递给其他方法
 *
 *  CPU密集型线程 Ncpu或者Ncpu + 1
 *  I/O密集型线程
 *
 *  基于数据的分割
 *  基于任务的分割
 *
 * @author HuSen
 * create on 2019/7/22 10:37
 */
public class WelcomeApp {
    public static void main(String[] args) {
        Thread welcomeThread = new WelcomeThread();
        // 多次调用start => IllegalThreadStateException
        welcomeThread.start();
        System.out.printf("1.Welcome! I'm %s.%n", Thread.currentThread().getName());
    }

    private static volatile Object instance;

    public static Object instance() {
        if (instance == null) {
            synchronized (WelcomeApp.class) {
                if (instance == null) {
                    instance = new Object();
                }
            }
        }
        return instance;
    }
}

class WelcomeThread extends Thread {
    @Override
    public void run() {
        System.out.printf("2.Welcome! I'm %s.%n", Thread.currentThread().getName());
    }
}
