package concurrency;

import java.util.concurrent.Exchanger;

import lombok.extern.slf4j.Slf4j;

/**
 * 交换数据
 *
 * @author parkstud@qq.com 2020-03-29
 */
public class ExchangeDemo {
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        new MakeString(exchanger);
        new UseString(exchanger);

    }
}

/**
 * 线程 制造数据
 */
@Slf4j
class MakeString implements Runnable {
    Exchanger<String> exchanger;
    String str="";

    public MakeString(Exchanger<String> exchanger) {
        this.exchanger = exchanger;
        new Thread(this).start();
    }

    @Override
    public void run() {
        char ch = 'A';
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                str += ch++;
            }

            try {
                str = exchanger.exchange(str);
            } catch (InterruptedException e) {
                log.error("MakeString ", e);
            }
        }
    }
}

/**
 * 使用数据
 */
@Slf4j
class UseString implements Runnable {
    Exchanger<String> exchanger;
    String str;

    public UseString(Exchanger<String> exchanger) {
        this.exchanger = exchanger;
        new Thread(this).start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            try {
                str = exchanger.exchange("");
                log.info("Get :{}", str);
            } catch (InterruptedException e) {
                log.error("UseString ", e);
            }
        }
    }
}
