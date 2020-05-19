package cm.spring.kafka.api.controller;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cm.spring.kafka.entry.Foo1;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;

/**
 * @author parkstud@qq.com 2020-05-18
 */
@Api(tags = "Foo kafak测试")
@RestController
@RequestMapping("/v1/kafka/foo")
@AllArgsConstructor
public class FooController {
    private final KafkaTemplate<Object, Object> template;

    @PostMapping("/send/foo/{what}")
    public void sendFoo(@ApiParam(name = "消息", required = true) @PathVariable String what) {
        this.template.send("topic1", new Foo1(what));
    }

}
