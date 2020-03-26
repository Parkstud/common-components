---
author: 陈苗
time: 2019/12/16
email:  parkstud@qq.com
tip: 文档根据Netty4.1学习,不同版本差别很大
---

[TOC]



# Netty学习

## Netty是什么

netty是一个异步事件驱动的网络应用程序框架,用于快速开发可维护的高性能服务器和客户端,它简化了TCP和UDP套接字服务器.它具有统一的API,支持多种传输类型,简单而强大的线程模型.真正的雾连接数据报套接字支持.

## 案例驱动学习

Netty官网的案例特别多,不敲敲不就浪费了Netty的良苦用心,毕竟是一个网络应用框架,我们需要具体怎么做,而不需要太多的理论知识.

### 实现一个DISCARD服务器

实现一个丢弃协议([DISCARD](https://tools.ietf.org/html/rfc863)),接受到任何数据都直接丢弃.

```java
/**
 * 实现丢弃协议
 *  ChannelInboundHandler 提供对各种事件的处理
 * @author parkstud@qq.com 2019-12-16
 */
@Slf4j
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 重写 channelRead 事件处理
     * 每当从客户端接受消息,就会调用此方法
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 直接丢弃接受到的数据
        log.info("丢弃数据: {}",msg);
        ((ByteBuf) msg).release();
    }

    /**
     * 当netty 处理IO发生异常或者处理事件发生异常就会调动exceptionCaught方法
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常关闭连接
        log.error("handle异常", cause);
        ctx.close();
    }
}

```



```java

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 丢弃协议服务器实现
 *
 * @author parkstud@qq.com 2019-12-16
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscardServer {
    /**
     * 端口号
     */
    private int port;

    public void run() throws InterruptedException {
        /*
         *处理I/O操作的多线程事件环, netty提供各种eventLoopGroup实现不同传输方式
         * 有两种NioEventLoopGroup使用,boss处理接受传入的连接, worker处理boss连接之后的处理.
         * 线程数和通道数依赖EventLoopGroup的具体实现,可以在构造函数配置
         */
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //ServerBootstrap是一个帮助设置服务器的帮助类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    // 使用NioServerSocketChannel类型实例化 来接收连接
                    .channel(NioServerSocketChannel.class)
                    /*
                     * 一个handler用来处理新的Channel
                     * ChannelInitializer 是一个特殊handler 来配置一个新的Channel
                     * 配置Channel中的ChannelPipeline的Handler(DiscardServerHandler...)实现应用
                     */
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    //对Channel设置参数 例如 tcpNoDelay keepAlive 这些tcp参数 针对boss线程组
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 针对worker线程组
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            //绑定并接受连接
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            // 等待 直到 服务器socket关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new DiscardServer(8080).run();
    }
}

```

**启动**

```
11:23:14.677 [main] DEBUG io.netty.util.internal.logging.InternalLoggerFactory - Using SLF4J as the default logging framework
11:23:14.683 [main] DEBUG io.netty.channel.MultithreadEventLoopGroup - -Dio.netty.eventLoopThreads: 16
11:23:14.712 [main] DEBUG io.netty.util.internal.InternalThreadLocalMap - -Dio.netty.threadLocalMap.stringBuilder.initialSize: 1024
11:23:14.712 [main] DEBUG io.netty.util.internal.InternalThreadLocalMap - -Dio.netty.threadLocalMap.stringBuilder.maxSize: 4096
11:23:14.726 [main] DEBUG io.netty.channel.nio.NioEventLoop - -Dio.netty.noKeySetOptimization: false
11:23:14.726 [main] DEBUG io.netty.channel.nio.NioEventLoop - -Dio.netty.selectorAutoRebuildThreshold: 512
11:23:14.754 [main] DEBUG io.netty.util.internal.PlatformDependent - Platform: Windows
11:23:14.757 [main] DEBUG io.netty.util.internal.PlatformDependent0 - -Dio.netty.noUnsafe: false
11:23:14.757 [main] DEBUG io.netty.util.internal.PlatformDependent0 - Java version: 8
11:23:14.759 [main] DEBUG io.netty.util.internal.PlatformDependent0 - sun.misc.Unsafe.theUnsafe: available
11:23:14.760 [main] DEBUG io.netty.util.internal.PlatformDependent0 - sun.misc.Unsafe.copyMemory: available
11:23:14.761 [main] DEBUG io.netty.util.internal.PlatformDependent0 - java.nio.Buffer.address: available
11:23:14.762 [main] DEBUG io.netty.util.internal.PlatformDependent0 - direct buffer constructor: available
11:23:14.763 [main] DEBUG io.netty.util.internal.PlatformDependent0 - java.nio.Bits.unaligned: available, true
11:23:14.764 [main] DEBUG io.netty.util.internal.PlatformDependent0 - jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable prior to Java9
11:23:14.764 [main] DEBUG io.netty.util.internal.PlatformDependent0 - java.nio.DirectByteBuffer.<init>(long, int): available
11:23:14.764 [main] DEBUG io.netty.util.internal.PlatformDependent - sun.misc.Unsafe: available
11:23:14.765 [main] DEBUG io.netty.util.internal.PlatformDependent - -Dio.netty.tmpdir: C:\Users\chen\AppData\Local\Temp (java.io.tmpdir)
11:23:14.765 [main] DEBUG io.netty.util.internal.PlatformDependent - -Dio.netty.bitMode: 64 (sun.arch.data.model)
11:23:14.766 [main] DEBUG io.netty.util.internal.PlatformDependent - -Dio.netty.maxDirectMemory: 3791650816 bytes
11:23:14.766 [main] DEBUG io.netty.util.internal.PlatformDependent - -Dio.netty.uninitializedArrayAllocationThreshold: -1
11:23:14.768 [main] DEBUG io.netty.util.internal.CleanerJava6 - java.nio.ByteBuffer.cleaner(): available
11:23:14.768 [main] DEBUG io.netty.util.internal.PlatformDependent - -Dio.netty.noPreferDirect: false
11:23:14.786 [main] DEBUG io.netty.util.internal.PlatformDependent - org.jctools-core.MpscChunkedArrayQueue: available
11:23:15.139 [main] DEBUG io.netty.channel.DefaultChannelId - -Dio.netty.processId: 17392 (auto-detected)
11:23:15.140 [main] DEBUG io.netty.util.NetUtil - -Djava.net.preferIPv4Stack: false
11:23:15.140 [main] DEBUG io.netty.util.NetUtil - -Djava.net.preferIPv6Addresses: false
11:23:15.307 [main] DEBUG io.netty.util.NetUtil - Loopback interface: lo (Software Loopback Interface 1, 127.0.0.1)
11:23:15.308 [main] DEBUG io.netty.util.NetUtil - Failed to get SOMAXCONN from sysctl and file \proc\sys\net\core\somaxconn. Default: 200
11:23:15.573 [main] DEBUG io.netty.channel.DefaultChannelId - -Dio.netty.machineId: d0:ab:d5:ff:fe:c7:74:55 (auto-detected)
11:23:15.596 [main] DEBUG io.netty.util.ResourceLeakDetector - -Dio.netty.leakDetection.level: simple
11:23:15.596 [main] DEBUG io.netty.util.ResourceLeakDetector - -Dio.netty.leakDetection.targetRecords: 4
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.numHeapArenas: 16
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.numDirectArenas: 16
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.pageSize: 8192
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.maxOrder: 11
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.chunkSize: 16777216
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.tinyCacheSize: 512
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.smallCacheSize: 256
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.normalCacheSize: 64
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.maxCachedBufferCapacity: 32768
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.cacheTrimInterval: 8192
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.cacheTrimIntervalMillis: 0
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.useCacheForAllThreads: true
11:23:15.647 [main] DEBUG io.netty.buffer.PooledByteBufAllocator - -Dio.netty.allocator.maxCachedByteBuffersPerChunk: 1023
11:23:15.661 [main] DEBUG io.netty.buffer.ByteBufUtil - -Dio.netty.allocator.type: pooled
11:23:15.661 [main] DEBUG io.netty.buffer.ByteBufUtil - -Dio.netty.threadLocalDirectBufferSize: 0
11:23:15.661 [main] DEBUG io.netty.buffer.ByteBufUtil - -Dio.netty.maxThreadLocalCharBufferSize: 16384
```

**测试**

打开tennet

![image.png](https://i.loli.net/2019/12/17/ibH3OKdgJov5nA9.png)

```
11:26:06.125 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxCapacityPerThread: 4096
11:26:06.125 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.maxSharedCapacityFactor: 2
11:26:06.125 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.linkCapacity: 16
11:26:06.125 [nioEventLoopGroup-3-1] DEBUG io.netty.util.Recycler - -Dio.netty.recycler.ratio: 8
11:26:06.130 [nioEventLoopGroup-3-1] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkAccessible: true
11:26:06.131 [nioEventLoopGroup-3-1] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkBounds: true
11:26:06.131 [nioEventLoopGroup-3-1] DEBUG io.netty.util.ResourceLeakDetectorFactory - Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@32be893d
11:26:06.133 [nioEventLoopGroup-3-1] INFO com.example.demoothers1.nettyDemo.demo5.DiscardServerHandler - 丢弃数据: PooledUnsafeDirectByteBuf(ridx: 0, widx: 1, cap: 1024)
11:26:06.799 [nioEventLoopGroup-3-1] INFO com.example.demoothers1.nettyDemo.demo5.DiscardServerHandler - 丢弃数据: PooledUnsafeDirectByteBuf(ridx: 0, widx: 1, cap: 1024)
11:26:06.988 [nioEventLoopGroup-3-1] INFO com.example.demoothers1.nettyDemo.demo5.DiscardServerHandler - 丢弃数据: PooledUnsafeDirectByteBuf(ridx: 0, widx: 1, cap: 512)
11:26:07.559 [nioEventLoopGroup-3-1] INFO com.example.demoothers1.nettyDemo.demo5.DiscardServerHandler - 丢弃数据: PooledUnsafeDirectByteBuf(ridx: 0, widx: 1, cap: 512)
11:26:08.179 [nioEventLoopGroup-3-1] INFO com.example.demoothers1.nettyDemo.demo5.DiscardServerHandler - 丢弃数据: PooledUnsafeDirectByteBuf(ridx: 0, widx: 1, cap: 496)

```

修改handler 查看具体丢弃的数据

```java
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 直接丢弃接受到的数据
        ByteBuf in = (ByteBuf) msg;
        try {
            String s = in.toString(Charset.defaultCharset());
            log.info("丢弃数据: {}", s);
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }
```

```
11:52:19.472 [nioEventLoopGroup-3-1] INFO com.example.demoothers1.nettyDemo.demo5.DiscardServerHandler - 丢弃数据: a
11:52:22.124 [nioEventLoopGroup-3-1] INFO com.example.demoothers1.nettyDemo.demo5.DiscardServerHandler - 丢弃数据: a
11:52:22.224 [nioEventLoopGroup-3-1] INFO com.example.demoothers1.nettyDemo.demo5.DiscardServerHandler - 丢弃数据: s
11:52:22.289 [nioEventLoopGroup-3-1] INFO com.example.demoothers1.nettyDemo.demo5.DiscardServerHandler - 丢弃数据: d
11:52:22.413 [nioEventLoopGroup-3-1] INFO com.example.demoothers1.nettyDemo.demo5.DiscardServerHandler - 丢弃数据: a
11:52:22.503 [nioEventLoopGroup-3-1] INFO com.example.demoothers1.nettyDemo.demo5.DiscardServerHandler - 丢弃数据: d
```

#### 服务器实现步骤

- XXServerHandler 实现了业务逻辑
- main() 方法引导服务器:
  - 创建一个ServerBootstrap的实例引导和绑定服务
  - 创建并分配一个NioEventLoopGroup实例进行事件的处理,接受新连接或者读写数据
  - 指定服务器绑定的本地InetSocketAddress
  - 使用XXServerHandler的实例化初始每一个新的Channel
  - 调用ServerBootstrao.bind() 方法绑定服务器

#### 客户端实现步骤

- 创建一个Bootstrap实例
- 为事件分配一个NioEventLoopGroup,其中事件处理包括创建连接和处理入栈和出站数据
- 为服务器连接创建一个InetSocketAddress
- 调用Bootstrap.connect方法连接远程节点

### 编写一个Echo服务器

上面的案例里面,没有响应信息,现在我们实现一个ECHO协议,将服务器响应信息写入到客户端.(发送信息之间返回)

只需要修改Handler中的channelRead方法

```java
  @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        /*
        * ChannelHandlerContext 提供各种操作触发IO事件
        * 这里调用writeAndFlush方法,将客户端发送的消息 写会到客户端
        * 注意我们没有调用release , 因为当我们write后 netty帮我们release了
        * write方法只写入到内部缓冲,flush 才刷入.
        * */
        ctx.writeAndFlush(msg);
    }
```

测试

![image.png](https://i.loli.net/2019/12/17/AUIxpZ8iRWy6n7Y.png)

### 编写时间服务器

实现协议[TIME](https://tools.ietf.org/html/rfc868) 客户端与服务器建立连接,服务器向客户端发送时间,然后关闭连接.

**服务端Handler**'

```java
**
 * 实现 时间服务器
 *
 * @author parkstud@qq.com 2019-12-17
 */
@Slf4j
public class TimeServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 当建立连接时 , 会调用channelActive方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        /*
         * 发送消息需要一杯4字节(32位)buffer
         * */
        final ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int) (Instant.now().toEpochMilli()/1000L+2208988800L));
        /*
         * 在NIO中写之前 需要flip 但是在netty 中不需要,
         * 它使用了两个point 一个用于读 一个用于写
         * ChannelFuture表示一个尚未发生的IO操作
         * */
        final ChannelFuture future = ctx.writeAndFlush(time);
        /*
         * 当write 完成 关闭
         * */
        future.addListener((ChannelFutureListener) f -> {
            Assert.isTrue(f == future, "对象处理错误!");
            ctx.close();
        });
    }

    /**
     * 处理异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("time handler error", cause);
        ctx.close();
    }

}

```

客户端

```java
package com.example.demoothers1.nettyDemo.demo5;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 时间服务器客户端
 *
 * @author parkstud@qq.com 2019-12-17
 */
@Slf4j
public class TimeClient {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8080;

    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        /*
         * 如果只有一个EventLoopGroup, 那么这个Group将即用于boss 也用于worker
         * 但是在客户端没有boss
         * */
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        // 客户端没有父级,所以没有childOption
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new TimeClientHandler());
            }
        });
        try {
            // start client,客户端时候connect,服务端使用bind
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(HOST, PORT)).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("客户端连接异常: ", e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}

/**
 * 时间客户端
 * 1. 从服务器接收32位整数
 * 2. 转化时间格式并打印
 * 3. 关闭连接
 */
@Slf4j
class TimeClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        try {
            long time = (buf.readUnsignedInt() - 2208988800L) * 1000L;
            log.info("获取系统时间:"+ LocalDateTime.ofEpochSecond(time / 1000L, 0, OffsetDateTime.now().getOffset()));
            ctx.close();
        } finally {
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端处理器异常:", cause);
        ctx.close();
    }
}

```



```
16:22:23.069 [nioEventLoopGroup-2-1] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkAccessible: true
16:22:23.070 [nioEventLoopGroup-2-1] DEBUG io.netty.buffer.AbstractByteBuf - -Dio.netty.buffer.checkBounds: true
16:22:23.071 [nioEventLoopGroup-2-1] DEBUG io.netty.util.ResourceLeakDetectorFactory - Loaded default ResourceLeakDetector: io.netty.util.ResourceLeakDetector@74e2a183
16:22:23.086 [nioEventLoopGroup-2-1] INFO com.example.demoothers1.nettyDemo.demo5.TimeClientHandler - 获取系统时间:2019-12-17T16:22:23
16:22:25.295 [nioEventLoopGroup-2-1] DEBUG io.netty.buffer.PoolThreadCache - Freed 1 thread-local buffer(s) from thread: nioEventLoopGroup-2-1
```

优化

```java
/**
 * 上面那种做法 可能会出现 IndexOutOfBoundsException 异常
 * 应为在基于流的传输中比如你发送的数据
 * [ABC][DEF][GHI]
 * 应用中读取可能是
 * [AB][CDEFG][H][I]
 * 这种碎片化的问题,在netty中可以简单解决,在ChannelPipeline中添加ChannelHandler
 * 处理碎片化
 */
class TimeDecoder extends ByteToMessageDecoder {
    /**
     * 每当接受到新数据,调用改方法
     * @param ctx
     * @param in
     * @param out
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /*
        * 数据小于4的时候不处理数据
        * 
        * */
        if (in.readableBytes() < 4) {
            //当累积的数据不足时,可以决定不向out中添加数据
            return;
        }
        /*
        * 向out中添加数据时表示解码器成功解码一条数据,缓冲中的会被丢弃
        * */
        out.add(in.readBytes(4));
    }
}

```

修改Client

```java
    bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                //添加多个处理
                ch.pipeline().addLast(new TimeDecoder(),new TimeClientHandler());
            }
        });
```



前面都是使用字节来处理数据的,很不方面,下面使用POJO来处理,实现TIME协议

修改如下

1. 添加UnixTime类

```java
/**
 * POJO实现TIME协议
 *
 * @author parkstud@qq.com 2019-12-17
 */

public class UnixTime {
    private final long value;

    public UnixTime() {
        this(System.currentTimeMillis() / 1000L + 2208988800L);
    }
    public UnixTime(long value) {
        this.value = value;
    }
    public long value() {
        return value;
    }
    @Override
    public String toString() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli((value() - 2208988800L) * 1000L),
                OffsetDateTime.now().getOffset()).toString();
    }
}

```

2. 修改TimeDecoder

```java
 @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /*
        * 数据小于4的时候不处理数据
        *
        * */
        if (in.readableBytes() < 4) {
            //当累积的数据不足时,可以决定不向out中添加数据
            return;
        }
        /*
        * 向out中添加数据时表示解码器成功解码一条数据,缓冲中的会被丢弃
        * */
        out.add(new UnixTime(in.readUnsignedInt()));
    }
```

3. 修改TimeClientHandler

```java
  @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        UnixTime unixTime= (UnixTime) msg;
        log.info("接收消息:{}",unixTime);
        ctx.close();
    }
```

4. 添加编码器(有两种实现)

```java
/**
 * 编码器
 * 实现一
 */
class TimeEncoder1 extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        UnixTime unixTime = (UnixTime) msg;
        ByteBuf encode = ctx.alloc().buffer(4);
        encode.writeInt((int) unixTime.value());
        ctx.write(encode, promise);
    }
}


/**
 * 编码器
 * 实现二
 */
class TimeEncoder2 extends MessageToByteEncoder<UnixTime> {

    @Override
    protected void encode(ChannelHandlerContext ctx, UnixTime msg, ByteBuf out) throws Exception {
        out.writeInt((int) msg.value());
    }
}

```

5. 修改TimeServerHanler

```java
 @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelFuture future = ctx.writeAndFlush(new UnixTime());
        // 处理完关闭
        future.addListener(ChannelFutureListener.CLOSE);
    }
```

5. 修改Server

   ```java
     .childHandler(new ChannelInitializer<SocketChannel>() {
                           @Override
                           protected void initChannel(SocketChannel ch) {
                               ch.pipeline().addLast(new TimeEncoder1(), new TimeServerHandler());
                           }
                       })
   ```

   

### 编写计算阶乘服务器

#### 类图

![image.png](https://i.loli.net/2019/12/27/ZQPKCvNWoAOVyHe.png)

#### 实现

#### 服务端(Server)

**FactorialClient**

```java
/**
 * 客户端获取整数,计算阶层
 *
 * @author parkstud@qq.com 2019-12-18
 */
public class FactorialServer {
    public static final boolean SSL = System.getProperty("ssl") != null;
    public static final int PORT = Integer.parseInt(System.getProperty("port", "8322"));

    public static void main(String[] args) throws CertificateException, SSLException, InterruptedException {
        // 配置SSL
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new FactorialServerInitializer(sslCtx));
        ChannelFuture future = b.bind(PORT).sync();
        try {
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

/**
 * 服务端
 * 创建一个已配置的ChannelPipeline
 *
 * @author parkstud@qq.com 2019-12-19
 */
@AllArgsConstructor
public class FactorialServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        //添加压缩
        pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
        //添加数字解码器
        pipeline.addLast(new BigIntegerDecoder());
        pipeline.addLast(new NumberEncoder());
        //添加业务逻辑
        pipeline.addLast(new FactorialServerHandler());
    }
}

/**
 * 处理服务端
 * 该程序保持状态信息,使用成员变量确定channel,因此一个channel只能一个handler
 * 当你创建一个channel的时候,你必须创建一个handler 来避免竞争条件
 *
 * @author parkstud@qq.com 2019-12-21
 */
@Slf4j
public class FactorialServerHandler extends SimpleChannelInboundHandler<BigInteger> {
    private BigInteger lastMultiplier = new BigInteger("1");
    private BigInteger factorial = new BigInteger("1");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BigInteger msg) throws Exception {
        // 计算累积阶乘并且发送到客户端
        lastMultiplier = msg;
        factorial = factorial.multiply(msg);
        ctx.writeAndFlush(factorial);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("Factorial of {} is: {}", lastMultiplier, factorial);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("异常: ", cause);
        ctx.close();
    }
}

```

#### 客户端(Client)

```java
/**
 * @author parkstud@qq.com 2019-12-21
 */
@Slf4j
public class FactorialClient {
    private static final boolean SSL = System.getProperty("ssl") != null;
    public static final String HOST = System.getProperty("host", "127.0.0.1");
    public static final int PORT = Integer.parseInt(System.getProperty("port", "8322"));
    public static final int COUNT = Integer.parseInt(System.getProperty("count", "5"));

    public static void main(String[] args) throws SSLException, InterruptedException {
        // 配置SSL
        final SslContext sslContext;
        if (SSL) {
            sslContext = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslContext = null;
        }

        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new FactorialClientInitializer(sslContext));

            // 创建一个新连接
            ChannelFuture f = b.connect(HOST, PORT).sync();
            // get一个handler实例 来获取答案
            FactorialClientHandler handler = (FactorialClientHandler) f.channel().pipeline().last();
            // 输出答案
            log.debug("Factorial of {} is : {}", COUNT, handler.getFactorial());
        } finally {
            group.shutdownGracefully();
        }
    }
}

/**
 * @author parkstud@qq.com 2019-12-21
 */
public class FactorialClientInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;

    public FactorialClientInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), FactorialClient.HOST, FactorialClient.PORT));
        }

        // 压缩
        pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
        // 添加number编码解码器
        pipeline.addLast(new BigIntegerDecoder());
        pipeline.addLast(new NumberEncoder());
        //添加业务逻辑
        pipeline.addLast(new FactorialClientHandler());
    }
}

/**
 * 客户端处理程序
 * 该程序保持状态信息,使用成员变量确定channel,因此一个channel只能一个handler
 * 当你创建一个channel的时候,你必须创建一个handler 来避免竞争条件
 *
 * @author parkstud@qq.com 2019-12-21
 */
@Slf4j
public class FactorialClientHandler extends SimpleChannelInboundHandler<BigInteger> {

    private ChannelHandlerContext ctx;
    private int receiverMessages;
    private int next = 1;
    final BlockingQueue<BigInteger> answer = new LinkedBlockingQueue<>();

    public BigInteger getFactorial() {
        boolean interrupted = false;
        try {
            for (; ; ) {
                try {
                    return answer.take();
                } catch (InterruptedException e) {
                    log.error("answer take error", e);
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        sendNumbers();
    }

    private void sendNumbers() {
        // 不能send 超过4096个numbers
        ChannelFuture future = null;
        for (int i = 0; i < 4096 && next <= FactorialClient.COUNT; i++) {
            future = ctx.write(next);
            next++;
        }
        if (next <= FactorialClient.COUNT) {
            Assert.notNull(future, "client future is null!");
            future.addListener(numberSender);
        }
        ctx.flush();

    }

    private final ChannelFutureListener numberSender = future -> {
        if (future.isSuccess()) {
            sendNumbers();
        } else {
            log.error("futrue is not success", future.cause());
            future.channel().close();
        }
    };

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BigInteger msg) throws Exception {
        receiverMessages++;
        if (receiverMessages == FactorialClient.COUNT) {
            // 关闭连接提供答案
            ctx.channel().close().addListener((ChannelFutureListener) future -> {
                boolean offered = answer.offer(msg);
                Assert.isTrue(offered, "队列空间不足!");
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("客户端处理异常!", cause);
        ctx.close();
    }
}
```

#### 公共转换类

```java
/**
 * 解码BigInteger 前缀带有魔法数据('F' or 0x46)的二进制表示形式
 * 例如将{'F',0,0,0,1,42} 解码成 new BigInteger("42")
 *
 * @author parkstud@qq.com 2019-12-21
 */
public class BigIntegerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //当长度不够时等待
        if (in.readableBytes() < 5) {
            return;
        }
        in.markReaderIndex();
        //检查魔法数字
        short magicNumber = in.readUnsignedByte();
        if (magicNumber != 'F') {
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number: " + magicNumber);
        }
        // 当数据可获得时
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        //转化接受的数据为一个BigInteger
        byte[] decoded = new byte[dataLength];
        in.readBytes(decoded);
        out.add(new BigInteger(decoded));


    }
}



/**
 * 编码一个 Number类型的数据 为二进制表示前缀表示一个魔法数字
 * ('F' or 0x46) 和一个32位的前缀 ,例如 42 将编码为
 * {'F',0,0,0,1,42}
 *
 * @author parkstud@qq.com 2019-12-21
 */
@Slf4j
public class NumberEncoder extends MessageToByteEncoder<Number> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Number msg, ByteBuf out) throws Exception {
        // 转换为BigInteger
        BigInteger v;
        if (msg instanceof BigInteger) {
            v = (BigInteger) msg;
        } else {
            v = new BigInteger(String.valueOf(msg));
        }
        // 转换number 为字节数组
        byte[] data = v.toByteArray();
        int dataLength = data.length;
        //写消息 'F'开头 字节长度 数据
        out.writeByte((byte)'F');
        out.writeInt(dataLength);
        out.writeBytes(data);
    }
}
    
```

## Netty核心组件

通过上面的案例,我们大概了解到了Netty如何使用,领略了它的魅力,下面我们来看看Netty的核心组件.

netty的核心组件包括:

- Channel:
- 回调:
- Futrue:
- 事件、ChannelHandler和ChannelPipeline
- EventLoop接口
- ChannelFutrue

### Channel

Channel是java NIO中的一个类,但是Netty重新定义了这个类,并赋予它其他的炒作. Channel可以看做是传入或者传出数据的载体,因此可以被打开或者关闭.

在JAVA 的网络编程中 基本的构造是Socket,Netty的Channel所提供的API 降低了Socket使用的复杂性,并且实现特定的Channel例如:

- EmbededChannel
- LocalServerChannel
- NioDatagramChannel;
- NioSctpChannel;
- NioSocketChnnel;

**Channel类图**

![image.png](https://i.loli.net/2019/12/30/vHMPOLTuwf4e5cR.png)

每个`Channel`都会被分配一个`ChannelPipeline` 和`ChannelConfig`,ChannelConfig包含了该Channel的所有配置

**Channel主要方法**

| 方法          | 描述                                                         |
| ------------- | ------------------------------------------------------------ |
| eventLoop     | 返回分配给 Channel 的 EventLoop                              |
| pipeline      | 返回分配给 Channel 的 ChannelPipeline                        |
| isActive      | 如果 Channel 是活动的，则返回 true。活动的意义可能依赖于底层的传输。Socket传输一旦连接远程节点就是活动的,Datagram传输打开就是活动的. |
| localAddress  | 返回本地的 SokcetAddress                                     |
| remoteAddress | 返回远程的 SocketAddress                                     |
| write         | 将数据写到远程节点。这个数据将被传递给 ChannelPipeline，并且排队直到它被flash |
| flush         | 将之前已写的数据冲刷到底层传输，如一个 Socket                |
| writeAndFlush | 一个简便的方法，等同于调用 write()并接着调用 flush()         |

**channel的4种状态**

channelRegistered -------> ChannelActive -------> ChannelInactive -------> ChannelUnregistered

| 状态                | 描述                                                         |
| ------------------- | ------------------------------------------------------------ |
| ChannelUnregistered | Channel 已经创建但是未注册到EventLoop                        |
| ChannelRegistered   | Channel 已经被注册到EventLoop                                |
| ChannelActive       | Channel 处于活动状态（已经连接到它的远程节点）。它现在可以接收和发送数据了 |
| ChannelInactive     | Channel 没有连接到远程节点                                   |



### 回调

一个回调就是一个方法,一个指向已经提供给另外一个方法的方法引用.使得后者可以在适当的时候调用前者.

### Future

Future 提供了另一种在操作完成时通知应用程序的方式,这个对象可以看做一个异步的操作结果的占位符,表示某个将来时刻完成,并提供对其结果的访问.

JDK 提供的Future 只允许手动检查对应的操作是否完成,或者阻塞直到完成.Netty提供的实现ChannelFuture可以在执行异步操作的时候使用.它提供了额外的方法,能够让我们注册 多个ChannelFutureListener 监听器,监听器的回调方法operationCoplete() 会在操作完成的时候调用.

### 事件、ChannelHandler和ChannelPipeline

Netty使用不同的事件来通知状态的改变或者操作的状态,我们可以根据已经发生的事件触发动作,比如

- 记录日志
- 数据转化
- 流程控制
- 程序逻辑

入栈触发事件:

- 连接已被激活或者连接失活
- 数据读取
- 用户事件
- 错误事件

出站事件动作:

- 打开或者关闭远程连接
- 将数据写到套接字

![temp _1_.jpg](https://i.loli.net/2019/12/28/1iy5gCJStTkd4Fp.png)

**`ChannelHandler`** 充当了所有处理入站和出站数据的应用程序逻辑容器.它的主要作用如下

- 将数据从A ---> B
- 异常通知
- Channel 从活动变为非活动 通知
- Channel 注册/注销到EventLoop 的通知
- 用户自定义通知

**ChannelHandler 的生命周期**

| 类型            | 描述                                                  |
| --------------- | ----------------------------------------------------- |
| handlerAdded    | 当把 ChannelHandler 添加到 ChannelPipeline 中时被调用 |
| handlerRemoved  | 当从 ChannelPipeline 中移除 ChannelHandler 时被调用   |
| exceptionCaught | 当处理过程中在 ChannelPipeline 中有错误产生时被调用   |

**ChannelInboundHandler方法**: 入栈消息处理

| 类型                      | 描述                                                         |
| ------------------------- | ------------------------------------------------------------ |
| channelRegistered         | 当Channel 已经注册到它的 EventLoop 并且能够处理 I/O 时被调用 |
| channelUnregistered       | 当 Channel 从它的 EventLoop 注销并且无法处理任何 I/O 时被调用 |
| channelActive             | 当 Channel 处于活动状态时被调用；Channel 已经连接/绑定并且已经就绪 |
| channelInactive           | 当 Channel 离开活动状态并且不再连接它的远程节点时被调用      |
| channelReadComplete       | 当Channel上的一个读操作完成时被调用                          |
| channelRead               | 当从 Channel 读取数据时被调用                                |
| ChannelWritabilityChanged | 当 Channel 的可写状态发生改变时被调用。                      |

**ChannelOutBoundHandler接口** :出站消息处理

| 类型     | 描述                                    |
| -------- | --------------------------------------- |
| bind     | 当请求将 Channel 绑定到本地地址时被调用 |
| connect  | 当请求将 Channel 连接到远程节点时被调用 |
| discount | 当请求将 Channel 从远程节点断开时被调用 |
| close    | 当请求关闭 Channel 时被调用             |
| read     | 当请求从 Channel 读取更多的数据时被调用 |
| flush    | 当请求通过 Channel 将入队数据冲刷到远程 |
| write    | 当请求通过 Channel 将数据写到远程节点时 |

**ReferenceCountUtil.release(msg)** 丢弃处理的消息



**`ChannelPipeline`** 提供了ChannelHandler链的容器,并定于在链上传播入站和出站事件流的API.它是一种拦截过滤器设计模式

ChannelHandler 注册到ChannelPipeline的过程:

- 一个ChannelInitializer的实现注册到ServerBootstrap
- 当ChannelInitializer.initChannel()方法被调用,ChannelInitializer将ChannelPipeline中 注册一组自定义的ChannelHandler
- ChannelInitializer将它自己冲ChannelPipeline中移出

### EveentLoop

EventLoop 定义了Netty核心的抽象,用于处理连接的生命周期所发生的事件.

Channel,EventLop,Thread和EventLoopGroup的关系

![image.png](https://i.loli.net/2019/12/29/Q6FJ2eql7Zj49G1.png)

- 一个EventLoopGroup 包含多个EventLoop
- EventLoop 在它的生命周期只和一个Thread绑定
- EventLoop 处理的IO事件 都将被专有的Thread处理
- 一个Channel 在他的生命周期只能注册到一个EventLoop
- 一个Event 可以有多个Channel

### ChannelFutrue

Netty中所有的IO操作都是异步的,所以需要一个在某个时间点上确定结果的方法.ChannelFuture接口中可以注册addListener注册ChannelFutureListener , 当某个操作完成时 得到通知.

### 编码器和解码器

当使用Netty发送或者接收一个消息的时候,就会进行消息转换.入站消息被解码,出站消息被编码.

**在编码器和解码器中一旦消息被编码或者解码 就会被ReferenceCountUtil.release(message)调用**

解码器处理入站消息 实现了`ChannelInboundHandler`

- 将字节解码为消息----- `ByteToMessageDecoder` 和 `ReplayingDecoder`
- 将一种详细解码为另一种----`MessageToMessageDecoder`

**`ByteToMessageDecoder`**API 

| 方法       | 描述                                                         |
| ---------- | ------------------------------------------------------------ |
| decode     | 必须实现的抽象方法,它传入数据的ByteBuf和添加解码消息的List,方法会重复进直到没有数据,如果list中有数据会被传输到下一个ChannelHandler |
| decodeLast | 当Channel状态为非活动时这个方法被调用.                       |

案例

**`ReplayingDecoder`** 扩展ByteToMessageDecoder,用户不用调用readableBytes()方法.使用自定义ReplayingDecoderByteBuf

```java
/**
 * 实现ReplayingDecoder 泛型 Void表示不需要状态管理
 *
 * @author parkstud@qq.com 2020-01-03
 */
public class ToIntegerDecoder2 extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 传入的ByteBuf是ReplayingDecoderByteBuf
        // 从入站ByteBuf中读取一个int,并添加到解码消息List
        out.add(in.readInt());
    }
}

```

**`MessageToMessageDecoder`**

**在 io.netty.handler.codec 子包下有很多编码解码器的实现**

通常使用**ByteToMessageDecoder** 解码 MessageToByteEncoder编码

### 引导类Bootstrap

Bootstrap 方便为程序网络层配置容器.Netty有两种引导类 Bootstrap 用于客户端,ServerBootstrap用于服务器.区别如下

| 类别                 | Bootstrap          | ServerBootstrap |
| -------------------- | ------------------ | --------------- |
| 作用                 | 连接远程主机和端口 | 绑定本地端口    |
| EventLoopGroup的数量 | 1                  | 2               |

EventLoopGroup的数量 为什么 ServerBootstrap的是2, 应为 服务器具有两个group 一个是boss 用于分发连接, 一个是worker用于处理连接.如图:

![image.png](https://i.loli.net/2019/12/29/dLseilVwW7QYRX3.png)

### 传输

Netty内置了部分传输

| 名称     | 包                          | 描述                                                         |
| -------- | --------------------------- | ------------------------------------------------------------ |
| NIO      | io.netty.channel.socket.nio | 使用 java.nio.channels 包作为基础——基于选择器的方式          |
| Epoll    | io.netty.channel.epoll      | 由 JNI 驱动的 epoll()和非阻塞 IO。这个传输支持只有在Linux上可用的多种特性，如SO_REUSEPORT，比 NIO 传输更快，而且是完全非阻塞的. |
| OIO      | io.netty.channel.socket.oio | 使用 java.net 包作为基础——使用阻塞流                         |
| Local    | io.netty.channel.local      | 可以在 VM 内部通过管道进行通信的本地传输                     |
| Embedded | io.netty.channel.embedded   | Embedded 传输，允许使用 ChannelHandler 而又不需要一个真正的基于网络的传输.这在测试你的 |

**NIO传输**

![image.png](https://i.loli.net/2019/12/30/sPXq8euyDMx2NfT.png)

**OIO传输**

![image.png](https://i.loli.net/2019/12/30/pT5P9GBuIS7lCdD.png)



## Netty API

#### ChannelInboundHandler 消息处理

**服务器**

这个接口用来定义入站事件的方法,但是简单的应用程序只需要用到少量的方法 , 所以Netty提供了ChannelInboundHandlerAdapter类 主要方法如下:

- **chnannelRead()** 每个出入的消息都需要调用
- **channelReadComplete** 通知ChannelInboundHandler 读取当前批量数据完毕
- **exceptionCaught** 异常调用

**@Sharable** 注解表示一个ChannelHandler 可以不多个Channel安全的共享 

**客户端**

客户端也需要一个ChannelInboundHandler来处理数据,可以使用SImpleChannelInboundHandler主要需要重新方法

- **channelActive()** 在服务器的连接建立之后调用
- **channelRead0()** 当服务器接收一条消息被调用
- **exceptionCaught**() 异常调用

#### ByteBuf

网络传输总是使用的字节,JDK的ByteBuffer作为字节容器过于复杂,Netty使用ByteBuf代替更好用.

![image.png](https://i.loli.net/2019/12/30/b6pEUMzijGyH8ar.png)

| 方法                     | 描述                         |
| ------------------------ | ---------------------------- |
| discardReadBytes()       | 丢弃已经读取的字节           |
| readByte()               | 读取字节,readerIndex 移动    |
| getByte()                | 获取字节,readerIndex不动     |
| writeBytes(ByteBuf dest) | 写字节                       |
| markReaderIndex()        | 标记读指针                   |
| markWriterIndex()        | 标记写指针                   |
| resetWriterIndex()       | 重置写指针                   |
| resetReaderIndex()       | 重置读指针                   |
| clear()                  | 读写指针为0                  |
| process(byte value)      | 检查输入值是否为正在查找的值 |
| forEachByte()            | 查找                         |
| isReadable               | 如果有字节可读 则返回true    |
| isWritable               | 如果有字节可写 则返回true    |

**ByteBuf 使用**

```java
 ByteBuf buffer = Unpooled.buffer();
        if (buffer.hasArray()) {
            byte[] array = buffer.array();
            int offset=buffer.arrayOffset()+buffer.readerIndex();
            int length=buffer.readableBytes();
        }

```

复合缓存区`CompositeByteBuf`

```java
        CompositeByteBuf messageBuf = Unpooled.compositeBuffer();
        ByteBuf header = Unpooled.directBuffer();
        ByteBuf body = Unpooled.buffer();
        messageBuf.addComponents(header, body);
        messageBuf.removeComponent(0);
        messageBuf.forEach(x -> System.out.println(x.toString()));
        int len = messageBuf.readableBytes();
        byte[] array = new byte[len];
        CompositeByteBuf bytes = messageBuf.getBytes(messageBuf.readerIndex(), array);
```



**获取ByteBuffer的几种类**

- ByteBufHolder接口 
- ByteBufAllocator 接口,可以通过channel.alloc(), ctx.alloc() 获取
- Unpooled 工具类获取buffer