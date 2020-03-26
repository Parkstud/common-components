# Redis学习

## 理论

### 特点或功能

速度快，完全基于内存，使用C语言实现，网络层使用epoll解决高并发问题，单线程模型避免了不必要的上下文切换及竞争条件；

丰富的数据类型，Redis有8种数据类型，当然常用的主要是 String、Hash、List、Set、 SortSet 这5种类型，他们都是基于键值的方式组织数据。每一种数据类型提供了非常丰富的操作命令，可以满足绝大部分需求，如果有特殊需求还能自己通过 lua 脚本自己创建新的命令（具备原子性）；

另外它还支持事务（没用过）、持久化(RDB,AOF)、主从复制让高可用、分布式成为可能。

**文档全面** http://redisdoc.com/

### redis 应用

1. 缓存，毫无疑问这是Redis当今最为人熟知的使用场景。再提升服务器性能方面非常有效；
2. 排行榜，如果使用传统的关系型数据库来做这个事儿，非常的麻烦，而利用Redis的SortSet数据结构能够非常方便搞定；
3. 计算器/限速器，利用Redis中原子性的自增操作，我们可以统计类似用户点赞数、用户访问数等，这类操作如果用MySQL，频繁的读写会带来相当大的压力；限速器比较典型的使用场景是限制某个用户访问某个API的频率，常用的有抢购时，防止用户疯狂点击带来不必要的压力；
4. 好友关系，利用集合的一些命令，比如求交集、并集、差集等。可以方便搞定一些共同好友、共同爱好之类的功能；
5. 简单消息队列，除了Redis自身的发布/订阅模式，我们也可以利用List来实现一个队列机制，比如：到货通知、邮件发送之类的需求，不需要高可靠，但是会带来非常大的DB压力，完全可以用List来完成异步解耦；
6. Session共享，以PHP为例，默认Session是保存在服务器的文件中，如果是集群服务，同一个用户过来可能落在不同机器上，这就会导致用户频繁登陆；采用Redis保存Session后，无论用户落在那台机器上都能够获取到对应的Session信息。

### redis高可用

在 `Web` 服务器中，**高可用** 是指服务器可以 **正常访问** 的时间，衡量的标准是在 **多长时间** 内可以提供正常服务（`99.9%`、`99.99%`、`99.999%` 等等）。在 `Redis` 层面，**高可用** 的含义要宽泛一些，除了保证提供 **正常服务**（如 **主从分离**、**快速容灾技术** 等），还需要考虑 **数据容量扩展**、**数据安全** 等等。

在 `Redis` 中，实现 **高可用** 的技术主要包括 **持久化**、**复制**、**哨兵** 和 **集群**，下面简单说明它们的作用，以及解决了什么样的问题：

- **持久化**：持久化是 **最简单的** 高可用方法。它的主要作用是 **数据备份**，即将数据存储在 **硬盘**，保证数据不会因进程退出而丢失。
- **复制**：复制是高可用 `Redis` 的基础，**哨兵** 和 **集群** 都是在 **复制基础** 上实现高可用的。复制主要实现了数据的多机备份以及对于读操作的负载均衡和简单的故障恢复。缺陷是故障恢复无法自动化、写操作无法负载均衡、存储能力受到单机的限制。
- **哨兵**：在复制的基础上，哨兵实现了 **自动化** 的 **故障恢复**。缺陷是 **写操作** 无法 **负载均衡**，**存储能力** 受到 **单机** 的限制。
- **集群**：通过集群，`Redis` 解决了 **写操作** 无法 **负载均衡** 以及 **存储能力** 受到 **单机限制** 的问题，实现了较为 **完善** 的 **高可用方案**。

### redis持久化

redis持久包括两种

1. `RDB`:(point in time dump)指定时间内有指定数量的写操作执行,当条件满足可以调用两天转存到硬盘的命令中的任何一条执行。创建快照的几种方式

- 客户端 向redis发送`BGSAVE`命令,redis调用fork创建子线程 扶着写入硬盘.
- 客户端想redis发送`save`命令,在创建快照之前不响应命令
- 设置save 选项 `save 60 10000` redis从最近创建一次快照后开始计算,当60秒内有10000此写入就触发BGSAVE命令.如果设置了多个save选项,任意一个就触发bgsave命令
- redis 听过SHUTDOWN 命令接收或者收到TERM信号时,执行一个SAVE命令阻断所有客户端,不在执行客户端发送的任何指令
- 当一个redis 服务器连接另一个redis服务器,并向对方发送SYNC命令开始复制操作,如果主服务没有执行BGSAVE操作,主服务器就会执行BGSAVE

2. `AOF`（append-only file ）所有修改数据库的命令都写入一个追加文件里,可以设置不同步,每秒同步一次和写入一个命令同步一次

   appendonly yes 打开AOF

   | 选项     | 同步频率                                            |
   | -------- | --------------------------------------------------- |
   | always   | 每个redis写命令都要写入硬盘,这样会严重降低redis速度 |
   | everysec | 每秒执行同步,将多个命令同步到硬盘                   |
   | no       | 让操作系统决定何时同步                              |

   AOF 文件体积增大 可以发送BFREWRITEAOF命令,重写AOF文件,AOF可以通过设置auto-aof-rewrite-percentage选项和auto-aof-rewrite-min-size选项自动执行BGREWRITEAOF

