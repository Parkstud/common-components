package concurrency;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 可重入锁案例
 *
 * @author parkstud@qq.com 2020-03-29
 */
public class ReentrantLockDemo {
    public static void main(String[] args) {

    }

    static class Shared {
        static int count = 0;
    }

    static class LockThread implements Runnable {

        private String name;
        private ReentrantLock lock;

        public LockThread(String name, ReentrantLock lock) {
            this.name = name;
            this.lock = lock;
        }

        @Override
        public void run() {

        }
    }
}
