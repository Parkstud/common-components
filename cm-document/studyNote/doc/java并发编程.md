#  java并发编程

## 理论

### 进程

**进程是资源分配和调度的基本单位**

进程是操作系统中运行的一个任务(一个应用程序在一个进程中)，进程(process)是一块包含了某些资源的内存区域，操作系统利用进程把它的工作划分为一些功能单元。进程中包含的一个或多个执行单元称为线程。进程还有一个私有的虚拟内存空间，该空间仅能被它所包含的线程访问。线程只能归属一个进程并且它只能访问该进程所拥有的资源。当操作系统创建一个进程后，该进程会主动申请一个主线程。

**进程三种基本状态**

- **运行态(running)**

当进程得到处理机，其执行程序正在处理机上运行时的状态称为运行状态。

在单CPU系统中，任何时刻最多只有一个进程处于运行状态。在多CPU系统中，处于运行状态的进程数最多为处理机的数目。

进程唤醒动作

1. 从相应的等待队列中移出进程；
2. 修改进程PCB的有关信息，如进程状态改为就绪态，并移入就绪队列；
3. 若被唤醒进程比当前运行进程优先级高，重新设置调度标志。

**---> 等待  等待事件发生(如等待IO完成)**

**---> 就绪 时间片到,出现高优先级任务**

- **就绪状态(ready)**

当一个进程已经准备就绪，一旦得到CPU，就可立即运行，这时进程所处的状态称为就绪状态。系统中有一个就绪进程队列，处于就绪状态进程按某种调度策略存在于该队列中。

**---> 运行  处理机为空,调度程序调度**

- **等待态(阻塞态) (Wait.Blocked)**

若一个进程正等待着某一事件发生(如等待输入输出操作的完成)而暂时停止执行的状态称为等待状态。   处于等待状态的进程不具备运行的条件，即使给它CPU，也无法执行。系统中有几个等待进程队列（按等待的事件组成相应的等待队列）。

执行动作

1. 停止进程执行，保存现场信息到PCB
2. 修改进程PCB有关内容，如进程状态由运行态改为等待态等，并把修改状态后的进程移入相应事件的等待队列中
3. 转入进程调度程序去调度其他进程运行。

**---> 就绪 事件已经发生(IO完成)**

### 线程

一个线程是一个进程的顺序执行流。同类的多个线程共享一块内存空间和一组系统资源，线程本身有一个供程序执行时的堆栈。线程在切换时负荷小，因此，线程也被称为轻负荷进程，一个进程中可以包含多个线程。

**线程是处理调度和分配的基本单位,线程共享进程所拥有的主存空间和资源**

## java多线程

### java线程状态

