package test.day1.demo1.group1;

import java.util.ArrayList;

/**
 * @author parkstud@qq.com 2020-09-15
 */
public class Demo1 {
    public static void main(String[] args) {
        ArrayList<Integer> integers = new ArrayList<>();
        integers.add(0);
        integers.add(0);
        integers.add(0);
        integers.add(1);
        integers.remove(1);
        System.out.println(integers);
    }
}
