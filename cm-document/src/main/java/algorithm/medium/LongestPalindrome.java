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
        int start = 0;
        int end = 0;
        int len = 0;
        for (int i = 0; i < s.length(); i++) {
            //处理xaba
            //a
            //aaa
            int len1 = centerLen(s, i, i);
            if (len1 > len) {
                start = i - len1 / 2;
                end = i + (len1 + 1) / 2;
                len = len1;
            }
            // 处理xabba
            //aa
            //aaaa
            int len2 = centerLen(s, i, i + 1);
            if (len2 > end - start) {
                start = i - (len2 - 1) / 2;
                end = i + len2 / 2 + 1;
                len = len2;
            }
        }
        return s.substring(start, end);
    }

    public int centerLen(String s, int left, int right) {
        int k = 1;
        if (left == right) {
            right++;
            left--;
            while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
                right++;
                left--;
                k += 2;
            }
        } else {
            k = 0;
            while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
                right++;
                left--;
                k += 2;
            }
        }

        return k;
    }

    public String longestPalindrome1(String s) {
        Set<Character> set = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        int max = 0;
        for (int i = 0; i < s.length(); i++) {
            if (set.contains(s.charAt(i))) {
                continue;
            }
            set.add(s.charAt(i));
            ArrayList<Integer> index = new ArrayList<>();
            for (int j = i; j < s.length(); j++) {
                if (s.charAt(j) == s.charAt(i)) {
                    index.add(j);
                } else {
                    continue;
                }
            }
            if (index.isEmpty()) {
                continue;
            }
            for (int j = index.get(0); j <= index.get(index.size() - 1); j++) {
                int h = j;
                int m = index.size() - 1;
                int k;
                if ((index.get(m) - h + 1) > max) {
                    while (m >= 0 && index.get(m) >= j && (index.get(m) - j + 1) > max) {
                        for (k = index.get(m); k >= h; k--) {
                            if (s.charAt(h) != s.charAt(k)) {
                                break;
                            }
                            h++;
                        }
                        if (h - 1 == (j + index.get(m)) / 2) {
                            max = index.get(m) - j + 1;
                            sb.setLength(0);
                            for (int n = j; n <= index.get(m); n++) {
                                sb.append(s.charAt(n));
                            }
                        }
                        m--;
                        h = j;
                    }

                }
            }

        }
        return sb.toString();
    }

    public static void main(String[] args) {
        LongestPalindrome lp = new LongestPalindrome();
        String a = "babad";
        String b = "cbbd";
        String c = "abc";
        String d = "abcba";
        String e = "babadada";
        String f = "beabcccb";
        String h = "aaaa";
        System.out.println(new LongestPalindrome().longestPalindrome(a));
        System.out.println(new LongestPalindrome().longestPalindrome(b));
        System.out.println(new LongestPalindrome().longestPalindrome(d));
        System.out.println(new LongestPalindrome().longestPalindrome(e));
        System.out.println(new LongestPalindrome().longestPalindrome(f));
        System.out.println(new LongestPalindrome().longestPalindrome(h));
        System.out.println(new LongestPalindrome().longestPalindrome(c));
    }
}
