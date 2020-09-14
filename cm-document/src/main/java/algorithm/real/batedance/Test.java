package algorithm.real.batedance;

import java.util.HashSet;

/**
 * @author parkstud@qq.com 2020-08-23
 */
public class Test {
    final int a=0;

    public Test() {
        //第一种
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                System.out.println("xxx");
            }
        };
        new Thread(runnable).start();

        //第二种
        new Thread(){
            @Override
            public void run() {
                System.out.println("xxx");
            }
        }.start();

        //同步方式synchronized

    }

    public static void main(String[] args) {
        HashSet<Integer> integers = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            integers.add(i);
            integers.remove(i-1);
        }
        System.out.println(integers);
    }
}
