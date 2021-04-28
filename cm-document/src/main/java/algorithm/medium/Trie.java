package algorithm.medium;

import java.util.HashMap;
import java.util.Map;

/**
 * Trie（发音类似 "try"）或者说 前缀树 是一种树形数据结构，用于高效地存储和检索字符串数据集中的键。这一数据结构有相当多的应用情景，例如自动补完和拼写检查。
 * <p>
 * 请你实现 Trie 类：
 * <p>
 * Trie() 初始化前缀树对象。
 * void insert(String word) 向前缀树中插入字符串 word 。
 * boolean search(String word) 如果字符串 word 在前缀树中，返回 true（即，在检索之前已经插入）；否则，返回 false 。
 * boolean startsWith(String prefix) 如果之前已经插入的字符串 word 的前缀之一为 prefix ，返回 true ；否则，返回 false 。
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/implement-trie-prefix-tree
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 *
 * @author miao.chen01@hand-china.com 2021-04-14
 */
public class Trie {

    class TreeNode {
        char value;
        short wordFlag = 0;
        Map<Character, TreeNode> trieMap = new HashMap<>();
    }

    TreeNode root;

    /**
     * Initialize your data structure here.
     */
    public Trie() {
        root = new TreeNode();
    }

    /**
     * Inserts a word into the trie.
     */
    public void insert(String word) {
        char[] chars = word.toCharArray();
        TreeNode temp = root;
        for (int i = 0; i < chars.length; i++) {
            TreeNode treeNode = temp.trieMap.get(chars[i]);
            if (treeNode == null) {
                treeNode = new TreeNode();
                treeNode.value = chars[i];
                temp.trieMap.put(chars[i], treeNode);
            }
            temp = treeNode;
            if (i == chars.length - 1) {
                temp.wordFlag = 1;
            }
        }
    }

    /**
     * Returns if the word is in the trie.
     */
    public boolean search(String word) {
        char[] chars = word.toCharArray();
        TreeNode temp = root;
        for (char c : chars) {
            if (null == temp.trieMap.get(c)) {
                return false;
            }
            temp = temp.trieMap.get(c);
        }
        return temp.wordFlag == 1;
    }

    /**
     * Returns if there is any word in the trie that starts with the given prefix.
     */
    public boolean startsWith(String prefix) {
        char[] chars = prefix.toCharArray();
        TreeNode temp = root;
        for (char c : chars) {
            if (null == temp.trieMap.get(c)) {
                return false;
            }
            temp = temp.trieMap.get(c);
        }
        return true;
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        trie.insert("wrod");
        System.out.println(trie.search("wrod"));
    }

}

/**
 * Your Trie object will be instantiated and called as such:
 * Trie obj = new Trie();
 * obj.insert(word);
 * boolean param_2 = obj.search(word);
 * boolean param_3 = obj.startsWith(prefix);
 */