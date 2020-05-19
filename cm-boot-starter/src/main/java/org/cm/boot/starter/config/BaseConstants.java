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
         * 换行符：\r\n
         */
        String LB = System.getProperty("line.separator");
        /**
         * 百分号：%
         */
        String PERCENTAGE = "%";


    }

}
