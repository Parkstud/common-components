package org.cm.boot.starter.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author parkstud@qq.com 2020-05-19
 */
public interface BaseConstants {
    /**
     * 原始常量
     */
    enum originConstants {
        /**
         * 默认页码字段名
         */
        PAGE,
        /**
         * 默认页面大小字段名
         */
        SIZE
    }

    /**
     * 字符串常量
     */
    @AllArgsConstructor
    @Getter
    enum StringConstants {
        /**
         * 默认页码
         */
        PAGE("0"),
        /**
         * 默认页面大小
         */
        SIZE("10"),
        /**
         * body
         */
        FIELD_BODY("body"),
        /**
         * KEY content
         */
        FIELD_CONTENT("content"),

        FIELD_MSG("message"),

        FIELD_FAILED("failed"),

        FIELD_SUCCESS("success"),

        FIELD_ERROR_MSG("errorMsg"),

        DEFAULT_ENV("dev"),

        /**
         * 默认国际管码
         */
        DEFAULT_CROWN_CODE("+86"),
        /**
         * 默认时区
         */
        DEFAULT_TIME_ZONE("GMT+8");

        /**
         * 属性值
         */
        private final String value;
    }

    /**
     * 数字类型常量
     */
    @Getter
    @AllArgsConstructor
    enum numConstants {
        /**
         * -1
         */
        NEGATIVE_ONE(-1),
        /**
         * 默认页码
         */
        PAGE_NUM(0),
        /**
         * 默认页面大小
         */
        PAGE_SIZE(10);

        private final Number value;
    }

    /**
     * 日期时间匹配格式
     */
    @Getter
    @AllArgsConstructor
    enum Pattern {
        /**
         * yyyy-MM-dd
         */
        DATE("yyyy-MM-dd"),
        DATETIME("yyyy-MM-dd HH:mm:ss");
        private final String patternStr;
    }

    /**
     * 1/0
     */
    interface Flag {
        /**
         * 1
         */
        Integer YES = 1;
        /**
         * 0
         */
        Integer NO = 0;
    }

    /**
     * 符号常量
     */
    interface Symbol {
        /**
         * 感叹号：!
         */
        String SIGH = "!";
        /**
         * 符号：@
         */
        String AT = "@";
        /**
         * 井号：#
         */
        String WELL = "#";
        /**
         * 美元符：$
         */
        String DOLLAR = "$";
        /**
         * 人民币符号：￥
         */
        String RMB = "￥";
        /**
         * 空格：
         */
        String SPACE = " ";
        /**
         * 换行符：\r\n
         */
        String LB = System.getProperty("line.separator");
        /**
         * 百分号：%
         */
        String PERCENTAGE = "%";
        /**
         * 符号：&amp;
         */
        String AND = "&";
        /**
         * 星号
         */
        String STAR = "*";
        /**
         * 中横线：-
         */
        String MIDDLE_LINE = "-";
        /**
         * 下划线：_
         */
        String LOWER_LINE = "_";
        /**
         * 等号：=
         */
        String EQUAL = "=";
        /**
         * 加号：+
         */
        String PLUS = "+";
        /**
         * 冒号：:
         */
        String COLON = ":";
        /**
         * 分号：;
         */
        String SEMICOLON = ";";
        /**
         * 逗号：,
         */
        String COMMA = ",";
        /**
         * 点号：.
         */
        String POINT = ".";
        /**
         * 斜杠：/
         */
        String SLASH = "/";
        /**
         * 竖杠：|
         */
        String VERTICAL_BAR = "|";
        /**
         * 双斜杠：//
         */
        String DOUBLE_SLASH = "//";
        /**
         * 反斜杠
         */
        String BACKSLASH = "\\";
        /**
         * 问号：?
         */
        String QUESTION = "?";
        /**
         * 左花括号：{
         */
        String LEFT_BIG_BRACE = "{";
        /**
         * 右花括号：}
         */
        String RIGHT_BIG_BRACE = "}";
        /**
         * 左中括号：[
         */
        String LEFT_MIDDLE_BRACE = "[";
        /**
         * 右中括号：[
         */
        String RIGHT_MIDDLE_BRACE = "]";
        /**
         * 反引号：`
         */
        String BACKQUOTE = "`";
    }

}
