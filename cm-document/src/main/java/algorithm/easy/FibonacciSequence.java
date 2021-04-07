package algorithm.easy;

/**
 * @author miao.chen01@hand-china.com 2021-04-08
 */
public class FibonacciSequence {
    public static void main(String[] args) {
        System.out.println(test3(5));
    }

    static int test3(int n) {
        if (n == 1 || n == 2) {
            return 1;
        }
        int pre = 1;
        int next = 1;
        for (int i = 3; i <= n; i++) {
            int sum = pre + next;
            pre=next;
            next=sum;
        }
        return next;
    }


    static int test2(int n, int[] arr) {
        if (n == 1 || n == 2) {
            arr[n] = 1;
            return 1;
        }

        arr[n] = arr[n] == 0 ? test2(n - 2, arr) + test2(n - 1, arr) : arr[n];
        return arr[n];

    }

    static int test1(int n, String x) {
        System.out.println(x + n);
        if (n == 1 || n == 2) {
            return 1;
        }

        return test1(n - 2, x + '\t') + test1(n - 1, x + '\t');
    }
}
