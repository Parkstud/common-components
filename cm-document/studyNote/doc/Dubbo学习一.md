---
author: chen miao
time : 2019/1/10
email:  parkstud@qq.com
---

# Dubbo学习笔记1

## 背景

### 解决的问题

**当服务越来越多时，服务 URL 配置管理变得非常困难，F5 硬件负载均衡器的单点压力也越来越大。** 此时需要一个服务注册中心，动态地注册和发现服务，使服务的位置透明。

**当进一步发展，服务间依赖关系变得错踪复杂，甚至分不清哪个应用要在哪个应用之前启动，架构师都不能完整的描述应用的架构关系。** 这时，需要自动画出应用间的依赖关系图，以帮助架构师理清关系。

**接着，服务的调用量越来越大，服务的容量问题就暴露出来，这个服务需要多少机器支撑？什么时候该加机器？** 为了解决这些问题，第一步，要将服务现在每天的调用量，响应时间，都统计出来，作为容量规划的参考指标。其次，要可以动态调整权重，在线上，将某台机器的权重一直加大，并在加大的过程中记录响应时间的变化，直到响应时间到达阈值，记录此时的访问量，再以此访问量乘以机器数反推总容量。

### Dubbo架构

![image.png](https://i.loli.net/2020/01/13/Sqn8GAkJzMo47Yu.png)

| **节点**        | 角色说明                               |
| --------------- | -------------------------------------- |
| **`Provider`**  | 暴露服务的服务提供方                   |
| **`Consumer`**  | 调用远程服务的服务消费方               |
| **`Registry`**  | 服务注册与发现的注册中心               |
| **`Monitor`**   | 统计服务的调用次数和调用时间的监控中心 |
| **`Container`** | 服务运行容器                           |

调用关系说明:

0. 服务容器启动,加载,运行服务提供者

1. 服务提供者在启动时，向注册中心注册自己提供的服务。
2. 服务消费者在启动时，向注册中心订阅自己所需的服务。
3. 注册中心返回服务提供者地址列表给消费者，如果有变更，注册中心将基于长连接推送变更数据给消费者。
4. 服务消费者，从提供者地址列表中，基于软负载均衡算法，选一台提供者进行调用，如果调用失败，再选另一台调用。
5. 服务消费者和提供者，在内存中累计调用次数和调用时间，定时每分钟发送一次统计数据到监控中心。

## 配置

| 配置                            | 含义                                                         |
| ------------------------------- | ------------------------------------------------------------ |
| **dubbo.reference.check**=false | 强制改变所有 reference 的 check 值，就算配置中有声明，也会被覆盖。 |
| **dubbo.consumer.check**=false  | 是设置 check 的缺省值，如果配置中有显式的声明,不会受影响     |
| **dubbo.registry.check**=false  | 前面两个都是指订阅成功，但提供者列表是否为空是否报错，如果注册订阅失败时，也允许启动，需使用此选项，将在后台定时重试。 |

### 负载均衡

#### Random LoadBalance

- **随机**，按权重设置随机概率。默认

#### RoundRobin LoadBalance

- **轮询**，按公约后的权重设置轮询比率。

#### LeastActive LoadBalance

- **最少活跃调用数**，相同活跃数的随机，活跃数指调用前后计数差。

#### ConsistentHash LoadBalance

- **一致性 Hash**，相同参数的请求总是发到同一提供者。
- 当某一台提供者挂时，原本发往该提供者的请求，基于虚拟节点，平摊到其它提供者，不会引起剧烈变动。

### 线程模型

如果事件处理的逻辑能迅速完成，并且不会发起新的 IO 请求，比如只是在内存中记个标识，则直接在 IO 线程上处理更快，因为减少了线程池调度。但如果事件处理逻辑较慢，或者需要发起新的 IO 请求，比如需要查询数据库，则必须派发到线程池，否则 IO 线程阻塞，将导致不能接收其它请求。

![](https://dubbo.apache.org/docs/zh-cn/user/sources/images/dubbo-protocol.jpg)

**Dispatcher**

- `all` 所有消息都派发到线程池，包括请求，响应，连接事件，断开事件，心跳等。
- `direct` 所有消息都不派发到线程池，全部在 IO 线程上直接执行。
- `message` 只有请求响应消息派发到线程池，其它连接断开事件，心跳等消息，直接在 IO 线程上执行。
- `execution` 只有请求消息派发到线程池，不含响应，响应和其它连接断开事件，心跳等消息，直接在 IO 线程上执行。
- `connection` 在 IO 线程上，将连接断开事件放入队列，有序逐个执行，其它消息派发到线程池。

**ThreadPool**

- `fixed` 固定大小线程池，启动时建立线程，不关闭，一直持有。(缺省)
- `cached` 缓存线程池，空闲一分钟自动删除，需要时重建。
- `limited` 可伸缩线程池，但池中的线程数只会增长不会收缩。只增长不收缩的目的是为了避免收缩时突然来了大流量引起的性能问题。
- `eager` 优先创建`Worker`线程池。在任务数量大于`corePoolSize`但是小于`maximumPoolSize`时，优先创建`Worker`来处理任务。当任务数量大于`maximumPoolSize`时，将任务放入阻塞队列中。阻塞队列充满时抛出`RejectedExecutionException`。(相比于`cached`:`cached`在任务数量超过`maximumPoolSize`时直接抛出异常而不是将任务放入阻塞队列)

### 结果缓存

结果缓存 ，用于加速热门数据的访问速度，Dubbo 提供声明式缓存，以减少用户加缓存的工作量 。

**缓存类型**

- `lru` 基于最近最少使用原则删除多余缓存，保持最热的数据被缓存。
- `threadlocal` 当前线程缓存，比如一个页面渲染，用到很多 portal，每个 portal 都要去查用户信息，通过线程缓存，可以减少这种多余访问。
- `jcache` 与 [JSR107](http://jcp.org/en/jsr/detail?id=107') 集成，可以桥接各种缓存实现。

### 路由规则

路由规则在发起一次RPC调用前起到过滤目标服务器地址的作用，过滤后的地址列表，将作为消费端最终发起RPC调用的备选地址。

- 条件路由。支持以服务或Consumer应用为粒度配置路由规则。
- 标签路由。以Provider应用为粒度配置路由规则。

## Dubbo注解

### @EnableDubbo

`@EnableDubbo` 注解是 `@EnableDubboConfig` 和 `@DubboComponentScan`两者组合的便捷表达方式。与注解驱动相关的是 `@DubboComponentScan`。

通过 `@EnableDubbo` 可以在指定的包名下（通过 `scanBasePackages`），或者指定的类中（通过 `scanBasePackageClasses`）扫描 Dubbo 的服务提供者（以 `@Service` 标注）以及 Dubbo 的服务消费者（以 `Reference` 标注）。

扫描到 Dubbo 的服务提供方和消费者之后，对其做相应的组装并初始化，并最终完成服务暴露或者引用的工作。

当然，如果不使用外部化配置（External Configuration）的话，也可以直接使用 `@DubboComponentScan`。

### @Service

`@Service` 用来配置 Dubbo 的服务提供方.可配置的属性包括

- `interfaceClass`：指定服务提供方实现的 interface 的类
- **interfaceName**：指定服务提供方实现的 interface 的类名
- **version**：指定服务的版本号
- **group**：指定服务的分组
- **export**：是否暴露服务
- **registry**：是否向注册中心注册服务
- **application**：应用配置
- **module**：模块配置
- **provider**：服务提供方配置
- **protocol**：协议配置
- **monitor**：监控中心配置
- **registry**：注册中心配置

### @Reference

可以定义在类中的一个字段上，也可以定义在一个方法上，甚至可以用来修饰另一个 annotation，表示一个服务的引用。通常 @Reference 定义在一个字段上

`@Reference` 用来配置 Dubbo 的服务消费方，比如：

```java
@Component
public class GreetingServiceConsumer {
    @Reference
    private GreetingService greetingService;

    public String doSayHello(String name) {
        return greetingService.sayHello(name);
    }
}
```

- **interfaceClass**: 指定服务的 interface 的类
- **interfaceName**: 指定服务的 interface 的类名
- **version**: 指定服务的版本号
- **group**: 指定服务的分组
- **url**: 通过指定服务提供方的 URL 地址直接绕过注册中心发起调用
- **application**: 应用配置
- **module**: 模块配置
- **consumer**: 服务消费方配置
- **protocol**：协议配置
- **monitor**：监控中心配置
- **registry**：注册中心配置

## Dubbo 案例

### 案例一 服务器和客户端使用zk调用服务

#### 服务端

**结构目录**

![image.png](https://i.loli.net/2020/02/24/Rq1AgS3i29YOlWQ.png)

**POM配置**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.cm.dubbo</groupId>
        <artifactId>demo-master</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>


    <artifactId>demo-server</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>demo-server</name>


    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <org.apache.zookeeper.version>3.4.6</org.apache.zookeeper.version>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
            <version>4.0.1</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.cm.dubbo</groupId>
            <artifactId>demo-api</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

**配置**

application.properties文件

```properties
server.port=8080
server.servlet.context-path=/dubbo-server
```

dubbo-server.properties 文件

```properties
application.dubbo.demo.server.name=dubbo-server
application.dubbo.demo.server.address=zookeeper://106.14.4.232:2181
application.dubbo.demo.server.client=curator

```

log4j.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
<log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/" debug="false">
    <appender name="CONSOLE" class="org.apache.log4j.ConsoleAppender">
        <layout class="org.apache.log4j.PatternLayout">
            <param name="ConversionPattern" value="[%d{dd/MM/yy hh:mm:ss:sss z}] %t %5p %c{2}: %m%n" />
        </layout>
    </appender>
    <root>
        <level value="INFO" />
        <appender-ref ref="CONSOLE" />
    </root>
</log4j:configuration>

```

**启动类**

```java
package demo1.dubbo.service;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author parkstud@qq.com 2020-01-16
 */
@SpringBootApplication
@DubboComponentScan("demo1.dubbo.service.service")
public class DemoServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoServerApplication.class, args);
    }
}

```

**配置Bean**

```java
package demo1.dubbo.service.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import demo1.dubbo.service.properties.DubboProperties;

/**
 * @author parkstud@qq.com 2020-01-16
 */
@Configuration
public class DubboConfig {
    @Resource
    private DubboProperties dubboProperties;

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(dubboProperties.getName());
        return applicationConfig;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryconfig = new RegistryConfig();
        registryconfig.setAddress(dubboProperties.getAddress());
        registryconfig.setClient(dubboProperties.getClient());
        return registryconfig;
    }

}

