package org.cm.cloud.eureka.client.config;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author parkstud@qq.com 2020-04-26
 */
@Component
public class StoreIntegration {
    @HystrixCommand(fallbackMethod = "defaultStores")
    public Object getStores(Map<String, Object> parameters) {

        return "getStores";
    }

    public Object defaultStores(Map<String, Object> parameters) {
        return "defaultStores";
    }
}