![img](http://static.oschina.net/uploads/space/2013/0621/174442_0BNr_182175.jpg)

1. **新建(NEW)** 新创建了一个线程对象，还未调用start()方法。

2. **就绪(RUNNABLE)** 线程对象创建后，其他线程(比如main线程）调用了该对象的start()方法。

3. **阻塞(BLOCKED)**

   阻塞状态是指线程因为某种原因放弃了cpu 使用权，暂时停止运行。直到线程进入可运行(runnable)状态，才有机会再次获得cpu timeslice 转到运行(running)状态。阻塞的情况分两种：

   - 同步阻塞：运行(running)的线程进入了一个synchronized方法，若该同步锁被别的线程占用，则JVM会把该线程放入锁池(lock pool)中。
   - 其他阻塞：运行(running)的线程发出了I/O请求时，JVM会把该线程置为阻塞状态。当I/O处理完毕时，线程重新转入可运行(runnable)状态。

4. **等待(WAITING)**

   运行中（Running）的线程执行了以下方法：

   - Object的wait方法，并且没有使用timeout参数;
   - Thread的join方法，没有使用timeout参数；
   - LockSupport的park方法；
   - Conditon的await方法。

5. **限期等待(TIMED_WAITING)**

   也可以称作 TIMED_WAITING（有等待时间的等待状态）。

   线程主动调用以下方法：

   - Thread.sleep方法；
   - Object的wait方法，带有时间；
   - Thread.join方法，带有时间；
   - LockSupport的parkNanos方法，带有时间。

6. **终止(TERMINATED)**

   线程run()、main() 方法执行结束，或者因异常退出了run()方法，则该线程结束生命周期。

### 线程使用

三种使用线程的方法：

- 实现 `Runnable` 接口;
- 实现 `Callable` 接口
- 继承 `Thread` 类

实现 `Runnable` 和 `Callable` 接口的类只能当做一个可以在线程中运行的任务，不是真正意义上的线程，因此最后还需要通过 Thread 来调用。可以理解为任务是通过线程驱动从而执行的。

#### 实现接口 VS 继承Thread

- Java 不支持多重继承，因此继承了 Thread 类就无法继承其它类，但是可以实现多个接口；
- 类可能只要求可执行就行，继承整个 Thread 类开销过大。

#### Daemon

守护线程是程序运行时在后台提供服务的线程，不属于程序中不可或缺的部分。

当所有非守护线程结束时，程序也就终止，同时会杀死所有守护线程。

main() 属于非守护线程。

在线程启动之前使用 setDaemon() 方法可以将一个线程设置为守护线程。

#### sleep()

Thread.sleep(millisec) 方法会休眠当前正在执行的线程，millisec 单位为毫秒。

sleep() 可能会抛出 InterruptedException，因为异常不能跨线程传播回 main() 中，因此必须在本地进行处理。线程中抛出的其它异常也同样需要在本地进行处理

#### yield()

对静态方法 Thread.yield() 的调用声明了当前线程已经完成了生命周期中最重要的部分，可以切换给其它线程来执行。该方法只是对线程调度器的一个建议，而且也只是建议具有相同优先级的其它线程可以运行。

#### 中断

一个线程执行完毕之后会自动结束，如果在运行过程中发生异常也会提前结束。

##### InterruptedException

通过调用一个线程的 interrupt() 来中断该线程，如果该线程处于阻塞、限期等待或者无限期等待状态，那么就会抛出 InterruptedException，从而提前结束该线程。但是不能中断 I/O 阻塞和 synchronized 锁阻塞。

##### interrupted()

如果一个线程的 run() 方法执行一个无限循环，并且没有执行 sleep() 等会抛出 InterruptedException 的操作，那么调用线程的 interrupt() 方法就无法使线程提前结束。

#### synchronized 同步

```java
// 它只作用于同一个对象，如果调用两个对象上的同步代码块，就不会进行同步。
public void func() {
    synchronized (this) {
        // ...
    }
}
```

#### join()

在线程中调用另一个线程的 join() 方法，会将当前线程挂起，而不是忙等待，直到目标线程结束。

#### wait() notify() notifyAll()

调用 wait() 使得线程等待某个条件满足，线程在等待时会被挂起，当其他线程的运行使得这个条件满足时，其它线程会调用 notify() 或者 notifyAll() 来唤醒挂起的线程。

它们都属于 Object 的一部分，而不属于 Thread。

只能用在同步方法或者同步控制块中使用，否则会在运行时抛出 IllegalMonitorStateException。

使用 wait() 挂起期间，线程会释放锁。这是因为，如果没有释放锁，那么其它线程就无法进入对象的同步方法或者同步控制块中，那么就无法执行 notify() 或者 notifyAll() 来唤醒挂起的线程，造成死锁。

**wait() 和 sleep() 的区别**

- wait() 是 Object 的方法，而 sleep() 是 Thread 的静态方法；
- wait() 会释放锁，sleep() 不会。

![在这里插入图片描述](http://www.pianshen.com/images/671/19af9fc62b65c1bbcf8e7ce749f96567.JPEG)

### Java内存模型

Java 内存模型试图屏蔽各种硬件和操作系统的内存访问差异，以实现让 Java 程序在各种平台下都能达到一致的内存访问效果。

![img](https://camo.githubusercontent.com/fce232dcfc7411192b429ae86765aa9bef029427/68747470733a2f2f63732d6e6f7465732d313235363130393739362e636f732e61702d6775616e677a686f752e6d7971636c6f75642e636f6d2f31353835313535352d356162632d343937642d616433342d6566656431306634336136622e706e67)

所有的变量都存储在主内存中，每个线程还有自己的工作内存，工作内存存储在高速缓存或者寄存器中，保存了该线程使用的变量的主内存副本拷贝。

线程只能直接操作工作内存中的变量，不同线程之间的变量值传递需要通过主内存来完成。

#### 内存交互操作

Java 内存模型定义了 8 个操作来完成主内存和工作内存的交互操作。

![img](https://camo.githubusercontent.com/42696b8f0b2cfbf78024f9cd0d806e0e6decb5fe/68747470733a2f2f63732d6e6f7465732d313235363130393739362e636f732e61702d6775616e677a686f752e6d7971636c6f75642e636f6d2f38623765626261642d393630342d343337352d383465332d6634313230393964313730632e706e67)

- `read`: 把一个变量的值从主内存传输到工作内存中
- `load`: 在 read 之后执行，把 read 得到的值放入工作内存的变量副本中
- `use` : 把工作内存中一个变量的值传递给执行引擎
- `assign`: 把一个从执行引擎接收到的值赋给工作内存的变量
- `store`: 把工作内存的一个变量的值传送到主内存中
- `write`:在 store 之后执行，把 store 得到的值放入主内存的变量中
- `lock`,`unlock` 作用于主内存变量

#### 内存模型的三大特性

##### 原子性

Java 内存模型保证了 read、load、use、assign、store、write、lock 和 unlock 操作具有原子性，例如对一个 int 类型的变量执行 assign 赋值操作，这个操作就是原子性的。但是 Java 内存模型允许虚拟机将没有被 volatile 修饰的 64 位数据（long，double）的读写操作划分为两次 32 位的操作来进行，即 **load、store、read 和 write** 操作可以不具备原子性。

使用原子类, synchronized 和锁来保证操作的原子性。它对应的内存间交互操作为：lock 和 unlock，在虚拟机实现上对应的字节码指令为 monitorenter 和 monitorexit。

##### 可见性

可见性指当一个线程修改了共享变量的值，其它线程能够立即得知这个修改。Java 内存模型是通过在变量修改后将新值同步回主内存，在变量读取前从主内存刷新变量值来实现可见性的。

主要有三种实现可见性的方式：

- volatile
- synchronized，对一个变量执行 unlock 操作之前，必须把变量值同步回主内存。
- final，被 final 关键字修饰的字段在构造器中一旦初始化完成，并且没有发生 [this 逃逸](https://blog.csdn.net/zhushuai1221/article/details/51221552)（其它线程通过 this 引用访问到初始化了一半的对象），那么其它线程就能看见 final 字段的值。

##### 有序性

有序性是指：在本线程内观察，所有操作都是有序的。在一个线程观察另一个线程，所有操作都是无序的，无序是因为发生了指令重排序。在 Java 内存模型中，允许编译器和处理器对指令进行重排序，重排序过程不会影响到单线程程序的执行，却会影响到多线程并发执行的正确性。

volatile 关键字通过添加内存屏障的方式来禁止指令重排，即重排序时不能把后面的指令放到内存屏障之前。

也可以通过 synchronized 来保证有序性，它保证每个时刻只有一个线程执行同步代码，相当于是让线程顺序执行同步代码。

#### 先行发生原则(happens-before)

**先行发生**是Java内存模型中定义的两项操作之间的偏序关系，如果说操作A先行发生于操作B，就是说A产生的影响能被B观察到，”影响“包括修改了内存中的共享变量值、发送了消息、调用了方法等。

- **单一线程原则**:在一个线程内，在程序前面的操作先行发生于后面的操作。
- **管程锁定规则**:一个 unlock 操作先行发生于后面对同一个锁的 lock 操作。
- **volatile 变量规则** :对一个 volatile 变量的写操作先行发生于后面对这个变量的读操作。
- **线程启动规则**:Thread 对象的 start() 方法调用先行发生于此线程的每一个动作。
- **线程加入规则**:Thread 对象的结束先行发生于 join() 方法返回。
- **线程中断规则**:对线程 interrupt() 方法的调用先行发生于被中断线程的代码检测到中断事件的发生，可以通过 interrupted() 方法检测到是否有中断发生。
- **对象终结规则**:一个对象的初始化完成（构造函数执行结束）先行发生于它的 finalize() 方法的开始。
- **传递性**:如果操作 A 先行发生于操作 B，操作 B 先行发生于操作 C，那么操作 A 先行发生于操作 C。

### 线程安全

多个线程不管以何种方式访问某个类，并且在主调代码中不需要进行同步，都能表现正确的行为。

#### 不可变

不可变（Immutable）的对象一定是线程安全的，不需要再采取任何的线程安全保障措施。只要一个不可变的对象被正确地构建出来，永远也不会看到它在多个线程之中处于不一致的状态。多线程环境下，应当尽量使对象成为不可变，来满足线程安全。

不可变的类型:

- final 关键字修饰的基本数据类型
- String
- 枚举类型
- Number 部分子类，如 Long 和 Double 等数值包装类型，BigInteger 和 BigDecimal 等大数据类型。但同为 Number 的原子类 AtomicInteger 和 AtomicLong 则是可变的。

对于集合类型，可以使用 Collections.unmodifiableXXX() 方法来获取一个不可变的集合。

#### 互斥同步

synchronized 和 Lock。

#### 非阻塞同步

互斥同步最主要的问题就是线程阻塞和唤醒所带来的性能问题，因此这种同步也称为阻塞同步。

互斥同步属于一种悲观的并发策略，总是认为只要不去做正确的同步措施，那就肯定会出现问题。无论共享数据是否真的会出现竞争，它都要进行加锁（这里讨论的是概念模型，实际上虚拟机会优化掉很大一部分不必要的加锁）、用户态核心态转换、维护锁计数器和检查是否有被阻塞的线程需要唤醒等操作。

随着硬件指令集的发展，我们可以使用基于冲突检测的乐观并发策略：先进行操作，如果没有其它线程争用共享数据，那操作就成功了，否则采取补偿措施（不断地重试，直到成功为止）。这种乐观的并发策略的许多实现都不需要将线程阻塞，因此这种同步操作称为非阻塞同步。

##### CAS

乐观锁需要操作和冲突检测这两个步骤具备原子性，这里就不能再使用互斥同步来保证了，只能靠硬件来完成。硬件支持的原子性操作最典型的是：比较并交换（Compare-and-Swap，CAS）。CAS 指令需要有 3 个操作数，分别是内存地址 V、旧的预期值 A 和新值 B。当执行操作时，只有当 V 的值等于 A，才将 V 的值更新为 B。

##### Atomic系列类

J.U.C 包里面的整数原子类 Atomic的方法调用了 Unsafe 类的 CAS 操作。

##### ABA问题

如果一个变量初次读取的时候是 A 值，它的值被改成了 B，后来又被改回为 A，那 CAS 操作就会误认为它从来没有被改变过。

J.U.C 包提供了一个带有标记的原子引用类 AtomicStampedReference 来解决这个问题，它可以通过控制变量值的版本来保证 CAS 的正确性。大部分情况下 ABA 问题不会影响程序并发的正确性，如果需要解决 ABA 问题，改用传统的互斥同步可能会比原子类更高效。

#### 无同步方案

要保证线程安全，并不是一定就要进行同步。如果一个方法本来就不涉及共享数据，那它自然就无须任何同步措施去保证正确性。

##### 栈封闭

多个线程访问同一个方法的局部变量时，不会出现线程安全问题，因为局部变量存储在虚拟机栈中，属于线程私有的。

##### 线程本地存储（Thread Local Storage）

如果一段代码中所需要的数据必须与其他代码共享，那就看看这些共享数据的代码是否能保证在同一个线程中执行。如果能保证，我们就可以把共享数据的可见范围限制在同一个线程之内，这样，无须同步也能保证线程之间不出现数据争用的问题。

符合这种特点的应用并不少见，大部分使用消费队列的架构模式（如“生产者-消费者”模式）都会将产品的消费过程尽量在一个线程中消费完。其中最重要的一个应用实例就是经典 Web 交互模型中的“一个请求对应一个服务器线程”（Thread-per-Request）的处理方式，这种处理方式的广泛应用使得很多 Web 服务端应用都可以使用线程本地存储来解决线程安全问题。

https://blog.csdn.net/thinkwon/article/details/102508721

### JUC包

#### java.util.concurrent

定义了一些核心特征,用于其他方式实现同步和线程间通信,而非内置方式.定义的关键特性有:

- **同步器**:通过多线程间高级的交互的高级方法
- **执行器**:管理线程执行,顶层接口Executor,启动线程
- **并发集合**:包括ConcurrentHashMap,ConcurrentLinkeQueue和CopyOnWriteArrayList.
- **Fork/Join框架**:支持并行编程主要包括ForkJoinTask,ForkJoinPool,RecursiveTask,RecursiveAction

##### 同步器

通过多线程间高级的交互的高级方法

| 类             | 描述                               |
| -------------- | ---------------------------------- |
| Semaphore      | 经典信号量                         |
| CountDownLatch | 进行等待,直到指定数量的事件为止    |
| CyclicBarrier  | 使一组线程在预定义的执行点等待     |
| Exchanger      | 在两个线程之间交换数据             |
| Phaser         | 对向前通过多借点执行的线程进行同步 |

###### Semaphore类

信号量通过计数器控制对共享资源的访问,如果计数器大于0,允许访问,如果是0拒绝访问

构造函数 `Semaphore(int num,boolean how)` num控制线程访问数,how设置线程顺序访问

获取访问 `acquire()`

释放 `release()`

**案例**

```java
package concurrency;

import java.util.concurrent.Semaphore;

import lombok.extern.slf4j.Slf4j;

/**
 * 信号量通过计数器控制对共享资源的访问,如果计数器大于0,允许访问,如果是0拒绝访问
 *
 * @author parkstud@qq.com 2020-03-28
 */
@Slf4j
public class SemaphoreDemo {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(1);
        new IncThread("A", semaphore);
        new DecThread("B", semaphore);
    }
}

/**
 * 共享资源
 */
class Shared {
    static int count = 0;
}

/**
 * 线程一 增加值
 */
@Slf4j
class IncThread implements Runnable {
    String name;
    Semaphore semaphore;

    public IncThread(String name, Semaphore semaphore) {
        this.name = name;
        this.semaphore = semaphore;
        new Thread(this).start();
    }

    @Override
    public void run() {
        log.info("starting : {}", name);

        try {
            log.info("{} is waitting for a permit.", name);
            semaphore.acquire();
            log.info("{} get a permit.", name);

            for (int i = 0; i < 5; i++) {
                Shared.count++;
                log.info("{} : {}", name, Shared.count);
                Thread.sleep(10);
            }
            log.info("{} release the permit", name);
            semaphore.release();
        } catch (InterruptedException e) {
            log.error("IncThread 中断", e);
        }
    }
}

/**
 * 线程二
 * 减少值
 */
@Slf4j
class DecThread implements Runnable {
    String name;
    Semaphore semaphore;

    public DecThread(String name, Semaphore semaphore) {
        this.name = name;
        this.semaphore = semaphore;
        new Thread(this).start();
    }

    @Override
    public void run() {
        log.info("Starting {}", name);

        log.info("{} is waiting for a permit", name);
        try {
            semaphore.acquire();
            log.info("{} get a permit.", name);

            for (int i = 0; i < 5; i++) {
                Shared.count--;
                log.info("{} : {}", name, Shared.count);

                Thread.sleep(10);
            }

            log.info("{} releases the permit", name);
            semaphore.release();
        } catch (InterruptedException e) {
            log.error("DecThread 中断", e);
        }
    }
}

```

###### CountDownLatch类

线程等待,直到一个或多个事件为止.`CountDownLatch`在初始化创建时带有事件数量计数器,在释放锁之前,必须发生指定数量的事件.每发生一个事件,计数器递减,当计数器到0,释放锁

构造函数 CountDownLatch(int num) num指定打开锁存器,必须发生的事件数量

等待锁存器 await 方法,知道cout

递减与调用对象关联的计数器 countDown()

**案例**

```java
package concurrency;

import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;

/**
 * 锁存器
 *
 * @author parkstud@qq.com 2020-03-28
 */
@Slf4j
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        log.info("Starting.....");
        new MyThread(countDownLatch);
        countDownLatch.await();
        log.info("Done");

    }
}

@Slf4j
class MyThread implements Runnable {
    CountDownLatch latch;

    public MyThread(CountDownLatch latch) {
        this.latch = latch;
        new Thread(this).start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            log.info("i:{}", i);
            latch.countDown();
        }
    }
}

```

###### CyclicBarrier类

并发编程中,具有两个或多个线程的线程组需要在预定的执行点进行等待,直到线程组中所有的线程都达到执行点为止,为处理这种情况,API提供了CycliBarrier类,使用该类可以定义同步对象,当指定数量的线程没有到达界限点时,同步对象会被挂起.

**CyclicBarrier可以重用**每次在指定线程调用await()方法后,就会等待释放线程

构造函数 `CyclicBarrier(int numThreads,Runable action)`,numThreads指定了在继续执行之前必须到达界限点的线程数量,action制定了当到达界限点时需要执行的线程

**使用**

1. 创建`CyclicBarrier`对象,指定等待的线程数量
2. 每当线程到达界限点时,`CyclicBarrier`调用await方法,暂停线程的执行,直到所有其他线程调用await方法位置
3. 指定线程数量到达界限点,await() 方法返回并恢复执行,如果指定要执行的线程,就执行那个线程

```
package concurrency;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import lombok.extern.slf4j.Slf4j;

/**
 * 循环屏障
 *
 * @author parkstud@qq.com 2020-03-29
 */
@Slf4j
public class CyclicBarrierDemo {
    public static void main(String[] args) throws InterruptedException {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(3, new CyclicBarrierAction());
        log.info("Starting......");

        new MyThread(cyclicBarrier, "A");
        new MyThread(cyclicBarrier, "B");
        new MyThread(cyclicBarrier, "C");

        Thread.sleep(100);
        new MyThread(cyclicBarrier, "X");
        new MyThread(cyclicBarrier, "Y");
        new MyThread(cyclicBarrier, "Z");

    }

    static class MyThread implements Runnable {
        private CyclicBarrier cyclicBarrier;
        private String name;

        public MyThread(CyclicBarrier cyclicBarrier, String name) {
            this.cyclicBarrier = cyclicBarrier;
            this.name = name;
            new Thread(this).start();
        }

        @Override
        public void run() {
            log.info("name :{}", name);
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                log.error("MyThread error", e);
            }
        }
    }
}

/**
 * CyclicBarrier 完毕 执行对象
 */
@Slf4j
class CyclicBarrierAction implements Runnable {

    @Override
    public void run() {
        log.info("CyclicBarrierAction start....");
    }
}
```

###### Exchange类

简化两个线程之间数据交换,简单的进行等待,直到两个独立的线程调用exchange()方法

`exchange(V objRef)`方法,objRef是对要交换数据的引用,从另一个线程接受的数据返回.关键在于 直到同一个Exchanger 对象被两个独立的线程分别调用后,方法才成功返回

```java
package concurrency;

import java.util.concurrent.Exchanger;

import lombok.extern.slf4j.Slf4j;

/**
 * 交换数据
 *
 * @author parkstud@qq.com 2020-03-29
 */
public class ExchangeDemo {
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        new MakeString(exchanger);
        new UseString(exchanger);

    }
}

/**
 * 线程 制造数据
 */
@Slf4j
class MakeString implements Runnable {
    Exchanger<String> exchanger;
    String str="";

    public MakeString(Exchanger<String> exchanger) {
        this.exchanger = exchanger;
        new Thread(this).start();
    }

    @Override
    public void run() {
        char ch = 'A';
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                str += ch++;
            }

            try {
                str = exchanger.exchange(str);
            } catch (InterruptedException e) {
                log.error("MakeString ", e);
            }
        }
    }
}

/**
 * 使用数据
 */
@Slf4j
class UseString implements Runnable {
    Exchanger<String> exchanger;
    String str;

    public UseString(Exchanger<String> exchanger) {
        this.exchanger = exchanger;
        new Thread(this).start();
    }

    @Override
    public void run() {
        for (int i = 0; i < 3; i++) {
            try {
                str = exchanger.exchange("");
                log.info("Get :{}", str);
            } catch (InterruptedException e) {
                log.error("UseString ", e);
            }
        }
    }
}

```

###### Phaser类

允许表示一个或多个活动阶段的线程进行同步.该类支持多个阶段外,其工作方式与前面的CyclicBarrier类似.

因此,Phaser可定义等待特定阶段完成的同步对象,然后推进到下一个阶段,进行等待.

构造方法 

`Phaser()` 

`Phaser(int numParties)`

第一个表示part的数量为0,party表示使用Phaser注册的对象

`registrer()` 注册party,返回注册party的阶段编号

`arrive()` 通知party 已经完成了某个阶段 ,将Phaser推进到下一个阶段,该方法不会等待该阶段完成

`arriveAndAwaitAdvance` 等待,直到所有party到达

`getPhase()` 返回当前阶段的编号

Pharse对象创建时,第一阶段编号0 二阶段编号1.... 如果Phaser终止返回负数

**案例**

```java
package concurrency;

import java.util.concurrent.Phaser;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author parkstud@qq.com 2020-03-29
 */
@Slf4j
public class PhaserDemo {
    public static void main(String[] args) {
        // 1个主线程
        Phaser phaser = new Phaser(1);
        int curPhase;
        log.info("Starting.....");

        new MyThread(phaser,"A");
        new MyThread(phaser,"B");
        new MyThread(phaser,"C");

        // 等待线程完成第一阶段
        curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        log.info("Phase {} Complete",curPhase);

        // 等待线程完成第二阶段
        curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        log.info("Phase {} Complete",curPhase);

        curPhase = phaser.getPhase();
        phaser.arriveAndAwaitAdvance();
        log.info("Phase {} Complete",curPhase);

        //注销主线程
        phaser.arriveAndDeregister();

        if(phaser.isTerminated()){
            log.info("The phaser is terminated");
        }
    }

    static class MyThread implements Runnable {

        private Phaser phaser;
        private String name;

        public MyThread(Phaser phaser, String name) {
            this.phaser = phaser;
            this.name = name;
            phaser.register();
            new Thread(this).start();
        }

        @SneakyThrows
        @Override
        public void run() {
            log.info(" Thread {} Beginning Phase One",name);
            phaser.arriveAndAwaitAdvance();
            //暂停一下以防止输出混乱，这仅用于说明，Phaser的正确操作不需要
            Thread.sleep(10);
            log.info(" Thread {} Beginning Phase Two",name);
            phaser.arriveAndAwaitAdvance();

            //暂停一下以防止输出混乱，这仅用于说明，Phaser的正确操作不需要
            Thread.sleep(10);
            log.info(" Thread {} Beginning Phase Three",name);
            phaser.arriveAndAwaitAdvance();
        }
    }
}
```

##### 执行器

用于启动并控制线程的执行,替换Thread类管理线程

###### Executor 接口

核心方法 `void execute(Runable Thread)` 

由Thread指定的线程执行,因此execute方法可以启动指定的线程

###### ExecutorService接口

对Executor接口扩展,添加了用于帮助管理和控制线程执行的方法

例如 `void shutdown()` 停止

扩展了三个执行器类 ,`ThreadPoolExecutor` ,`ScheduledThreadPoolExecutro`和 `ForkJoinPool`

可以使用Exectors工具创建线程池

`static ExecutorService newCachedThreadPool()`

`static ExectorService newFixedThreadPool(int numThread)`

`static ScheduleExecutroService newSchedukedThreadPool(int numThreads)`

```java
package concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程池简单使用
 *
 * @author parkstud@qq.com 2020-03-29
 */
@Slf4j
public class SimpleExecutorDemo {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch a = new CountDownLatch(5);
        CountDownLatch b = new CountDownLatch(5);
        CountDownLatch c = new CountDownLatch(5);
        CountDownLatch d = new CountDownLatch(5);

        ExecutorService es = Executors.newFixedThreadPool(2);

        log.info("Starting.......");

        es.execute(new MyThread("A",a));
        es.execute(new MyThread("B",b));
        es.execute(new MyThread("C",c));
        es.execute(new MyThread("D",d));

        a.await();
        b.await();
        c.await();
        d.await();

        // 完成线程池中的任务后关闭线程池
        es.shutdown();
        log.info("Done......");


    }

    static class MyThread implements Runnable {
        private String name;
        private CountDownLatch countDownLatch;

        public MyThread(String name, CountDownLatch countDownLatch) {
            this.name = name;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                log.info("name {} : {}", name, i);
                countDownLatch.countDown();
            }
        }
    }
}

```

**自定义线程池**

```java
 /**
     * 获取默认线程池
     *
     * @return 线程池
     */
    public static ThreadPoolExecutor getDefaultThreadPoolExecutor() {
        return new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10240), new ThreadFactoryBuilder().setNameFormat("default-%d").build());
    }
```



###### Callable接口

该接口可以将计算结果,返回给调用线程.

主要方法

`V call() throws Exception` 在call()中定义希望执行的任务.在任务完成后返回结果,如果不能计算结果,call抛出异常

ExecutroService 对象调用submit方法执行Callable任务,

`<T> Future<T> submit(Callable<T> task)`

task中是要执行的Callable对象,结果通过Future类型对象得到返回

###### Future接口

`get()` 方法获取任务返回值

```java
package concurrency;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;

/**
 * 返回任务使用
 *
 * @author parkstud@qq.com 2020-03-29
 */
@Slf4j
public class CallAbleDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService es = Executors.newFixedThreadPool(3);
        Future<Integer> sum = es.submit(new Sum(10));
        log.info("sum:{}",sum.get());
        Future<Double> hypot = es.submit(new Hypot(3D, 4D));
        log.info("hypot:{}",hypot.get());
        Future<Integer> factorial = es.submit(new Factorial(5));
        log.info("factorial:{}",factorial.get());
        es.shutdown();
        log.info("Done.....");
    }


    /**
     * 计算阶乘
     */
    static class Factorial implements Callable<Integer> {
        private Integer stop;

        public Factorial(Integer stop) {
            this.stop = stop;
        }

        @Override
        public Integer call() throws Exception {
            int  fact=1;
            for (int i = 2; i <=stop; i++) {
                fact*=i;
            }
            return fact;
        }
    }

    /**
     * 计算斜长
     */
    static class Hypot implements Callable<Double> {
        private Double side1, side2;

        public Hypot(Double side1, Double side2) {
            this.side1 = side1;
            this.side2 = side2;
        }

        @Override
        public Double call() throws Exception {
            return Math.sqrt(side1 * side1 + side2 * side2);
        }
    }

    /**
     * 计算和
     */
    static class Sum implements Callable<Integer> {

        private Integer stop;

        public Sum(Integer stop) {
            this.stop = stop;
        }

        @Override
        public Integer call() throws Exception {
            int sum = 0;
            for (int i = 1; i <= stop; i++) {
                sum += i;
            }
            return sum;
        }
    }
}

```

##### 并发集合

###### ConcurrentLinkedDeque(非阻塞)

ConcurrentLinkedDeque可以在并发环境中直接使用，所谓的非阻塞，就是当列表为空的时候，我们还继续从列表中取数据的话，它会直接返回null或者抛出异常。下面列出来一些常用的方法

`peekFirst()`、`peekLast()` ：返回列表中首位跟末尾元素，如果列表为空则返回null。返回的元素不从列表中删除。

`getFirst()`、`getLast()` ：返回列表中首位跟末尾元素，如果列表为空则抛出`NoSuchElementExceotion`异常。返回的元素不从列表中删除。

`removeFirst()`、`removeLast()` ：返回列表中首位跟末尾元素，如果列表为空则抛出`NoSuchElementExceotion`异常。【返回的元素会从列表中删除】。


###### LinkedBlockingDeque(有界队列)

LinkedBlockingDeque是一个阻塞式的线程安全列表，它跟 ConcurrentLinkedDeque最大的区别就是，当列表中元素满了或者为空的时候，我们对该列表的操作不会立即返回，而是阻塞当前操作，直到该操作可以执行时才返回。常用于 “工作窃取算法”

`put()` ：插入元素至列表中，当表中元素已满的时候，该操作将会被阻塞，直到表中存在空余空间。

`take()` : 从列表中获取元素，当列表为空，该操作会被阻塞，直到列表不为空。

`peekFirst()`、`peekLast()` ：返回列表中首位跟末尾元素，如果列表为空则返回null。返回的元素不从列表中删除。

`getFirst()`、`getLast()` ：返回列表中首位跟末尾元素，如果列表为空则抛出`NoSuchElementExceotion`异常。返回的元素不从列表中删除。

`addFirst()`、`addLast()` ：将元素添加至首位跟末尾，如果列表已满，则会抛出`IllegalStateException

###### PriorityBlockingQueue()

在`PriorityBlockingQueue`中，存放进去的元素必须要实现Comparable接口。

###### ArrayBlockingQueue(有界队列)

阻塞队列（BlockingQueue）是一个支持两个附加操作的队列。这两个附加的操作是：在队列为空时，获取元素的线程会等待队列变为非空。当队列满时，存储元素的线程会等待队列可用。阻塞队列常用于生产者和消费者的场景，生产者是往队列里添加元素的线程，消费者是从队列里拿元素的线程。阻塞队列就是生产者存放元素的容器，而消费者也只从容器里拿元素。

| 方法 | 抛出异常 | 返回特殊值(null) | 阻塞   | 超时退出           |
| ---- | -------- | ---------------- | ------ | ------------------ |
| 插入 | add(e)   | offer(e)         | put(e) | offer(e,time,unit) |
| 移出 | remove() | poll()           | take() | poll(time,unit)    |
| 检查 | element  | peek()           |        |                    |

###### SynchronousQueue(有界队列)

内部容量为零，适用于元素数量少的场景，尤其特别适合做交换数据用，内部使用 队列来实现公平性的调度，使用栈来实现非公平的调度.

###### DelayQueue

DelayQueue 里面存放着带有日期的元素，当我们从列表获取数据的时候，未到时间的元素将会被忽略。

https://juejin.im/post/5ce143e2e51d4510b64670ae

###### ConcurrentHashMap

ConcurrentHashMap使用了一个table来存储Node，ConcurrentHashMap同样使用记录的key的hashCode来寻找记录的存储index，而处理哈希冲突的方式与HashMap也是类似的，冲突的记录将被存储在同一个位置上，形成一条链表，当链表的长度大于8的时候会将链表转化为一棵红黑树，从而将查找的复杂度从O(N)降到了O(lgN)。

https://www.jianshu.com/p/cf5e024d9432


###### LinkedTransferQueue

是一个由链表结构组成的无界阻塞TransferQueue队列。相对于其他阻塞队列，LinkedTransferQueue多了tryTransfer和transfer方法。

LinkedTransferQueue采用一种预占模式。意思就是消费者线程取元素时，如果队列不为空，则直接取走数据，若队列为空，那就生成一个节点（节点元素为null）入队，然后消费者线程被等待在这个节点上，后面生产者线程入队时发现有一个元素为null的节点，生产者线程就不入队了，直接就将元素填充到该节点，并唤醒该节点等待的线程，被唤醒的消费者线程取走元素，从调用的方法返回。我们称这种节点操作为“匹配”方式。

内部很多方法都指向`sfer`方法

- 参数1 如果是 `put`类型就是实际值,反之就是`null`
- 参数2 是否包含数据 `put`类型是`true` ,`take`就是`fasle`
- 参数3 执行类型 有数据立即返回`NOW`,异步`ASYNC` 阻塞`SYNC` 超时的`TIMED`
- 参数4 只有`TIMED`才有作用

方法逻辑:

找到 `head` 节点,如果 `head` 节点是匹配的操作,就直接赋值,如果不是,添加到队列中

注意：队列中永远只有一种类型的操作,要么是 `put` 类型, 要么是 `take` 类型.

![img](https://upload-images.jianshu.io/upload_images/4236553-d60a5b0368dd8d8c.png?imageMogr2/auto-orient/strip|imageView2/2/w/1200/format/webp)

`LinkedTransferQueue`是 `SynchronousQueue` 和 `LinkedBlockingQueue` 的合体，性能比 `LinkedBlockingQueue` 更高（没有锁操作），比 `SynchronousQueue`能存储更多的元素。

当 `put` 时，如果有等待的线程，就直接将元素 “交给” 等待者， 否则直接进入队列。

`put`和 `transfer` 方法的区别是，put 是立即返回的， transfer 是阻塞等待消费者拿到数据才返回。`transfer`方法和 `SynchronousQueue`的 put 方法类似。

###### CopyOnWriteArrayList

CopyOnWriteArrayList，是一个写入时复制的容器，它是如何工作的呢？简单来说，就是平时查询的时候，都不需要加锁，随便访问，只有在写入/删除的时候，才会从原来的数据复制一个副本出来，然后修改这个副本，最后把原数据替换成当前的副本。修改操作的同时，读操作不会被阻塞，而是继续读取旧的数据。

##### Fork/Join框架

Fork/Join框架通过两种方式增强多线程编程,首先Fork/Join简化多线程的创建和使用,其次,Fork/Join框架自动使用dip处理器.

**分治策略**

将任务递归化分更小的子任务,子任务足够小来处理.分治策略可以并行发生.

**核心类**

- `ForkJoinTask<V>` 用来定义任务的抽象类
- `ForkJoinPool` 管理`ForkJoinTask`的执行
- `RecursiveAction`: ForkJoinTask<V>的子类,用于不返回值的任务
- `RecursiveTask` :ForkJoinTask<V>的子类,用于返回值的任务

###### `ForkJoinTask`

**核心方法**

- `fork()` 为调用任务的异步执行提交调用任务,自动使用ForkJoinPool线程池
- `V join()` 等待调用该方法的任务终止,任务结果返回
- `V inovke()` 将并行的fork和连接的join操作合并到一个调用中,并等待任务结束

###### `RecursiveAction`

封装不返回结果的任务.

**核心方法**

compute() 代表任务计算部分

###### `RecursiveTask`

封装返回结果的任务.

**核心方法**

V compute() 任务计算部分

```java
package concurrency;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import lombok.extern.slf4j.Slf4j;

/**
 * Fork/Join 框架案例
 *
 * @author parkstud@qq.com 2020-03-31
 */
@Slf4j
public class ForkJoinDemo {

    public static void main(String[] args) {
        ForkJoinPool fjp = new ForkJoinPool();
        double[] nums = new double[100000];
        for (int i = 0; i < nums.length; i++) {
            nums[i]=i;
        }

        log.info("A portion of the original sequence:");
        for (int i = 0; i < 100; i++) {
            log.info(nums[i]+"  ");
        }

        SqrtTransForm task = new SqrtTransForm(nums, 0, nums.length);
        fjp.invoke(task);
        for (int i = 0; i < 100; i++) {
            log.info(nums[i]+"  ");
        }

    }
}

class SqrtTransForm extends RecursiveAction {

    final int seqThreadHold = 1000;
    double[] data;
    int start, end;

    public SqrtTransForm(double[] data, int start, int end) {
        this.data = data;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute() {
        if (end - start < seqThreadHold) {
            for (int i = start; i <end ; i++) {
                data[i]=Math.sqrt(data[i]);
            }
        }else {
            int middle=(start+end)/2;
            invokeAll(new SqrtTransForm(data,start,middle),new SqrtTransForm(data,middle,end));
        }
    }
}

```



#### java.util.concurrent.atomic

简化开发环境中变量使用,提供了一种高效变量值的方法,不需要使用锁.主要方法

compareAndSet() decrementAndGet() 和 getAndSet()

- **基本类型**
  - `AtomicBoolean` 布尔类型原子类
  - `AtomicInteger` 整型原子类
  - `AtomicLong`   长整型原子类
- **引用类型**
  - `AtomicReference`  引用类型原子类
  - `AtomicMarkableReference` 带有标记位的引用类型原子类
  - `AtomicStampedReference` 带有版本号的引用类型原子类
- **数组类型**
  - `AtomicIntegerArray`  整形数组原子类
  - `AtomicLongArray` 长整型数组原子类
  - `AtomicReferenceArray` 引用类型数组原子类
- **属性更新器类型**
  - `AtomicIntegerFieldUpdater` 整型字段的原子更新
  - `AtomicLongFieldUpdater` 长整型字段的原子更新器
  - `AtomicReferenceFieldUpdater`  原子更新引用类型里的字段

#### java.util.concurrent.locks

为同步方法提供的代替方案,核心是Lock接口改接口定义访问对象和放弃对象的基本机制.关键方法lock,tryLock,unlock()

##### Lock接口

| 方法                      | 描述                                       |
| ------------------------- | ------------------------------------------ |
| void lock()               | 进行等待,知道可以获得调用锁为止            |
| void lockInterruptibley() | 除非被中断,否则可以进行等待,知道获得调用锁 |
| Condition newCoundition() | 返回调用锁关联的Condition对象              |
| boolenn tryLock()         | 尝试获得锁,如果锁不可得 返回false          |
| void unlock()             | 释放锁                                     |

###### ReentrantLock类 

可重入锁,当前线程可以重复获得锁,获得多少次就需要释放多少次

案例

```java
package concurrency;

import org.cm.boot.starter.util.ConcurrencyUtil;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 可重入锁案例
 *
 * @author parkstud@qq.com 2020-03-29
 */
@Slf4j
public class ReentrantLockDemo {
    public static void main(String[] args) {

        ThreadPoolExecutor poolExecutor = ConcurrencyUtil.getDefaultThreadPoolExecutor();
        Lock lock=new ReentrantLock();
        poolExecutor.execute(new LockThread("A", lock));
        poolExecutor.execute(new LockThread("B", lock));
        poolExecutor.execute(new LockThread("C", lock));
        poolExecutor.execute(new LockThread("D", lock));
        poolExecutor.shutdown();
    }

    static class Shared {
        static int count = 0;
    }

    static class LockThread implements Runnable {

        private String name;
        private Lock lock;

        public LockThread(String name, Lock lock) {
            this.name = name;
            this.lock = lock;
        }

        @SneakyThrows
        @Override
        public void run() {
            log.info("Starting...{}",name);
            log.info("{} is waiting to lock count.",name);
            lock.lock();

            try {
                log.info("{} is locking count.",name);
                Shared.count++;
                log.info("{} : {}",name,Shared.count);

                log.info("{} Thread is sleeping.",name);
                Thread.sleep(1000);

            }finally {
                // !!! 必须在finally中释放锁
                log.info("{} is unlocking count.",name);
                lock.unlock();
            }
        }
    }
}

```

###### Condition类

Condition接口在使用前必须先调用ReentrantLock的lock()方法获得锁。之后调用Condition接口的await()将释放锁,并且在该Condition上等待,直到有其他线程调用Condition的signal()方法唤醒线程。使用方式和wait,notify类似。

Condition类中的await()方法使当前线程处于wait状态，相当于Object类中的wait()方法，当前线程在执行await()函数后会立刻释放对象锁。Condition类中的await(long)方法也相当于Object类中的wait(long)方法，当前线程在执行await(long)函数后会立刻释放对象锁。await()和await(long)只能在线程获得对象锁之后才能调用，否则会报java.lang.IllegalMonitorStateException错误。

Condition类中的signal()和signalAll()方法分别相当于Object类中的notify()和notifyAll()方法，signal()和signalAll()方法也会唤醒同类线程，但是可以使用不同类型的线程使用不同的condition来达到唤醒特定类型线程的目的。在多生产者和多消费者模式中注意使用signalAll()方法而不要使用signal()方法，避免出现线程假死的情况。

```java
package concurrency;

import org.cm.boot.starter.util.ConcurrenceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 多消费者生产者案例
 *
 * @author parkstud@qq.com 2020-03-29
 */
public class ProviderAndConsumerDemo<T> {
    /**
     * 锁
     */
    public static final Lock LOCK = new ReentrantLock();
    /**
     * 队列为null 需要通知消费这生产
     */
    public static final Condition EMPTY = LOCK.newCondition();
    /**
     * 队列满,通知消费者生产
     */
    public static final Condition FULL = LOCK.newCondition();

    /**
     * 队列最大的大小
     */
    public static final int SIZE = 100;

    /**
     * 生产和消费队列
     */
    private List<T> queue = new ArrayList<>();

    public static void main(String[] args) {
        ThreadPoolExecutor poolExecutor = ConcurrenceUtil.getDefaultThreadPoolExecutor();
        ProviderAndConsumerDemo<DateInfo> demo = new ProviderAndConsumerDemo<>();
        poolExecutor.execute(new Provider<>("Provider-A",demo.queue));
        poolExecutor.execute(new Provider<>("Provider-B",demo.queue));
        poolExecutor.execute(new Provider<>("Provider-C",demo.queue));

        poolExecutor.execute(new Consumer<>("Consumer-D",demo.queue));
        poolExecutor.execute(new Consumer<>("Consumer-E",demo.queue));
        poolExecutor.execute(new Consumer<>("Consumer-F",demo.queue));

        poolExecutor.shutdown();

    }

}

/**
 * 数据信息 , 每个数据有一个编号
 */
@Data
class DateInfo {
    private Integer num;

    public DateInfo(Integer num) {
        this.num = num;
    }
}

@Slf4j
class Provider<T> implements Runnable {
    private static AtomicInteger count = new AtomicInteger();
    private String name;
    private List<T> queue;

    public Provider(String name, List<T> queue) {
        this.name = name;
        this.queue = queue;
    }

    @SneakyThrows
    @Override
    public void run() {
        ProviderAndConsumerDemo.LOCK.lock();
        try {
            log.info("{} is providing", name);
            while (queue.size() >= ProviderAndConsumerDemo.SIZE) {
                ProviderAndConsumerDemo.FULL.await();
            }
            DateInfo dateInfo = new DateInfo(count.addAndGet(1));
            queue.add((T) dateInfo);
            ProviderAndConsumerDemo.EMPTY.signalAll();
        } finally {
            ProviderAndConsumerDemo.LOCK.unlock();
        }

    }
}

@Slf4j
class Consumer<T> implements Runnable {
    private String name;
    private List<T> queue;

    public Consumer(String name, List<T> queue) {
        this.name = name;
        this.queue = queue;
    }

    @SneakyThrows
    @Override
    public void run() {
        ProviderAndConsumerDemo.LOCK.lock();
        try {
            log.info("{} is consumer...", name);
            while (queue.isEmpty()) {
                ProviderAndConsumerDemo.EMPTY.await();
            }
            DateInfo info = (DateInfo) queue.remove(0);
            System.out.println(info);
            ProviderAndConsumerDemo.FULL.signalAll();
        } finally {
            ProviderAndConsumerDemo.LOCK.unlock();
        }

    }
}
```

![引自并发编程的艺术](http://www.pianshen.com/images/940/02db5c00e1869925200eeb5fb8b52cbc.JPEG)

https://blog.csdn.net/qq_38293564/article/details/80554516

###### 读写锁(ReentranReadWriteLock)

**ReadWriteLock同Lock一样也是一个接口，提供了readLock和writeLock两种锁的操作机制，一个是只读的锁，一个是写锁。**

读锁可以在没有写锁的时候被多个线程同时持有，写锁是独占的(排他的)。 每次只能有一个写线程，但是可以有多个线程并发地读数据。

所有读写锁的实现必须确保写操作对读操作的内存影响。换句话说，一个获得了读锁的线程必须能看到前一个释放的写锁所更新的内容

写锁提供了Condition实现，`ReentrantLock.newCondition`;读锁不支持Condition。

```java
package concurrency;

import org.cm.boot.starter.util.ConcurrenceUtil;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.extern.slf4j.Slf4j;

/**
 * 读写锁案例
 *
 * @author parkstud@qq.com 2020-03-30
 */
@Slf4j
public class ReadWriteLockDemo {
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private int value = 0;

    public void read() {
        Lock lock = this.lock.readLock();
        lock.lock();
        try {
            log.info("read value:{}", value);
        } finally {
            lock.unlock();
        }
    }

    public void write() {
        Lock lock = this.lock.writeLock();
        lock.lock();
        try {
            log.info("write :{}", ++value);
        } finally {
            lock.unlock();
        }
    }

    static class ReadThread implements Runnable {
        ReadWriteLockDemo readWriteLockDemo;

        public ReadThread(ReadWriteLockDemo readWriteLockDemo) {
            this.readWriteLockDemo = readWriteLockDemo;
        }

        @Override
        public void run() {
            readWriteLockDemo.read();
        }
    }

    static class WriteThread implements Runnable {
        ReadWriteLockDemo readWriteLockDemo;

        public WriteThread(ReadWriteLockDemo readWriteLockDemo) {
            this.readWriteLockDemo = readWriteLockDemo;
        }

        @Override
        public void run() {
            readWriteLockDemo.write();
        }
    }

    public static void main(String[] args) {
        ThreadPoolExecutor pool = ConcurrenceUtil.getDefaultThreadPoolExecutor();
        ReadWriteLockDemo readWriteLockDemo = new ReadWriteLockDemo();
        for (int i = 0; i < 10; i++) {
            pool.execute(new ReadWriteLockDemo.ReadThread(readWriteLockDemo));
            pool.execute(new ReadWriteLockDemo.ReadThread(readWriteLockDemo));
            pool.execute(new ReadWriteLockDemo.ReadThread(readWriteLockDemo));
            pool.execute(new ReadWriteLockDemo.WriteThread(readWriteLockDemo));
        }
        pool.shutdown();
    }
}

```

###### StampedLock类

StampedLock类，在JDK1.8时引入，是对读写锁ReentrantReadWriteLock的增强，该类提供了一些功能，优化了读锁、写锁的访问，同时使读写锁之间可以互相转换，更细粒度控制并发。(解决读写锁饥饿问题)

`StampedLock`和`ReadWriteLock`相比，改进之处在于：读的过程中也允许获取写锁后写入！这样一来，我们读的数据就可能不一致，所以，需要一点额外的代码来判断读的过程中是否有写入，这种读锁是一种乐观锁。

`StampedLock`是不可重入锁，不能在一个线程中反复获取同一个锁。

```java
package concurrency;

import org.cm.boot.starter.util.BaseConcurrenceUtil;
import org.cm.boot.starter.util.BaseSystemUtil;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.StampedLock;

import lombok.extern.slf4j.Slf4j;

/**
 * StampedLock案例
 *
 * @author parkstud@qq.com 2020-03-31
 */
@Slf4j
public class StampedLockDemo {
    private final StampedLock stampedLock = new StampedLock();
    private double x;
    private double y;

    public void move(double moveX, double moveY) {
        long stamp = stampedLock.writeLock();
        try {
            x += moveX;
            y += moveY;
            log.info("X:{}  Y:{}", x, y);
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }

    public double distance() {
        long stamp = stampedLock.tryOptimisticRead();
        double currentX = x;
        double currentY = y;
        if (!stampedLock.validate(stamp)) {
            // 悲观读锁
            stamp = stampedLock.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }
        log.info("currentX:{}  currentY:{}", currentX, currentY);
        return Math.sqrt(currentX * currentX + currentY + currentY);
    }

    static class MoveTask implements Runnable {
        private StampedLockDemo demo;

        public MoveTask(StampedLockDemo demo) {
            this.demo = demo;
        }

        @Override
        public void run() {
            demo.move(1, 1);
        }
    }

    static class DistanceTask implements Runnable {
        private StampedLockDemo demo;

        public DistanceTask(StampedLockDemo demo) {
            this.demo = demo;
        }

        @Override
        public void run() {
            demo.distance();
        }
    }

    public static void main(String[] args) {
        long time = BaseSystemUtil.computeRuntime(StampedLockDemo::test1);
        log.info("time:{}", time);
    }

    private static ThreadPoolExecutor test1() {
        StampedLockDemo demo = new StampedLockDemo();
        ThreadPoolExecutor pool = BaseConcurrenceUtil.getDefaultThreadPoolExecutor();
        for (int i = 0; i < 100; i++) {
            pool.execute(new MoveTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
            pool.execute(new DistanceTask(demo));
        }
        pool.shutdown();
        return pool;
    }
}
```

#### [CompletableFuture类](https://www.jianshu.com/p/b3c4dd85901e)

并发辅助类,CompletableFuture 提供了四个静态方法来创建一个异步操作。

https://www.jianshu.com/p/6bac52527ca4

```java
public static CompletableFuture<Void> runAsync(Runnable runnable)
public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor)
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)
public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor)
```

没有指定Executor的方法会使用ForkJoinPool.commonPool() 作为它的线程池执行异步代码。如果指定线程池，则使用指定的线程池运行。

- `runAsync`方法不支持返回值.
- `supplyAsync`可以支持返回值.

```java
 /**
     * 无返回值
     */
    public static void runAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            sleepOneSeconds();
            log.info("{} run end.....", Thread.currentThread().getName());
        });
        // 不调用,不执行
        future.get();
    }

    /**
     * 有返回值
     *
     * @throws ExecutionException   e
     * @throws InterruptedException e
     */
    public static void supplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
            sleepOneSeconds();
            log.info("{} run end.....", Thread.currentThread().getName());
            return System.currentTimeMillis();
        });

        Long time = future.get();
        log.info("time:{}", time);
    }

    private static void sleepOneSeconds() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error("sleepOneSeconds InterruptedException", e);
        }
    }
