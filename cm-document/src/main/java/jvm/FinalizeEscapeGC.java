package jvm;

/**
 * 1. 对象可以在gc时自救
 * 2. finallize 方法只能救一次
 *
 * @author parkstud@qq.com 2020-04-13
 */
public class FinalizeEscapeGC {
    private static FinalizeEscapeGC finalizeEscapeGC;

    public void Active() {
        System.out.println("I am  active");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("finalize method run");
        finalizeEscapeGC = this;
    }

    public static void main(String[] args) throws Throwable {
        finalizeEscapeGC = new FinalizeEscapeGC();
        finalizeEscapeGC = null;
        System.gc();
        Thread.sleep(500);
        if (finalizeEscapeGC == null) {
            System.out.println("i am dead");
        } else {
            finalizeEscapeGC.Active();
        }
        finalizeEscapeGC = null;
        System.gc();
        Thread.sleep(500);
        if (finalizeEscapeGC == null) {
            System.out.println("i am dead");
        } else {
            finalizeEscapeGC.Active();
        }
    }
}
