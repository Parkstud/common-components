package algorithm.easy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 算法之最快到达终点问题
 * <p>
 * 给定一个正整数数组，每个元素大小表示从该元素出发最多可移动几个节点。
 * 假设总是从第一个元素开始移动。问如何移动可以以最少的步数移动到最后。
 *
 * @author miao.chen01@hand-china.com 2020-03-28
 */
public class ReachDestination {
    //    3,4,2,1,3,1
    static List<Integer> min = Arrays.asList(3,4,2,1,3,1);

    public static List<Integer> move(int[] array, List<Integer> path, int walk) {
        if (walk >= array.length - 1) {
            if (min.size() >= path.size()) {
                min = new ArrayList<>(path);
            }
            return path;
        }
        int n = array[walk];
        for (int i = 1; i <= n; i++) {
            if (walk + i > array.length - 1) {
                break;
            }
            path.add(walk + i);
            move(array, path, walk + i);
            path.remove(path.size() - 1);
        }
        return min;

    }

    public static void main(String[] args) {
        int[] array = {3,4,2,1,3,1};
        ArrayList<Integer> path = new ArrayList<>();
        path.add(0);
        move(array, path, 0);

        System.out.println(min);
    }
}
