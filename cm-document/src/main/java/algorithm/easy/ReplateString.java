package algorithm.easy;

import java.util.ArrayList;

/**
 * A、B、C 是 3 个字符串。把 A 中包含的所有 B 都替换为 C，如果替换以后还有 B 就继续换，直到 A 不包含 B 为止。1.
 * 请编写程序实现以上功能。
 * 不允许使用系统提供的字符串比较、查找和替换函数2.
 * 以上程序是否总是能正常输出结果？如果不是，
 * 列出哪些情况下无法正常输出结果，尽可能详细和全面。
 * @author parkstud@qq.com 2020-03-29
 */
public class ReplateString {
    public static void main(String[] args) {
        String a = "abcabc";
        String b = "ab";
        String c = "a";
        System.out.println(replaceString(a, b, c));
    }

    /**
     * 替换字符串
     *
     * @param A 被替换字符串
     * @param B 匹配替换的字符串
     * @param C 代替字符串
     * @return 替换后的字符串
     */
    public static String replaceString(String A, String B, String C) {
        ArrayList<Character> arrayA = new ArrayList<>(A.length());
        for (int i = 0; i < A.length(); i++) {
            arrayA.add(A.charAt(i));
        }
        ArrayList<Character> arrayC = new ArrayList<>();
        for (int i = 0; i < C.length(); i++) {
            arrayC.add(C.charAt(i));
        }
        for (int i = 0; i < arrayA.size(); i++) {
            int j = 0;
            int mark = i;
            while (j < B.length() && arrayA.get(mark) == B.charAt(j)) {
                j++;
                mark++;
            }
            // 可替换 [i,mark)
            if (j == B.length()) {
                // 删除原来的元素
                for (int k = 0; k < B.length(); k++) {
                    arrayA.remove(i);
                }
                arrayA.addAll(i, arrayC);

                //从当前替换位置的继续遍历
                i--;

            }
        }
        StringBuilder result = new StringBuilder();
        arrayA.forEach(result::append);
        return result.toString();
    }
}
