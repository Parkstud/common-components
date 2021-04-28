package algorithm.sort;

/**
 * 选择排序
 *
 * @author wenqi.ma@hand-china.com 2021-04-28
 */
public class SelectionSort implements ISort {

    /**
     * 选择最小或最大放第一个,再从未排序元素中找
     *
     * @param arr 排序数组
     */
    private void selectionSort(int[] arr) {
        // 最小元素下标
        int minIndex;
        for (int i = 0; i < arr.length; i++) {
            minIndex = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            SortHelper.swap(arr, i, minIndex);
        }
    }

    @Override
    public void test(int[] arr) {
        selectionSort(arr);
    }
}
