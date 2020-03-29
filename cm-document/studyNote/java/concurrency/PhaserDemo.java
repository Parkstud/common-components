package concurrency;

import java.util.concurrent.Phaser;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author parkstud@qq.com 2020-03-29
 */
@Slf4j
public class PhaserDemo {
    public static void main(String[] args) {
        // 1个主线程
        Phaser phaser = new Phaser(1);
        int curPhase;
        log.info("Starting.....");

        new MyThread(phaser,"A");
        new MyThread(phaser,"B");
        new MyThread(phaser,"C");

        // 等待线程完成第一阶段
        curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        log.info("Phase {} Complete",curPhase);

        // 等待线程完成第二阶段
        curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        log.info("Phase {} Complete",curPhase);

        curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        log.info("Phase {} Complete",curPhase);

        //注销主线程
        phaser.arriveAndDeregister();

        if(phaser.isTerminated()){
            log.info("The phaser is terminated");
        }
    }

    static class MyThread implements Runnable {

        private Phaser phaser;
        private String name;

        public MyThread(Phaser phaser, String name) {
            this.phaser = phaser;
            this.name = name;
            phaser.register();
            new Thread(this).start();
        }

        @SneakyThrows
        @Override
        public void run() {
            log.info(" Thread {} Beginning Phase One",name);
            phaser.arriveAndAwaitAdvance();
            //暂停一下以防止输出混乱，这仅用于说明，Phaser的正确操作不需要
            Thread.sleep(10);
            log.info(" Thread {} Beginning Phase Two",name);
            phaser.arriveAndAwaitAdvance();

            //暂停一下以防止输出混乱，这仅用于说明，Phaser的正确操作不需要
            Thread.sleep(10);
            log.info(" Thread {} Beginning Phase Three",name);
            phaser.arriveAndAwaitAdvance();
        }
    }
}
