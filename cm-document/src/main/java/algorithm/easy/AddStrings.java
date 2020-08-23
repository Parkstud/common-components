package algorithm.easy;

/**
 * 字符串相加
 * <p>
 * 给定两个字符串形式的非负整数 num1 和num2 ，计算它们的和。
 * <p>
 * 提示：
 * <p>
 * num1 和num2 的长度都小于 5100
 * num1 和num2 都只包含数字 0-9
 * num1 和num2 都不包含任何前导零
 * 你不能使用任何內建 BigInteger 库， 也不能直接将输入的字符串转换为整数形式
 *
 * @author parkstud@qq.com 2020-08-04
 */
public class AddStrings {
    public static void main(String[] args) {
        String a = "0";
        String b = "9999";
        AddStrings test = new AddStrings();
        System.out.println( test.addStrings(a,b));

    }
    public String addStrings(String num1, String num2) {
        if (num1.length() < num2.length()) {
            String temp = num1;
            num1 = num2;
            num2 = temp;
        }
        int aCount = num1.length() ;
        int carryNum = 0;
        int bCount = num2.length();
        StringBuilder result = new StringBuilder("");
        while (aCount-- > 0) {
            int numA = num1.charAt(aCount) - 48;
            int numB=0;
            if (bCount-->0) {
                numB= num2.charAt(bCount) - 48;
            }
            int sumAB = numA + numB + carryNum;
            if (sumAB >= 10) {
                carryNum = sumAB / 10;
                sumAB = sumAB % 10;
            } else {
                carryNum = 0;
            }
            result.append(sumAB);
        }
        if(carryNum>0){
            result.append(carryNum);
        }
        return result.reverse().toString();
    }
}
