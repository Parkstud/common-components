package org.cm.cloud.eureka.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author parkstud@qq.com 2020-04-25
 */
@EnableDiscoveryClient
@EnableCircuitBreaker
@SpringBootApplication
public class CmEurekaClient {
    public static void main(String[] args) {
        SpringApplication.run(CmEurekaClient.class, args);
    }
}

