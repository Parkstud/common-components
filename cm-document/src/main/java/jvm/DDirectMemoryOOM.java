package jvm;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * VM args: -Xmx20m -XX:MaxDirectMemorySize=10M
 *
 * @author parkstud@qq.com 2020-04-12
 */
public class DDirectMemoryOOM {
    private static final int INT_1MB = 1024 * 1024;

    public static void main(String[] args) throws IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        while (true) {
            unsafe.allocateMemory(INT_1MB);
        }


    }
}