```

**计算结果完成/异常时的回调方法**

```java
public CompletableFuture<T> whenComplete(BiConsumer<? super T,? super Throwable> action)
public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T,? super Throwable> action)
public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T,? super Throwable> action, Executor executor)
public CompletableFuture<T> exceptionally(Function<Throwable,? extends T> fn)
```

- `whenComplete`：是执行当前任务的线程执行继续执行 whenComplete 的任务。
- `whenCompleteAsync`：是执行把 whenCompleteAsync 这个任务继续提交给线程池来进行执行。

```
   public static void whenComplete() throws InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            sleepOneSeconds();
            if (new Random().nextInt() % 2 >= 0) {
                int i = 12 / 0;
            }
            log.info("{} run end.....", Thread.currentThread().getName());
        });

        future.whenComplete((aVoid, throwable) -> log.info("执行完成!"));

        future.exceptionally(throwable -> {
            log.info("执行失败", throwable);
            return null;
        });
        //主线程等待
        TimeUnit.SECONDS.sleep(2);
    }
```

**thenApply方法**

当一个线程依赖另一个线程时，可以使用 thenApply 方法来把这两个线程串行化。

```java
public <U> CompletableFuture<U> thenApply(Function<? super T,? extends U> fn)
public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn)
public <U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor)
```

`Function<? super T,? extends U>`
`T`：上一个任务返回结果的类型
`U`：当前任务的返回值类型

```java
 public static void thenApply(){
        String ok = CompletableFuture.supplyAsync(() -> {
            long result = new Random().nextInt(100);
            log.info("result:{}", result);
            return result;
        }).thenApply(o -> {
            long res =  5/0;
            log.info("res : {}", res);
            return res + "";
        }).whenComplete((s, throwable) -> log.info("receive s:{}", s)).exceptionally(e -> {
            log.error("执行失败:e", e);
            return null;
        }).join();
    }
