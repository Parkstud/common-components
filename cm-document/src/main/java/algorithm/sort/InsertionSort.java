package algorithm.sort;

/**
 * 插入排序
 *
 * @author wenqi.ma@hand-china.com 2021-04-28
 */
public class InsertionSort implements ISort {


    /**
     * 1.从第一个元素开始，该元素可以认为已经被排序；
     * 2.取出下一个元素，在已经排序的元素序列中从后向前扫描；
     * 3.如果该元素（已排序）大于新元素，将该元素移到下一位置；
     * 4.重复步骤3，直到找到已排序的元素小于或者等于新元素的位置；
     * 5.将新元素插入到该位置后；
     * 6.重复步骤2~5。
     *
     * @param arr 排序数组
     */
    private void insertionSort(int[] arr) {
        // 前一位元素下标
        int preIndex;
        // 当前元素值
        int current;
        for (int i = 1; i < arr.length; i++) {
            // 前一位元素下标
            preIndex = i - 1;
            current = arr[i];
            while (preIndex >= 0 && arr[preIndex] > current) {
                // 后移
                arr[preIndex + 1] = arr[preIndex];
                preIndex--;
            }
            // 插入元素
            arr[preIndex + 1] = current;
        }
    }

    @Override
    public void test(int[] arr) {
        insertionSort(arr);
    }


}
