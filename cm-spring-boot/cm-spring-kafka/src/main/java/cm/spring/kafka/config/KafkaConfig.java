package cm.spring.kafka.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import lombok.extern.slf4j.Slf4j;

/**
 * @author parkstud@qq.com 2020-05-21
 */
@Configuration
@Slf4j
public class KafkaConfig {
    private final TaskExecutor exec =  new SimpleAsyncTaskExecutor();
}
