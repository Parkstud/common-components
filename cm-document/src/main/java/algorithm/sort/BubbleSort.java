package algorithm.sort;

import static algorithm.sort.SortHelper.swap;

/**
 * 冒泡排序
 *
 * @author wenqi.ma@hand-china.com 2021-04-28
 */
public class BubbleSort implements ISort {

    @Override
    public void test(int[] arr) {
        bubbleSort(arr);
    }


    /**
     * 两两对比,元素交换
     *
     * @param arr 排序数组
     */
    private void bubbleSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    swap(arr, j, j + 1);
                }
            }
        }
    }

}
