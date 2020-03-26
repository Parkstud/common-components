---
author：陈苗
time：2019/2/5
email：parkstud@qq.com
---



# Zookeeper学习

[TOC]



## 什么是Zookeeper

Zookeeper主要是一个分布式服务协调框架，实现同步服务，配置维护和命名服务等分布式应用。是一个高性能的分布式数据一致性解决方案。

分布式协调服务可以在分布式系统中共享配置,协调锁资源,提供命名服务.

### 什么是CAP理论

CAP 是一个分布式理论,CAP 指的是在一个分布式系统中 ,Consistency（一致性）、 Availability（可用性）、Partition tolerance（分区容错性）这三个基本需求，最多只能同时满足其中的2个。

| 选项                              | 描述                                                         |
| --------------------------------- | ------------------------------------------------------------ |
| Consistency（一致性）             | 指数据在多个副本之间能够保持一致的特性（严格的一致性）       |
| Availability（可用性）            | 指系统提供的服务必须一直处于可用的状态，每次请求都能获取到非错的响应（不保证获取的数据为最新数据） |
| Partition tolerance（分区容错性） | 分布式系统在遇到任何网络分区故障的时候，仍然能够对外提供满足一致性和可用性的服务，除非整个网络环境都发生了故障 |

> 分区指的是:在分布式系统中，不同的节点分布在不同的子网络中，由于一些特殊的原因，这些子节点之间出现了网络不通的状态，但他们的内部子网络是正常的。从而导致了整个系统的环境被切分成了若干个孤立的区域，这就是分区。

### CAP与Zookeeper的关系

 ZooKeeper是个CP（一致性+分区容错性）的，即任何时刻对ZooKeeper的访问请求能得到一致的数据结果，同时系统对网络分割具备容错性；但是它不能保证每次服务请求的可用性。也就是在极端环境下，ZooKeeper可能会丢弃一些请求，消费者程序需要重新请求才能获得结果。

### 数据模型 

Zookeeper提供基于类似于文件系统的目录节点树方式的数据存储,但是Zookeeper并不是用来专门存储数据的，它的作用主要是用来维护和监控你存储的数据的状态变化。通过监控这些数据状态的变化，从而可以达到基于数据的集群管理。

