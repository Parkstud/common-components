#  Maven学习

## 概念

Maven是基于POM（工程对象模型），通过一小段描述来对项目的代码、报告、文件进管理的工具。

Maven是一个跨平台的项目管理工具，它是使用java开发的，它要依赖于jdk1.6及以上.

Maven主要有两大功能：管理依赖、项目构建。依赖指的就是jar包。

### 坐标

为了定位一个唯一确定的jar包。

```pom
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.12</version>
    <scope>provided</scope>
</dependency>
```

- **groupId**  定义当前Maven组织名称
- **artifactId** 定义实际项目名称
- **version** 定义当前项目的当前版本

### 依赖管理

**依赖范围**： scope 用于控制依赖和编译、测试、运行的classpath的关系。

- `compile`: 默认。 编译、测试、运行时，classpath都有效。
- `test`: 测试有效 junit
- `provide`: 编译、测试。lombok
- `runtime`: 运行时提供。 例如： jdbc 驱动

**依赖传递**

- 当第二直接依赖范围是compile时，可正常传递；

- 第二是test时，不传递；

- ...provided时，只传递为第一直接依赖范围为provided的依赖，且传递性范围同样为provided；

- ...runtime，传递范围与第一直接依赖一致，compile例外，此时传递范围为runtime。

**依赖冲突**

- 跨pom文件的冲突： 就近原则-依赖最直接最近的。
- 同一个pom文件的冲突： 就近原则-越下面的越近。

**可选依赖**

`Optional`标签表示：该依赖是否可选。

```pom
<optinal>true</optional>    //默认为false：该依赖会传递下去；  true： 该依赖不会传递下去
```

**排除依赖** Exclusions标签：可以排除依赖。 在引入first.jar 时，可添加此标签，标注 排除掉first.jar传递过来的某个依赖。

### 生命周期

**Maven的生命周期就是为了对所有的构建过程进行抽象和统一**

Maven拥有三套相互独立的生命周期，它们分别为clean、default和site。clean生命周期的目的是**清理项目**，default生命周期的目的是**构建项目**，而site生命周期的目的是**建立项目站点**。

每个生命周期包含一些阶段(phase)，这些阶段是有顺序的，并且后面的阶段依赖于前面的阶段，用户和Maven最直接的交互方式就是调用这些生命周期阶段。

三套生命周期本身是相互独立的，用户可以仅仅调用clean生命周期的某个阶段，或者仅仅调用default生命周期的某个阶段，而不会对其他生命周期产生任何影响。

#### clean生命周期

clean生命周期的目的是**清理项目**，它包含三个阶段：

- `pre-clean`执行一些清理前需要完成的工作
- `clean`清理上一次构建生成的文件。
- `post-clean`执行一些清理后需要完成的工作。

#### default生命周期

default 生命周期定义了**真正构建时所需要执行的所有步骤**，它是所有生命周期中最核心的部分

- `validate`
- `initialize`
- `generate-sources`
- `process-sources`处理项目主资源文件。一般来说，是对`src/main/resources`目录的内容进行变量替换等工作后，复制到项目输出的主`classpath`目录中。
- `generate-resources`
- `process-resources`
- `compile`编译项目的主源码。一般来说，是编译`src/main/java`目录下的`Java`文件至项目输出的主`classpath`目录中。
- `process-classes`
- `generate-test-sources`
- `process-test-sources`处理项目测试资源文件。一般来说，是对`src/test/resources`目录的内容进行变量替换等工作后，复制到项目输出的测试`classpath`目录中。

- `generate-test-resources`
- `process-test-resources`
- `test-compile`编译项目的测试代码。一般来说，是编译src/test/java目录下的Java文件至项目输出的测试classpath目录中。

- `process-test-classes`
- `test`使用单元测试框架运行测试，测试代码不会被打包或部署。
- `prepare-package`
- `package`接受编译好的代码，打包成可发布的格式，如JAR。

- `pre-integration-test`
- `integration-test`
- `post-integration-test`

- `verify`
- `install`将包安装到Maven本地仓库，供本地其他Maven项目使用。
- `deploy`将最终的包复制到远程仓库，供其他开发人员和Maven项目使用。

#### site声明周期

site生命周期的目的是**建立和发布项目站点**，Maven能够基于POM所包含的信息，自动生成一个友好的站点，方便团队交流和发布项目信息。

