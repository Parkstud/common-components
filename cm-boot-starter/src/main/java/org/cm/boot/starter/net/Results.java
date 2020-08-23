package org.cm.boot.starter.net;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * api返回类
 *
 * @author parkstud@qq.com 2020-04-25
 */
@SuppressWarnings("all")
public class Results {
    private static final ResponseEntity NO_CONTENT = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    private static final ResponseEntity INVALID = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    private static final ResponseEntity ERROR = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

    private Results() {
    }

    public static <T> ResponseEntity<T> success(T data) {
        return data == null ? NO_CONTENT : ResponseEntity.ok(data);
    }

    public static <T> ResponseEntity<T> success() {
        return NO_CONTENT;
    }

    public static <T> ResponseEntity<T> error() {
        return ERROR;
    }

    public static <T> ResponseEntity<T> error(T data) {
        return data == null ? ERROR : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(data);
    }

    public static <T> ResponseEntity<T> invalid() {
        return INVALID;
    }

    public static <T> ResponseEntity<T> invalid(T data) {
        return data == null ? INVALID : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

}
