package org.cm.boot.starter.convert;

import org.cm.boot.starter.config.BaseConstants;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nullable;

/**
 * @author parkstud@qq.com 2020-05-21
 */
public class LocalDateConverter implements Converter<String, LocalDate> {
    @Override
    public LocalDate convert(@Nullable String localDate) {
        if(!StringUtils.hasText(localDate)){
            return null;
        }
        return LocalDate.parse(localDate, DateTimeFormatter.ofPattern(BaseConstants.Pattern.DATE.getPatternStr()));
    }
}