- `pre-site`执行一些在生成项目站点之前需要完成的工作。
- `site`生成项目站点文档。
- `post-site`执行一些在生成项目站点之后需要完成的工作。
- `site-deploy`将生成的项目站点发布到服务器上。

### 继承

指pom文件的继承

1. 创建父工程：不用模板，选择打包方式必须为pom方式。
2. 创建子工程：创新工程、修改老工程。 选择打包方式界面-下方-Parent Project处添加父工程。

>子工程pom文件多了<parent></parent>标签。（修改老工程其实就是直接修改其pom文件：增加此标签，指定老工程的父工程）

作用

> 父工程统一依赖jar包：在父工程中对jar包进行依赖，在子工程中都会继承此依赖。

**父工程统一管理版本号**

jar包依赖太多了，每个要写一个dependency很冗余，于是：

1. Maven使用dependencyManagement管理依赖的版本号。 注意：此处只是定义依赖jar包的版本号，并不实际依赖。如果子工程中需要依赖jar包还需要添加dependency节点。

> 父工程
>
> <properties>
>
>  		<xx.version>1</xx.version> 
>
> </properties>
>
> <dependencyManagement> 
>
> ```
> <dependency>
>     <groupId>org.springframework.boot</groupId>
>     <artifactId>xxx</artifactId>
>     <version>${xx.version}</version>
> </dependency>
> ```
>
> <dependencyManagement/>
>
> 子工程
>
> <dependency>
>     <groupId>org.springframework.boot</groupId>
>     <artifactId>xxx</artifactId>
> </dependency>



1. 父工程抽取版本号 父工程统一管理依赖后，会有很多，所以对版本号抽象出来，单独定义，方便修改管理。

## 使用

Maven的命令要在pom.xml所在目录中去执行

- mvn clean -- 清除命令，清除已经编译好的class文件，具体说清除的是target整个目录
- mvn compile -- 编译
- mvn test -- 测试命令，该命令会将test目录中的源码进行编译
- mvn package -- 打包
- mvn install --安装命令，会将打好的包，安装到本地仓库
- mvn deploy 将本地的包推送到远程仓库

**可以组合命令,空格分隔**

pom.xml 详解https://blog.csdn.net/sunzhenhua0608/article/details/32938533

### 排除依赖

```xml
           <dependency>
                <groupId>org.springframework.amqp</groupId>
                <artifactId>spring-rabbit</artifactId>
                <version>${spring-amqp.version}</version>
                <exclusions>
                    <exclusion>
                        <artifactId>http-client</artifactId>
                        <groupId>com.rabbitmq</groupId>
                    </exclusion>
                </exclusions>
            </dependency>
```

### 资源打包

```xml
    <build>
       <!-- 资源目录 -->    
        <resources>    
            <resource>    
                <!-- 设定主资源目录  -->    
                <directory>src/main/java</directory>    
 
                <!-- maven default生命周期，process-resources阶段执行maven-resources-plugin插件的resources目标处理主资源目下的资源文件时，只处理如下配置中包含的资源类型 -->     
				 <includes>
					  <include>**/*.xml</include>
				 </includes>  
                     
                <!-- maven default生命周期，process-resources阶段执行maven-resources-plugin插件的resources目标处理主资源目下的资源文件时，不处理如下配置中包含的资源类型（剔除下如下配置中包含的资源类型）-->      
				<excludes>  
					<exclude>**/*.yaml</exclude>  
				</excludes>  
 
                <!-- maven default生命周期，process-resources阶段执行maven-resources-plugin插件的resources目标处理主资源目下的资源文件时，指定处理后的资源文件输出目录，默认是${build.outputDirectory}指定的目录-->      
                <!--<targetPath>${build.outputDirectory}</targetPath> -->      
   
                <!-- maven default生命周期，process-resources阶段执行maven-resources-plugin插件的resources目标处理主资源目下的资源文件时，是否对主资源目录开启资源过滤 -->    
                <filtering>true</filtering>     
            </resource>  			
        </resources> 	
  </build>
```

资源打包,并且不破坏文件

