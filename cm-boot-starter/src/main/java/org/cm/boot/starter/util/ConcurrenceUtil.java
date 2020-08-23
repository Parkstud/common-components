package org.cm.boot.starter.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 并发工具类
 *
 * @author parkstud@qq.com 2020-03-29
 */
public interface ConcurrenceUtil {


    /**
     * 获取公共线程池
     *
     * @return 公共线程池
     */
    static ForkJoinPool getCommonPool() {
        return ForkJoinPool.commonPool();
    }

    /**
     * 手动创建线程池 不要使用Executors
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   非核心线程空闲存活时间
     * @param unit            时间单位
     * @param workQueue       队列 (需要声明一个有界队列)
     * @param threadFactory   线程工厂 (推荐) 使用guava的ThreadFactoryBuilder
     * @return 线程池
     */
    static ThreadPoolExecutor getThreadPoolExecutor(int corePoolSize,
                                                    int maximumPoolSize,
                                                    long keepAliveTime,
                                                    TimeUnit unit,
                                                    BlockingQueue<Runnable> workQueue,
                                                    ThreadFactory threadFactory) {
        return new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue, threadFactory);
    }


    /**
     * 获取默认线程池
     *
     * @return 线程池
     */
    static ThreadPoolExecutor getDefaultThreadPoolExecutor() {
        return new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10240), new ThreadFactoryBuilder().setNameFormat("default-%d").build());
    }

}
