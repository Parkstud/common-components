package concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;

/**
 * 返回任务使用
 *
 * @author parkstud@qq.com 2020-03-29
 */
@Slf4j
public class CallAbleDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(3);
        Future<Integer> sum = es.submit(new Sum(10));
        log.info("sum:{}",sum.get());
        Future<Double> hypot = es.submit(new Hypot(3D, 4D));
        log.info("hypot:{}",hypot.get());
        Future<Integer> factorial = es.submit(new Factorial(5));
        log.info("factorial:{}",factorial.get());
        es.shutdown();
        log.info("Done.....");
    }


    /**
     * 计算阶乘
     */
    static class Factorial implements Callable<Integer> {
        private Integer stop;

        public Factorial(Integer stop) {
            this.stop = stop;
        }

        @Override
        public Integer call() throws Exception {
            int  fact=1;
            for (int i = 2; i <=stop; i++) {
                fact*=i;
            }
            return fact;
        }
    }

    /**
     * 计算斜长
     */
    static class Hypot implements Callable<Double> {
        private Double side1, side2;

        public Hypot(Double side1, Double side2) {
            this.side1 = side1;
            this.side2 = side2;
        }

        @Override
        public Double call() throws Exception {
            return Math.sqrt(side1 * side1 + side2 * side2);
        }
    }

    /**
     * 计算和
     */
    static class Sum implements Callable<Integer> {

        private Integer stop;

        public Sum(Integer stop) {
            this.stop = stop;
        }

        @Override
        public Integer call() throws Exception {
            int sum = 0;
            for (int i = 1; i <= stop; i++) {
                sum += i;
            }
            return sum;
        }
    }
}