```xml
            <!--
                    此plugin可以用
                    利用此plugin，把源代码中的xml文件，打包到相应位置，
                    这里主要是为了打包Mybatis的mapper.xml文件
                    -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
        	    <artifactId>maven-resources-plugin</artifactId>
                <configuration>
           			<encoding>UTF-8</encoding>
                      <!-- 过滤文件 -->    
                    <nonFilteredFileExtensions>
                        <nonFilteredFileExtension>xlsx</nonFilteredFileExtension>
                        <nonFilteredFileExtension>xls</nonFilteredFileExtension>
                    </nonFilteredFileExtensions>
         		</configuration>
                <executions>
                    <execution>
                        <id>copy-xmls</id>
                        <phase>process-sources</phase>
                        <goals>
                            <goal>copy-resources</goal>
                        </goals>
                        <configuration>
                            <outputDirectory>${basedir}/target/classes</outputDirectory>
                            <resources>
                                <resource>
                                    <directory>${basedir}/src/main/java</directory>
                                    <includes>
                                        <include>**/*.xml</include>
                                    </includes>
                                </resource>
                            </resources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```



## Maven 私服



## Maven插件

Maven的核心仅仅定义了抽象的生命周期，具体的任务都是交由插件完成的。每个插件都能实现一个功能，每个功能就是一个插件目标。Maven的生命周期与插件目标相互绑定，以完成某个具体的构建任务。

>例如compile就是插件maven-compiler-plugin的一个插件目标

### 配置

### maven-compiler-plugin

`maven`是个项目管理工具，如果我们不告诉它我们的代码要使用什么样的`jdk`版本编译的话，它就会用`maven-compiler-plugin`默认的`jdk`版本来进行处理，这样就容易出现版本不匹配，以至于可能导致编译不通过的问题。

```xml
<plugin>                                                                                                                                      
    <!-- 指定maven编译的jdk版本,如果不指定,maven3默认用jdk 1.5 maven2默认用jdk1.3 -->                                                                           
    <groupId>org.apache.maven.plugins</groupId>                                                                                               
    <artifactId>maven-compiler-plugin</artifactId>                                                                                            
    <version>3.1</version>                                                                                                                    
    <configuration>                                                                                                                           
        <!-- 一般而言，target与source是保持一致的，但是，有时候为了让程序能在其他版本的jdk中运行(对于低版本目标jdk，源代码中不能使用低版本jdk中不支持的语法)，会存在target不同于source的情况 -->                    
        <source>1.8</source> <!-- 源代码使用的JDK版本 -->                                                                                             
        <target>1.8</target> <!-- 需要生成的目标class文件的编译版本 -->                                                                                     
        <encoding>UTF-8</encoding><!-- 字符集编码 -->
        <skipTests>true</skipTests><!-- 跳过测试 -->                                                                             
        <verbose>true</verbose>
        <showWarnings>true</showWarnings>                                                                                                               
        <fork>true</fork><!-- 要使compilerVersion标签生效，还需要将fork设为true，用于明确表示编译版本配置的可用 -->                                                        
        <executable><!-- path-to-javac --></executable><!-- 使用指定的javac命令，例如：<executable>${JAVA_1_4_HOME}/bin/javac</executable> -->           
        <compilerVersion>1.3</compilerVersion><!-- 指定插件将使用的编译器的版本 -->                                                                         
        <meminitial>128m</meminitial><!-- 编译器使用的初始内存 -->                                                                                      
        <maxmem>512m</maxmem><!-- 编译器使用的最大内存 -->                                                                                              
        <compilerArgument>-verbose -bootclasspath ${java.home}\lib\rt.jar</compilerArgument><!-- 这个选项用来传递编译器自身不包含但是却支持的参数选项 -->               
    </configuration>                                                                                                                          
</plugin>                                                                                                                                     
```



### Spring Boot Maven Plugin

Spring Boot Maven Plugin在Maven中提供了Spring Boot支持，使您可以打包可执行jar或war归档文件并立即运行应用程序。

#### goals

