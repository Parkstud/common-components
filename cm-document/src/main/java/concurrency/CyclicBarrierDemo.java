package concurrency;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import lombok.extern.slf4j.Slf4j;

/**
 * 循环屏障
 *
 * @author parkstud@qq.com 2020-03-29
 */
@Slf4j
public class CyclicBarrierDemo {
    public static void main(String[] args) throws InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, new CyclicBarrierAction());
        log.info("Starting......");

        new MyThread(cyclicBarrier, "A");
        new MyThread(cyclicBarrier, "B");
        new MyThread(cyclicBarrier, "C");

        Thread.sleep(100);
        new MyThread(cyclicBarrier, "X");
        new MyThread(cyclicBarrier, "Y");
        new MyThread(cyclicBarrier, "Z");

    }

    static class MyThread implements Runnable {
        private CyclicBarrier cyclicBarrier;
        private String name;

        public MyThread(CyclicBarrier cyclicBarrier, String name) {
            this.cyclicBarrier = cyclicBarrier;
            this.name = name;
            new Thread(this).start();
        }

        @Override
        public void run() {
            log.info("name :{}", name);
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                log.error("MyThread error", e);
            }
        }
    }
}

/**
 * CyclicBarrier 完毕 执行对象
 */
@Slf4j
class CyclicBarrierAction implements Runnable {

    @Override
    public void run() {
        log.info("CyclicBarrierAction start....");
    }
}


