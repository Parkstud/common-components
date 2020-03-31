package concurrency;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author parkstud@qq.com 2020-03-31
 */
@Slf4j
public class CompletableFutureDemo {

    /**
     * handle方法即可以处理异常也可以处理正常结果
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void handle() throws ExecutionException, InterruptedException {
        Integer a = CompletableFuture.supplyAsync(() -> {
            log.warn(Thread.currentThread().getName());
            sleepOneSeconds();
            int i = 10 / new Random().nextInt(0);
            return new Random().nextInt(10);
        }).handle((param, throwable) -> {
            int result = -1;
            if (throwable == null) {
                result = param * 2;
            } else {
                log.info(Thread.currentThread().getName() + "handle 出现异常", throwable);
            }
            return result;
        }).join();
        log.info("result:{}", a);
    }

    public static void thenApply() {
        String ok = CompletableFuture.supplyAsync(() -> {
            log.warn(Thread.currentThread().getName());
            sleepOneSeconds();
            long result = new Random().nextInt(0);
            log.info("result:{}", result);
            return result;
        }).thenApply(o -> {
            //            long res = 5 / 0;
            log.info("res : ");
            return "";
        }).whenComplete((s, throwable) -> log.info("receive s:{}", s)).exceptionally(e -> {
            log.error(Thread.currentThread().getName() + " 执行失败:e", e);
            return null;
        }).join();
    }

    public static void whenComplete() throws InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            sleepOneSeconds();
            int i = 0 / 0;
            log.info("{} run end.....", Thread.currentThread().getName());
        });

        // 不管如何都会调用这个方法
        future.whenComplete((aVoid, throwable) -> log.info("执行完成!"));

        future.exceptionally(throwable -> {
            // 有异常 这个执行
            log.info("执行失败", throwable);
            return null;
        });
        //主线程等待
        TimeUnit.SECONDS.sleep(2);
    }

    /**
     * 无返回值
     */
    public static void runAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            sleepOneSeconds();
            log.info("{} run end.....", Thread.currentThread().getName());
        });
        // 不调用,不执行
        future.get();
    }

    /**
     * 有返回值
     *
     * @throws ExecutionException   e
     * @throws InterruptedException e
     */
    public static void supplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> future1 = CompletableFuture.supplyAsync(() -> {
            sleepOneSeconds();
            log.info("{} run end.....", Thread.currentThread().getName());
            return System.currentTimeMillis();
        });

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            sleepOneSeconds();
            log.info("{} run end.....", Thread.currentThread().getName());
        });
        CompletableFuture<Void> future2 = CompletableFuture.allOf(future1, future);
        future2.join();
    }

    private static void sleepOneSeconds() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error("sleepOneSeconds InterruptedException", e);
        }
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        supplyAsync();
    }


}