- [spring-boot：run](https://docs.spring.io/spring-boot/docs/current/maven-plugin/run-mojo.html)运行您的Spring Boot应用程序。
- [spring-boot：repackage](https://docs.spring.io/spring-boot/docs/current/maven-plugin/repackage-mojo.html)将jar / war [重新](https://docs.spring.io/spring-boot/docs/current/maven-plugin/repackage-mojo.html)打包为可执行文件。
- [spring-boot：start](https://docs.spring.io/spring-boot/docs/current/maven-plugin/start-mojo.html)和[spring-boot：stop](https://docs.spring.io/spring-boot/docs/current/maven-plugin/stop-mojo.html)来管理Spring Boot应用程序的生命周期（即用于集成测试）。
- [spring-boot：build-info](https://docs.spring.io/spring-boot/docs/current/maven-plugin/build-info-mojo.html)生成供执行器使用的构建信息。

```xml
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <!--   跳过集成测试   -->
                    <skip>${maven.springboot.test.skip}</skip>
                    <!--   排除不需要的jar(provided)  -->
                    <excludes>
                        <exclude>
                            <groupId>com.foo</groupId>
                            <artifactId>bar</artifactId>
                        </exclude>
                    </excludes>
                    <!--        dev环境            -->
                    <profiles>
                        <profile>dev</profile>
                    </profiles>
                </configuration>
                <executions>
                    <!-- 执行从新打包 -->
                    <execution>
                        <goals>
                            <goal>repackage</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```



### [maven-resources-plugin](http://maven.apache.org/plugins/maven-resources-plugin/)

资源插件处理项目资源到输出目录的复制。有两种不同的资源：主资源和测试资源。区别在于主要资源是与主要源代码关联的资源，而测试资源是与测试源代码关联的资源。

因此，这允许分离用于主要源代码及其单元测试的资源。

#### goals

- [resources：resources](http://maven.apache.org/plugins/maven-resources-plugin/resources-mojo.html)将主要源代码的[资源](http://maven.apache.org/plugins/maven-resources-plugin/resources-mojo.html)复制到主输出目录

  该目标通常自动执行，因为默认情况下它绑定到流程资源生命周期阶段。它始终使用project.build.resources元素指定资源，并且默认情况下使用project.build.outputDirectory指定复制目标。

- [resources：testResources](http://maven.apache.org/plugins/maven-resources-plugin/testResources-mojo.html)将测试源代码的资源复制到测试输出目录。

  该目标通常自动执行，因为默认情况下它绑定到process-test-resources生命周期阶段。它始终使用project.build.testResources元素指定资源，并且默认情况下使用project.build.testOutputDirectory指定复制目标。

- [resources：copy-resources将资源](http://maven.apache.org/plugins/maven-resources-plugin/copy-resources-mojo.html)复制到输出目录。

  此目标要求您配置要复制的资源，并指定outputDirectory。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.cm</groupId>
    <artifactId>cm-parent</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <!--  配置自定义参数-->
    <properties>
        <!--  maven-resources-plugin自动使用 配置编码  -->
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>


    <build>
        <!--  org.apache.maven.plugins 配置资源文件      -->
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                </includes>
                <filtering>true</filtering>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <!--    包含资源            -->
                <includes>
                    <include>**/**</include>
                </includes>
                <!--     排除资源            -->
                <excludes>
                    <exclude>**/*.bmp</exclude>
                    <exclude>**/*.jpg</exclude>
                    <exclude>**/*.jpeg</exclude>
                    <exclude>**/*.gif</exclude>
                </excludes>
                <filtering>true</filtering>
            </resource>
        </resources>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-resources-plugin</artifactId>
                <version>3.1.0</version>
                <configuration>
                    <!--  手动配置编码    -->
                    <encoding>UTF-8</encoding>
                    <!-- 不过滤的文件  -->
                    <nonFilteredFileExtensions>
                        <nonFilteredFileExtension>xlsx</nonFilteredFileExtension>
                        <nonFilteredFileExtension>xls</nonFilteredFileExtension>
                        <nonFilteredFileExtension>ttf</nonFilteredFileExtension>
                        <nonFilteredFileExtension>ttc</nonFilteredFileExtension>
                    </nonFilteredFileExtensions>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### maven-javadoc-plugin

Javadoc插件使用Javadoc工具为指定项目生成javadocs.

```xml
  <!--     配置文档生成       -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-javadoc-plugin</artifactId>
                <configuration>
                    <skip>${maven.javadoc.skip}</skip>
                </configuration>
                <executions>
                    <execution>
                        <id>attach-javadocs</id>
                        <phase>package</phase>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                        <!--              关闭 HTML 4.01标准规范          -->
                        <configuration>
                            <additionalOptions>
                                <additionalOption>
                                    -Xdoclint:none
                                </additionalOption>
                            </additionalOptions>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
```



### [maven-surefire-plugin](https://blog.csdn.net/u012027337/article/details/78772372)

mvn test 默认绑定的测试

```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <configuration>
                    <includes>
                        <include>**/*Test.java</include>
                        <include>**/*Spec.java</include>
                    </includes>
                </configuration>
            </plugin>
```