```
DubboProperties.java

```java
package demo1.dubbo.service.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

/**
 * @author parkstud@qq.com 2020-01-16
 */
@Configuration
@ConfigurationProperties(prefix = "application.dubbo.demo.server")
@PropertySource("classpath:dubbo-server.properties")
@Data
public class DubboProperties {

    private String name;

    private String address;

    private String client;

}

```

HelloServiceImpl.java

```java
package demo1.dubbo.service.service;

import com.alibaba.dubbo.config.annotation.Service;

import demo1.dubbo.service.HelloService;

/**
 * @author parkstud@qq.com 2020-01-16
 */
@Service(timeout = 5000,version = "1.0",group = "dubbo-server")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hello " + name + " ! ";
    }
}

```

#### 客户端

**目录结构**

![image.png](https://i.loli.net/2020/02/24/iAGt142MwlmEWZo.png)

**pom**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.cm.dubbo</groupId>
        <artifactId>demo-master</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <modelVersion>4.0.0</modelVersion>
    <groupId>com.cm.dubbo</groupId>
    <artifactId>demo-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>demo-client</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <org.apache.zookeeper.version>3.4.6</org.apache.zookeeper.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.cm.dubbo</groupId>
            <artifactId>demo-api</artifactId>
            <version>0.0.1-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.apache.curator</groupId>
            <artifactId>curator-framework</artifactId>
            <version>4.0.1</version>
        </dependency>
    </dependencies>
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>


</project>
```

