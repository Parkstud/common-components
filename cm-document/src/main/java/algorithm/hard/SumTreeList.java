package algorithm.hard;


/**
 * 二叉树根节点到叶子节点的所有路径和
 * 限定语言：Kotlin、Typescript、Python、C++、Groovy、Rust、Java、Go、Scala、Javascript、Ruby、Swift、Php、Python 3
 * 给定一个仅包含数字\ 0-9 0−9 的二叉树，每一条从根节点到叶子节点的路径都可以用一个数字表示。
 * 例如根节点到叶子节点的一条路径是1\to 2\to 31→2→3,那么这条路径就用\ 123 123 来代替。
 * 找出根节点到叶子节点的所有路径表示的数字之和
 * 例如：
 * <p>
 * 这颗二叉树一共有两条路径，
 * 根节点到叶子节点的路径 1\to 21→2 用数字\ 12 12 代替
 * 根节点到叶子节点的路径 1\to 31→3 用数字\ 13 13 代替
 * 所以答案为\ 12+13=25 12+13=25
 * 示例1
 * 输入
 * {1,0}
 * 输出
 * 10
 * 示例2
 * 输入
 * {1,#,9}
 * 输出
 * 19
 *
 * @author miao.chen01@hand-china.com 2021-04-13
 */
public class SumTreeList {
    static class TreeNode {
        int value;
        TreeNode left;
        TreeNode right;

        public TreeNode(int value) {
            this.value = value;
        }
    }

    static int result = 0;

    public static int sumNumbers(TreeNode root, int sum) {
        if (root == null) {
            return 0;
        }
        int value = root.value;
        if (root.left == null && root.right == null) {
            result =result+ sum + value;
            System.out.println(sum+value);
            return result;
        }
        sum += value;
        sumNumbers(root.left, sum * 10);
        sumNumbers(root.right, sum * 10);
        return result;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(1);
        root.left=new TreeNode(0);
        root.left.left = new TreeNode(2);
        root.left.right = new TreeNode(3);
        System.out.println(sumNumbers(root, 0));

    }
}
