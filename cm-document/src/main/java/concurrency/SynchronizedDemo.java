package concurrency;

import org.cm.boot.starter.util.BaseConcurrenceUtil;

import java.util.concurrent.ThreadPoolExecutor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * synchronized 测试
 *
 * @author parkstud@qq.com 2020-03-30
 */
@Slf4j
public class SynchronizedDemo implements Runnable {
    private static int count =0;

    public void func1() {
        synchronized (this){
            count++;
        }

    }

    public static void main(String[] args) {
        ThreadPoolExecutor pool = BaseConcurrenceUtil.getDefaultThreadPoolExecutor();
        // 多个实例对象锁,锁不了
        SynchronizedDemo A = new SynchronizedDemo();
        SynchronizedDemo B = new SynchronizedDemo();
        SynchronizedDemo C = new SynchronizedDemo();
        SynchronizedDemo D = new SynchronizedDemo();
        for (int i = 0; i <2000 ; i++) {
            pool.execute(A);
            pool.execute(B);
            pool.execute(C);
            pool.execute(D);
        }
        pool.shutdown();
        while (!pool.isTerminated()){

        }
        log.info("count:{}",count);
    }

    @SneakyThrows
    @Override
    public void run() {
        func1();
    }
}