**启动类**

```
package demo1.dubbo.client;

import com.alibaba.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author chen
 */
@SpringBootApplication
@DubboComponentScan(value = "demo1.dubbo.client.service")
public class DemoClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoClientApplication.class, args);
    }
}

```

**配置**

application.properties

```properties
server.port=8081
server.servlet.context-path=/demo-client
```

dubbo-client.properties

```properties
application.dubbo.demo.client.name=dubbo-client
application.dubbo.demo.client.address=zookeeper://106.14.4.232:2181
application.dubbo.demo.client.client=curator

```

**配置bean**

```java
package demo1.dubbo.client.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.RegistryConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import demo1.dubbo.client.properties.DubboProperties;
import lombok.extern.slf4j.Slf4j;

/**
 * @author parkstud@qq.com 2020-01-16
 */
@Configuration
@Slf4j
public class DubboClientConfig {
    @Resource
    private DubboProperties dubboProperties;

    @Bean
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(dubboProperties.getName());
        return applicationConfig;
    }

    @Bean
    public ConsumerConfig consumerConfig() {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setTimeout(3000);
        return consumerConfig;
    }

    @Bean
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(dubboProperties.getAddress());
        registryConfig.setClient(dubboProperties.getClient());
        return registryConfig;
    }
}

```

```java
package demo1.dubbo.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import demo1.dubbo.client.service.BuinessService;

/**
 * @author parkstud@qq.com 2020-01-28
 */
@RestController
public class TestController {
    @Autowired
    BuinessService buinessService;

    @GetMapping("/test")
    public void test() {
        buinessService.testHello("chenmiao");
    }
}

```

```java
package demo1.dubbo.client.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

/**
 * @author parkstud@qq.com 2020-01-16
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "application.dubbo.demo.client")
@PropertySource("classpath:dubbo-client.properties")
public class DubboProperties {
    private String name;

    private String address;

    private String client;
}

```

```java
package demo1.dubbo.client.service;


import com.alibaba.dubbo.config.annotation.Reference;

import org.springframework.stereotype.Service;

import demo1.dubbo.service.HelloService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author parkstud@qq.com 2020-01-16
 */
@Service
@Slf4j
public class BuinessService {
    @Reference(version = "1.0", group = "dubbo-server")
    private HelloService helloService;

    public void testHello(String name) {
        String s = helloService.sayHello(name);
        log.error("调用结果：{}",s);
    }
}

```



#### API服务定义

目录结构

![image.png](https://i.loli.net/2020/02/24/97eo8SfWDI3XABd.png)



**POM**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <artifactId>demo-api</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>
    <name>dubbo-api</name>

    <parent>
        <groupId>org.cm.dubbo</groupId>
        <artifactId>demo-master</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>


    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>


</project>
```

```java
package demo1.dubbo.service;

/**
 * @author parkstud@qq.com 2020-01-16
 */
public interface HelloService {
    /**
     * 打印测试
     * @param name
     * @return
     */
    String sayHello(String name);
}

```





