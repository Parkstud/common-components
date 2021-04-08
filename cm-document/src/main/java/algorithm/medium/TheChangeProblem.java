package algorithm.medium;

import java.util.Arrays;

/**
 * @author miao.chen01@hand-china.com 2021-04-08
 */
public class TheChangeProblem {
    public static void main(String[] args) {
        //        System.out.println(coinChange(new int[]{1, 3, 5}, 11, new int[12]));
        System.out.println(coinChange(new int[]{3, 5}, 7));
    }

    static int coinChange(int[] coins, int amount) {
        int[] db = new int[amount + 1];
        for (int i = 0; i < db.length; i++) {
            db[i] =amount+1;
        }
        db[0]=0;
        for (int i = 0; i < db.length; i++) {
            for (int j = 0; j < coins.length; j++) {
                if (i - coins[j] < 0) {
                    continue;
                }
                System.out.println(i+"\t"+coins[j]);
                db[i] = Math.min(db[i], 1 + db[i - coins[j]]);
                System.out.println(Arrays.toString(db));
            }
        }
        return (db[amount] == amount + 1) ? -1 : db[amount];
    }

    static int coinChange(int[] coins, int amount, int[] memo) {

        if (amount < 0) {
            return -1;
        }
        if (amount == 0) {
            return 0;
        }
        System.out.println(amount + "\t" + memo[amount]);
        if (memo[amount] != 0) {
            return memo[amount];
        }
        int res = Integer.MAX_VALUE;
        for (int i = 0; i < coins.length; i++) {
            int subproblem = coinChange(coins, amount - coins[i], memo);
            if (subproblem == -1) {
                continue;
            }
            res = Math.min(res, 1 + subproblem);
        }
        if (res != Integer.MAX_VALUE) {
            memo[amount] = res;
        }

        return res;
    }
}
