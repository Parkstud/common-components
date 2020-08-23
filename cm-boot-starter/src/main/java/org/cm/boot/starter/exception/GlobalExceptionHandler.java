package org.cm.boot.starter.exception;

import org.cm.boot.starter.config.BaseConstants;
import org.cm.boot.starter.net.Results;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author parkstud@qq.com 2020-05-19
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @Value("${spring.profiles.active}")
    private String env;


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> process(Exception e) {
        log.error(e.getMessage(), e);
        ExceptionResponse exceptionResponse = new ExceptionResponse(BaseConstants.ErrorCode.ERROR);
        setDevException(exceptionResponse, e);
        return Results.error(exceptionResponse);
    }

    /**
     * 开发环境设置异常栈
     *
     * @param er 异常返回对象
     * @param ex 异常
     */
    private void setDevException(ExceptionResponse er, Exception ex) {
        if (BaseConstants.StringConstants.DEFAULT_ENV.getValue().equals(env)) {
            er.setThrowable(ex);
        }
    }

}
