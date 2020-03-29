package concurrency;

import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

/**
 * 锁存器
 *
 * @author parkstud@qq.com 2020-03-28
 */
@Slf4j
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        log.info("Starting.....");
        new MyThread(countDownLatch);
        countDownLatch.await();
        log.info("Done");

    }
}

@Slf4j
class MyThread implements Runnable {
    CountDownLatch latch;

    public MyThread(CountDownLatch latch) {
        this.latch = latch;
        new Thread(this).start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            log.info("i:{}", i);
            latch.countDown();

        }
    }
}
