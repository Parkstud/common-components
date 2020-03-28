package concurrency;

import java.util.concurrent.Semaphore;

import lombok.extern.slf4j.Slf4j;

/**
 * 信号量通过计数器控制对共享资源的访问,如果计数器大于0,允许访问,如果是0拒绝访问
 *
 * @author parkstud@qq.com 2020-03-28
 */
@Slf4j
public class SemaphoreDemo {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(1);
        new IncThread("A", semaphore);
        new DecThread("B", semaphore);
    }
}

/**
 * 共享资源
 */
class Shared {
    static int count = 0;
}

/**
 * 线程一 增加值
 */
@Slf4j
class IncThread implements Runnable {
    String name;
    Semaphore semaphore;

    public IncThread(String name, Semaphore semaphore) {
        this.name = name;
        this.semaphore = semaphore;
        new Thread(this).start();
    }

    @Override
    public void run() {
        log.info("starting : {}", name);

        try {
            log.info("{} is waitting for a permit.", name);
            semaphore.acquire();
            log.info("{} get a permit.", name);

            for (int i = 0; i < 5; i++) {
                Shared.count++;
                log.info("{} : {}", name, Shared.count);
                Thread.sleep(10);
            }
            log.info("{} release the permit", name);
            semaphore.release();
        } catch (InterruptedException e) {
            log.error("IncThread 中断", e);
        }
    }
}

/**
 * 线程二
 * 减少值
 */
@Slf4j
class DecThread implements Runnable {
    String name;
    Semaphore semaphore;

    public DecThread(String name, Semaphore semaphore) {
        this.name = name;
        this.semaphore = semaphore;
        new Thread(this).start();
    }

    @Override
    public void run() {
        log.info("Starting {}", name);

        log.info("{} is waiting for a permit", name);
        try {
            semaphore.acquire();
            log.info("{} get a permit.", name);

            for (int i = 0; i < 5; i++) {
                Shared.count--;
                log.info("{} : {}", name, Shared.count);

                Thread.sleep(10);
            }

            log.info("{} releases the permit", name);
            semaphore.release();
        } catch (InterruptedException e) {
            log.error("DecThread 中断", e);
        }
    }
}
