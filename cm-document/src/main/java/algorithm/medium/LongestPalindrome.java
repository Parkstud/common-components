package algorithm.medium;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 最长回文子串
 * 给定一个字符串 s，找到 s 中最长的回文子串。你可以假设 s 的最大长度为 1000。
 * <p>
 * 输入: "babad"
 * 输出: "bab"
 * 注意: "aba" 也是一个有效答案。
 * <p>
 * <p>
 * 输入: "cbbd"
 * 输出: "bb"
 *
 * @author parkstud@qq.com 2020-04-08
 */
public class LongestPalindrome {

    public String longestPalindrome(String s) {
        Set<Character> set = new HashSet<>();
        StringBuilder sb=new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (set.contains(s.charAt(i))) {
                continue;
            }
            set.add(s.charAt(i));
            ArrayList<Integer> index = new ArrayList<>();
            for (int j = i; j < s.length(); j++) {
                if (s.charAt(j) == s.charAt(i)) {
                    index.add(j);
                }
                int x=i,y=j;


            }


        }
        return "";
    }

    public static void main(String[] args) {

    }
}
