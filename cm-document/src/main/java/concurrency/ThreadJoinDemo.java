package concurrency;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * Thread join方法测试
 *
 * @author parkstud@qq.com 2020-03-30
 */
@Slf4j
public class ThreadJoinDemo {
    private static class A extends Thread {
        @Override
        public void run() {
            log.info("A");
        }
    }

    private static class B extends Thread {
        private A a;

        public B(A a) {
            this.a = a;
        }

        @SneakyThrows
        @Override
        public void run() {
            a.join();
            log.info("B");
        }
    }

    public static void main(String[] args) {
        A a = new A();
        new B(a).start();
        a.start();
    }
}
