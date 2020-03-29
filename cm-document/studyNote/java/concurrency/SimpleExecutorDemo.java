package concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程池简单使用
 *
 * @author parkstud@qq.com 2020-03-29
 */
@Slf4j
public class SimpleExecutorDemo {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch a = new CountDownLatch(5);
        CountDownLatch b = new CountDownLatch(5);
        CountDownLatch c = new CountDownLatch(5);
        CountDownLatch d = new CountDownLatch(5);

        ExecutorService es = Executors.newFixedThreadPool(2);

        log.info("Starting.......");

        es.execute(new MyThread("A",a));
        es.execute(new MyThread("B",b));
        es.execute(new MyThread("C",c));
        es.execute(new MyThread("D",d));

        a.await();
        b.await();
        c.await();
        d.await();

        // 完成线程池中的任务后关闭线程池
        es.shutdown();
        log.info("Done......");


    }

    static class MyThread implements Runnable {
        private String name;
        private CountDownLatch countDownLatch;

        public MyThread(String name, CountDownLatch countDownLatch) {
            this.name = name;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                log.info("name {} : {}", name, i);
                countDownLatch.countDown();
            }
        }
    }
}
