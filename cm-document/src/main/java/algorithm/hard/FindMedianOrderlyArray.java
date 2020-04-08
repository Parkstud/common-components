package algorithm.hard;

/**
 * 寻找两个有序数组的中位数
 * <p>
 * 给定两个大小为 m 和 n 的有序数组 nums1 和 nums2。
 * <p>
 * 请你找出这两个有序数组的中位数，并且要求算法的时间复杂度为 O(log(m + n))。
 * <p>
 * 你可以假设 nums1 和 nums2 不会同时为空。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/median-of-two-sorted-arrays
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * https://www.nowcoder.com/discuss/196951
 * @author parkstud@qq.com 2020-04-03
 */
public class FindMedianOrderlyArray {

    /**
     * 数组1的长度m, 数组2的长度n
     * 中位数 求法 x=(m+n+1)/2  , y=(m+n+2)/2  ----> (x+y)/2
     * 求第x,y大的数 k 就是中位数
     * 对k进行二分划分
     *
     * @param nums1 数组1
     * @param nums2 数组2
     * @return 中位数
     */
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int l = (nums1.length + nums2.length + 1) / 2;
        int r = (nums1.length + nums2.length + 2) / 2;
        return (getKValue(nums1, 0, nums2, 0, l) + getKValue(nums1, 0, nums2, 0, r)) / 2;
    }

    /**
     * 获取第k大的数据
     *
     * @param num1   数组1
     * @param start1 数组1开始
     * @param num2   数组2
     * @param start2 数组2开始
     * @param k      第k大
     * @return
     */
    public double getKValue(int[] num1, int start1, int[] num2, int start2, int k) {
        // 数组越界处理
        if (start1 > num1.length - 1) {
            return num2[start2 + k - 1];
        }
        if (start2 > num2.length - 1) {
            return num1[start1 + k - 1];
        }

        // 第一大的元素
        if (k == 1) {
            return Math.min(num1[start1], num2[start2]);
        }


        int media1 = start1 + k / 2 - 1 < num1.length ? num1[start1+k / 2 - 1] : Integer.MAX_VALUE;
        int media2 = start2 + k / 2 - 1 < num2.length ? num2[start2+k / 2 - 1] : Integer.MAX_VALUE;
        if (media1 < media2) {
            return getKValue(num1, start1 + k / 2 , num2, start2, k - k / 2);
        } else {
            return getKValue(num1, start1, num2, start2 + k / 2 , k - k / 2);
        }
    }

    public static void main(String[] args) {
        FindMedianOrderlyArray arr = new FindMedianOrderlyArray();
        int[] nums1={1,2};
        int[] num2={-1,3};
        System.out.println(arr.findMedianSortedArrays(num2,nums1));
    }
}
