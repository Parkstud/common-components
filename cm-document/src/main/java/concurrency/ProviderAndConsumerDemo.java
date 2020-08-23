package concurrency;

import org.cm.boot.starter.util.ConcurrenceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 多消费者生产者案例
 *
 * @author parkstud@qq.com 2020-03-29
 */
public class ProviderAndConsumerDemo<T> {
    /**
     * 锁
     */
    public static final Lock LOCK = new ReentrantLock();
    /**
     * 队列为null 需要通知消费这生产
     */
    public static final Condition EMPTY = LOCK.newCondition();
    /**
     * 队列满,通知消费者生产
     */
    public static final Condition FULL = LOCK.newCondition();

    /**
     * 队列最大的大小
     */
    public static final int SIZE = 100;

    /**
     * 生产和消费队列
     */
    private List<T> queue = new ArrayList<>();

    public static void main(String[] args) {
        ThreadPoolExecutor poolExecutor = ConcurrenceUtil.getDefaultThreadPoolExecutor();
        ProviderAndConsumerDemo<DateInfo> demo = new ProviderAndConsumerDemo<>();
        poolExecutor.execute(new Provider<>("Provider-A",demo.queue));
        poolExecutor.execute(new Provider<>("Provider-B",demo.queue));
        poolExecutor.execute(new Provider<>("Provider-C",demo.queue));

        poolExecutor.execute(new Consumer<>("Consumer-D",demo.queue));
        poolExecutor.execute(new Consumer<>("Consumer-E",demo.queue));
        poolExecutor.execute(new Consumer<>("Consumer-F",demo.queue));

        poolExecutor.shutdown();

    }

}

/**
 * 数据信息 , 每个数据有一个编号
 */
@Data
class DateInfo {
    private Integer num;

    public DateInfo(Integer num) {
        this.num = num;
    }
}

@Slf4j
class Provider<T> implements Runnable {
    private static AtomicInteger count = new AtomicInteger();
    private String name;
    private List<T> queue;

    public Provider(String name, List<T> queue) {
        this.name = name;
        this.queue = queue;
    }

    @SneakyThrows
    @Override
    public void run() {
        ProviderAndConsumerDemo.LOCK.lock();
        try {
            log.info("{} is providing", name);
            while (queue.size() >= ProviderAndConsumerDemo.SIZE) {
                ProviderAndConsumerDemo.FULL.await();
            }
            DateInfo dateInfo = new DateInfo(count.addAndGet(1));
            queue.add((T) dateInfo);
            ProviderAndConsumerDemo.EMPTY.signalAll();
        } finally {
            ProviderAndConsumerDemo.LOCK.unlock();
        }

    }
}

@Slf4j
class Consumer<T> implements Runnable {
    private String name;
    private List<T> queue;

    public Consumer(String name, List<T> queue) {
        this.name = name;
        this.queue = queue;
    }

    @SneakyThrows
    @Override
    public void run() {
        ProviderAndConsumerDemo.LOCK.lock();
        try {
            log.info("{} is consumer...", name);
            while (queue.isEmpty()) {
                ProviderAndConsumerDemo.EMPTY.await();
            }
            DateInfo info = (DateInfo) queue.remove(0);
            System.out.println(info);
            ProviderAndConsumerDemo.FULL.signalAll();
        } finally {
            ProviderAndConsumerDemo.LOCK.unlock();
        }

    }
}