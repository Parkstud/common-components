#  java并发编程



## 常用工具

### java.util.concurrent

定义了一些核心特征,用于其他方式实现同步和线程间通信,而非内置方式.定义的关键特性有:

- **同步器**:通过多线程间高级的交互的高级方法
- **执行器**:管理线程执行,顶层接口Executor,启动线程
- **并发集合**:包括ConcurrentHashMap,ConcurrentLinkeQueue和CopyOnWriteArrayList.
- **Fork/Join框**架:支持并行编程主要包括ForkJoinTask,ForkJoinPool,RecursiveTask,RecursiveAction

#### 同步器

通过多线程间高级的交互的高级方法

| 类             | 描述                               |
| -------------- | ---------------------------------- |
| Semaphore      | 经典信号量                         |
| CountDownLatch | 进行等待,直到指定数量的事件为止    |
| CyclicBarrier  | 使一组线程在预定义的执行点等待     |
| Exchanger      | 在两个线程之间交换数据             |
| Phaser         | 对向前通过多借点执行的线程进行同步 |

`Semaphore`

信号量听过计数器控制对共享资源的访问,如果计数器大于0,允许访问,如果是0拒绝访问





### java.util.concurrent.atomic

简化开发环境中变量使用,提供了一种高效变量值的方法,不需要使用锁.主要方法

compareAndSet() decrementAndGet() 和 getAndSet()

### java.util.concurrent.locks

为同步方法提供的代替方案,核心是Lock接口改接口定义访问对象和放弃对象的基本机制.关键方法lock,tryLock,unlock()

## 应用

### 线程池

- 线程池创建

```java
    /**
             * 手动创建线程池 不要使用Executors
             * 参数
             * - corePoolSize:核心线程数
             * - maximumPoolSize: 最大线程数
             * - keepAliveTime: 非核心线程空闲存活时间
             * - TimeUnit 时间单位
             * - BlockingQueue 队列 (需要声明一个有界队列)
             * - ThreadFactory 线程工厂 (推荐) 使用guava的ThreadFactoryBuilder
             *
             */
            ThreadPoolExecutor pool = new ThreadPoolExecutor(10,
                    20,
                    60,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(1000),
```

### 并发编程辅助类

- **CompletableFuture**

```java

// 案例一
class Machina {
    public enum State {
        START, ONE, TWO, THREE, END;

        State step() {
            if (equals(END))
                return END;
            return values()[ordinal() + 1];
        }
    }

    private State state = State.START;
    private final int id;

    public Machina(int id) {
        this.id = id;
    }

    public static Machina work(Machina m) {
        if (!m.state.equals(State.END)) {
            m.state = m.state.step();
        }
        System.out.println(m);
        return m;
    }

    @Override
    public String toString() {
        return "Machina" + id + ": " + (state.equals(State.END) ? "complete" : state);
    }

    public static void main(String[] args) {
        CompletableFuture<Machina> cf =
                CompletableFuture.completedFuture(
                        new Machina(0));
        try {
            Machina m = cf.get();  // Doesn't block
        } catch (InterruptedException |
                ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}

// 案例二
class Batter {
    static class Eggs {
    }

    static class Milk {
    }

    static class Sugar {
    }

    static class Flour {
    }

    static <T> T prepare(T ingredient) {
        return ingredient;
    }

    static <T> CompletableFuture<T> prep(T ingredient) {
        return CompletableFuture
                .completedFuture(ingredient)
                .thenApplyAsync(Batter::prepare);
    }

    public static CompletableFuture<Batter> mix() {
        CompletableFuture<Eggs> eggs = prep(new Eggs());
        CompletableFuture<Milk> milk = prep(new Milk());
        CompletableFuture<Sugar> sugar = prep(new Sugar());
        CompletableFuture<Flour> flour = prep(new Flour());
        CompletableFuture.allOf(eggs, milk, sugar, flour).join();
        return CompletableFuture.completedFuture(new Batter());
    }
}

class Baked {
    static class Pan {
    }

    static Pan pan(Batter b) {
        return new Pan();
    }

    static Baked heat(Pan p) {
        return new Baked();
    }

    static CompletableFuture<Baked> bake(CompletableFuture<Batter> cfb) {
        return cfb.thenApplyAsync(Baked::pan)
                .thenApplyAsync(Baked::heat);
    }

    public static Stream<CompletableFuture<Baked>> batch() {
        CompletableFuture<Batter> batter = Batter.mix();
        return Stream.of(bake(batter), bake(batter), bake(batter), bake(batter));
    }
}

class Frosting {
    private Frosting() {
    }

    static CompletableFuture<Frosting> make() {
        return CompletableFuture.completedFuture(new Frosting());
    }
}

class FrostedCake {
    public FrostedCake(Baked baked, Frosting frosting) {
        new Nap(0.1);
    }

    @Override
    public String toString() {
        return "FrostedCake";
    }

    public static void main(String[] args) {
        Baked.batch()
                .forEach(baked -> baked.thenCombineAsync(Frosting.make(), (cake, frosting) -> new FrostedCake(cake, frosting))
                        .thenAcceptAsync(System.out::println)
                        .join());
    }
}
```

