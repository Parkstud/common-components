package concurrency;

import org.cm.boot.starter.util.ConcurrenceUtil;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.extern.slf4j.Slf4j;

/**
 * 读写锁案例
 *
 * @author parkstud@qq.com 2020-03-30
 */
@Slf4j
public class ReadWriteLockDemo {
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private int value = 0;

    public void read() {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            log.info("read value:{}", value);
        } finally {
            lock.unlock();
        }
    }

    public void write() {
        Lock lock = this.lock.writeLock();
        lock.lock();
        try {
            log.info("write :{}", ++value);
        } finally {
            lock.unlock();
        }
    }

    static class ReadThread implements Runnable {
        ReadWriteLockDemo readWriteLockDemo;

        public ReadThread(ReadWriteLockDemo readWriteLockDemo) {
            this.readWriteLockDemo = readWriteLockDemo;
        }

        @Override
        public void run() {
            readWriteLockDemo.read();
        }
    }

    static class WriteThread implements Runnable {
        ReadWriteLockDemo readWriteLockDemo;

        public WriteThread(ReadWriteLockDemo readWriteLockDemo) {
            this.readWriteLockDemo = readWriteLockDemo;
        }

        @Override
        public void run() {
            readWriteLockDemo.write();
        }
    }

    public static void main(String[] args) {
        ThreadPoolExecutor pool = ConcurrenceUtil.getDefaultThreadPoolExecutor();
        ReadWriteLockDemo readWriteLockDemo = new ReadWriteLockDemo();
        for (int i = 0; i < 10; i++) {
            pool.execute(new ReadWriteLockDemo.ReadThread(readWriteLockDemo));
            pool.execute(new ReadWriteLockDemo.ReadThread(readWriteLockDemo));
            pool.execute(new ReadWriteLockDemo.ReadThread(readWriteLockDemo));
            pool.execute(new ReadWriteLockDemo.WriteThread(readWriteLockDemo));
        }
        pool.shutdown();
    }
}
