package org.cm.boot.starter.util;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * 系统相关工具类
 *
 * @author parkstud@qq.com 2020-03-31
 */
public interface BaseSystemUtil {

    /**
     * 计算运行时间
     *
     * @param task 任务
     * @return 运行时间
     */
    public static long computeRuntime(Runnable task) {
        Instant start = Instant.now();
        task.run();
        Instant end = Instant.now();
        return Duration.between(start, end).toNanos();
    }

    /**
     * 计算运行时间 (可计算多线程耗费时间)
     *
     * @param task 任务
     * @return 运行时间
     */
    public static <T> long computeRuntime(Supplier<T> task) {
        Instant start = Instant.now();
        T result = task.get();
        if (result instanceof ExecutorService) {
            ExecutorService service = (ExecutorService) result;
            while (!service.isTerminated()) {

            }
        }
        Instant end = Instant.now();
        return Duration.between(start, end).toMillis();
    }
}
