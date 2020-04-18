package jvm;

/**
 * testGC()方法执行后,ObjA和ObjB会被GC
 *
 * @author parkstud@qq.com 2020-04-12
 */
public class ReferenceCountingGC {
    public Object instance;
    /**
     * 对象占用点内存,gc能输出
     */
    private static final int INT_1MB = 1024 * 1024;

    public static void main(String[] args) {
        ReferenceCountingGC objA = new ReferenceCountingGC();
        ReferenceCountingGC objB = new ReferenceCountingGC();
        objA.instance = objB;
        objB.instance = objA;

        //gc
        System.gc();
    }
}
