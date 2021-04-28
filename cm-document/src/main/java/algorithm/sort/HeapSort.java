package algorithm.sort;

/**
 * 堆排序
 *
 * @author wenqi.ma@hand-china.com 2021-04-28
 */
public class HeapSort implements ISort {

    /**
     * 堆排序
     * （1）根据初始数组去构造初始堆（构建一个完全二叉树，保证所有的父结点都比它的孩子结点数值大）。
     * （2）每次交换第一个和最后一个元素，输出最后一个元素（最大值），然后把剩下元素重新调整为大根堆
     *
     * @param arr 排序数组
     */
    private void heapSort(int[] arr) {
        // 初始堆
        for (int i = arr.length / 2; i >= 0; i--) {
            headAdjust(arr, i, arr.length-1);
        }
        // 进行n-1次循环，完成排序
        for (int i = arr.length; i > 0; i--) {
            // 最后一个元素和第一元素进行交换
            SortHelper.swap(arr, i-1, 0);
            headAdjust(arr, 0, i-1);
        }
    }

    /**
     * 调整堆元素
     *
     * @param arr         排序数组
     * @param parentIndex 父节点下标
     * @param length      长度
     */
    private void headAdjust(int[] arr, int parentIndex, int length) {
        // 父节点
        int parent = arr[parentIndex];
        // 左孩子
        int child = 2 * parentIndex + 1;

        while (child < length) {
            // 如果有右孩子结点，并且右孩子结点的值大于左孩子结点，则选取右孩子结点
            if (child + 1 < length && arr[child] < arr[child + 1]) {
                child++;
            }
            // 父节点 值大于子节点 结束
            if (parent >= arr[child]) {
                break;
            }
            // 把孩子结点的值赋给父结点
            arr[parentIndex] = arr[child];

            // 孩子左节点 向下筛选
            parentIndex = child;
            child = 2 * child + 1;
        }
        arr[parentIndex] = parent;
    }

    @Override
    public void test(int[] arr) {
        heapSort(arr);
    }
}
