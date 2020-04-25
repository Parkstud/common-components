package org.cm.cloud.eureka.server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author parkstud@qq.com 2020-04-25
 */
@RestController
public class TestController {
    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok().body("hello word");
    }
}
