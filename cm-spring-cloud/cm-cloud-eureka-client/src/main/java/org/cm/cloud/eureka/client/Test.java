package org.cm.cloud.eureka.client;

import java.util.ArrayList;
import java.util.Spliterator;

/**
 * @author parkstud@qq.com 2020-04-27
 */
public class Test {
    public static void main(String[] args) {
        ArrayList<Integer> list = new ArrayList<>();
        ArrayList<Integer> remove = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        remove.add(2);
        remove.add(4);
        Spliterator<Integer> spliterator = list.spliterator();
        Spliterator<Integer> a = spliterator.trySplit();
        Spliterator<Integer> b = a.trySplit();

        spliterator.tryAdvance(o -> o=o+1);
        int characteristics = spliterator.characteristics();
        System.out.println(list);
    }
}
