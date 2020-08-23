package algorithm.easy;

import java.util.ArrayList;
import java.util.List;

/**
 * 将一个给定字符串根据给定的行数，以从上往下、从左到右进行 Z 字形排列。
 * <p>
 * 比如输入字符串为 "L EET C ODE I SHI R ING" 行数为 3 时，排列如下：
 * <p>
 * L   C   I   R
 * E T O E S I I G
 * E   D   H   N
 * 之后，你的输出需要从左往右逐行读取，产生出一个新的字符串，比如："LCIRETOESIIGEDHN"。
 * <p>
 * 请你实现这个将字符串进行指定行数变换的函数：
 * <p>
 * string convert(string s, int numRows);
 * 示例 1:
 * <p>
 * 输入: s = "LEETCODEISHIRING", numRows = 3
 * 输出: "LCIRETOESIIGEDHN"
 * 示例 2:
 * <p>
 * 输入: s = "L EETCO D EIS HI R ING", numRows = 4
 * 输出: "LDREOEIIECIHNTSG"
 * 解释:
 * <p>
 * L     D     R
 * E   O E   I I
 * E C   I H   N
 * T     S     G
 * 通过次数155,231
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/zigzag-conversion
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author parkstud@qq.com 2020-08-05
 * (n-1)*2
 * n-1
 */
public class ZGlyphTransformation {

    class Node {
        Character element;
        Node next;

        public Node(Character element, Node next) {
            this.element = element;
            this.next = next;
        }
    }

    public String convert(String s, int numRows) {
        if(numRows==1){
            return s;
        }
        List<Node> nodes = new ArrayList<>(numRows);
        for (int i = 0; i < numRows; i++) {
            nodes.add(new Node(null, null));
        }
        int spit = (numRows - 1) * 2;
        for (int i = 0; i < s.length(); ) {
            if (i % spit == 0) {
                int temp = i;
                while (i - temp < numRows && i<s.length()) {
                    Node node = nodes.get(i - temp);
                    while (node.element != null) {
                        node=node.next;
                    }
                    node.element = s.charAt(i);
                    node.next = new Node(null, null);
                    i++;
                }
                while (i < spit + temp && i<s.length()) {
                    Node node = nodes.get(spit + temp - i);
                    while (node.element != null) {
                        node=node.next;
                    }
                    node.element = s.charAt(i);
                    node.next = new Node(null, null);
                    i++;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            while (node != null && node.element != null) {
                sb.append(node.element);
                node = node.next;
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        ZGlyphTransformation zGlyphTransformation = new ZGlyphTransformation();
        String leetcodeishiring1 = zGlyphTransformation.convert("ABC", 2);
        System.out.println(leetcodeishiring1);
    }
}
