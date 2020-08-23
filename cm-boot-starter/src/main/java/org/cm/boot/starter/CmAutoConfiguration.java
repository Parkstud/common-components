package org.cm.boot.starter;

import org.cm.boot.starter.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author parkstud@qq.com 2020-05-21
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true,exposeProxy = true)
public class CmAutoConfiguration {
    @Bean
    public GlobalExceptionHandler globalExceptionHandler(){return new GlobalExceptionHandler();}

}
