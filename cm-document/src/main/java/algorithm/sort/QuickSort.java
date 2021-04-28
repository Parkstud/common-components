package algorithm.sort;

/**
 * 快速排序
 *
 * @author wenqi.ma@hand-china.com 2021-04-28
 */
public class QuickSort implements ISort {

    /**
     * 快速排序
     *
     * @param arr 排序数组
     */
    private void quickSort(int[] arr) {
        quickSort(arr, 0, arr.length - 1);
    }

    private void quickSort(int[] arr, int left, int right) {
        if (left < right) {
            int i = left;
            int j = right;
            int p = arr[i];
            while (i != j) {
                // 基数
                // 右->左边第一小于p的数
                while (j > i && arr[j] > p) {
                    j--;
                }
                arr[i]=arr[j];
                // 左->右边第一个大于p的数
                if (i < j && arr[i] < p) {
                    i++;
                }
                arr[j]=arr[i];
            }
            arr[i] = p;
            quickSort(arr, left, i - 1);
            quickSort(arr, i + 1, right);
        }
    }

    @Override
    public void test(int[] arr) {
        quickSort(arr);
    }
}
