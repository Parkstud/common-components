package algorithm.sort;

/**
 * 希尔排序
 *
 * @author wenqi.ma@hand-china.com 2021-04-28
 */
public class ShellSort implements ISort {

    /**
     * 选择一个增量序列t1，t2，…，tk，其中ti>tj，tk=1；
     * 按增量序列个数k，对序列进行k 趟排序；
     * 每趟排序，根据对应的增量ti，将待排序列分割成若干长度为m 的子序列，分别对各子表进行直接插入排序。仅增量因子为1 时，整个序列作为一个表来处理，表长度即为整个序列的长度。
     *
     * @param arr 排序数组
     */
    private void shellSort(int[] arr) {
        // 增量gap
        for (int gap = arr.length / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < arr.length; i++) {
                int j = i;
                while (j - gap >= 0 && arr[j] < arr[j - gap]) {
                    SortHelper.swap(arr, j, j - gap);
                    j = j - gap;
                }
            }
        }
    }


    @Override
    public void test(int[] arr) {
        shellSort(arr);
    }
}
