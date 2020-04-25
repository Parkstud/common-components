package org.cm.cloud.eureka.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author parkstud@qq.com 2020-04-25
 */
@EnableEurekaServer
@SpringBootApplication
public class CmEurekaServer {
    public static void main(String[] args) {
        SpringApplication.run(CmEurekaServer.class, args);
    }
}