![数据模型](https://user-gold-cdn.xitu.io/2018/5/22/16386fa1db2c2136?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

#### ZNode介绍

**`ZNode`**:Zookeeper的数据节点称为ZNode，ZNode是Zookeeper中数据的最小单元，每个ZNode都可以保存数据，同时还可以挂载子节点，因此构成了一个层次化的命名空间，称为树。

![image.png](https://i.loli.net/2020/02/05/QHbjX1igcAK8spD.png)

- **PERSISTENT**: 持久化ZNode节点，一旦创建这个ZNode点存储的数据不会主动消失，除非是客户端主动的delete
- **EPHEMERAL**: 临时ZNode节点，Client连接到Zookeeper Service的时候会建立一个Session，之后用这个Zookeeper连接实例创建该类型的znode，一旦Client关闭了Zookeeper的连接，服务器就会清除Session，然后这个Session建立的ZNode节点都会从命名空间消失。总结就是，这个类型的znode的生命周期是和Client建立的连接一样的。
- **PERSISTENT_SEQUENTIAL**: 顺序自动编号的ZNode节点，这种znoe节点会根据当前已近存在的ZNode节点编号自动加 1，而且不会随Session断开而消失。
- **EPEMERAL_SEQUENTIAL**: 临时自动编号节点，ZNode节点编号会自动增加，但是会随Session消失而消失

| Znode内容 | 描述                                                         |
| --------- | ------------------------------------------------------------ |
| **data**  | Znode存储的数据信息。                                        |
| **ACL**   | 记录Znode的访问权限，即哪些人或哪些IP可以访问本节点。        |
| **stat**  | 包含Znode的各种元数据，比如事务ID、版本号、时间戳、大小等等。 |
| **child** | 当前节点的子节点引用，类似于二叉树的左孩子右孩子。           |

> Znode并不是用来存储大规模业务数据，而是用于存储少量的状态和配置信息，**每个节点的数据最大不能超过1MB**。

操作Znode方法

| 方法            | 描述                   |
| --------------- | ---------------------- |
| **create**      | 创建节点               |
| **delete**      | 删除节点               |
| **exists**      | 判断节点是否存在       |
| **getData**     | 获得一个节点的数据     |
| **setData**     | 设置一个节点的数据     |
| **getChildren** | 获取节点下的所有子节点 |

这其中，exists，getData，getChildren属于读操作。Zookeeper客户端在请求读操作的时候，可以选择是否设置**Watch**。

#### Watcher介绍

**`Watcher`**: Watcher机制主要包括客户端线程、客户端WatcherManager、Zookeeper服务器三部分。客户端在向Zookeeper服务器注册的同时，会将Watcher对象存储在客户端的WatcherManager当中。当Zookeeper服务器触发Watcher事件后，会向客户端发送通知，客户端线程从WatcherManager中取出对应的Watcher对象来执行回调逻辑。

![](https://user-gold-cdn.xitu.io/2018/5/22/16386fa1da16f271?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

#### 内存数据

Zookeeper的数据模型是树结构，在内存数据库中，存储了整棵树的内容，包括所有的节点路径、节点数据、ACL信息，Zookeeper会定时将这个数据存储到磁盘上。

- DataTree: DataTree是内存数据存储的核心，是一个树结构，代表了内存中一份完整的数据。DataTree不包含任何与网络、客户端连接及请求处理相关的业务逻辑，是一个独立的组件。
- DataNode:  DataNode是数据存储的最小单元，其内部除了保存了结点的数据内容、ACL列表、节点状态之外，还记录了父节点的引用和子节点列表两个属性，其也提供了对子节点列表进行操作的接口。
- ZKDatabase: Zookeeper的内存数据库，管理Zookeeper的所有会话、DataTree存储和事务日志。ZKDatabase会定时向磁盘dump快照数据，同时在Zookeeper启动时，会通过磁盘的事务日志和快照文件恢复成一个完整的内存数据库。

#### ACL介绍

ACL(Access Control List )  访问控制列表,在Linux系统 ACL 分为两个维度，一个是属组，一个是权限。 子目录/文件默认继承父目录的ACL。而在Zookeeper中，ZNode的ACL是没有继承关系的，是独立控制的。Zookeeper的ACL，可以从三个维度来理解：一是权限模式 Scheme; 二是授权对象 ID; 三是权限 Permission,通常使用 `scheme:id:permission`标识一个有效的ACL信息。

在Zookeeper中涉及到分布式锁、Master选举和协调等应用场景,需要保障Zookeeper中的数据安全.

**授权模式**

| 模式   | 描述                                                         |
| ------ | ------------------------------------------------------------ |
| world  | 默认方式，它下面只有一个id, 叫anyone, world:anyone代表任何人，zookeeper中对所有人有权限的结点就是属于world:anyone的 |
| auth   | 代表已经认证通过的用户(cli中可以通过addauth digest user:pwd 来添加当前上下文中的授权用户) |
| digest | 它对应的id为username:BASE64(SHA1(password))，它需要先通过username:password形式的authentication |
| ip     | 它对应的id为客户机的IP地址，设置的时候可以设置一个ip段，比如ip:192.168.1.0/16, 表示匹配前16个bit的IP段 |
| super  | 在这种scheme情况下，对应的id拥有超级权限，可以做任何事情(cdrwa) |

### Zookeeper 网络结构

Zookeeper 的集群分为一个Leader 和其他follower,Leader 和follower通信,如果Leader挂了 Zookeeper会重新选举.

   ![image.png](https://i.loli.net/2020/02/05/izJYanytLxeVUOS.png)

#### ZAB协议

ZAB 协议是为分布式协调服务ZooKeeper专门设计的一种支持崩溃恢复的一致性协议。基于该协议，ZooKeeper 实现了一种主从模式的系统架构来保持集群中各个副本之间的数据一致性。

ZAB协议运行过程中，所有的客户端更新都发往Leader，Leader写入本地日志后再复制到所有的Follower节点。

一旦Leader节点故障无法工作，ZAB协议能够自动从Follower节点中重新选择出一个合适的替代者，这个过程被称为选主，选主也是ZAB协议中最为重要和复杂的过程。

##### 发生时机

**Leader节点异常**

Leader节点运行后会周期性地向Follower发送心跳信息（称之为ping），如果一个Follower未收到Leader节点的心跳信息，Follower节点的状态会从FOLLOWING转变为LOOKING,Follower进入选主阶段

**多数Follower节点异常**

Leader节点也会检测Follower节点的状态，如果多数Follower节点不再响应Leader节点（可能是Leader节点与Follower节点之间产生了网络分区），那么Leader节点可能此时也不再是合法的Leader了，也必须要进行一次新的选主。

##### 选主流程

一些概念

| 术语               | 描述                                                         |
| ------------------ | ------------------------------------------------------------ |
| **election epoch** | 由于分布式系统的特点，无法使用精准的时钟来维护事件的先后顺序，因此，Lampert提出的Logical Clock就成为了界定事件顺序的最主要方式。<br />分布式系统中以消息标记事件，所谓的Logical Clock就是为每个消息加上一个逻辑的时间戳。在ZAB协议中，每个消息都被赋予了一个zxid，zxid全局唯一。zxid有两部分组成：高32位是epoch，低32位是epoch内的自增id，由0开始。每次选出新的Leader，epoch会递增，同时zxid的低32位清0。 |
| **zxid**           | 每个消息的编号，在分布式系统中，事件以消息来表示，事件发生的顺序以消息的编号来标记。ZAB协议中，消息的编号只能由Leader节点来分配 |
| **LOOKING**        | 节点正处于选主状态，不对外提供服务，直至选主结束；           |
| **FOLLOWING**      | 作为系统的从节点，接受主节点的更新并写入本地日志；           |
| **LEADING**        | 作为系统主节点，接受客户端更新，写入本地日志并复制到从节点   |

参与选主的节点

- 发起选主的节点
- 集群其他节点，这些节点会为发起选主的节点进行投票

节点B判断确定A可以成为主，那么节点B就投票给节点A，判断的依据是:

>election epoch(A) > election epoch (B)
>
>zxid(A) > zxid(B)
>
>sid(A) > sid(B)

流程

1. 候选节点A初始化自身的zxid和epoch
2. 向其他所有节点发送选主通知
3. 等待其他节点的回复
4. 如果来自B节点的回复不为空，且B是一个有效节点，判断B此时的运行状态是LOOKING（也在发起选主）还是LEADING/FOLLOWING（正常请求处理过程）

如果投票节点是**LOOKING**

a) 处于LOOKING状态的A发起一次选主请求，并将请求广播至B、C节点，而此时B、C也恰好处于LOOKING状状态

![image.png](https://i.loli.net/2020/02/05/Xjxw3TGgDcWoKY5.png)

b) B、C节点处理A的选主消息，其中，B接受A的提议，C拒绝A的提议.

![image.png](https://i.loli.net/2020/02/05/txVlDqj83uQXwId.png)

c) B 将投票信息发送给A,C

![image.png](https://i.loli.net/2020/02/05/paKQHYdqEg7JjfS.png)

d) C同时发送选举消息给B A

![image.png](https://i.loli.net/2020/02/05/vNyX6BUswjQ8ZcM.png)

e)  A B 处理C的选主请求

![image.png](https://i.loli.net/2020/02/05/w1SREZLVjTOc4Ak.png)

f) 发送信息

![image.png](https://i.loli.net/2020/02/05/otwbcEVQYqgkKFH.png)

选主成功

**投票节点是FOLLOWING/LEADING状态**

- 节点A（Follower）与Leader出现网络问题而触发一次选主，但是其他Follower与Leader正常;
- 新节点加入集群也会有同样的情况发生。

处理流程

- 如果Logical Clock相同，将数据保存在recvset，如果Sender宣称自己是Leader，那么判断是不是半数以上的服务器都选举它，如果是设置角色并退出选举。
- 否则，这是一条与当前LogicalClock不符合的消息，说明在另一个选举过程中已经有了选举结果(另一个选举过程指的是什么)，于是将该选举结果加入到OutOfElection集合中，根据OutOfElection来判断是否可以结束选举，如果可以也是保存LogicalClock，更新角色，退出选举。出现这种情况可能是由于原集群中有一个新的服务器上线/重新启动，但是原来的已有集群的机器已经选主成功，因此，别无他法，只有加入原来的集群成为Follower。

- logical clock相同可能是因为出现这种情况：A、B同时发起选主，此时他们的election epoch可能相同，如果B率先完成了选主过程（B可能变成了Leader，也有可能B选择了其他节点为Leader），但是A还在选主过程中，此时如果B收到了A的选主消息，那么B就将自己的选主结果和自己的状态（LEADING/FOLLOWING）连同自己的election epoch回复给A，对于A来说，它收到了一个来自选主完成的节点B的election epoch相同的回复，便有了上面的第一种情况

- logical clock不相同可能是因为新增了一个节点或者某个节点出现了网络隔离导致其触发一次新的选主，然后系统中其他节点状态依然正常，此时发起选主的节点由于要递增其logical clock，必然会导致其logical clock要大于其他正常节点的logical clock（当然也可能小于，考虑一个新上线节点触发选主，其logical clock从1开始计算）。因此就出现了上面的第二种情况

- 如果对方节点处于FOLLOWING/LEADING状态，除检查是否过半外，同时还要检查leader是否给自己发送过投票信息，从投票信息中确认该leader是不是LEADING状态。这个解释如下：

  > 因为目前leader和follower都是各自检测是否进入leader选举过程。leader检测到未过半的server的ping回复，则leader会进入LOOKING状态，但是follower有自己的检测，感知这一事件，还需要一定时间，在此期间，如果其他server加入到该集群，可能会收到其他follower的过半的对之前leader的投票，但是此时该leader已经不处于LEADING状态了，所以需要这么一个检查来排除这种情况。

**Leader/Follower信息同步**

选出了Leader还不算完，根据ZAB协议定义，在真正对外提供服务之前还需要一个信息同步的过程。具体来说，Leader和Follower之间需要同步以下信息：

- **下一次zxid**：这是因为选出新的Leader后，epoch势必发生改变，因此，需要经过多方协商后选择出当前最大的epoch，然后再拼凑出下一轮提供服务的zxid
- **日志内容**：ZAB使用日志同步来维护多个节点的一致性状态，同步过程是由Leader发往Follower，因此可能会存在大家步调不一致的情况，表现出的现象就是节点日志内容不同，可能某些节点领先，而某些节点落后。

**Epoch协商**

选主过程结束后，接下来就是多数派节点协商出一个最大的epoch（但如果是采用FastLeaderElection算法的话，选出来的Leader其实就拥有了最大的epoch）。

这个过程涉及到Leader和Follower节点的通信，具体流程：

1. Leader节点启动时调用getEpochToPropose()，并将自己的zxid解析出来的epoch作为参数；
2. Follower节点启动时也会连接Leader，并从自己的最后一条zxid解析出epoch发送给Leader，leader中处理该Follower消息的线程同样调用getEpochToPropose()，只是此时传入的参数是该Follower的epoch；
3. getEpochToPropose()中会判断参数中传入的epoch和当前最大的epoch，选择两者中最大的，并且判断该选择是否已经获得了多数派的认可，如果没有得到，则阻塞调用getEpochToPropose()的线程；如果获得认可，那就唤醒那些等待epoch协商结果的线程，于是，Follower就得到了多数派认可的全新的epoch，大家就从这个epoch开始生成新的zxid；
4. Leader的发起epoch更新过程在函数Leader::lead()中，Follower的发起epoch更新过程在函数Follower::followLeader()中，Leader处理Follower的epoch更新请求在函数LearnerHandler::run()中。

**日志同步**

选主结束后，接下来需要在Leader和Follower之间同步日志，根据ZAB协议定义，这个同步过程可能是Leader流向Follower。

对比的原理是将Follower的最新的日志zxid和Leader的已经提交的日志zxid对比，会有以下几种可能：

- 如果Leader的最新提交的日志zxid比Follower的最新日志的zxid大，那就将多的日志发送给Follower，让他补齐；
- 如果Leader的最新提交的日志zxid比Follower的最新日志的zxid小，那就发送命令给Follower，将其多余的日志截断；
- 如果两者恰好一样，那什么都不用做。



即使是一个日志同步过程也要经历以下几个同步过程：

1. Leader发送同步日志给Follower，该过程传输的主要是日志数据流或者Leader给Follower的各种命令；
2. Leader发送NEWLEADER命令给Follower，该命令的作用应该是告诉Follower日志同步已经完成，Follower对该NEWLEADER作出ACK，而Leader会等待该ACK消息；
3. Leader最后发送UPTODATE命令至Follower，这个命令的作用应该是告诉Follower，我已经收到了你的ACK，而Follower这边收到该消息的时候说明一切与Leader同步的初始化工作都已经完成，可以进入正常的处理流程了，而Leader这边发完该命令后也可以进入正常的请求处理流程了。

### Zookeeper读写数据

![](https://user-gold-cdn.xitu.io/2018/5/22/16386fa1da601d9c?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

#### 写数据

1. 客户端发出写入数据请求给任意Follower。
2. Follower把写入数据请求转发给Leader。
3. Leader采用二阶段提交方式，先发送Propose广播给Follower。
4. Follower接到Propose消息，写入日志成功后，返回ACK消息给Leader。
5. Leader接到半数以上ACK消息，返回成功给客户端，并且广播Commit请求给Follower。

#### 读数据

因为集群中所有的Zookeeper节点都呈现一个同样的命名空间视图（就是结构数据），上面的写请求已经保证了写一次数据必须保证集群所有的Zookeeper节点都是同步命名空间的，所以读的时候可以在任意一台Zookeeper节点上。

## Zookeeper实践

### ZK安装部署

1. 使用docker安装,docker search zookeeper 查看zookeeper镜像

![image-20200205150700220](C:\Users\chen\AppData\Roaming\Typora\typora-user-images\image-20200205150700220.png)

2. docker pull zookeeper 拉取镜像
3. docker inspect zookeeper 查看镜像信息

```bash
[root@chenmiao ~]# docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
zookeeper           latest              2f0427341b7b        5 weeks ago         224MB
rabbitmq            3-management        44c4867e4a8b        2 months ago        180MB
mysql               5.7                 1e4405fe1ea9        2 months ago        437MB
mysql               latest              d435eee2caa5        2 months ago        456MB
[root@chenmiao ~]# docker inspect zookeeper
[
    {
        "Id": "sha256:2f0427341b7be98855454850457fe7b8b32000b1384e983a517dcb26320cb289",
        "RepoTags": [
            "zookeeper:latest"
        ],
        "RepoDigests": [
            "zookeeper@sha256:6b6b5f7fb6a47d2b311df5af1718af5a425a679dbb844d77913fa68d1a8bf0fd"
        ],
        "Parent": "",
        "Comment": "",
        "Created": "2019-12-29T09:16:18.374512042Z",
        "Container": "b1a99eb6f2a4f05bae13ac9b3882fad32a12cf1f8d37d652436999f34b7b356f",
        "ContainerConfig": {
            "Hostname": "b1a99eb6f2a4",
            "Domainname": "",
            "User": "",
            "AttachStdin": false,
            "AttachStdout": false,
            "AttachStderr": false,
            "ExposedPorts": {
                "2181/tcp": {},
                "2888/tcp": {},
                "3888/tcp": {},
                "8080/tcp": {}
            },
            "Tty": false,
            "OpenStdin": false,
            "StdinOnce": false,
            "Env": [
                "PATH=/usr/local/openjdk-8/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/apache-zookeeper-3.5.6-bin/bin",
                "LANG=C.UTF-8",
                "JAVA_HOME=/usr/local/openjdk-8",
                "JAVA_VERSION=8u232",
                "JAVA_BASE_URL=https://github.com/AdoptOpenJDK/openjdk8-upstream-binaries/releases/download/jdk8u232-b09/OpenJDK8U-jre_",
                "JAVA_URL_VERSION=8u232b09",
                "ZOO_CONF_DIR=/conf",
                "ZOO_DATA_DIR=/data",
                "ZOO_DATA_LOG_DIR=/datalog",
                "ZOO_LOG_DIR=/logs",
                "ZOO_TICK_TIME=2000",
                "ZOO_INIT_LIMIT=5",
                "ZOO_SYNC_LIMIT=2",
                "ZOO_AUTOPURGE_PURGEINTERVAL=0",
                "ZOO_AUTOPURGE_SNAPRETAINCOUNT=3",
                "ZOO_MAX_CLIENT_CNXNS=60",
                "ZOO_STANDALONE_ENABLED=true",
                "ZOO_ADMINSERVER_ENABLED=true",
                "ZOOCFGDIR=/conf"
            ],
            "Cmd": [
                "/bin/sh",
                "-c",
                "#(nop) ",
                "CMD [\"zkServer.sh\" \"start-foreground\"]"
            ],
            "ArgsEscaped": true,
            "Image": "sha256:5c5cfc828f9dd076258fcf32148fb9de9b0e09f6fae881f4039a516c9638d17b",
            "Volumes": {
                "/data": {},
                "/datalog": {},
                "/logs": {}
            },
            "WorkingDir": "/apache-zookeeper-3.5.6-bin",
            "Entrypoint": [
                "/docker-entrypoint.sh"
            ],
            "OnBuild": null,
            "Labels": {}
        },
        "DockerVersion": "18.06.1-ce",
        "Author": "",
        "Config": {
            "Hostname": "",
            "Domainname": "",
            "User": "",
            "AttachStdin": false,
            "AttachStdout": false,
            "AttachStderr": false,
            "ExposedPorts": {
                "2181/tcp": {},
                "2888/tcp": {},
                "3888/tcp": {},
                "8080/tcp": {}
            },
            "Tty": false,
            "OpenStdin": false,
            "StdinOnce": false,
            "Env": [
                "PATH=/usr/local/openjdk-8/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/apache-zookeeper-3.5.6-bin/bin",
                "LANG=C.UTF-8",
                "JAVA_HOME=/usr/local/openjdk-8",
                "JAVA_VERSION=8u232",
                "JAVA_BASE_URL=https://github.com/AdoptOpenJDK/openjdk8-upstream-binaries/releases/download/jdk8u232-b09/OpenJDK8U-jre_",
                "JAVA_URL_VERSION=8u232b09",
                "ZOO_CONF_DIR=/conf",
                "ZOO_DATA_DIR=/data",
                "ZOO_DATA_LOG_DIR=/datalog",
                "ZOO_LOG_DIR=/logs",
                "ZOO_TICK_TIME=2000",
                "ZOO_INIT_LIMIT=5",
                "ZOO_SYNC_LIMIT=2",
                "ZOO_AUTOPURGE_PURGEINTERVAL=0",
                "ZOO_AUTOPURGE_SNAPRETAINCOUNT=3",
                "ZOO_MAX_CLIENT_CNXNS=60",
                "ZOO_STANDALONE_ENABLED=true",
                "ZOO_ADMINSERVER_ENABLED=true",
                "ZOOCFGDIR=/conf"
            ],
            "Cmd": [
                "zkServer.sh",
                "start-foreground"
            ],
            "ArgsEscaped": true,
            "Image": "sha256:5c5cfc828f9dd076258fcf32148fb9de9b0e09f6fae881f4039a516c9638d17b",
            "Volumes": {
                "/data": {},
                "/datalog": {},
                "/logs": {}
            },
            "WorkingDir": "/apache-zookeeper-3.5.6-bin",
            "Entrypoint": [
                "/docker-entrypoint.sh"
            ],
            "OnBuild": null,
            "Labels": null
        },
        "Architecture": "amd64",
        "Os": "linux",
        "Size": 223809386,
        "VirtualSize": 223809386,
        "GraphDriver": {
            "Data": {
                "LowerDir": "/var/lib/docker/overlay2/034f339f2fb9d7d3c0d807bd474d8074025015a08ba203e11bb9025a343dcc81/diff:/var/lib/docker/overlay2/acbbca4ffcc94b45cc7bc9f15e921ebff7a0c615b41aabf55819e1504ebc31b2/diff:/var/lib/docker/overlay2/e948f113d31437dadd6d349dfc25a03ecfaaf2a8504f5a4caa9265754b5bdb68/diff:/var/lib/docker/overlay2/920476313ca8f50a0ae409fd3e042cd31130ad06b839e63656bde610cc8889b3/diff:/var/lib/docker/overlay2/4675d9e62119aadce8f19a67ad26cb14ef3123602d3c96dee0785614bfcde75b/diff:/var/lib/docker/overlay2/14883d0aefeac07f386fe7a6f84a5b2ad5e95fe8418f886ee803a9fb6d8fcbd3/diff:/var/lib/docker/overlay2/8e48a0f08fc303d0dd75aa7585e23f2606136a8df18a06b15e569395461b6aad/diff",
                "MergedDir": "/var/lib/docker/overlay2/65cbc22c44f4d3c8e357cf0b117278cc7580ba9d24755d57361189bbd9094339/merged",
                "UpperDir": "/var/lib/docker/overlay2/65cbc22c44f4d3c8e357cf0b117278cc7580ba9d24755d57361189bbd9094339/diff",
                "WorkDir": "/var/lib/docker/overlay2/65cbc22c44f4d3c8e357cf0b117278cc7580ba9d24755d57361189bbd9094339/work"
            },
            "Name": "overlay2"
        },
        "RootFS": {
            "Type": "layers",
            "Layers": [
                "sha256:556c5fb0d91b726083a8ce42e2faaed99f11bc68d3f70e2c7bbce87e7e0b3e10",
                "sha256:109e67eff29c31dfce98bf5c13fc86a9a2d7b9db9a7244c227f185b8c27e437e",
                "sha256:e871e75299d219d25e6420961722ece64ab0ac3a0b3699185c4252c62de4e41a",
                "sha256:5b71a1781cde37c69a34351364f5c1778a780b982da081767fd7308ac1b36adc",
                "sha256:4a050aa8c8065226e29d72db5b86f814bd34fc0f59e70a5931e903cb398e38d4",
                "sha256:2970e374ce6e2c8569289d3a84f4c39bf6a9c9e29fd4a8822c46e6ff48115447",
                "sha256:48cb4ae733fe176322e9732dbcdca4b4aff968dc43713b386f8904910b080796",
                "sha256:b9d2a64ce3d6638d737d793f64d1bcb6e2efde09d74a7a10725d4cd7ae900b39"
            ]
        },
        "Metadata": {
            "LastTagTime": "0001-01-01T00:00:00Z"
        }
    }
]
[root@chenmiao ~]# 

```

4. 启动zookeeper 命令

```bash
docker run -d -p 2181:2181 --name zk3.5 --restart always 2f0427341b7b
```

5. 进入docker docker exec -it d5c6f857cd88 bash,使用命令 ./bin/zkCli.sh启动
6. 使用[ZooInspector](https://github.com/zzhang5/zooinspector)连接访问Zookeeper

### 基本命令

-  **help 查看客户端帮助命令**

  ```
  [zk: localhost:2181(CONNECTED) 0] help
  ZooKeeper -server host:port cmd args
  	addauth scheme auth
  	close 
  	config [-c] [-w] [-s]
  	connect host:port
  	create [-s] [-e] [-c] [-t ttl] path [data] [acl]
  	delete [-v version] path
  	deleteall path
  	delquota [-n|-b] path
  	get [-s] [-w] path
  	getAcl [-s] path
  	history 
  	listquota path
  	ls [-s] [-w] [-R] path
  	ls2 path [watch]
  	printwatches on|off
  	quit 
  	reconfig [-s] [-v version] [[-file path] | [-members serverID=host:port1:port2;port3[,...]*]] | [-add serverId=host:port1:port2;port3[,...]]* [-remove serverId[,...]*]
  	redo cmdno
  	removewatches path [-c|-d|-a] [-l]
  	rmr path
  	set [-s] [-v version] path data
  	setAcl [-s] [-v version] [-R] path acl
  	setquota -n|-b val path
  	stat [-w] path
  	sync path
  Command not found: Command not found help
  
  ```

-  **ls 查看**

  ```
  [zk: localhost:2181(CONNECTED) 1] ls
  ls [-s] [-w] [-R] path
  [zk: localhost:2181(CONNECTED) 2] ls /
  [dubbo, test, zookeeper]
  [zk: localhost:2181(CONNECTED) 3] ls /dubbo
  [demo1.dubbo.service.HelloService]
  [zk: localhost:2181(CONNECTED) 4] 
  ```

  

-  **get 获取节点数据和更新信息**

  - cZxid: 创建节点的id
  - ctime: 节点的创建时间
  - mZxid: 修改节点的id
  - mtime: 修改节点的时间
  - pZxid: 子节点的id
  - cversion: 子节点的版本
  - dataVersion: 当前节点的数据版本
  - aclVersion : 权限的版本
  - ephemeralOwner: 判断是否是零时节点
  - dataLength: 数据长度
  - numChildren: 子节点的数量

  ```
  [zk: localhost:2181(CONNECTED) 10] get -s /
  
  cZxid = 0x0
  ctime = Thu Jan 01 00:00:00 UTC 1970
  mZxid = 0x0
  mtime = Thu Jan 01 00:00:00 UTC 1970
  pZxid = 0x48
  cversion = 3
  dataVersion = 0
  aclVersion = 0
  ephemeralOwner = 0x0
  dataLength = 0
  numChildren = 3
  
  ```

-  **stat 获得节点的更新信息**

  ```
  [zk: localhost:2181(CONNECTED) 16] stat /test
  cZxid = 0x48
  ctime = Thu Feb 06 07:42:49 UTC 2020
  mZxid = 0x48
  mtime = Thu Feb 06 07:42:49 UTC 2020
  pZxid = 0x51
  cversion = 7
  dataVersion = 0
  aclVersion = 0
  ephemeralOwner = 0x0
  dataLength = 4
  numChildren = 5
  
  ```

-  **ls2 ls命令和stat命令的整合**

  ```
  [zk: localhost:2181(CONNECTED) 19] ls2  /zookeeper
  'ls2' has been deprecated. Please use 'ls [-s] path' instead.
  [config, quota]
  cZxid = 0x0
  ctime = Thu Jan 01 00:00:00 UTC 1970
  mZxid = 0x0
  mtime = Thu Jan 01 00:00:00 UTC 1970
  pZxid = 0x0
  cversion = -2
  dataVersion = 0
  aclVersion = 0
  ephemeralOwner = 0x0
  dataLength = 0
  numChildren = 2
  
  ```

  

-  **create 创建节点** 

  - `create [-s] [-e] [-c] [-t ttl] path [data] [acl]`

  ```
  [zk: localhost:2181(CONNECTED) 20] create
  create [-s] [-e] [-c] [-t ttl] path [data] [acl]
  [zk: localhost:2181(CONNECTED) 21] create /demo1 demo1
  Created /demo1
  [zk: localhost:2181(CONNECTED) 22] get /demo1
  demo1
  [zk: localhost:2181(CONNECTED) 23] get -s  /demo1
  demo1
  cZxid = 0x54
  ctime = Thu Feb 06 08:34:35 UTC 2020
  mZxid = 0x54
  mtime = Thu Feb 06 08:34:35 UTC 2020
  pZxid = 0x54
  cversion = 0
  dataVersion = 0
  aclVersion = 0
  ephemeralOwner = 0x0
  dataLength = 5
  numChildren = 0
  [zk: localhost:2181(CONNECTED) 24] 
  
  ```

  `-e 表示临时节点`

  `-s 创建顺序节点 自动累加`

- **修改节点 set [-s] [-v version] path data**

  ```
  [zk: localhost:2181(CONNECTED) 24] set
  set [-s] [-v version] path data
  [zk: localhost:2181(CONNECTED) 25] set /demo1 demo1-1
  [zk: localhost:2181(CONNECTED) 26] get -s /demo1
  demo1-1
  cZxid = 0x54
  ctime = Thu Feb 06 08:34:35 UTC 2020
  mZxid = 0x55
  mtime = Thu Feb 06 08:39:12 UTC 2020
  pZxid = 0x54
  cversion = 0
  dataVersion = 1
  aclVersion = 0
  ephemeralOwner = 0x0
  dataLength = 7
  numChildren = 0
  
  ```

  

- **删除节点 delete [-v version] path**

  ```
  [zk: localhost:2181(CONNECTED) 31] delete
  delete [-v version] path
  [zk: localhost:2181(CONNECTED) 32] delete -v 1 /demo1
  version No is not valid : /demo1
  [zk: localhost:2181(CONNECTED) 33] delete -v 2 /demo1
  [zk: localhost:2181(CONNECTED) 34] get -s /demo1
  org.apache.zookeeper.KeeperException$NoNodeException: KeeperErrorCode = NoNode for /demo1
  
  ```

- 设置watcher通知

  - stat  -w path 设置watcher事件
  - get -w path 设置watcher事件
  - 子节点创建和删除时触发watch事件，子节点修改不会触发该事件,并且事件只触发一次,消费了就没了

  ```
  [zk: localhost:2181(CONNECTED) 35] stat
  stat [-w] path
  # 添加watcher事件
  [zk: localhost:2181(CONNECTED) 36] stat -w /demo2
  Node does not exist: /demo2
  # 创建demo2节点触发watcher事件
  [zk: localhost:2181(CONNECTED) 37] create /demo2 demo2
  
  WATCHER::
  
  WatchedEvent state:SyncConnected type:NodeCreated path:/demo2
  Created /demo2
  # 删除时事件已经消费
  [zk: localhost:2181(CONNECTED) 40] delete /demo2
  [zk: localhost:2181(CONNECTED) 41] get /demo2
  org.apache.zookeeper.KeeperException$NoNodeException: KeeperErrorCode = NoNode for /demo2
  
  
  ```

  

- **获取Acl权限信息 getAcl [-s] path**

  ```
  [zk: localhost:2181(CONNECTED) 43] getAcl
  getAcl [-s] path
  # 默认权限'world,'anyone : cdrwa 任何人都可以访问
  [zk: localhost:2181(CONNECTED) 44] getAcl /test
  'world,'anyone
  : cdrwa
  [zk: localhost:2181(CONNECTED) 45] 
  ```

  

- **设置权限 setAcl [-s] [-v version] [-R] path acl**

  ```
  setAcl
  setAcl [-s] [-v version] [-R] path acl
  [zk: localhost:2181(CONNECTED) 46] create /demo3 demo3
  Created /demo3
  [zk: localhost:2181(CONNECTED) 47] getAcl /demo3
  'world,'anyone
  : cdrwa
  [zk: localhost:2181(CONNECTED) 48] setAcl /demo3 world:anyone:crwa
  [zk: localhost:2181(CONNECTED) 49] getAcl /demo3
  'world,'anyone
  : crwa
  
  ```

  

  

### 实践

**对zookeeper进行简单封装**

```java
package com.example.zk.demo;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * 对zookeeper 进行简单封装
 *
 * @author parkstud@qq.com 2020-02-06
 */
@Slf4j
public class ZookeeperDemo1 {
    private ZooKeeper zooKeeper;

    /**
     * 连接获取Zookeeper
     *
     * @param host 主机地址
     * @param port 端口
     * @return Zookeeper实例
     * @throws IOException
     */
    public ZooKeeper connect(String host, int port) throws IOException {
        zooKeeper = new ZooKeeper(host, port, event -> log.info("# connect " + event.toString()));
        return zooKeeper;
    }

    /**
     * 关闭Zookeeper 连接
     *
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
        zooKeeper.close();
    }

    /**
     * 创建一个znode
     *
     * @param path       路径
     * @param data       数据
     * @param createMode 创建模式
     * @return 创建节点的实际路劲
     * @throws KeeperException
     * @throws InterruptedException
     */
    public String createNode(String path, byte[] data, CreateMode createMode) throws KeeperException, InterruptedException {
        return zooKeeper.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
    }

    /**
     * 删除一个Znode
     *
     * @param path 节点路劲
     */
    public void deleteNode(String path) throws KeeperException, InterruptedException {
        zooKeeper.delete(path, zooKeeper.exists(path, true).getVersion());
    }

    /**
     * 获取路径的元数据信息
     * @param path zookeeper路径
     * @return stat
     * @throws KeeperException
     * @throws InterruptedException
     */
    public Stat watch(String path) throws KeeperException, InterruptedException {
        Stat stat = new Stat();
        zooKeeper.getData(path, watchedEvent -> log.info("# event" + watchedEvent.toString()), stat);
        return stat;
    }

}

```

**测试创建节点**

```java
package com.example.zk.demo;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author parkstud@qq.com 2020-02-06
 */
@Slf4j
public class ZookeeperDemo1Test {
    private ZookeeperDemo1 zookeeperDemo1;

    @Before
    public void before() {
        zookeeperDemo1 = new ZookeeperDemo1();
    }

    @Test
    public void createNode() throws IOException, KeeperException, InterruptedException {
        zookeeperDemo1.connect("106.14.4.232", 2181);
        log.info(zookeeperDemo1.createNode("/test", "test".getBytes(),
                CreateMode.PERSISTENT));
        for (int i = 0; i < 5; i++) {
            log.info(zookeeperDemo1.createNode("/test/child", null,
                    CreateMode.PERSISTENT_SEQUENTIAL));
        }
    }
}
```

```

17:12:50.085 [main-SendThread(106.14.4.232:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Reading reply sessionid:0x100e3abdcb70015, packet:: clientPath:null serverPath:null finished:false header:: 1,1  replyHeader:: 1,109,0  request:: '/test,#74657374,v{s{31,s{'world,'anyone}}},0  response:: '/test 
17:12:50.085 [main] INFO com.example.zk.demo.ZookeeperDemo1Test - /test
17:12:50.102 [main-SendThread(106.14.4.232:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Reading reply sessionid:0x100e3abdcb70015, packet:: clientPath:null serverPath:null finished:false header:: 2,1  replyHeader:: 2,110,0  request:: '/test/child,,v{s{31,s{'world,'anyone}}},2  response:: '/test/child0000000000 
17:12:50.102 [main] INFO com.example.zk.demo.ZookeeperDemo1Test - /test/child0000000000
17:12:50.117 [main-SendThread(106.14.4.232:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Reading reply sessionid:0x100e3abdcb70015, packet:: clientPath:null serverPath:null finished:false header:: 3,1  replyHeader:: 3,111,0  request:: '/test/child,,v{s{31,s{'world,'anyone}}},2  response:: '/test/child0000000001 
17:12:50.117 [main] INFO com.example.zk.demo.ZookeeperDemo1Test - /test/child0000000001
17:12:50.126 [main-SendThread(106.14.4.232:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Reading reply sessionid:0x100e3abdcb70015, packet:: clientPath:null serverPath:null finished:false header:: 4,1  replyHeader:: 4,112,0  request:: '/test/child,,v{s{31,s{'world,'anyone}}},2  response:: '/test/child0000000002 
17:12:50.126 [main] INFO com.example.zk.demo.ZookeeperDemo1Test - /test/child0000000002
17:12:50.139 [main-SendThread(106.14.4.232:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Reading reply sessionid:0x100e3abdcb70015, packet:: clientPath:null serverPath:null finished:false header:: 5,1  replyHeader:: 5,113,0  request:: '/test/child,,v{s{31,s{'world,'anyone}}},2  response:: '/test/child0000000003 
17:12:50.139 [main] INFO com.example.zk.demo.ZookeeperDemo1Test - /test/child0000000003
17:12:50.148 [main-SendThread(106.14.4.232:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Reading reply sessionid:0x100e3abdcb70015, packet:: clientPath:null serverPath:null finished:false header:: 6,1  replyHeader:: 6,114,0  request:: '/test/child,,v{s{31,s{'world,'anyone}}},2  response:: '/test/child0000000004 
17:12:50.149 [main] INFO com.example.zk.demo.ZookeeperDemo1Test - /test/child0000000004
Disconnected from the target VM, address: '127.0.0.1:53851', transport: 'socket'
```

![image.png](https://i.loli.net/2020/02/06/U9JGniqR1cjvozl.png)

**测试监控节点**

```java
    @Test
    public void watchNode() throws IOException, KeeperException, InterruptedException {
        zookeeperDemo1.connect("106.14.4.232", 2181);
        zookeeperDemo1.createNode("/test/watch","watch".getBytes(),CreateMode.PERSISTENT);
        zookeeperDemo1.watch("/test/watch");
        zookeeperDemo1.deleteNode("/test/watch");
    }
```



```
17:15:05.196 [main-EventThread] INFO com.example.zk.demo.ZookeeperDemo1 - # connect WatchedEvent state:SyncConnected type:None path:null
17:15:05.217 [main-SendThread(106.14.4.232:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Reading reply sessionid:0x100e3abdcb70016, packet:: clientPath:null serverPath:null finished:false header:: 1,1  replyHeader:: 1,117,0  request:: '/test/watch,#7761746368,v{s{31,s{'world,'anyone}}},0  response:: '/test/watch 
17:15:05.230 [main-SendThread(106.14.4.232:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Reading reply sessionid:0x100e3abdcb70016, packet:: clientPath:null serverPath:null finished:false header:: 2,4  replyHeader:: 2,117,0  request:: '/test/watch,T  response:: #7761746368,s{117,117,1580980505155,1580980505155,0,0,0,0,5,0,117} 
17:15:05.249 [main-SendThread(106.14.4.232:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Reading reply sessionid:0x100e3abdcb70016, packet:: clientPath:null serverPath:null finished:false header:: 3,3  replyHeader:: 3,117,0  request:: '/test/watch,T  response:: s{117,117,1580980505155,1580980505155,0,0,0,0,5,0,117} 
17:15:05.267 [main-SendThread(106.14.4.232:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Got notification sessionid:0x100e3abdcb70016
17:15:05.268 [main-SendThread(106.14.4.232:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Got WatchedEvent state:SyncConnected type:NodeDeleted path:/test/watch for sessionid 0x100e3abdcb70016
17:15:05.268 [main-EventThread] INFO com.example.zk.demo.ZookeeperDemo1 - # connect WatchedEvent state:SyncConnected type:NodeDeleted path:/test/watch
17:15:05.268 [main-EventThread] INFO com.example.zk.demo.ZookeeperDemo1 - # eventWatchedEvent state:SyncConnected type:NodeDeleted path:/test/watch
17:15:05.268 [main-SendThread(106.14.4.232:2181)] DEBUG org.apache.zookeeper.ClientCnxn - Reading reply sessionid:0x100e3abdcb70016, packet:: clientPath:null serverPath:null finished:false header:: 4,2  replyHeader:: 4,118,0  request:: '/test/watch,0  response:: null
Disconnected from the target VM, address: '127.0.0.1:53892', transport: 'socket'
```

### Zookeeper实现分布式锁

#### 不可重入的分布式锁（有羊群效应，不公平锁）

约定一个路径的创建作为抢占锁的条件，比如路径/zk-lock/foo，然后集群中每台机器上的程序都尝试去创建这样一个临时节点,释放锁实际上就是将/zk-lock/foo节点删除，之前说过了其它没有抢到锁的机器都在这个节点上面添加了一个watcher，所以删除/zk-lock/foo实际上就是将其它休眠的机器唤醒让它们重新抢占锁。

![](https://img2018.cnblogs.com/blog/784924/201901/784924-20190114224258141-1126127088.png)

**锁定义**

```java
package com.example.zk.demo1;

/**
 * Zookeeper锁定义
 * @author parkstud@qq.com 2020-02-06
 */
public interface ZookeeperLock {
    /**
     * 获取锁
     * @throws InterruptedException
     */
    void lock() throws InterruptedException;

    /**
     * 释放锁
     */
    void unLock();

    /**
     * 释放zk
     */
    void releaseResource();

}

```

锁实现**1**

```java
package com.example.zk.demo1;

import com.example.zk.demo.ZookeeperEncapsulation;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

/**
 * Zookeeper锁实现
 * 不可重入有羊群效应的分布式锁（不公平锁）
 *
 * @author parkstud@qq.com 2020-02-06
 */
@Slf4j
public class ZookeeperLockImpl1 implements ZookeeperLock {
    public static final String SPLIT = "/";
    /**
     * 当前线程
     */
    private final Thread currentThread;

    /**
     * 锁的基础路径
     */
    private String lockBasePath;
    /**
     * 锁名称
     */
    private String lockName;
    /**
     * zk锁全路径
     */
    private String lockFullPath;

    /**
     * zookeeper实例
     */
    private ZooKeeper zooKeeper;
    /**
     * UUID
     */
    private String myId;
    /**
     * 当前线程名称
     */
    private String myName;

    public ZookeeperLockImpl1(String lockBasePath, String lockName) {
        this.currentThread = Thread.currentThread();
        this.lockBasePath = lockBasePath;
        this.lockName = lockName;
        this.lockFullPath = this.lockBasePath + SPLIT + lockName;
        this.zooKeeper = new ZookeeperEncapsulation().getZooKeeper();
        this.myId = UUID.randomUUID().toString();
        this.myName = Thread.currentThread().getName();
        createLockBasePath();
    }

    /**
     * 创建基础路径
     */
    private void createLockBasePath() {
        try {
            if (zooKeeper.exists(lockBasePath, null) == null) {
                zooKeeper.create(lockBasePath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("创建基础路径失败!", e);
        }
    }


    @Override
    public void lock() throws InterruptedException {
        try {
            zooKeeper.create(lockFullPath, myId.getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            log.info("{} : get lock ", myName);
        } catch (KeeperException.NodeExistsException e) {
            //节点存在,监听此节点,当节点被删除时抢占
            try {
                zooKeeper.exists(lockFullPath, event -> {
                    synchronized (currentThread) {
                        currentThread.notify();
                        log.info("{} : wake", myName);
                    }
                });
            } catch (KeeperException.NoNodeException ex) {
                // 如果没有节点
                lock();
            } catch (KeeperException ex) {
                log.error("抢占节点失败", ex);
            }
            synchronized (currentThread) {
                currentThread.wait();
            }
            lock();
        } catch (KeeperException e) {
            log.error("创建节点失败", e);
        }

    }

    @Override
    public void unLock() {
        try {
            byte[] nodeBytes = zooKeeper.getData(lockFullPath, false, null);
            String currentHoldLockNodeId = new String(nodeBytes, StandardCharsets.UTF_8);
            // 只有当前锁的持有者是自己才能删除
            if (myId.equalsIgnoreCase(currentHoldLockNodeId)) {
                zooKeeper.delete(lockFullPath, -1);
            }
        } catch (KeeperException | InterruptedException e) {
            log.error("释放锁失败!", e);
        }

        log.info("{} : release lock", myName);
    }

    @Override
    public void releaseResource() {
        try {
            // 将zookeeper连接释放掉
            zooKeeper.close();
        } catch (InterruptedException e) {
            log.error("zookeeper close fail", e);
        }
    }

}

```

**测试**

```java
package com.example.zk.demo1;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试锁1
 *
 * @author parkstud@qq.com 2020-02-06
 */
@Slf4j
public class ZookeeperLockImpl1Test {
    private static String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 测试锁1
     */
    public void test1() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                ZookeeperLock lock = new ZookeeperLockImpl1("/zk-lock", "test");
                try {
                    lock.lock();
                    String myName = Thread.currentThread().getName();
                    log.warn("{} : hold lock ,now= {}", myName, now());
                    TimeUnit.MICROSECONDS.sleep(1);
                    lock.unLock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.releaseResource();
                }
            });
        }
        executorService.shutdown();
    }

    public static void main(String[] args) {
        ZookeeperLockImpl1Test zookeeperLockImpl1Test = new ZookeeperLockImpl1Test();
        zookeeperLockImpl1Test.test1();
    }
}
```

#### 不可重入的分布式锁（无羊群效应，公平锁）

上面的当释放锁的时候会把其它所有机器全部唤醒，但是最终只有一台机器会抢到锁，我们可以让其排队，每台机器来抢锁的时候都要排队，排队的时候领一个号码称为自己的ticket，这个ticket是有序的，每次增加1，所以只需要前面的人释放锁的时候叫醒后面的第一个人就可以了，即每台机器在领完号码的时候都往自己前面的机器的ticket上添加一个watcher.

![](https://img2018.cnblogs.com/blog/784924/201901/784924-20190114225220342-2079518552.png)

```java
public class ZookeeperLockImpl2 implements ZookeeperLock {
    private final Thread currentThread;

    private String lockBasePath;
    private String lockPrefix;
    private String lockFullPath;

    private ZooKeeper zooKeeper;
    private String myName;
    private int myTicket;

    public ZookeeperLockImpl2(String lockBasePath, String lockPrefix){
        this.lockBasePath = lockBasePath;
        this.lockPrefix = lockPrefix;
        this.lockFullPath = lockBasePath + "/" + lockPrefix;
        this.zooKeeper = new ZookeeperEncapsulation().getZooKeeper();
        this.currentThread = Thread.currentThread();
        this.myName = currentThread.getName();
        createLockBasePath();
    }

    private void createLockBasePath() {
        try {
            if (zooKeeper.exists(lockBasePath, null) == null) {
                zooKeeper.create(lockBasePath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException | InterruptedException ignored) {
        }
    }

    @Override
    public void lock() throws InterruptedException {
        log("begin get lock");
        try {
            String path = zooKeeper.create(lockFullPath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            myTicket = extractMyTicket(path);
            String previousNode = buildPath(myTicket - 1);
            Stat exists = zooKeeper.exists(previousNode, event -> {
                synchronized (currentThread) {
                    currentThread.notify();
                    log("wake");
                }
            });
            if (exists != null) {
                synchronized (currentThread) {
                    currentThread.wait();
                }
            }
            log("get lock success");
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unLock() {
        log("begin release lock");
        try {
            zooKeeper.delete(buildPath(myTicket), -1);
        } catch (KeeperException | InterruptedException e) {
            e.printStackTrace();
        }
        log("end release lock");
    }

    private int extractMyTicket(String path) {
        int splitIndex = path.lastIndexOf("/");
        return Integer.parseInt(path.substring(splitIndex + 1).replace(lockPrefix, ""));
    }

    private String buildPath(int ticket) {
        return String.format("%s%010d", lockFullPath, ticket);
    }

    @Override
    public void releaseResource() {
        try {
            // 将zookeeper连接释放掉
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void log(String msg) {
        System.out.printf("[%d] %s : %s\n", myTicket, myName, msg);
    }
}

```

```
    /**
     * 测试锁2
     */
    public void test2() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                ZookeeperLock lock = new ZookeeperLockImpl2("/zk-lock", "test1");
                try {
                    lock.lock();
                    String myName = Thread.currentThread().getName();
                    log.warn("{} : hold lock ,now= {}", myName, now());
                    TimeUnit.MICROSECONDS.sleep(1);
                    lock.unLock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.releaseResource();
                }
            });
        }
        executorService.shutdown();
    }
```

#### 可重入的分布式锁（无羊群效应，公平锁）

当已经持有锁时再尝试获取锁时，可重入锁会直接获取成功，同时将重入次数加1，当释放锁时，将重入次数减1，只有当重入次数为0时才彻底释放掉锁。

![](https://img2018.cnblogs.com/blog/784924/201901/784924-20190114225220752-627526303.png)

```java
package com.example.zk.demo1;

import com.example.zk.demo.ZookeeperEncapsulation;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import lombok.extern.slf4j.Slf4j;

/**
 * @author parkstud@qq.com 2020-02-07
 */
@Slf4j
public class ZookeeperLockImpl3 implements ZookeeperLock {
    private final Thread currentThread;

    private String lockBasePath;
    private String lockPrefix;
    private String lockFullPath;

    private ZooKeeper zooKeeper;
    private String myName;
    private int myTicket;

    public ZookeeperLockImpl3(String lockBasePath, String lockName) {
        this.lockBasePath = lockBasePath;
        this.lockPrefix = lockName;
        this.lockFullPath = lockBasePath + "/" + lockName;
        this.zooKeeper = new ZookeeperEncapsulation().getZooKeeper();
        this.currentThread = Thread.currentThread();
        this.myName = currentThread.getName();
        createLockBasePath();
    }

    private void createLockBasePath() {
        try {
            if (zooKeeper.exists(lockBasePath, null) == null) {
                zooKeeper.create(lockBasePath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException | InterruptedException ignored) {
        }
    }

    /**
     * 使用一个本机变量记载重复次数，这个值就不存储在zookeeper上了，
     */
    private int reentrantCount = 0;

    @Override
    public void lock() throws InterruptedException {
        log.warn("begin get lock" + reentrantCount);

        // 如果reentrantCount不为0说明当前节点已经持有锁了，无需等待，直接增加重入次数即可

        if (reentrantCount != 0) {
            reentrantCount++;
            log.warn("get lock successful");
            return;
        }
        // 说明还没有获取到锁，需要设置watcher监听上一个节点释放锁事件
        String path = null;
        try {
            path = zooKeeper.create(lockFullPath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            myTicket = extractMyTicket(path);
            String previousNode = buildPathByTicket(myTicket - 1);
            Stat exists = zooKeeper.exists(previousNode, event -> {
                synchronized (currentThread) {
                    currentThread.notify();
                    log.warn("wake");
                }
            });
            if (exists != null) {
                synchronized (currentThread) {
                    currentThread.wait();
                }
            }

            reentrantCount++;
            log.warn("get lock success");
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    private int extractMyTicket(String path) {
        int splitIndex = path.lastIndexOf("/");
        return Integer.parseInt(path.substring(splitIndex + 1).replace(lockPrefix, ""));
    }

    private String buildPathByTicket(int ticket) {
        return String.format("%s%010d", lockFullPath, ticket);
    }

    @Override
    public void unLock() {
        log.warn("begin release lock");
        // 每次unlock的时候将递归次数减1，没有减到0说明还在递归中
        reentrantCount--;
        if (reentrantCount != 0) {
            log.warn("end release lock");
            return;
        }
        // 只有当重入次数为0的时候才删除节点，将锁释放掉
        try {
            zooKeeper.delete(buildPathByTicket(myTicket), -1);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
        log.warn("end release lock");
    }

    @Override
    public void releaseResource() {
        try {
            // 将zookeeper连接释放掉
            zooKeeper.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

```

**测试**

```java
 public void test3(){
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                ZookeeperLock lock = new ZookeeperLockImpl3("/zk-lock", "test1");
                try {
                    lock.lock();
                    String myName = Thread.currentThread().getName();
                    log.warn("{} : hold lock ,now= {}", myName, now());
                    TimeUnit.MICROSECONDS.sleep(1);
                    int reentrantTimes = new Random().nextInt(10);
                    int reentrantCount = 0;
                    for (int j = 0; j < reentrantTimes; j++) {
                        lock.lock();
                        reentrantCount++;
                        if (Math.random() < 0.5) {
                            lock.unLock();
                            reentrantCount--;
                        }
                    }
                    while (reentrantCount-- > 0) {
                        lock.unLock();
                    }

                    lock.unLock();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.releaseResource();
                }
            });
        }
        executorService.shutdown();
    }
```

### 基于Curator客户端实现分布式锁

Apache Curator是一个Zookeeper的开源客户端，它提供了Zookeeper各种应用场景（Recipe，如共享锁服务、master选举、分布式计数器等）的抽象封装，接下来将利用Curator提供的类来实现分布式锁。

Curator提供的跟分布式锁相关的类有5个，分别是：

- Shared Reentrant Lock 可重入锁
- Shared Lock 共享不可重入锁
- Shared Reentrant Read Write Lock 可重入读写锁
- Shared Semaphore 信号量
- Multi Shared Lock 多锁

> 关于错误处理：还是强烈推荐使用ConnectionStateListener处理连接状态的改变。当连接LOST时你不再拥有锁。

- maven 地址

```
	<dependency>
			<groupId>org.apache.curator</groupId>
			<artifactId>curator-framework</artifactId>
			<version>4.2.0</version>
	</dependency>
```

[ZAB协议选主过程详解](https://zhuanlan.zhihu.com/p/27335748)

https://juejin.im/post/5b03d58a6fb9a07a9e4d8f01

[CAP定理](https://juejin.im/post/5b26634b6fb9a00e765e75d1)

https://juejin.im/post/5b037d5c518825426e024473