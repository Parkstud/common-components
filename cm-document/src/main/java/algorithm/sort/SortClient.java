package algorithm.sort;

/**
 * 排序测试
 *
 * @author wenqi.ma@hand-china.com 2021-04-28
 */
public class SortClient {
    public static void main(String[] args) {
        // 冒泡排序
        //        SortHelper.test(new BubbleSort());

        // 选择排序
        //        SortHelper.test(new SelectionSort());

        // 插入排序
        //        SortHelper.test(new InsertionSort());

        // 希尔排序
        //        SortHelper.test(new ShellSort());

        // 归并排序
        //        SortHelper.test(new MergeSort());

        // 快速排序
        //        SortHelper.test(new QuickSort());

        // 堆排序
        SortHelper.test(new HeapSort());

    }
}
