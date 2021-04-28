package algorithm.sort;

import java.util.Arrays;

/**
 * @author wenqi.ma@hand-china.com 2021-04-28
 */
public class SortHelper {
    /**
     * 排序默认数组
     */
    protected static int[] DEFAULT_ARRAY = new int[]{38, 44, 3, 5, 15, 2, 26, 4, 19, 27, 36, 46, 50, 47, 48};

    /**
     * 数组交换x,y下标的元素
     *
     * @param arr 数组
     * @param x   x下标
     * @param y   y下标
     */
    public static void swap(int[] arr, int x, int y) {
        int temp = arr[x];
        arr[x] = arr[y];
        arr[y] = temp;
    }

    /**
     * 排序方法测试
     *
     * @param sort 排序
     */
    public static void test(ISort sort) {
        System.out.println("------------排序前-------");
        System.out.println(Arrays.toString(SortHelper.DEFAULT_ARRAY));
        sort.test(DEFAULT_ARRAY);
        System.out.println();
        System.out.println("------------排序后-------");
        System.out.println(Arrays.toString(SortHelper.DEFAULT_ARRAY));
    }
}
