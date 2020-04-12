package jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试OOM异常  Xms 堆最小值 , Xmx 堆最大值 HeapDumpOnOutOfMemoryError 出现OOM异常时,Dump当前内存快照
 * VM args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 * -XX:+HeapDumpOnOutOfMemoryError
 * -XX:HeapDumpPath=C:\Users\chen\Desktop\temp
 *
 * @author parkstud@qq.com 2020-04-11
 */
public class HeapOOM {

    public static void main(String[] args) {
        test1();
    }

    private static void test2() {
        String append = new StringBuilder("计算").append("机").toString();
        String intern = append.intern();
        System.out.println(intern == append);

        String string = new StringBuilder("ja").append("va").toString();
        String intern1 = string.intern();
        System.out.println(string == intern1);
    }

    private static void test1() {
        List<HeapOOM> ooms = new ArrayList<>();
        while (true) {
            ooms.add(new HeapOOM());
        }
    }
}
