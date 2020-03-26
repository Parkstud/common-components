# Docker 学习

## 走进Docker

### 面临的问题

- 开发人员需要在一台机器上搭建开发环境,需要安装mysql,redis,java,tomcat等,而且不同版本的应用程序通常配置文件需要改动,比如修改程序的配置,系统的环境变量等.
- 测试人员,对开发出来的应用进行测试,需要安装开发给的文档进行部署,配置一堆文件,然后测试,当测出来又问题之后,又是环境问题引起的.
- 多人维护同一个服务器,服务器的配置文件被修改,但是别人却不知道你改了那些配置,这样出问题的时候,需要问别人.
- 一个应用维护了很久,一直运行正常,突然机器故障.当需要重新部署的时候,由于文档不全,运维人员不知道如何部署
- 服务器资源不足,需要扩容,在运行一台服务器,运维人员需要重新安装 基础程序,修改配置,安装应用程序修改配置,任务繁琐.

### 优化策略

1. 将应用部署分为基础环境和应用环境.
   - 基础环境: 机器硬件,操作系统,提供的基础服务(ssh) .
   - 应用环境: 应用需要的软件包,和配置文件.
2. 基础环境保持一致,应用环境分解为一个个服务,每个服务分配置和软件包使用版本控制和中心仓库对包和配置进行管理

> **存在的问题**
>
> - 基础环境不易改动,应用服务依赖基础环境,如果修改可能应用服务无法工作
> - 应用环境维护成本高,如果应用服务很多,需要维护很多个配置和服务包.

### Docker解决方案

#### 如何处理不同环境部署,引起的配置问题?

在程序员世界里,比较出名的就是java,在不同操作系统上都可以运行.可以参考java的解决方案,java使用不同java虚拟机来处理java语言生成的字节码文件,不同操作系统有不同的虚拟机处理,将字节码在机器上解释执行.

Docker也借鉴了这种思想,使用容器引擎解决平台依赖问题.Docker 在宿主机上启动守护进程,屏蔽了具体的平台信息,对上层提供统一的接口,不同平台提供不同的执行驱动,存储驱动和网络驱动.

#### 如何使用,部署Docker化应用?

参考Android.它是一个开源的手机操作系统,它的App应用使用apk 方式打包 发布到安卓市场.

Docker也是一样,Docker 使用镜像(image)形式发布到任何一个具有Docker引擎的操作系统上,Docker有个官方的镜像仓库,提供进行下载,用户使用image运行.

#### Docker 如何处理版本变化

Docker 使用分层技术,将应用分为多层比如第一层是操作系统,第二层是依赖库,第三次是软件包和配置文件,如图所示:

![image.png](https://i.loli.net/2020/03/10/NOzh5s8j9wx1SUT.png)

如果配置文件有变动,它不会直接在第三层变动,会通过新加一层,使用上层的配置文件.

![image.png](https://i.loli.net/2020/03/10/vLDKNkrI3htZyAT.png)

### Docker架构

#### Docker 仓库

docker 分为Docker仓库,就是一个上传下载Docker 镜像的的网址https://hub.docker.com,有官方的镜像也有个人开发者的镜像

#### docker 程序

Docker本身是一个单机版的程序,运行在Linux系统上属于用户态通过接口和内核交互,采用C/S架构,Docker Daemon 作为Server端,以守护进程的形式运行,Docker client 可以通过在本机上执行bin命令 例如 docker run 或者远程通过restful  api 发送指令

#### Docker工作流程

比如启动一个Docker应用app

- Docker client 想Docker  Daemon 发送指令 docker run app
- 因为本机上没有app这个镜像,Docker Daemon会向Docker 仓库发送请求,下载app这个镜像
- Docker Daemon 启动app这个应用
- 将启动的结果返回Docker client

![image.png](https://i.loli.net/2020/03/11/WSQlGrH3vdtJCN6.png)

#### Docker 镜像和容器

**镜像**: 只有完整的文件系统和程序包,没有动态生成新的文件的需求

**容器**: 当下载到宿主机运行对外提供服务,有可能修改文件(比如 日志),需要要有空白层写时拷贝

#### Docker应用的存在形式

用户只关心怎么把软件运行起来,用户不关心安装软件,软件运行在什么操作系统下,那么就可以把软件依赖的环境.配置一起打包以虚拟机的形式,放到仓库供大家使用.

但是这样部署软件有一个问题,文件太大了 ,一个操作系统就好几个G,所以docker使用分层的概念 下层可以共享 比如第一层操作系统

![image.png](https://i.loli.net/2020/03/11/jYVMH9dok5zEhSI.png)

但是这样存在 冲突,比如应用B需要修改 操作系统的配置文件.Docker 约定上层和下层有相同文件和配置时上层的覆盖下层

![image.png](https://i.loli.net/2020/03/11/WxULDhsgOPYNZA8.png)

docker 的分层和写时拷贝,解决了部分问题但是,我们都知道虚拟机比较大比如我们熟悉的VMWare,VirtualBox等启动就要启动很久,并且要消耗大量的cpu资源,如何吧虚拟机做轻量呢?

OpenVZ,VServer,LXC等容器类虚拟机,是一种虚拟化内核 ,它与宿主机运行相同的内核,性能消耗很小,Docker就是采用LXC后来使用libcontainer

#### Docker 安装

https://docs.docker.com/install/linux/docker-ce/centos/#install-docker-ce

官方网站有详细安装说明 在此就不写了

安装好的docker信息

```shell
[root@chenmiao ~]# docker version
Client: Docker Engine - Community
 Version:           19.03.1
 API version:       1.40
 Go version:        go1.12.5
 Git commit:        74b1e89
 Built:             Thu Jul 25 21:21:07 2019
 OS/Arch:           linux/amd64
 Experimental:      false

Server: Docker Engine - Community
 Engine:
  Version:          19.03.1
  API version:      1.40 (minimum version 1.12)
  Go version:       go1.12.5
  Git commit:       74b1e89
  Built:            Thu Jul 25 21:19:36 2019
  OS/Arch:          linux/amd64
  Experimental:     false
 containerd:
  Version:          1.2.6
  GitCommit:        894b81a4b802e4eb2a91d1ce216b8817763c29fb
 runc:
  Version:          1.0.0-rc8
  GitCommit:        425e105d5a03fabd737a126ad93d62a9eeede87f
 docker-init:
  Version:          0.18.0
  GitCommit:        fec3683

```

启动一个容器测试

```shell
[root@chenmiao ~]# docker run hello-world
Unable to find image 'hello-world:latest' locally
latest: Pulling from library/hello-world
1b930d010525: Pull complete 
Digest: sha256:fc6a51919cfeb2e6763f62b6d9e8815acbf7cd2e476ea353743570610737b752
Status: Downloaded newer image for hello-world:latest

Hello from Docker!
This message shows that your installation appears to be working correctly.

To generate this message, Docker took the following steps:
 1. The Docker client contacted the Docker daemon.
 2. The Docker daemon pulled the "hello-world" image from the Docker Hub.
    (amd64)
 3. The Docker daemon created a new container from that image which runs the
    executable that produces the output you are currently reading.
 4. The Docker daemon streamed that output to the Docker client, which sent it
    to your terminal.

To try something more ambitious, you can run an Ubuntu container with:
 $ docker run -it ubuntu bash

Share images, automate workflows, and more with a free Docker ID:
 https://hub.docker.com/

For more examples and ideas, visit:
 https://docs.docker.com/get-started/
```



## Docker 使用

### Docker 命令

```shell
[root@chenmiao ~]# docker 

Usage:	docker [OPTIONS] COMMAND

A self-sufficient runtime for containers

Options:
      --config string      Location of client config files (default "/root/.docker")
  -c, --context string     Name of the context to use to connect to the daemon (overrides DOCKER_HOST env var and default
                           context set with "docker context use")
  -D, --debug              Enable debug mode
  -H, --host list          Daemon socket(s) to connect to
  -l, --log-level string   Set the logging level ("debug"|"info"|"warn"|"error"|"fatal") (default "info")
      --tls                Use TLS; implied by --tlsverify
      --tlscacert string   Trust certs signed only by this CA (default "/root/.docker/ca.pem")
      --tlscert string     Path to TLS certificate file (default "/root/.docker/cert.pem")
      --tlskey string      Path to TLS key file (default "/root/.docker/key.pem")
      --tlsverify          Use TLS and verify the remote
  -v, --version            Print version information and quit

Management Commands:
  builder     Manage builds
  config      Manage Docker configs
  container   Manage containers
  context     Manage contexts
  engine      Manage the docker engine
  image       Manage images
  network     Manage networks
  node        Manage Swarm nodes
  plugin      Manage plugins
  secret      Manage Docker secrets
  service     Manage services
  stack       Manage Docker stacks
  swarm       Manage Swarm
  system      Manage Docker
  trust       Manage trust on Docker images
  volume      Manage volumes

Commands:
  attach      Attach local standard input, output, and error streams to a running container
  build       Build an image from a Dockerfile
  commit      Create a new image from a container's changes
  cp          Copy files/folders between a container and the local filesystem
  create      Create a new container
  diff        Inspect changes to files or directories on a container's filesystem
  events      Get real time events from the server
  exec        Run a command in a running container
  export      Export a container's filesystem as a tar archive
  history     Show the history of an image
  images      List images
  import      Import the contents from a tarball to create a filesystem image
  info        Display system-wide information
  inspect     Return low-level information on Docker objects
  kill        Kill one or more running containers
  load        Load an image from a tar archive or STDIN
  login       Log in to a Docker registry
  logout      Log out from a Docker registry
  logs        Fetch the logs of a container
  pause       Pause all processes within one or more containers
  port        List port mappings or a specific mapping for the container
  ps          List containers
  pull        Pull an image or a repository from a registry
  push        Push an image or a repository to a registry
  rename      Rename a container
  restart     Restart one or more containers
  rm          Remove one or more containers
  rmi         Remove one or more images
  run         Run a command in a new container
  save        Save one or more images to a tar archive (streamed to STDOUT by default)
  search      Search the Docker Hub for images
  start       Start one or more stopped containers
  stats       Display a live stream of container(s) resource usage statistics
  stop        Stop one or more running containers
  tag         Create a tag TARGET_IMAGE that refers to SOURCE_IMAGE
  top         Display the running processes of a container
  unpause     Unpause all processes within one or more containers
  update      Update configuration of one or more containers
  version     Show the Docker version information
  wait        Block until one or more containers stop, then print their exit codes

Run 'docker COMMAND --help' for more information on a command.

```

- **Dockr 指令基本用法**

`docker +(command)+[arg...]`

- **批量停止容器**

`docker stop $(docker ps -a -q)`

- **批量删除停止的容器**

`docker rm $(docker ps -a -q)`

- **查询镜像**

`docker search (镜像名称)`

- **容器创建新镜像**

`docker commit (容器ID) (用户名/镜像名) `

`docker build -t [容器名] .`

- **查询容器信息**

`docker inspect [-f {{.xx}}] (容器名)`

- **启动容器**

`docker start (容器id) `

- **容器日志**

`docker logs (容器名)`

- **容器网络 内存 cpu信息**

`docker stats (容器名)`

- **容器内部命令**

`docker exec  (容器名)  容器执行命令`

`docker exec -it (容器名) /bin/bash`

- **登陆docker hub**

`docker login`

- **上传镜像**

`docker push (镜像名)`

- **查看docker信息(包括swarm)**

`docker info`

- **查看集群节点信息**

`docker node ls`

### Dockerfile

Docker镜像有两种特性

- **已有的分层只能读不能修改**

- **上层的镜像优先高于底层镜像**

从镜像A 如何修改文件a.txt 生成镜像A'

1. 基于镜像A,启动容器M1
2. M1中修改a.txt的内容
3. docker commit 命令提交生成新的镜像A'

这样就与问题了,会让镜像的层数越来越多,达到联合文件系统允许的最多层数(aufs最多128),解决办法是使用Dockerfile

**语法规则**

每行以关键字为行首,如果内容很长,使用\ 把多行连接到一起

- **`FROM`** 表示基础镜像开始
- **`MAINTAINER`**镜像创建这
- **`ENV`**环境变量
- **`RUN`** 允许shell命令 ,多条命令使用`&&`连接
- **`COPY`** 将编译机本地文件拷贝到镜像文件系统中
- **`EXPOSE`** 指定监听端口
- **`WORKDIR`**WORKDIR指令用于指定容器的一个目录， 容器启动时执行的命令会在该目录下执行。
- **`VOLUME`** 数据卷,让容器从宿主机中读取文件或持久化数据到宿主机
- **`ENTRYPOINT`**与上面的指令不同,这个事预执行命令,创建镜像时不执行,使用容器启动后执行命令

**1. 编写Dockerfile**

```dockerfile
FROM sameersbn/ubuntu:14.04.20160121
MAINTAINER parkstud@qq.com

ENV REDIS_USER=redis \
	REDIS_DATA_DIR=/var/lib/redis \
	REDIS_LOG_DIR=/var/log/redis

RUN apt-get update \
&& DENIAN_FRONTEND=noninteractive apt-get install -y redis-server \
&& sed 's/^daemonize yes/daemonize no/' -i /etc/redis/redis.conf \
&& sed 's/^bind 127.0.0.1/bind 0.0.0.0/' -i /etc/redis/redis.conf \
&& sed 's/^# unixsocket /unixsocket /' -i /etc/redis/redis.conf \
&& sed 's/^# unixsocketperm 755/unixsocketperm 777/'  -i /etc/redis/redis.conf \
&& sed '/^logfile/d' -i  /etc/redis/redis.conf \
&& rm -rf /var/lib/apt/lists/*

COPY entrypoint.sh /sbin/entrypoint.sh
RUN chmod 755 /sbin/entrypoint.sh

EXPOSE 6379/tcp
VOLUME ["${REDIS_DATA_DIR}"]
ENTRYPOINT ["/sbin/entrypoint.sh"]
```

**2. 编写entrypoint.sh**

```shell
#!/bin/bash
set -e

REDIS_PASSWORD=${REDIS_PASSWORD:-}

map_redis_uid(){
	USERMAP_ORIG_UID=$(id -u -redis)
	USERMAP_ORIG_GID=$(id -g -redis)
	USERMAP_GID=${USERMAP_GID:-${USERMAP_UID:-$USERMAP_ORIG_GID}}
	USERMAP_GID=${USERMAP_UID:-$USERMAP_ORIG_UID}
	if [ "${USERMAP_UID}" != "${USERMAP_ORIG_UID}" ] || [ "${USERMAP_GID}" != "#{USERMAP_ORIG_GID}" ]; then
		echo "Adapting uid and gid for redis:redis"
		groupmod -g "${USERMAP_GID}" redis
		sed -i -e "s/:${USERMAP_ORIG_UID}:${USERMAP_GID}:/:${USERMAP_UID}:${USERMAP_GID}:/" /etc/passwd
	fi
}

create_socket_dir(){
	mkdir -p /run/redis
	chmod -R 0755 /run/redis
	chown -R ${REDIS_USER}:${REDIS_USER} /run/redis
}

create_data_dir(){
	mkdir -p ${REDIS_DATA_DIR}
	chmod -R 0755 ${REDIS_DATA_DIR}
	chown -R ${REDIS_USER}:${REDIS_USER} ${REDIS_DATA_DIR}
}

create_log_dir(){
	mkdir -p ${REDIS_LOG_DIR}
	chmod -R 0755 ${REDIS_LOG_DIR}
	chown -R ${REDIS_USER}:${REDIS_USER} ${REDIS_LOG_DIR}
}

map_redis_uid
create_socket_dir
create_data_dir
create_log_dir

# allow arguments to be passed to redis-server

if [[ ${1:0:1} = '-' ]]; then
	EXTRA_ARGS="$@"
	set --
fi

# default behaviour is to launch redis-server
if [[ -z $(1) ]]; then
	echo "Strting redis-server"
	exec start-stop-daemon --start --chuid ${REDIS_USER}:${REDIS_USER} -exec \
	${which redis-server} -- /etc/redis/redis.conf ${REDIS_PASSWORD:+--requirepass ${EXTRA_ARGS}}
else
	exec "$@"
fi


```

**3. 制作镜像**

```shell
[root@chenmiao image_redis]# docker build -t image_redis:v1.0 .
Sending build context to Docker daemon  4.608kB
Step 1/9 : FROM sameersbn/ubuntu:14.04.20160121
14.04.20160121: Pulling from sameersbn/ubuntu
[DEPRECATION NOTICE] registry v2 schema1 support will be removed in an upcoming release. Please contact admins of the docker.io registry NOW to avoid future disruption.
8387d9ff0016: Already exists 
3b52deaaf0ed: Already exists 
4bd501fad6de: Already exists 
a3ed95caeb02: Already exists 
012013682669: Already exists 
Digest: sha256:7a9612516cdb0f173581061d58b77bd5176056859fa882d1a8366735005085d9
Status: Downloaded newer image for sameersbn/ubuntu:14.04.20160121
 ---> 4dc780eb0d90
Step 2/9 : MAINTAINER parkstud@qq.com
 ---> Running in 3881ca681751
Removing intermediate container 3881ca681751
 ---> 8601b71014bf
Step 3/9 : ENV REDIS_USER=redis 	REDIS_DATA_DIR=/var/lib/redis 	REDIS_LOG_DIR=/var/log/redis
 ---> Running in 85b4c38d30ae
Removing intermediate container 85b4c38d30ae
 ---> 05364eb1c1a3
Step 4/9 : RUN apt-get update && DENIAN_FRONTEND=noninteractive apt-get install -y redis-server && sed 's/^daemonize yes/daemonize no/' -i /etc/redis/redis.conf && sed 's/^bind 127.0.0.1/bind 0.0.0.0/' -i /etc/redis/redis.conf && sed 's/^# unixsocket /unixsocket /' -i /etc/redis/redis.conf && sed 's/^# unixsocketperm 755/unixsocketperm 777/'  -i /etc/redis/redis.conf && sed '/^logfile/d' -i  /etc/redis/redis.conf && rm -rf /var/lib/apt/lists/*
 ---> Running in b3e36541ad64
Ign http://archive.ubuntu.com trusty InRelease
Get:1 http://archive.ubuntu.com trusty-updates InRelease [65.9 kB]
Get:2 http://archive.ubuntu.com trusty-security InRelease [65.9 kB]
Get:3 http://archive.ubuntu.com trusty Release.gpg [933 B]
Get:4 http://archive.ubuntu.com trusty Release [58.5 kB]
Get:5 http://archive.ubuntu.com trusty-updates/main Sources [532 kB]
Get:6 http://archive.ubuntu.com trusty-updates/restricted Sources [6444 B]
Get:7 http://archive.ubuntu.com trusty-updates/universe Sources [288 kB]
Get:8 http://archive.ubuntu.com trusty-updates/main amd64 Packages [1460 kB]
Get:9 http://archive.ubuntu.com trusty-updates/restricted amd64 Packages [21.4 kB]
Get:10 http://archive.ubuntu.com trusty-updates/universe amd64 Packages [671 kB]
Get:11 http://archive.ubuntu.com trusty-security/main Sources [220 kB]
Get:12 http://archive.ubuntu.com trusty-security/restricted Sources [5050 B]
Get:13 http://archive.ubuntu.com trusty-security/universe Sources [126 kB]
Get:14 http://archive.ubuntu.com trusty-security/main amd64 Packages [1032 kB]
Get:15 http://archive.ubuntu.com trusty-security/restricted amd64 Packages [18.1 kB]
Get:16 http://archive.ubuntu.com trusty-security/universe amd64 Packages [377 kB]
Get:17 http://archive.ubuntu.com trusty/main Sources [1335 kB]
Get:18 http://archive.ubuntu.com trusty/restricted Sources [5335 B]
Get:19 http://archive.ubuntu.com trusty/universe Sources [7926 kB]
Get:20 http://archive.ubuntu.com trusty/main amd64 Packages [1743 kB]
Get:21 http://archive.ubuntu.com trusty/restricted amd64 Packages [16.0 kB]
Get:22 http://archive.ubuntu.com trusty/universe amd64 Packages [7589 kB]
Fetched 23.6 MB in 1min 19s (297 kB/s)
Reading package lists...
Reading package lists...
Building dependency tree...
Reading state information...
The following extra packages will be installed:
  libjemalloc1 redis-tools
The following NEW packages will be installed:
  libjemalloc1 redis-server redis-tools
0 upgraded, 3 newly installed, 0 to remove and 93 not upgraded.
Need to get 412 kB of archives.
After this operation, 1272 kB of additional disk space will be used.
Get:1 http://archive.ubuntu.com/ubuntu/ trusty/universe libjemalloc1 amd64 3.5.1-2 [76.8 kB]
Get:2 http://archive.ubuntu.com/ubuntu/ trusty-updates/universe redis-tools amd64 2:2.8.4-2ubuntu0.2 [66.8 kB]
Get:3 http://archive.ubuntu.com/ubuntu/ trusty-updates/universe redis-server amd64 2:2.8.4-2ubuntu0.2 [269 kB]
debconf: unable to initialize frontend: Dialog
debconf: (TERM is not set, so the dialog frontend is not usable.)
debconf: falling back to frontend: Readline
debconf: unable to initialize frontend: Readline
debconf: (This frontend requires a controlling tty.)
debconf: falling back to frontend: Teletype
dpkg-preconfigure: unable to re-open stdin: 
Fetched 412 kB in 9s (44.3 kB/s)
Selecting previously unselected package libjemalloc1.
(Reading database ... 11875 files and directories currently installed.)
Preparing to unpack .../libjemalloc1_3.5.1-2_amd64.deb ...
Unpacking libjemalloc1 (3.5.1-2) ...
Selecting previously unselected package redis-tools.
Preparing to unpack .../redis-tools_2%3a2.8.4-2ubuntu0.2_amd64.deb ...
Unpacking redis-tools (2:2.8.4-2ubuntu0.2) ...
Selecting previously unselected package redis-server.
Preparing to unpack .../redis-server_2%3a2.8.4-2ubuntu0.2_amd64.deb ...
Unpacking redis-server (2:2.8.4-2ubuntu0.2) ...
Processing triggers for ureadahead (0.100.0-16) ...
Setting up libjemalloc1 (3.5.1-2) ...
Setting up redis-tools (2:2.8.4-2ubuntu0.2) ...
Setting up redis-server (2:2.8.4-2ubuntu0.2) ...
invoke-rc.d: policy-rc.d denied execution of start.
Processing triggers for libc-bin (2.19-0ubuntu6.6) ...
Processing triggers for ureadahead (0.100.0-16) ...
Removing intermediate container b3e36541ad64
 ---> 3ae5d4280056
Step 5/9 : COPY entrypoint.sh /sbin/entrypoint.sh
 ---> 5b24de87a212
Step 6/9 : RUN chmod 755 /sbin/entrypoint.sh
 ---> Running in f712cecd4f70
Removing intermediate container f712cecd4f70
 ---> 881e23dcb3e2
Step 7/9 : EXPOSE 6379/tcp
 ---> Running in 9036a701d3df
Removing intermediate container 9036a701d3df
 ---> 97679c5b42c6
Step 8/9 : VOLUME ["${REDIS_DATA_DIR}"]
 ---> Running in 68e33416d428
Removing intermediate container 68e33416d428
 ---> 7c1df7d25068
Step 9/9 : ENTRYPOINT ["/sbin/entrypoint.sh"]
 ---> Running in 6910cc9a9c4d
Removing intermediate container 6910cc9a9c4d
 ---> 5cf18c9c2dfe
Successfully built 5cf18c9c2dfe
Successfully tagged image_redis:v1.0

```

### Docker 仓库

Docker 官方维护了一个公有仓库Docker Hub,如果只是学习Docker,Dockerhub足够使用了,但是如果想建立Docker的pass平台就不行了

1. 公司环境无法访问外网
2. 程序放在公有仓库不安全
3. 网络速度

#### 搭建私有镜像仓库

##### 使用docker registry镜像

docker registry镜像监听5000端口

`docker run -p 8080:5000 registry`

设置参数

`docker run \`

​	`-e SETTINGS_FLAVOR=s3 \`

​	`-e AWS_BUCKET=mybucket \`

​	`-e STORAGE_PATH=/registry \`

​	`-e AWS_KEY=myawskey \`

​	`-e SEARCH_BACKEND=sqlalchemy \`

​	`-p 8080:5000 \`

`registry`

##### 使用rpm包

`# yml install docker-registry -y`

`# service docker-registry start`

`# service docker-registry status`

##### 配置文件

docker-registry使用config_sample.yml 配置,rpm方式使用/etc/docker-registry.yml

config_sample.yml的实例

**comm: 公共基础设置,其他模块可以引入**

**local: 存储数据到本地系统**

**s3:存储数据到aws s3**

**ceph-s3:通过Ceph对象网关将数据存储到Ceph集群**

**dev: 使用local模板的基本设置**

**test:测试环境**

**prod:生产环境**

**gcs: 存储数据到Googgle**

### Docker 网络

Docker在启动的时候,默认自动创建网桥设备Docker0,docker启动容器.会创建一对veth虚拟网络设备,并将其中一个veth网络附加到docker0 另一个加入容器的网络名字空间 eth0,这样只是解决Host内部容器之间的通信

要解决容器访问外部网络,docker创建MASQUEREDE规则

-tant -A POSTROUTING -s 172.17.0.0/16 ! -o docker0 -j MASQUEREADE

将容器出发,目的地址为HOST外部网络的包的IP都修改成HOST的IP

外部网络访问容器,创建SNAT规则

docker run -d -p 80:80 apache

iptables -t ant -A PREROUTING -m addrtype --dst-type LOCAL -j DOCKER

iptables -t ant-A DOCKET ! -i docker0 -p tcp -m tcp --dport89 -j DNAT --to-destinaion 172.17.0.2:80

![image.png](https://i.loli.net/2020/03/17/Pfr7JoSiuTL23W1.png)

#### 网络配置

##### Docker进程网络配置

- `-b/--bridge` : 指定Docker使用的网桥设备,默认使用docker0
- `--bio`: 指定网桥设备的docker0的IP和掩码,使用CIDR形式 入192.168.1.5/24
- `--dns/--dns-search`: 配置容器的DNS

##### docker run 网络配置

**--net** 用于指定容器的网络通信方式,可以有下面4个值

- `bridge`:Docker中的默认方式
- `none`:容器没有网络栈,无法与外界通信
- `container:(name/id)`: 使用其他容器的网络栈
- `host`:表示容器使用Host网络,没有自己独立的网络栈



### Docker数据管理

docker容器一旦被删除,容器本身的文件系统也会被删除,但我们希望有些数据不要被删除进行持久化处理

Docker 提供了数据卷,可以持久化数据以及容器之间共享数据.

`-v` 参数给容器创建数据卷 [host-dir]:[container-dir]:[rw/ro]

- `host-dir`:Host上的目录,如果不存在Docker自动创建
- `container-dir`:容器内部对应的目录,不存在docker内部创建该目录
- `rw|ro`: 用于控制卷的读写

```shell
[root@chenmiao ~]# docker run -it --rm -v /data/volumel:/volumel ubuntu:14.04  /bin/bash
root@bdb8efd872ae:/# df -lh
Filesystem      Size  Used Avail Use% Mounted on
overlay          40G   14G   24G  36% /
tmpfs            64M     0   64M   0% /dev
tmpfs           920M     0  920M   0% /sys/fs/cgroup
shm              64M     0   64M   0% /dev/shm
/dev/vda1        40G   14G   24G  36% /volumel
tmpfs           920M     0  920M   0% /proc/acpi
tmpfs           920M     0  920M   0% /proc/scsi
tmpfs           920M     0  920M   0% /sys/firmware
root@bdb8efd872ae:/# echo "hello" > /volumel/hello.txt
root@bdb8efd872ae:/# exit
exit
[root@chenmiao ~]# cat /data/volumel/hello.txt 
hello


```

挂载时区

```shell
[root@chenmiao ~]# date +%z
+0800
[root@chenmiao ~]# docker run -it --rm ubuntu:14.04 /bin/bash
root@20c9ac31e4fc:/# date +%z
+0000
root@20c9ac31e4fc:/# exit    
exit
[root@chenmiao ~]# docker run -it --rm -v /etc/localtime:/etc/localtime  ubuntu:14.04 /bin/bash
root@0f3d6306fa11:/# date +%z
+0800
root@0f3d6306fa11:/# 

```

#### 数据卷容器

多个容器间共享host的文件.

创建一个dbdata容器,包含数据卷/dbdata

```shell
[root@chenmiao ~]# docker run -d --rm -v /dbdata --name dbdata training/postgres echo \
> Data-only container for postgres

-- 其他容器挂载

[root@chenmiao ~]# docker run -d --volumes-from dbdata --name bd1 training/postgres
[root@chenmiao ~]# docker run -d --volumes-from dbdata --name bd2 training/postgres
[root@chenmiao ~]# docker run -d --volumes-from bd1 --name bd3 training/postgres


```



### Docker应用 

#### 安装WordPress

wordPress是一个功能强大的个人博客系统,使用WordPress可以快速搭建博客网站.

1. **安装db**

```shell
[root@chenmiao ~]# docker run --name db --env MYSQL_ROOT_PASSWORD=example -d mariadb
Unable to find image 'mariadb:latest' locally
latest: Pulling from library/mariadb
423ae2b273f4: Pulling fs layer 
de83a2304fa1: Pulling fs layer 
f9a83bce3af0: Pulling fs layer 
b6b53be908de: Pulling fs layer 
2b41ae57cefb: Pulling fs layer 
7ecd5cacc370: Pulling fs layer 
9f96ac6b2583: Pull complete 
9224e6c8f841: Pull complete 
8fdc4c2808be: Pull complete 
a2ae8752de58: Pull complete 
5adda6a0eec5: Pull complete 
c3b660834848: Pull complete 
1b16e5f6713a: Pull complete 
3465aee3f57d: Pull complete 
Digest: sha256:d1ceee944c90ee3b596266de1b0ac25d2f34adbe9c35156b75bcb9a7047c7545
Status: Downloaded newer image for mariadb:latest
bc8cd9c61af74d547f7c48bdaff2b89dcee7003292e61af433f45449e9efcfc2
	
```

 **命令解释**

- `docker run` 是Docker指令 后面的内容是参数
- 启动一个mariadb数据库(Mysql分支) 数据库root 密码example  数据库的名称为db
- `--env` 表示设置环境变量,-`d` 参数 表示mariadb数据库后台运行


2. **安装wordPress**

```shell
 docker run --name MyWordPress --link db:mysql -p 8080:80 -d wordpress
Unable to find image 'wordpress:latest' locally
latest: Pulling from library/wordpress
68ced04f60ab: Pulling fs layer 
1d2a5d8fa585: Pulling fs layer 
5d59ec4ae241: Pulling fs layer 
d42331ef4d44: Pulling fs layer 
408b7b7ee112: Pull complete 
570cd47896d5: Pull complete 
2419413b2a16: Pull complete 
8c722e1dceb9: Pull complete 
34fb68439fc4: Pull complete 
e775bf0f756d: Pull complete 
b1949a1e9661: Pull complete 
6ed8bcec42ae: Pull complete 
f6247da7d55f: Pull complete 
a090bafe99ea: Pull complete 
1499724c614a: Pull complete 
838e071223d3: Pull complete 
4f3f081f645a: Pull complete 
5727cb8d10d6: Pull complete 
77e0ad51ba4d: Pull complete 
00c188d7a522: Pull complete 
0421cc6f1038: Pull complete 
Digest: sha256:6e17ef2ddd5ec3a0d4c8e86df409dc702db205330823df70518ce3f192e9b6c7
Status: Downloaded newer image for wordpress:latest
e5f37a1f6c5d5f7dc76fff51ea817655278d9adb577a1d641175b3a62b9ee006

```
**命令解释**

- `--link db:mysql`表示WordPress与数据建立连接
- WordPress在容器中使用端口80 对外服务,宿主机中我们使用8080 端口映射 `-p 8080:80`

3. **访问界面**

![image.png](https://i.loli.net/2020/03/11/JPA2DaGNihtBn7j.png)

####  搭建Gitlab

GitLab是一个类GitHub的开源的代码管理工具,实现了GitHub大部分的功能,可以本地部署,搭建公司内部的版本控制.

使用sameersbn/docker-gitlab

运行环境主要包括

- postgresql 数据库
- redis
- gitlab

1. **启动postgresql**

```shell
[root@chenmiao ~]# docker run --name gitlab-postgresql -d \
> --env 'DB_NAME=gitlabhq_production' \
> --env 'DB_USER=gitlab' \
> --env 'DB_PASS=password' \
> sameersbn/postgresql:9.4-12
Unable to find image 'sameersbn/postgresql:9.4-12' locally
9.4-12: Pulling from sameersbn/postgresql
[DEPRECATION NOTICE] registry v2 schema1 support will be removed in an upcoming release. Please contact admins of the docker.io registry NOW to avoid future disruption.
8387d9ff0016: Pull complete 
3b52deaaf0ed: Pull complete 
4bd501fad6de: Pull complete 
a3ed95caeb02: Pull complete 
012013682669: Pull complete 
798cd0ed4a93: Pull complete 
69dd52628887: Pull complete 
e15547401b0e: Pull complete 
Digest: sha256:05227ee56e789cc38db3f2882bf1e0beb138785e9de1634b4b25c3d238c9536d
Status: Downloaded newer image for sameersbn/postgresql:9.4-12
6d8984b7e6dc2e373194d96367d75c6c13026e3b2060c30a4607aee2028b3ee6

```



2. **启动redis**

```shell
[root@chenmiao ~]# docker run --name gitlab-redis -d sameersbn/redis:latest
Unable to find image 'sameersbn/redis:latest' locally
latest: Pulling from sameersbn/redis
5b7339215d1d: Pull complete 
14ca88e9f672: Pull complete 
a31c3b1caad4: Pull complete 
b054a26005b7: Pull complete 
4aaa6aa96b16: Pull complete 
8b7229652d38: Pull complete 
Digest: sha256:50d3d6ec3c441f662108a7f28456bfc35adb5f705a02b6fd8d96b6fa9a16aeb4
Status: Downloaded newer image for sameersbn/redis:latest
c5d769128fe5fcf25f080b18530c782472d3c679e63cb5e3deaf35309d3799e5

```



3. **启动gitlab**

```
[root@chenmiao ~]# docker run --name gitlab -d \
> --link gitlab-postgresql:postgresql --link gitlab-redis:redisio \
> --publish 8080:80 --publish 8022:22 \
> --env 'GITLAB_PORT=8080' --env 'GITLAB_SSH_PORT=8022' \
> --env 'GITLAB_SECRETS_DB_KEY_BASE=long-and-random-alpha-numeric-string' \
> sameersbn/gitlab:8.4.4
Unable to find image 'sameersbn/gitlab:8.4.4' locally
8.4.4: Pulling from sameersbn/gitlab
[DEPRECATION NOTICE] registry v2 schema1 support will be removed in an upcoming release. Please contact admins of the docker.io registry NOW to avoid future disruption.
8387d9ff0016: Already exists 
3b52deaaf0ed: Already exists 
4bd501fad6de: Already exists 
a3ed95caeb02: Pull complete 
012013682669: Already exists 
17608004c9c9: Pull complete 
3b732d8080f7: Pull complete 
7811d16e0d67: Pull complete 
7f4db642c505: Pull complete 
de68371bb239: Pull complete 
Digest: sha256:aac60228d9ee5031a70bf1884cd0b4dd87f8ae1ca522314873ac42d423504fc6
Status: Downloaded newer image for sameersbn/gitlab:8.4.4
ac0cab95bffff6309aa73b71594368edfd6f5b8040769dfec1a3b8067c9c787b

```

**Docker 指令中参数标示符可以重复使用比如传递多个环境变量,就使用了多个 --env**

4. 访问界面,默认用户密 root 默认密码 5iveL!fe

![image.png](https://i.loli.net/2020/03/11/7rYaEpwTR2tGgxW.png)



#### 搭建项目管理系统-Redmine

Redmine是一个跨平台的项目管理系统 ,通过项目的形式把任务,文档讨论及各种资源组织在一起.搭建Redmine服务,使用sameersbn/redmine镜像,项目地址https://github.com/sameersbn/docker-redmine

1. 启动数据库

```shell
[root@chenmiao ~]# docker run --name=postgresql-redmine -d \
> --env='DB_NAME=redmine-production' \
> --env='DB_USER=redmine' \
> --env='DB_PASS=password' \
> sameersbn/postgresql:9.4-12
ead97367a16aa87ab47b7e576098cb309d4a0f0adcd8eec6632a42ccf0437a45

```



2. 启动redmine

```shell
[root@chenmiao ~]# docker run --name=redmine -d --link=postgresql-redmine:postgresql --publish=8080:80 \
> --env='REDMINE_PORT=8080' \
> sameersbn/redmine:3.2.0-4
Unable to find image 'sameersbn/redmine:3.2.0-4' locally
3.2.0-4: Pulling from sameersbn/redmine
[DEPRECATION NOTICE] registry v2 schema1 support will be removed in an upcoming release. Please contact admins of the docker.io registry NOW to avoid future disruption.
8387d9ff0016: Already exists 
3b52deaaf0ed: Already exists 
4bd501fad6de: Already exists 
a3ed95caeb02: Pull complete 
012013682669: Already exists 
16325a98f6c8: Pull complete 
b7c84814a063: Pull complete 
d242f6983f8d: Pull complete 
8f7aa319f1e8: Pull complete 
0085fca6a23b: Pull complete 
b48b8dd1efcd: Pull complete 
Digest: sha256:3df4b64773a022cb7920d7a04e2303ada7385c5549db9f069d20267590f33a50
Status: Downloaded newer image for sameersbn/redmine:3.2.0-4
0c6d63156ebb2b556e75c711ecea5ac940d5fcbafd9db4b7e70865208a1be1dc

```

3. 访问 8080端口 默认用户名 admin 默认密码admin

![image.png](https://i.loli.net/2020/03/11/IwVy2jAi6fQUY3n.png)



### Docker Compose

对于WordPress 需要依次启动两个容器 docker start db 和docker start MyWordPress ,Docker提供 --link建立容器之间的互联,但是前提条件是B 容器运行时必须要A容器已经运行, 关闭A容器要先关B容器.

Docker Compose是一个容器编排工具,允许用户以yaml格式定义一组相关联的应用容器.

#### 命令

1. **启动当前文件夹下docker-compose.yml文件**

- `docker-compose up [-d]`
- `docker-compose start`

2. **停止命令**

- `docker-compose stop`
- `docker-compose -f docker-compose.yml stop`

3. **查看一个docker-compose的项目**

- `docker-compose -f docker-compose.yml  ps` 

3. **删除项目**

- `docker-compose -f docker-compose.yml down`

##### 案例

######  启动worpress

1. 编辑docker-compose.yml

```yml
wordpress:
 image: wordpress
 links:
   - db:mysql
 ports:
   - 8080:80
db: 
 image: mariadb
 environment:
   MYSQL_ROOT_PASSWORD: examle

```



1. 启动 
   - docker-compose up
2. 结果

```shell
[root@chenmiao wordpress]# docker-compose up
Creating wordpress_db_1 ... done
Creating wordpress_wordpress_1 ... done
Attaching to wordpress_db_1, wordpress_wordpress_1
db_1         | 2020-03-14 08:49:50+00:00 [Note] [Entrypoint]: Entrypoint script for MySQL Server 1:10.4.12+maria~bionic started.
db_1         | 2020-03-14 08:49:51+00:00 [Note] [Entrypoint]: Switching to dedicated user 'mysql'
db_1         | 2020-03-14 08:49:51+00:00 [Note] [Entrypoint]: Entrypoint script for MySQL Server 1:10.4.12+maria~bionic started.
wordpress_1  | WordPress not found in /var/www/html - copying now...
db_1         | 2020-03-14 08:49:52+00:00 [Note] [Entrypoint]: Initializing database files
db_1         | 
db_1         | 
db_1         | PLEASE REMEMBER TO SET A PASSWORD FOR THE MariaDB root USER !
db_1         | To do so, start the server, then issue the following commands:
.......
......
另一个终端显示
[root@chenmiao ~]# docker ps -a
CONTAINER ID        IMAGE               COMMAND                  CREATED              STATUS              PORTS                  NAMES
ae5232e8bc3d        wordpress           "docker-entrypoint.s…"   About a minute ago   Up About a minute   0.0.0.0:8080->80/tcp   wordpress_wordpress_1
20fb83bed473        mariadb             "docker-entrypoint.s…"   About a minute ago   Up About a minute   3306/tcp               wordpress_db_1

```

###### gitlab案例

1. docker-compose文件

```yaml
postgresql:
  image: sameersbn/postgresql:9.4-12
  environment:
    - DB_USER=gitlab
    - DB_PASS=password
    - DB_NAME=gitlabhq_production
redis:
  image: sameersbn/redis:latest
gitlab:
  image: sameersbn/gitlab:8.4.4
  links: 
    - redis:redisio
    - postgresql:postgresql
  ports:
    - "8080:80"
    - "8022:22"
  environment:
    - GITLAB_PORT=8080
    - GITLAB_SSH_PORT=8022
    - GITLAB_SECRETS_DB_KEY_BASE=long-and-random-alphanumeric-string
```



2. 启动

```shell
[root@chenmiao gitlab]# docker-compose up -d
Creating gitlab_redis_1      ... done
Creating gitlab_postgresql_1 ... done
Creating gitlab_gitlab_1     ... done
[root@chenmiao gitlab]# docker ps
CONTAINER ID        IMAGE                         COMMAND                  CREATED             STATUS              PORTS                                                 NAMES
cbe402014d87        sameersbn/gitlab:8.4.4        "/sbin/entrypoint.sh…"   8 seconds ago       Up 6 seconds        443/tcp, 0.0.0.0:8022->22/tcp, 0.0.0.0:8080->80/tcp   gitlab_gitlab_1
44752f4bbe86        sameersbn/redis:latest        "/sbin/entrypoint.sh"    9 seconds ago       Up 7 seconds        6379/tcp                                              gitlab_redis_1
16bfeb0edb21        sameersbn/postgresql:9.4-12   "/sbin/entrypoint.sh"    9 seconds ago       Up 7 seconds        5432/tcp                                              gitlab_postgresql_1
[root@chenmiao gitlab]# 

```

3. 查看

```shell
[root@chenmiao gitlab]# docker-compose -f docker-compose.yml ps
       Name                      Command              State                          Ports                       
-----------------------------------------------------------------------------------------------------------------
gitlab_gitlab_1       /sbin/entrypoint.sh app:start   Up      0.0.0.0:8022->22/tcp, 443/tcp, 0.0.0.0:8080->80/tcp
gitlab_postgresql_1   /sbin/entrypoint.sh             Up      5432/tcp                                           
gitlab_redis_1        /sbin/entrypoint.sh             Up      6379/tcp                                           
[root@chenmiao gitlab]# 

```

### Docker Swarm

Docker公司推出的容器集群项目,在Docker Engine上内嵌集群管理功能,并新增了集群管理的用户接口.

#### 核心设计

- Docker Engine 内嵌Swarmkit 提供集群管理

- Swarmkit 所有节点对等,每个节点可选择转化Manger或者Worker,Manger 内嵌raft协议实现高可用,并存储集群状态

- 集群针对微服务模型设计,Service 表示作业, Task表示作业副本,一个Service可以包含多个Task,一个Task是一个容器,同一个Service的所有Task状态对等

  - 声明式的Service状态定义,Servie 定义Task 希望维持的状态
  - 支持Task扩容
  - 自动容错, 一个worker 节点挂了,容器自动迁移到其他Worker节点
  - 支持灰度升级

- 支持跨主机模型

  - 依赖Libnetwork 项目实现集群网络
  - 基于Vxlan协议实现SDN
  - 使用Docker NAT访问外网
  - 基于DNS 服务也LVS技术实现服务发现和负载均衡

- 每个节点使用TLS通信

  - TLS 证书周期滚动,Manager节点下发

  ![image.png](https://i.loli.net/2020/03/18/52orkFL8hSmWgzf.png)

#### 搭建集群

一个Manager和2个workrer 节点组成的集群,需要三台机器,或者虚拟机,安装DOcker engine ,这里使用Docker macheine 创建(普通阿里云无法创建虚拟机)

```shell
# 创建三个虚拟机
$ docker-machine create -d virtualbox manager1
$ docker-machine create -d virtualbox worker1
$ docker-machine create -d virtualbox worker2
# 查看虚拟机
$ docker-machine ls
NAME       ACTIVE   DRIVER       STATE     URL                         SWARM   DOCKER     ERRORS
default    *        virtualbox   Running   tcp://192.168.99.100:2376           v19.03.1
manager1   -        virtualbox   Running   tcp://192.168.99.101:2376           v19.03.1
worker1    -        virtualbox   Running   tcp://192.168.99.102:2376           v19.03.1
worker2    -        virtualbox   Running   tcp://192.168.99.103:2376           v19.03.1

# 切换manager1
$ eval $(docker-machine env manager1)
$ docker-machine active
manager1

# 创建swarm集群,manager1成为dokcer的Manager角色
$ docker swarm init --advertise-addr 192.168.99.101
Swarm initialized: current node (3orhdz7aa8sg85rezlpu83qtd) is now a manager.

To add a worker to this swarm, run the following command:

    docker swarm join --token SWMTKN-1-1i05k2x6s18547xcn931nm2x6pji8mydbxa8terwp4c11mec6f-do4bc8vpqikf3t83l331p2t87 192.168.99.101:2377

To add a manager to this swarm, run 'docker swarm join-token manager' and follow the instructions.

# 查看swarm节点信息
$ docker node ls
ID                            HOSTNAME            STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
3orhdz7aa8sg85rezlpu83qtd *   manager1            Ready               Active              Leader              19.03.1

# 切换worker1,worker 执行docker swarm join -token 创建worker节点
$ eval $(docker-machine env worker1)
$ docker swarm join --token SWMTKN-1-1i05k2x6s18547xcn931nm2x6pji8mydbxa8terwp4c11mec6f-do4bc8vpqikf3t83l331p2t87 192.168.99.101:2377
$ eval $(docker-machine env worker2)
$ docker swarm join --token SWMTKN-1-1i05k2x6s18547xcn931nm2x6pji8mydbxa8terwp4c11mec6f-do4bc8vpqikf3t83l331p2t87 192.168.99.101:2377

# 切换manager1 查看节点信息
$ eval $(docker-machine env manager1)
$ docker node ls
ID                            HOSTNAME            STATUS              AVAILABILITY        MANAGER STATUS      ENGINE VERSION
3orhdz7aa8sg85rezlpu83qtd *   manager1            Ready               Active              Leader              19.03.1
8mgo4wb5tl6msxr2asng73dm5     worker1             Ready               Active                                  19.03.1
safxf12ardcj8f0k3x632qz9r     worker2             Ready               Active                                  19.03.1

# 创建服务,查看,删除
$ docker service create --replicas 1 --name helloworld alpine ping \
> docker.com
rht8oqr5uc27e6owx8larnepk
overall progress: 1 out of 1 tasks
1/1: running   [==================================================>]
verify: Service converged

$ docker service ls
ID                  NAME                MODE                REPLICAS            IMAGE               PORTS
rht8oqr5uc27        helloworld          replicated          1/1                 alpine:latest

$ docker service inspect --pretty helloworld

ID:             rht8oqr5uc27e6owx8larnepk
Name:           helloworld
Service Mode:   Replicated
 Replicas:      1
Placement:
UpdateConfig:
 Parallelism:   1
 On failure:    pause
 Monitoring Period: 5s
 Max failure ratio: 0
 Update order:      stop-first
RollbackConfig:
 Parallelism:   1
 On failure:    pause
 Monitoring Period: 5s
 Max failure ratio: 0
 Rollback order:    stop-first
ContainerSpec:
 Image:         alpine:latest@sha256:ab00606a42621fb68f2ed6ad3c88be54397f981a7b70a79db3d1172b11c4367d
 Args:          ping docker.com
 Init:          false
Resources:
Endpoint Mode:  vip

$ docker service rm helloworld

# 扩容
$ docker service scale helloworld=3
helloworld scaled to 3
overall progress: 3 out of 3 tasks
1/3: running   [==================================================>]
2/3: running   [==================================================>]
3/3: running   [==================================================>]
verify: Service converged

# 灰度升级,-update-delay参数配置service 升级时间间隔 ,默认一次升级一个task,可以同时使用--update-parallelism设置升级并发数
$ docker service create --replicas 3 --name redis --update-delay 10s redis:3.0.6
$ docker service inspect --pretty redis
$ docker service update --image redis:3.0.7 redis
$ docker service ps redis

# 端口映射
$ docker service create --name my_web --replicas 3 --publish 8080:80 nginx
$ docker-machine ssh manager1

# 创建跨主机网络 Overlay
$ docker network create --driver overlay my-network
$ docker service create --replicas 3 --network my-network --name my_web nginx 
$ docker service ps nginx

# 下线 上线
$ docker node update --availability drain worker2
$ docker node update --availability active worker2

# 转换manager和worker
$ docker node promote worker1
$ docker node demoete manager1

# 移出swarm
$ docker swarm leave



```

## 疑难

- 配置远程访问

```shell
#1.添加参数
# vim /usr/lib/systemd/system/docker.service
[Service]
ExecStart=
ExecStart=/usr/bin/dockerd -H tcp://0.0.0.0:2375 -H unix://var/run/docker.sock

#2.重启
# systemctl daemon-reload
# systemctl restart docker

#3.查看
# ps -ef|grep docker

```

