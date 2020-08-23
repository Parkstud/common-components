package org.cm.boot.starter.config;

import org.cm.boot.starter.convert.DateConverter;
import org.cm.boot.starter.convert.LocalDateConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author parkstud@qq.com 2020-05-21
 */
@Configuration
public class ConvertWebMvcConfigurer implements WebMvcConfigurer {
    @Value("${cm.date.converter.enable:true}")
    private boolean enable;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        if(enable){
            registry.addConverter(new DateConverter());
            registry.addConverter(new LocalDateConverter());
        }

    }
}
