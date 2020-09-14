package algorithm.real.batedance;

import java.util.Scanner;

/**
 * @author parkstud@qq.com 2020-08-23
 */
public class Test2019Demo2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int d = scanner.nextInt();
        int[] array = new int[n];
        for (int i = 0; i < n; i++) {
            array[i] = scanner.nextInt();
        }
        int j = 0;
        long result = 0;
        for (int i = 0; i < n; i++) {
            while (i >= 2 && (array[i] - array[j]) > d) {
                j++;
            }
            result += C2(i - j) % 99997867;
        }
        System.out.println(result% 99997867);
    }

    public static long C2(int n) {
        return (n - 1) * n / 2;
    }

}
