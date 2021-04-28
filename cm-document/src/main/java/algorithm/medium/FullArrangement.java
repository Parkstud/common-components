package algorithm.medium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 全排列 47
 *
 * @author miao.chen01@hand-china.com 2021-04-11
 */
public class FullArrangement {
    private static LinkedList<Integer> trance = new LinkedList<>();
    private static List<List<Integer>> result = new ArrayList<>();

    public static List<List<Integer>> permuteUnique(int[] nums) {
        Arrays.sort(nums);
        getItem(nums);
        return result;
    }

    private static void getItem(int[] nums) {
        if (trance.size() == nums.length) {
            List<Integer> list = new ArrayList<>();
            for (Integer integer : trance) {
                list.add(nums[integer]);
            }
            System.out.println(trance);

            result.add(new ArrayList<>(list));
        }
        for (int i = 0; i < nums.length; i++) {
            if ((i > 0 && nums[i] == nums[i - 1] && trance.contains(i - 1))) {
                continue;
            }
            if (trance.contains(i)) {
                continue;
            }
            trance.add(i);
            permuteUnique(nums);
            trance.removeLast();
        }
    }

    public static void main(String[] args) {
        System.out.println(permuteUnique(new int[]{1,1, 1, 3}));
    }
}