![image.png](https://i.loli.net/2020/03/23/DSb81aHX7fNrYg2.png)

### Redis 故障处理

1. redis-check-aof和redis-check-dump

2. **更换故障主服务器**

   A主B从,新服务器C ,A挂了 首先向机器B发送save命令,创建快照文件,将快照文件发送给机器C,在机器C上启动Redis

### redis 数据结构

`type` 命令实际返回的就是当前 **键** 的 **数据结构类型**，它们分别是：`string`（**字符串**）、`hash`（**哈希**）、`list`（**列表**）、`set`（**集合**）、`zset`（**有序集合**），但这些只是 `Redis` 对外的 **数据结构**。如图所示：

![165f79470dff7693](https://user-gold-cdn.xitu.io/2018/9/20/165f79470dff7693?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

对于每种 **数据结构**，实际上都有自己底层的 **内部编码** 实现，而且是 **多种实现**。这样 `Redis` 会在合适的 **场景** 选择合适的 **内部编码**，如图所示：

![](https://user-gold-cdn.xitu.io/2018/9/20/165f79470e44f30b?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

每种 **数据结构** 都有 **两种以上** 的 **内部编码实现**。例如 `list` **数据结构** 包含了 `linkedlist` 和 `ziplist` 两种 **内部编码**。同时有些 **内部编码**，例如 `ziplist`，可以作为 **多种外部数据结构** 的内部实现，可以通过 `object encoding` 命令查询 **内部编码**

`Redis` 这样设计有两个好处：

- **其一**：可以改进 **内部编码**，而对外的 **数据结构** 和 **命令** 没有影响。例如 `Redis3.2` 提供的 `quicklist`，结合了 `ziplist` 和 `linkedlist` 两者的优势，为 **列表类型** 提供了一种 **更加高效** 的 **内部编码实现**。
- **其二**：不同 **内部编码** 可以在 **不同场景** 下发挥各自的 **优势**。例如 `ziplist` 比较 **节省内存**，但是在列表 **元素比较多** 的情况下，**性能** 会有所 **下降**，这时候 `Redis` 会根据 **配置**，将列表类型的 **内部实现** 转换为 `linkedlist`。

#### 字符串

**字符串** 类型的 **内部编码** 有 `3` 种：

- **int**：`8` 个字节的 **长整型**。
- **embstr**：**小于等于** `39` 个字节的字符串。
- **raw**：**大于** `39` 个字节的字符串。

`Redis` 会根据当前值的 **类型** 和 **长度** 决定使用哪种 **内部编码实现**。

#### 哈希

大部分编程语言都提供了 **哈希**（`hash`）类型，它们的叫法可能是 **哈希**、**字典**、**关联数组**。在 `Redis` 中，**哈希类型** 是指键值本身又是一个 **键值对结构**

**哈希** 形如 `value={ {field1，value1}，...{fieldN，valueN} }`，`Redis` **键值对** 和 **哈希类型** 

内部编码

 1. ziplist（压缩列表）

当 **哈希类型** 元素个数 **小于** `hash-max-ziplist-entries` 配置（默认 `512` 个）、同时 **所有值** 都 **小于** `hash-max-ziplist-value` 配置（默认 `64` 字节）时，`Redis` 会使用 `ziplist` 作为 **哈希** 的 **内部实现**，`ziplist` 使用更加 **紧凑的结构** 实现多个元素的 **连续存储**，所以在 **节省内存** 方面比 `hashtable` 更加优秀。

  2. hashtable（哈希表）

当 **哈希类型** 无法满足 `ziplist` 的条件时，`Redis` 会使用 `hashtable` 作为 **哈希** 的 **内部实现**，因为此时 `ziplist` 的 **读写效率** 会下降，而 `hashtable` 的读写 **时间复杂度** 为 `O（1）`。

下面的示例演示了 **哈希类型** 的 **内部编码**，以及相应的变化。

当 `field` 个数 **比较少**，且没有大的 `value` 时，**内部编码** 为 `ziplist`,

当有 `value` **大于** `64` 字节时，**内部编码** 会由 `ziplist` 变为 `hashtable`,

当 `field` 个数 **超过** `512`，**内部编码** 也会由 `ziplist` 变为 `hashtable`,

#### 列表

**列表**（`list`）类型是用来存储多个 **有序** 的 **字符串**。在 `Redis` 中，可以对列表的 **两端** 进行 **插入**（`push`）和 **弹出**（`pop`）操作，还可以获取 **指定范围** 的 **元素列表**、获取 **指定索引下标** 的 **元素** 等。'

**列表** 是一种比较 **灵活** 的 **数据结构**，它可以充当 **栈** 和 **队列** 的角色，在实际开发上有很多应用场景。

当列表的元素个数 **小于** `list-max-ziplist-entries` 配置（默认 `512` 个），同时列表中 **每个元素** 的值都 **小于**  `list-max-ziplist-value` 配置时（默认 `64` 字节），`Redis` 会选用 `ziplist` 来作为 **列表** 的 **内部实现** 来减少内存的使用。

当 **列表类型** 无法满足 `ziplist` 的条件时， `Redis` 会使用 `linkedlist` 作为 **列表** 的 **内部实现**。


#### 集合

**集合**（`set`）类型也是用来保存多个 **字符串元素**，但和 **列表类型** 不一样的是，集合中 **不允许有重复元素**，并且集合中的元素是 **无序的**，不能通过 **索引下标** 获取元素。

如图所示，集合 `user:1:follow` 包含着 `"it"`、`"music"`、`"his"`、`"sports"` 四个元素，一个 **集合** 最多可以存储 `2 ^ 32 - 1` 个元素。`Redis` 除了支持 **集合内** 的 **增删改查**，同时还支持 **多个集合** 取 **交集**、**并集**、**差集**。合理地使用好集合类型，能在实际开发中解决很多实际问题。

![](https://user-gold-cdn.xitu.io/2018/11/8/166f3ef48a69ee12?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

内部编码

1.  **intset（整数集合）**

当集合中的元素都是 **整数** 且 **元素个数** 小于 `set-max-intset-entries` 配置（默认 `512` 个）时，`Redis` 会选用 `intset` 来作为 **集合** 的 **内部实现**，从而 **减少内存** 的使用。

2. **hashtable**

当集合类型 **无法满足** `intset` 的条件时，`Redis` 会使用 `hashtable` 作为集合的 **内部实现**。




### 单线程架构

1. **内存**

`Redis` 将所有数据放在 **内存** 中，内存的 **响应时长** 大约为 `100` **纳秒**，这是 `Redis` 达到 **每秒万级别** 访问的重要基础。

2. **非阻塞IO**

`Redis` 使用 `epoll` 作为 `I/O` **多路复用技术** 的实现，再加上 `Redis` 自身的 **事件处理模型** 将 `epoll` 中的 **连接**、**读写**、**关闭** 都转换为 **事件**，从而不用不在 **网络** `I/O` 上浪费过多的时间，如图所示：

![img](https://user-gold-cdn.xitu.io/2018/9/24/1660be813703e6b6?imageView2/0/w/1280/h/960/format/webp/ignore-error/1)

采用 **单线程** 就能达到如此 **高的性能**，那么不失为一种不错的选择，因为 **单线程** 能带来几个好处：

- **单线程** 可以简化 **数据结构和算法** 的实现，开发人员不需要了解复杂的 **并发数据结构**。
- **单线程** 避免了 **线程切换** 和 **竞态** 产生的消耗，对于服务端开发来说，**锁和线程切换** 通常是性能杀手。

> **单线程** 的问题：对于 **每个命令** 的 **执行时间** 是有要求的。如果某个命令 **执行过长**，会造成其他命令的 **阻塞**，对于 `Redis` 这种 **高性能** 的服务来说是致命的，所以 `Redis` 是面向 **快速执行** 场景的数据库。



## redis使用

### 常用redis命令

- 全局命令

  查看所有键 `keys *` 线上环境禁止使用 O(n)

  键总数 `dbsize`

  键是否存在 `exists key`

  删除键 `del key` 返回删除成功的个数

  添加键过期时间 `expire key seconds` 

  查看键剩余时间 `ttl key`  返回>=0 表示键剩余过期时间 -1表示没设置过期时间 -2 键不存在

  键数据类型 `type key`

  查看编码 `object encoding`

- 字符串操作

  添加 `set key value [ex seconds] [px milliseconds] [nx|xx]`,

  1. **ex seconds**：为 **键** 设置 **秒级过期时间**。简化 `setex` 
  2. **px milliseconds**：为 **键** 设置 **毫秒级过期时间**。
  3. **nx**：键必须 **不存在**，才可以设置成功，用于 **添加**。简化 `setnx`
  4. **xx**：与 `nx` 相反，键必须 **存在**，才可以设置成功，用于 **更新**。

  批量添加 `mset key value [key value ...]`

  查询 `get key`,不存在返回nil

  批量查询 mget key [key ...]

- 数字操作

  增加 `incr key`,除了 `incr` 命令，`Redis` 还提供了 `decr`（**自减**）、`incrby`（**自增指定数字**）、`decrby`（**自减指定数字**）、`incrbyfloat`（**自增浮点数**）等命令操作：

  1. 值不是 **整数**，返回 **错误**。
  2. 值是 **整数**，返回 **自增** 后的结果。
  3. 键不存在，按照值为 `0` **自增**，返回结果为 `1`。

- 哈希操作

  添加 `hset key field value` 成功返回1 否则返回0

  查看 `hget key field`  成功返回值 否则返回nil

  删除 `hdel key field [,,]` ,`hdel` 会删除 **一个或多个** `field`，返回结果为 **成功删除** `field` 的个数

  计算field的个数 `hlen key`

  批量设置或获取 ``hmset` 和 `hmget` 分别是 **批量设置** 和 **获取** `field-value`，`hmset` 需要的参数是 `key` 和 **多对** `field-value`，`hmget` 需要的参数是 `key` 和 **多个** `field`。例如：`

  判断field 是否存在 `hexists key field`

  获取所有field `hkeys key`

  获取所有的value `hvals key`

  获取field和value `hgetall key`

  > 在使用 `hgetall` 时，如果 **哈希元素** 个数比较多，会存在 **阻塞** `Redis` 的可能。如果开发人员只需要获取 **部分** `field`，可以使用 `hmget`，如果一定要获取 **全部** `field-value`，可以使用 `hscan` 命令，该命令会 **渐进式遍历** 哈希类型。

- **list操作**

  添加 `rpush` , `lpush` ,`linsert`

  查看 `lrange` ，`lindex`，`llen`

  刪除 `lpop`,`rpop`,`lrem`,`ltrim`

  修改 `lset`

  阻塞操作 `blpop`,`brpop`

  查看list列表 **`lrange key start end`** start从0开始 倒数第一个元素为-1

- **set操作**

  添加 `sadd [key] [value1] [value2]` 

  遍历 **`smembers [key]`**

  检查是否存在 `sismember [key] value`

  统计元素个数 `scard [key]`

  删除 `srem [key] value`

  交集  `sinter key [...]`

  并集 `suinon key [...]`

  差集 `sdiff key [...]`

  集合结果保存 `sinterstore destination key [key ...] suionstore destination key [key ...] sdiffstore destination key [key ...]`,**集合间** 的运算在 **元素较多** 的情况下会 **比较耗时**，所以 `Redis` 提供了以下 **三个命令**（**原命令** + `store`）将 **集合间交集**、**并集**、**差集** 的结果保存在 `destination key` 中。

- **zset操作**,在set上加一个score值

  添加 `zadd key score value`

  查询 `zrange key start end [withscores]`

  删除 `zrem key value`

  zset长度 `zcard key`


### SpringBoot 集成使用redis

```java
# pom文件
  		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.apache.commons/commons-pool2 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
        </dependency>
# 配置bean

 @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        //初始化一个RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        //设置CacheManager的值序列化方式为json序列化
        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair
                .fromSerializer(jsonSerializer);
        RedisCacheConfiguration defaultCacheConfig=RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(pair);
        //设置默认超过期时间是30秒
        defaultCacheConfig.entryTtl(Duration.ofSeconds(30));
        //初始化RedisCacheManager
        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);

    }

    @Bean
    @SuppressWarnings({"rawtypes", "unchecked"})
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<Object,Object> redisTemplate=new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        //使用Jackson2JsonRedisSerializer替换默认的序列化规则
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer=new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
        //设置value的序列化规则
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        //设置key的序列化规则
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

# yml文件
spring:
  redis:
    database: 0
    host: 106.14.4.232
    port: 6379
    timeout: 2000
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
            
# 使用
@Service
@Slf4j
public class RedisServiceDemo1 {
    @Autowired
    private StringRedisTemplate jsonRedisTemplate;

    public void test1() {
        jsonRedisTemplate.opsForValue().set("asdasd", "qweqwe");
        jsonRedisTemplate.opsForHash().put("hxxx","hxxx_filed","hxxx_value");
        jsonRedisTemplate.opsForList().leftPush("lpxx","qweqwe");
        jsonRedisTemplate.opsForSet().add("set_xxx","asdasd","asdasd");
        jsonRedisTemplate.opsForZSet().add("zset_xxx","adasd",0.1);
    }
    public void test2(){
        log.info(jsonRedisTemplate.getClass().toString());
        jsonRedisTemplate.boundHashOps("user").put("1", JSON.toJSONString( User.builder()
                .birthday(new Date()).userAge(12).userName("陈苗").build()));
    }
}
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class User implements Serializable {
    private String userName;
    private Integer userAge;
    private Date birthday;
}            
```

#### Redis pipline

Redis是采用基于C/S模式的请求/响应协议的TCP服务器。redis客户端通过socket连接发起请求，每个请求在命令触发后会阻塞等待redis服务器进行处理，处理完毕后将结果返回给client。每个请求都存在往返时间，即使redis性能高，当数据量大时也会极大影响性能，还可能引起其他意外情况。

在很多场景下，我们要完成一个业务，可能会对redis做连续多个操作。譬如库存减一，订单加一等等，有很多步骤需要连续依次执行。这种场景下，网络传输的耗时将是限制redis处理量的主要瓶颈。

使用Pipeline可以解决以上问题：Pipeline把所有命令一次性发过去，避免频繁的发送、接收带来的网络开销。redis在打包接收到一堆命令后，依次执行，然后把执行结果再打包返回给客户端。

```java
//获取指定key的所有hashKey
Set<String> studentIdSet = redisTemplate.opsForHash().keys("key:" + "_" + classId);
if (CollectionUtils.isEmpty(studentIdSet)) {
    return false;
}
//根据hashKey依次获取所有value
List<Object> executeResult = redisTemplate.executePipelined(new RedisCallback<Object>() {
    @Override
    public Object doInRedis(RedisConnection connection) throws DataAccessException {
        //业务操作
        if (!CollectionUtils.isEmpty(studentIdSet)) {
            for (String studentId : studentIdSet) {
                connection.get(("key:" + classId + "_" + studentId).toString().getBytes());
            }
        }
        return null;
    }
});
if (!CollectionUtils.isEmpty(executeResult)) {
    for (Object studentObj : executeResult){
        System.out.println("classId:"+classId+"的学生："+JSON.parseObject(studentObj,Student.class));
    }
}

```

#### redis 事务

```java
stringRedisTemplate.setEnableTransactionSupport(true);
    try {
        stringRedisTemplate.multi();//开启事务
        stringRedisTemplate.opsForValue().increment("count", 1);
        stringRedisTemplate.opsForValue().increment("count1", 2);
        //提交
        stringRedisTemplate.exec();
    }catch (Exception e){
        log.error(e.getMessage(), e);
        //开启回滚
        stringRedisTemplate.discard();
    }
```

#### redis 锁

```java
package com.midea.mideacloud.paascommon.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class RedisLockHelper {

    public static final String PACKAGE_PREFIX = "redis_lock";
    public static final String LOCK_PREFIX = "redis_lock_";
    public static final int LOCK_EXPIRE = 1800000; // 单位ms  30分钟过期
    
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    
    /**
     *  redis分布式锁
     *
     * @param key
     * @return 是否获取到
     */
    public boolean lock(String key){
        String lock = PACKAGE_PREFIX+":"+LOCK_PREFIX + key;
        return (Boolean) stringRedisTemplate.execute((RedisCallback) connection -> {
            long expireAt = System.currentTimeMillis() + LOCK_EXPIRE + 1;
            Boolean acquire = connection.setNX(lock.getBytes(), String.valueOf(expireAt).getBytes());
            if (acquire) {
                return true;
            } else {
                byte[] value = connection.get(lock.getBytes());
                if (Objects.nonNull(value) && value.length > 0) {
                    long expireTime = Long.parseLong(new String(value));
                    if (expireTime < System.currentTimeMillis()) {
                        // 如果锁已经过期，获取旧值设置新值，防止死锁
                        byte[] oldValue = connection.getSet(lock.getBytes(), String.valueOf(System.currentTimeMillis() + LOCK_EXPIRE + 1).getBytes());
                        return Long.parseLong(new String(oldValue)) < System.currentTimeMillis();
                    }
                }
            }
            return false;
        });
    }
    
    /**
     * 删除锁
     *
     * @param key
     */
    public void delete(String key) {
        stringRedisTemplate.delete(PACKAGE_PREFIX+":"+LOCK_PREFIX + key);
    }

}

```



#### redis 乐观锁

```java
redisTemplate.watch("key"); // 1
redisTemplate.multi();
redisTemplate.boundValueOps("key").set(""+id);
List<Object> list= redisTemplate.exec();
System.out.println(list);
if(list != null ){
    //操作成功
    System.out.println(id+"操作成功");
}else{
    //操作失败
    System.out.println(id+"操作失败");
}
```

#### redis 分布式锁

```java
String lockKey = "key";
String lockValue = lockKey+System.currentTimeMillis();
// value需要记住用于解锁
while (true){

   Boolean ifPresent = stringRedisTemplate.opsForValue().
                setIfAbsent("redis-lock:" + lockKey, lockValue, 3, TimeUnit.SECONDS);

   if (ifPresent){
          log.info("get redis-lock success");
          break;
    }
 }
//解锁
  String lockKey = "key";
 String lockValue = lockKey + System.currentTimeMillis();
 boolean result = false;
 // value需要记住用于解锁
 stringRedisTemplate.watch("redis-lock:" + lockKey);
 String value = stringRedisTemplate.opsForValue().get("redis-lock:" + lockKey);
 if (null == value){
     result = true;
 }else if (value.equals(lockValue)) {
     stringRedisTemplate.delete("redis-lock:" + lockKey);
     result = true;
 }
 stringRedisTemplate.unwatch();  
```



### Redis Lua脚本

从 Redis 2.6.0 版本开始，通过内置的 Lua 解释器，可以使用 [EVAL](http://redisdoc.com/script/eval.html#eval) 命令对 Lua 脚本进行求值。

`EVAL script numkeys key [key …] arg [arg …]`

`script` 参数是一段 Lua 5.1 脚本程序，它会被运行在 Redis 服务器上下文中，这段脚本不必(也不应该)定义为一个 Lua 函数。

`numkeys` 参数用于指定键名参数的个数。

键名参数 `key [key ...]` 从 [EVAL](http://redisdoc.com/script/eval.html#eval) 的第三个参数开始算起，表示在脚本中所用到的那些 Redis 键(key)，这些键名参数可以在 Lua 中通过全局变量 `KEYS` 数组，用 `1` 为基址的形式访问( `KEYS[1]` ， `KEYS[2]` ，以此类推)。

在命令的最后，那些不是键名参数的附加参数 `arg [arg ...]` ，可以在 Lua 中通过全局变量 `ARGV` 数组访问，访问的形式和 `KEYS` 变量类似( `ARGV[1]` 、 `ARGV[2]` ，诸如此类)。

在 Lua 脚本中，可以使用两个不同函数来执行 Redis 命令，它们分别是：

- `redis.call()`
- `redis.pcall()`

这两个函数的唯一区别在于它们使用不同的方式处理执行命令所产生的错误

`redis.call()` 在执行命令的过程中发生错误时，脚本会停止执行，并返回一个脚本错误，错误的输出信息会说明错误造成的原因.

`redis.pcall()` 出错时并不引发(raise)错误，而是返回一个带 `err` 域的 Lua 表(table)

```lua
-- 设置值
> eval "return redis.call('set','foo','bar')" 0
OK
-- 也可以这样
 eval "return redis.call('set',KEYS[1],'bar')" 1 foo
OK

```

**原子性**

Redis 使用单个 Lua 解释器去运行所有脚本，并且， Redis 也保证脚本会以原子性(atomic)的方式执行：当某个脚本正在运行的时候，不会有其他脚本或 Redis 命令被执行。这和使用 [MULTI](http://redisdoc.com/transaction/multi.html#multi) / [EXEC](http://redisdoc.com/transaction/exec.html#exec) 包围的事务很类似。在其他别的客户端看来，脚本的效果(effect)要么是不可见的(not visible)，要么就是已完成的(already completed)。

另一方面，这也意味着，执行一个运行缓慢的脚本并不是一个好主意。写一个跑得很快很顺溜的脚本并不难，因为脚本的运行开销(overhead)非常少，但是当你不得不使用一些跑得比较慢的脚本时，请小心，因为当这些蜗牛脚本在慢吞吞地运行的时候，其他客户端会因为服务器正忙而无法执行命令。

**EVALSHA命令**

[EVAL](http://redisdoc.com/script/eval.html#eval) 命令要求你在每次执行脚本的时候都发送一次脚本主体(script body)。Redis 有一个内部的缓存机制，因此它不会每次都重新编译脚本，不过在很多场合，付出无谓的带宽来传送脚本主体并不是最佳选择。

为了减少带宽的消耗， Redis 实现了 EVALSHA 命令，它的作用和 [EVAL](http://redisdoc.com/script/eval.html#eval) 一样，都用于对脚本求值，但它接受的第一个参数不是脚本，而是脚本的 SHA1 校验和(sum)。

EVALSHA 命令的表现如下：

- 如果服务器还记得给定的 SHA1 校验和所指定的脚本，那么执行这个脚本
- 如果服务器不记得给定的 SHA1 校验和所指定的脚本，那么它返回一个特殊的错误，提醒用户使用 [EVAL](http://redisdoc.com/script/eval.html#eval) 代替 EVALSHA

**redis内置的库**

Redis 内置的 Lua 解释器加载了以下 Lua 库：

- `base`
- `table`
- `string`
- `math`
- `debug`
- `cjson`
- `cmsgpack`

其中 `cjson` 库可以让 Lua 以非常快的速度处理 JSON 数据，除此之外，其他别的都是 Lua 的标准库。

**脚本redis日志**

在 Lua 脚本中，可以通过调用 `redis.log` 函数来写 Redis 日志(log)：

```
redis.log(loglevel, message)
```

其中， `message` 参数是一个字符串，而 `loglevel` 参数可以是以下任意一个值：

- `redis.LOG_DEBUG`
- `redis.LOG_VERBOSE`
- `redis.LOG_NOTICE`
- `redis.LOG_WARNING`

上面的这些等级(level)和标准 Redis 日志的等级相对应。

对于脚本散发(emit)的日志，只有那些和当前 Redis 实例所设置的日志等级相同或更高级的日志才会被散发。

以下是一个日志示例：

```lua
redis.log(redis.LOG_WARNING, "Something is wrong with this script.")
```

执行上面的函数会产生这样的信息：

```lua
[32343] 22 Mar 15:21:39 # Something is wrong with this script.
```

控制脚本的执行时间

redisconf中的`lua-time-limit`(毫秒)

## redis哨兵

**redis主从搭建**

https://blog.csdn.net/qq_28804275/article/details/80907796

https://juejin.im/post/5b76e732f265da4376203849

`Redis` 的 **主从复制** 模式下，一旦 **主节点** 由于故障不能提供服务，需要手动将 **从节点** 晋升为 **主节点**，同时还要通知 **客户端** 更新 **主节点地址**，这种故障处理方式从一定程度上是无法接受的。`Redis 2.8` 以后提供了 `Redis Sentinel` **哨兵机制** 来解决这个问题。

### Redis Sentinel

`Redis Sentinel` 是 `Redis` **高可用** 的实现方案。`Sentinel` 是一个管理多个 `Redis` 实例的工具，它可以实现对 `Redis` 的 **监控**、**通知**、**自动故障转移**。下面先对 `Redis Sentinel` 的 **基本概念** 进行简单的介绍。

| 名称             | 逻辑结构                   | 物理结构                            |
| ---------------- | -------------------------- | ----------------------------------- |
| Redis数据节点    | 主节点和从节点             | 主节点和从节点的进程                |
| 主节点(master)   | Redis主数据库              | 一个独立的Redis进程                 |
| 从节点(slave)    | Redis从数据库              | 一个独立的Redis进程                 |
| Sentinel节点     | 监控Redis数据节点          | 一个独立的Sentinel进程              |
| Sentinel节点集合 | 若干Sentinel节点的抽象组合 | 若干Sentinel节点进程                |
| Redis Sentinel   | Redis高可用实现方案        | Sentinel节点集合和Redis数据节点进程 |
| 应用客户端       | 泛指一个或多个客户端       | 一个或者多个客户端进程或者线程      |

主从复制存在的问题

一旦 **主节点宕机**，**从节点** 晋升成 **主节点**，同时需要修改 **应用方** 的 **主节点地址**，还需要命令所有 **从节点** 去 **复制** 新的主节点，整个过程需要 **人工干预**。

**主节点** 的 **写能力** 受到 **单机的限制**。

**主节点** 的 **存储能力** 受到 **单机的限制**。

**原生复制** 的弊端在早期的版本中也会比较突出，比如：`Redis` **复制中断** 后，**从节点** 会发起 `psync`。此时如果 **同步不成功**，则会进行 **全量同步**，**主库** 执行 **全量备份** 的同时，可能会造成毫秒或秒级的 **卡顿**。

### Redis Sentinel的主要功能

`Sentinel` 的主要功能包括 **主节点存活检测**、**主从运行情况检测**、**自动故障转移** （`failover`）、**主从切换**。`Redis` 的 `Sentinel` 最小配置是 **一主一从**。

`Redis` 的 `Sentinel` 系统可以用来管理多个 `Redis` 服务器，该系统可以执行以下四个任务：

- **监控**

`Sentinel` 会不断的检查 **主服务器** 和 **从服务器** 是否正常运行。

- **通知**

当被监控的某个 `Redis` 服务器出现问题，`Sentinel` 通过 `API` **脚本** 向 **管理员** 或者其他的 **应用程序** 发送通知。

- **自动故障转移**

当 **主节点** 不能正常工作时，`Sentinel` 会开始一次 **自动的** 故障转移操作，它会将与 **失效主节点** 是 **主从关系** 的其中一个 **从节点** 升级为新的 **主节点**，并且将其他的 **从节点** 指向 **新的主节点**。

- **配置提供者**

在 `Redis Sentinel` 模式下，**客户端应用** 在初始化时连接的是 `Sentinel` **节点集合**，从中获取 **主节点** 的信息。



默认情况下，**每个** `Sentinel` 节点会以 **每秒一次** 的频率对 `Redis` 节点和 **其它** 的 `Sentinel` 节点发送 `PING` 命令，并通过节点的 **回复** 来判断节点是否在线。

- **主观下线**

**主观下线** 适用于所有 **主节点** 和 **从节点**。如果在 `down-after-milliseconds` 毫秒内，`Sentinel` 没有收到 **目标节点** 的有效回复，则会判定 **该节点** 为 **主观下线**。

- **客观下线**

**客观下线** 只适用于 **主节点**。如果 **主节点** 出现故障，`Sentinel` 节点会通过 `sentinel is-master-down-by-addr` 命令，向其它 `Sentinel` 节点询问对该节点的 **状态判断**。如果超过 `` 个数的节点判定 **主节点** 不可达，则该 `Sentinel` 节点会判断 **主节点** 为 **客观下线**。

### Sentinel的通信命令

`Sentinel` 节点连接一个 `Redis` 实例的时候，会创建 `cmd` 和 `pub/sub` 两个 **连接**。`Sentinel` 通过 `cmd` 连接给 `Redis` 发送命令，通过 `pub/sub` 连接到 `Redis` 实例上的其他 `Sentinel` 实例。

`Sentinel`和`redis`的交互命令

| 命令      | 作用                                                         |
| --------- | ------------------------------------------------------------ |
| PING      | `entinel` 向 `Redis` 节点发送 `PING` 命令，检查节点的状态    |
| INFO      | `Sentinel` 向 `Redis` 节点发送 `INFO` 命令，获取它的 **从节点信息** |
| PUBLISH   | `Sentinel` 向其监控的 `Redis` 节点 `__sentinel__:hello` 这个 `channel` 发布 **自己的信息** 及 **主节点** 相关的配置 |
| SUBSCRIBE | `Sentinel` 通过订阅 `Redis` **主节点** 和 **从节点** 的 `__sentinel__:hello` 这个 `channnel`，获取正在监控相同服务的其他 `Sentinel` 节点 |

`Sentinel` 与 `Sentinel` 交互的命令，主要包括：

| 命令                            | 作 用                                                        |
| ------------------------------- | ------------------------------------------------------------ |
| PING                            | `Sentinel` 向其他 `Sentinel` 节点发送 `PING` 命令，检查节点的状态 |
| SENTINEL:is-master-down-by-addr | 和其他 `Sentinel` 协商 **主节点** 的状态，如果 **主节点** 处于 `SDOWN` 状态，则投票自动选出新的 **主节点** |

### redis集群

在 `Redis 3.0` 之前，使用 **哨兵**（`sentinel`）机制来监控各个节点之间的状态。`Redis Cluster` 是 `Redis` 的 **分布式解决方案**，在 `3.0` 版本正式推出，有效地解决了 `Redis` 在 **分布式** 方面的需求。当遇到 **单机内存**、**并发**、**流量** 等瓶颈时，可以采用 `Cluster` 架构方案达到 **负载均衡** 的目的。

#### 集群解决方案

`Redis Cluster` 集群模式通常具有 **高可用**、**可扩展性**、**分布式**、**容错** 等特性。`Redis` 分布式方案一般有两种：

##### 客户端分区方案

**客户端** 就已经决定数据会被 **存储** 到哪个 `redis` 节点或者从哪个 `redis` 节点 **读取数据**。其主要思想是采用 **哈希算法** 将 `Redis` 数据的 `key` 进行散列，通过 `hash` 函数，特定的 `key`会 **映射** 到特定的 `Redis` 节点上。

![image.png](https://i.loli.net/2020/03/21/xQEnHVkyqFZoeiK.png)

**客户端分区方案** 的代表为 `Redis Sharding`，`Redis Sharding` 是 `Redis Cluster` 出来之前，业界普遍使用的 `Redis` **多实例集群** 方法。`Java` 的 `Redis` 客户端驱动库 `Jedis`，支持 `Redis Sharding` 功能，即 `ShardedJedis` 以及 **结合缓存池** 的 `ShardedJedisPool`。

- **优点**

不使用 **第三方中间件**，**分区逻辑** 可控，**配置** 简单，节点之间无关联，容易 **线性扩展**，灵活性强。

- **缺点**

**客户端** 无法 **动态增删** 服务节点，客户端需要自行维护 **分发逻辑**，客户端之间 **无连接共享**，会造成 **连接浪费**。

##### 代理分区方案

**客户端** 发送请求到一个 **代理组件**，**代理** 解析 **客户端** 的数据，并将请求转发至正确的节点，最后将结果回复给客户端。

- **优点**：简化 **客户端** 的分布式逻辑，**客户端** 透明接入，切换成本低，代理的 **转发** 和 **存储** 分离。
- **缺点**：多了一层 **代理层**，加重了 **架构部署复杂度** 和 **性能损耗**。

![image.png](https://i.loli.net/2020/03/21/ytQ6I1SMfnPwdNa.png)

**代理分区** 主流实现的有方案有 `Twemproxy` 和 `Codis`。

#####  查询路由方案

**客户端随机地** 请求任意一个 `Redis` 实例，然后由 `Redis` 将请求 **转发** 给 **正确** 的 `Redis` 节点。`Redis Cluster` 实现了一种 **混合形式** 的 **查询路由**，但并不是 **直接** 将请求从一个 `Redis` 节点 **转发** 到另一个 `Redis` 节点，而是在 **客户端** 的帮助下直接 **重定向**（ `redirected`）到正确的 `Redis` 节点。

![image.png](https://i.loli.net/2020/03/21/2lOYQzitwbvR4d5.png)

#### 数据分布

**分布式数据库** 首先要解决把 **整个数据集** 按照 **分区规则** 映射到 **多个节点** 的问题，即把 **数据集** 划分到 **多个节点** 上，每个节点负责 **整体数据** 的一个 **子集**。

![image.png](https://i.loli.net/2020/03/21/BEKfF7zQySRiwxp.png)

数据分区包括 **哈希分区**,和**顺序分区**

| 分区方式 | 特点                                             | 相关产品                         |
| -------- | ------------------------------------------------ | -------------------------------- |
| 哈希分区 | 离散程度好，数据分布与业务无关，无法顺序访问     | Redis Cluster，Cassandra，Dynamo |
| 顺序分区 | 离散程度易倾斜，数据分布与业务相关，可以顺序访问 | BigTable，HBase，Hypertable      |

由于 `Redis Cluster` 采用 **哈希分区规则**，这里重点讨论 **哈希分区**。

使用特定的数据，如 `Redis` 的 **键** 或 **用户** `ID`，再根据 **节点数量** `N` 使用公式：`hash（key）% N` 计算出 **哈希值**，用来决定数据 **映射** 到哪一个节点上。

![image.png](https://i.loli.net/2020/03/21/jp6PJCoy2XuhiWN.png)

- **优点**

这种方式的突出优点是 **简单性**，常用于 **数据库** 的 **分库分表规则**。一般采用 **预分区** 的方式，提前根据 **数据量** 规划好 **分区数**，比如划分为 `512` 或 `1024` 张表，保证可支撑未来一段时间的 **数据容量**，再根据 **负载情况** 将 **表** 迁移到其他 **数据库** 中。扩容时通常采用 **翻倍扩容**，避免 **数据映射** 全部被 **打乱**，导致 **全量迁移** 的情况。

- **缺点**

当 **节点数量** 变化时，如 **扩容** 或 **收缩** 节点，数据节点 **映射关系** 需要重新计算，会导致数据的 **重新迁移**。

##### 一致性hash分区

**一致性哈希** 可以很好的解决 **稳定性问题**，可以将所有的 **存储节点** 排列在 **收尾相接** 的 `Hash` 环上，每个 `key` 在计算 `Hash` 后会 **顺时针** 找到 **临接** 的 **存储节点** 存放。而当有节点 **加入** 或 **退出** 时，仅影响该节点在 `Hash` 环上 **顺时针相邻** 的 **后续节点**。

![image.png](https://i.loli.net/2020/03/21/UqcxLIjEof7TXb2.png)

- **优点**

**加入** 和 **删除** 节点只影响 **哈希环** 中 **顺时针方向** 的 **相邻的节点**，对其他节点无影响。

- **缺点**

**加减节点** 会造成 **哈希环** 中部分数据 **无法命中**。当使用 **少量节点** 时，**节点变化** 将大范围影响 **哈希环** 中 **数据映射**，不适合 **少量数据节点** 的分布式方案。**普通** 的 **一致性哈希分区** 在增减节点时需要 **增加一倍** 或 **减去一半** 节点才能保证 **数据** 和 **负载的均衡**。。

##### 虚拟槽分区

**虚拟槽分区** 巧妙地使用了 **哈希空间**，使用 **分散度良好** 的 **哈希函数** 把所有数据 **映射** 到一个 **固定范围** 的 **整数集合** 中，整数定义为 **槽**（`slot`）。这个范围一般 **远远大于** 节点数，比如 `Redis Cluster` 槽范围是 `0 ~ 16383`。**槽** 是集群内 **数据管理** 和 **迁移** 的 **基本单位**。采用 **大范围槽** 的主要目的是为了方便 **数据拆分** 和 **集群扩展**。每个节点会负责 **一定数量的槽**，如图所示：

![image.png](https://i.loli.net/2020/03/21/r36Da8GxSgw1ydh.png)

当前集群有 `5` 个节点，每个节点平均大约负责 `3276` 个 **槽**。由于采用 **高质量** 的 **哈希算法**，每个槽所映射的数据通常比较 **均匀**，将数据平均划分到 `5` 个节点进行 **数据分区**。`Redis Cluster` 就是采用 **虚拟槽分区**。

- **节点1**： 包含 `0` 到 `3276` 号哈希槽。
- **节点2**：包含 `3277`  到 `6553` 号哈希槽。
- **节点3**：包含 `6554` 到 `9830` 号哈希槽。
- **节点4**：包含 `9831` 到 `13107` 号哈希槽。
- **节点5**：包含 `13108` 到 `16383` 号哈希槽。

这种结构很容易 **添加** 或者 **删除** 节点。如果 **增加** 一个节点 `6`，就需要从节点 `1 ~ 5` 获得部分 **槽** 分配到节点 `6` 上。如果想 **移除** 节点 `1`，需要将节点 `1` 中的 **槽** 移到节点 `2 ~ 5` 上，然后将 **没有任何槽** 的节点 `1` 从集群中 **移除** 即可。