```

**handle方法**

handle 是执行任务完成时对结果的处理。
 handle 方法和 thenApply 方法处理方式基本一样。不同的是 handle 是在任务完成后再执行，还可以处理异常的任务。thenApply 只可以执行正常的任务，任务出现异常则不执行 thenApply 方法。

```java
public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn);
public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn);
public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn,Executor executor);
```

在 handle 中可以根据任务是否有异常来进行做相应的后续处理操作。而 thenApply 方法，如果上个任务出现错误，则不会执行 thenApply 方法。

```java
  /**
     * handle方法即可以处理异常也可以处理正常结果
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void handle() throws ExecutionException, InterruptedException {
        Integer a = CompletableFuture.supplyAsync(() -> {
            int i = 10 / new Random().nextInt(10);
            return new Random().nextInt(10);
        }).handle((param, throwable) -> {
            int result = -1;
            if (throwable == null) {
                result = param * 2;
            } else {
                log.info("handle 出现异常", throwable);
            }
            return result;
        }).get();
        log.info("result:{}", a);
    }
```

**thenAccept/thenRun 消费处理结果**

`thenAccept`接收任务的处理结果，并消费处理，无返回结果。

`thenRun`  不接受结果,只要上面的任务执行完成，就开始执行 thenAccept 。

```java
public CompletionStage<Void> thenAccept(Consumer<? super T> action);
public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action);
public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action,Executor executor);
```

```java
public static void thenAccept() throws Exception{
    CompletableFuture<Void> future = CompletableFuture.supplyAsync(new Supplier<Integer>() {
        @Override
        public Integer get() {
            return new Random().nextInt(10);
        }
    }).thenAccept(integer -> {
        System.out.println(integer);
    });
    future.get();
}
```

**thenCombine /thenAcceptBoth/ applyToEither 合并任务**

`thenCombine` 会把 两个 CompletionStage 的任务都执行完成后，把两个任务的结果一块交给 thenCombine 来处理

`thenAcceptBoth` 当两个CompletionStage都执行完成后，把结果一块交给thenAcceptBoth来进行消耗

`applyToEither` 两个CompletionStage，谁执行返回的结果快，我就用那个CompletionStage的结果进行下一步的转化操作。

```java
public <U,V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn);
public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn);
public <U,V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends U> other,BiFunction<? super T,? super U,? extends V> fn,Executor executor);
```

```java
private static void thenCombine() throws Exception {
    CompletableFuture<String> future1 = CompletableFuture.supplyAsync(new Supplier<String>() {
        @Override
        public String get() {
            return "hello";
        }
    });
    CompletableFuture<String> future2 = CompletableFuture.supplyAsync(new Supplier<String>() {
        @Override
        public String get() {
            return "hello";
        }
    });
    CompletableFuture<String> result = future1.thenCombine(future2, new BiFunction<String, String, String>() {
        @Override
        public String apply(String t, String u) {
            return t+" "+u;
        }
    });
    System.out.println(result.get());
}	
```

```java
public <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other,BiConsumer<? super T, ? super U> action);
public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,BiConsumer<? super T, ? super U> action);
public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,BiConsumer<? super T, ? super U> action,     Executor executor);
```

```java
public <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> other,Function<? super T, U> fn);
public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other,Function<? super T, U> fn);
public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other,Function<? super T, U> fn,Executor executor);
```

##### 辅助方法allOf和anyOf

`allOf`方法是当所有的`CompletableFuture`都执行完后执行计算。

`anyOf`方法是当任意一个`CompletableFuture`执行完后就会执行计算，计算的结果相同。

**案例**

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



