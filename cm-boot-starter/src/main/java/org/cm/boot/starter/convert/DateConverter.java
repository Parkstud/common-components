package org.cm.boot.starter.convert;

import org.cm.boot.starter.config.BaseConstants;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.annotation.Nullable;

import lombok.extern.slf4j.Slf4j;

/**
 * 全局时间转化器
 *
 * @author parkstud@qq.com 2020-05-21
 */
@Slf4j
public class DateConverter implements Converter<String, Date> {
    /**
     * 字符串转Date
     * @param date 字符串时间
     * @return date
     */
    @Override
    public Date convert( @Nullable  String date) {
        if (!StringUtils.hasText(date)) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(date, BaseConstants.dtf);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
