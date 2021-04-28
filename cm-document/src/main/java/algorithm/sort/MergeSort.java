package algorithm.sort;

import java.util.Arrays;

/**
 * 归并
 *
 * @author wenqi.ma@hand-china.com 2021-04-28
 */
public class MergeSort implements ISort {

    /**
     * 递归归并
     *
     * @param arr 排序数组
     * @return 排序结果
     */
    private int[] mergeSort(int[] arr) {
        if (arr.length <= 1) {
            return arr;
        }
        int middle = arr.length / 2;
        int[] left = Arrays.copyOfRange(arr, 0, middle);
        int[] right = Arrays.copyOfRange(arr, middle, arr.length);
        return merge(mergeSort(left), mergeSort(right));
    }

    /**
     * 合并有序数组
     *
     * @param left  左数组
     * @param right 右数组
     * @return
     */
    private int[] merge(int[] left, int[] right) {
        int[] result = new int[left.length + right.length];
        int l = 0;
        int r = 0;
        int res = 0;
        while (l < left.length && r < right.length) {
            if (left[l] < right[r]) {
                result[res++] = left[l++];
            } else {
                result[res++] = right[r++];
            }
        }
        while (l < left.length) {
            result[res++] = left[l++];
        }
        while (r < right.length) {
            result[res++] = right[r++];
        }
        return result;
    }

    @Override
    public void test(int[] arr) {
        int[] result = mergeSort(arr);
        System.arraycopy(result, 0, arr, 0, arr.length);
    }
}
