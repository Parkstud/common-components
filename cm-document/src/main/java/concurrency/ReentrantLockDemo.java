package concurrency;

import org.cm.boot.starter.util.BaseConcurrenceUtil;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 可重入锁案例
 *
 * @author parkstud@qq.com 2020-03-29
 */
@Slf4j
public class ReentrantLockDemo {
    public static void main(String[] args) {

        ThreadPoolExecutor poolExecutor = BaseConcurrenceUtil.getDefaultThreadPoolExecutor();
        Lock lock=new ReentrantLock();
        poolExecutor.execute(new LockThread("A", lock));
        poolExecutor.execute(new LockThread("B", lock));
        poolExecutor.execute(new LockThread("C", lock));
        poolExecutor.execute(new LockThread("D", lock));
        poolExecutor.shutdown();
    }

    static class Shared {
        static int count = 0;
    }

    static class LockThread implements Runnable {

        private String name;
        private Lock lock;

        public LockThread(String name, Lock lock) {
            this.name = name;
            this.lock = lock;
        }

        @SneakyThrows
        @Override
        public void run() {
            log.info("Starting...{}",name);
            log.info("{} is waiting to lock count.",name);
            lock.lock();
            try {
                log.info("{} is locking count.",name);
                Shared.count++;
                log.info("{} : {}",name,Shared.count);

                log.info("{} Thread is sleeping.",name);
                Thread.sleep(1000);

            }finally {
                lock.unlock();
                // !!! 必须在finally中释放锁
                log.info("{} is unlocking count.",name);

            }
        }
    }
}
