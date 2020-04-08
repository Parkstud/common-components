package algorithm.easy;

/**
 * 先上后下数组 , 找最大
 *
 * @author parkstud@qq.com 2020-04-08
 */
public class FindArrayMax {


    private static int max = 0;

    public static void findMax(int[] arr, int left, int right) {
        if (left >= right || left > arr.length || right > arr.length) {
            return;
        }
        int media = (left + right) / 2;
        int mediaVal = arr[media];
        if (mediaVal > max) {
            max = mediaVal;
        }

        if (media - 1 < 0) {
            return;
        }
        if (media + 1 >= arr.length) {
            return;
        }
        // 上升
        if (mediaVal >= arr[media - 1] && mediaVal <= arr[media + 1]) {
            findMax(arr, media, right);
        }

        // 下降
        if (mediaVal <= arr[media - 1] && mediaVal >= arr[media + 1]) {
            findMax(arr, left, media);
        }


    }

    public static void main(String[] args) {
        int[] a = {1, 3, 4, 5, 7, 8, 10, 32, 56, 57,100, 65, 43, 21, 9, 8, 4, 1};
        findMax(a, 0, a.length - 1);
        System.out.println(max);

    }
}
