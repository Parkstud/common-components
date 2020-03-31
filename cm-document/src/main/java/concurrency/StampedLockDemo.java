package concurrency;

import org.cm.boot.starter.util.BaseConcurrenceUtil;
import org.cm.boot.starter.util.BaseSystemUtil;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.StampedLock;

import lombok.extern.slf4j.Slf4j;

/**
 * StampedLock案例
 *
 * @author parkstud@qq.com 2020-03-31
 */
@Slf4j
public class StampedLockDemo {
    private final StampedLock stampedLock = new StampedLock();
    private double x;
    private double y;

    public void move(double moveX, double moveY) {
        long stamp = stampedLock.writeLock();
        try {
            x += moveX;
            y += moveY;
            log.info("X:{}  Y:{}", x, y);
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    public double distance() {
        long stamp = stampedLock.tryOptimisticRead();
        double currentX = x;
        double currentY = y;
        if (!stampedLock.validate(stamp)) {
            // 悲观读锁
            stamp = stampedLock.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
//        log.info("currentX:{}  currentY:{}", currentX, currentY);
        return Math.sqrt(currentX * currentX + currentY + currentY);
    }

    static class MoveTask implements Runnable {
        private StampedLockDemo demo;

        public MoveTask(StampedLockDemo demo) {
            this.demo = demo;
        }

        @Override
        public void run() {
            demo.move(1, 1);
        }
    }

    static class DistanceTask implements Runnable {
        private StampedLockDemo demo;

        public DistanceTask(StampedLockDemo demo) {
            this.demo = demo;
        }

        @Override
        public void run() {
            demo.distance();
        }
    }

    public static void main(String[] args) {
        long time = BaseSystemUtil.computeRuntime(StampedLockDemo::test1);
        log.info("time:{}", time);
    }

    private static ThreadPoolExecutor test1() {
        StampedLockDemo demo = new StampedLockDemo();
        ThreadPoolExecutor pool = BaseConcurrenceUtil.getDefaultThreadPoolExecutor();
        for (int i = 0; i < 1000; i++) {
            pool.execute(new MoveTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
        }
        pool.shutdown();
        return pool;
    }

}
