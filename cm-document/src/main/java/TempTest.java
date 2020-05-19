import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @author parkstud@qq.com 2020-04-09
 */
public class TempTest {
    public static void main(String[] args) {
        ArrayList<Integer> arr = new ArrayList<>();
        arr.add(new Integer(1));
        arr.add(new Integer(1000));
        Integer integer = arr.get(1);
        Object[] objects = arr.toArray();
        System.out.println(integer);
        System.out.println(objects[1]);
        System.out.println(objects[1]==integer);

        Integer a=Integer.valueOf(127);
        Integer b=Integer.valueOf(127);

        Integer a1=128;
        Integer b1=128;
        int a2=128;
        System.out.println(a == b);
        System.out.println(a1 == b1);
        System.out.println(a2 == a1);


    }

    private static void test1() {
        List<Integer> arr = Arrays.asList(3, 5, 2, 7, 8, 1);
        MyPriorityQueue<Integer> mpq = new MyPriorityQueue<>(arr, null);
        Integer poll ;
        while ((poll=mpq.poll()) !=null){
            System.out.println(poll);
        }
        System.out.println(mpq);
    }
}

class MyPriorityQueue<T> {
    private Object[] queue;
    /**
     * 初始容量
     */
    private static final int DEFAULT_INITIAL_CAPACITY = 11;

    /**
     * 节点的个数(具体数据的长度)
     */
    private int size = 0;

    private final Comparator<? super T> comparator;

    public MyPriorityQueue(int initialCapacity, Comparator<? super T> comparator) {
        this.queue = new Object[initialCapacity];
        size = queue.length;
        this.comparator = comparator;
        heapify();
    }

    public MyPriorityQueue(Collection<? extends T> c, Comparator<? super T> comparator) {
        this.comparator = comparator;
        Object[] a = c.toArray();
        if (a.getClass() != Object[].class) {
            Arrays.copyOf(a, a.length, Object[].class);
        }
        if (comparator != null) {
            for (int i = 0; i < a.length; i++) {
                if (a[i] == null) {
                    throw new NullPointerException();
                }
            }
        }
        queue = a;
        size = a.length;
        if (comparator == null) {
            heapifyAble();
        } else {
            heapify();
        }

    }

    // 比较器进行
    private void heapify() {


    }

    private void heapifyAble() {
        for (int i = (size >>> 1) - 1; i >= 0; i--) {
            siftDownComparable(i, (T) queue[i]);
        }
    }

    private void siftDownComparable(int k, T o) {
        Comparable<T> key = (Comparable<T>) o;
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            Object c = queue[child];
            int right = child + 1;

            if (right < size && ((Comparable<T>) c).compareTo((T) queue[right]) > 0) {
                c = queue[child = right];
            }
            if (key.compareTo((T) c) <= 0) {
                break;
            }
            queue[k] = c;
            k = child;
        }
        queue[k] = key;
    }

    public T poll() {
        if (size == 0) {
            return null;
        }
        int s = --size;
        T result = (T) queue[0];
        T x = (T) queue[s];
        queue[s] = null;
        if (s != 0) {
            siftDownComparable(0, x);
        }
        return result;

    }

    @Override
    public String toString() {
        return "MyPriorityQueue{" +
                "queue=" + Arrays.toString(queue) +
                '}';
    }
}