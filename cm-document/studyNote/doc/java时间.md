---
author: chen miao
time : 2019/11/09
email:  parkstud@qq.com
---
# java时间

这两周太多关于时间的计算,这里做个记录.在Java 8以前，日期和时间处理都不太好处理，首先是`java.util`和`java.sql`中，都包含`Date`类，如果要进行时间格式化，还需要`java.text.DateFormat`类处理。同时`java.util.Date`中既包含了日期，又包含了时间。java8新增了LocalDate和LocalTime类来处理日期和时间。

## LocalDate
`LocalDate`是一个不可变的日期时间对象，表示日期，通常被视为年月日。 也可以访问其他日期字段，例如日期，星期几。
`Temporal`对时间对象的读写访问的框架级接口，例如日期，时间，偏移或这些的一些组合。时间有关的接口.
### 常用方法
LocalDateTime [atStartOfDay](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#atStartOfDay--)() 获取当前日期的00点00分00苗 例如


```java
public class Demo1 {
    public static final DateTimeFormatter FORMATTER = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        // 返回值是一个LocalDateTime
        LocalDateTime localDateTime = now.atStartOfDay();
        System.out.println(FORMATTER.format(localDateTime));
    }
}

--------结果值---------
2019-11-03 11:11:11

```


LocalDateTime  [atTime](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#atTime-int-int-int-)(int hour, int minute, int second) 可以通设置时,分,秒 得到一个`LocalDateTime`,该方法有多个重载方法.
参数限制
`hour` - 从0到23的使用时间

`minute` - 从0到59使用的小时

`second` - 从0到59的秒表示

```java
public class Demo1 {

    public static final DateTimeFormatter FORMATTER =
     DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        LocalDateTime localDateTime = now.atTime(11, 11, 11);
        System.out.println(FORMATTER.format(localDateTime));
    }
}

---------结果值---------
2019-11-03 11:11:11

```

int [compareTo](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#compareTo-java.time.chrono.ChronoLocalDate-)(ChronoLocalDate other) 与其他时间比较,如果相等为0 大于返回正数,小于返回负数

```java
public class Demo1 {
    public static final DateTimeFormatter FORMATTER =
     DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) {
        LocalDate time1 = LocalDate.parse("2019-10-08");
        LocalDate time1_1 = LocalDate.parse("2019-10-08");
        LocalDate time2 = LocalDate.parse("2019-10-09");
        LocalDate time3= LocalDate.parse("2019-10-07");
        System.out.println(time1.compareTo(time1_1));
        System.out.println(time1.compareTo(time2));
        System.out.println(time1.compareTo(time3));
    }
}

----------结果值-----------
0
-1
1
```

String [format](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#format-java.time.format.DateTimeFormatter-)(DateTimeFormatter formatter) 使用格式化器,格式化此时间

```java
public class Demo1 {
    public static final DateTimeFormatter FORMATTER_DATE_TIME = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATE =
     DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_TIME = 
    DateTimeFormatter.ofPattern("HH:mm:ss");
    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        String format = now.format(FORMATTER_DATE);
        System.out.println(format);
    }
}

------------结果值--------
2019-11-05
```
<span style="color:red">注意</span>
LocalDate 只能格式到日期
LocalTime 只能格式时间
LocalDateTime 可以格式时间个日期

 int [getDayOfMonth](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#getDayOfMonth--)() 获取月份中的日期

 int |  [getMonthValue](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#getMonthValue--)() 虎丘月份字段从1到12

int [getYear](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#getYear--)() 获取年字段

```java
 */
public class Demo1 {
    public static final DateTimeFormatter FORMATTER_DATE_TIME = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATE =
     DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        String format = now.format(FORMATTER_DATE);
        System.out.println(format);
        System.out.println("获取年:"+now.getYear());
        System.out.println("获取月份:"+now.getMonthValue());
        System.out.println("获取月中的第几日:"+now.getDayOfMonth());

    }
}

-------------结果值---------------
2019-11-05
获取年:2019
获取月份:11
获取月中的第几日:5
```

LocalDate [minusDays](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#minusDays-long-)(long daysToSubtract) 减去指定的日期

LocalDate  [minusMonths](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#minusMonths-long-)(long monthsToSubtract)  减去指定的月

LocalDate [minusWeeks](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#minusWeeks-long-)(long weeksToSubtract) 减去指定的周

LocalDate [minusYears](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#minusYears-long-)(long yearsToSubtract) 减去指定的年

 LocalDate  [plusDays](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#plusDays-long-) (long daysToAdd) 添加指定的天
...其他同理

LocalDate [withDayOfMonth](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalDate.html#withDayOfMonth-int-)(int dayOfMonth) 设置指定月份的日期
...其他同理

```java
public class Demo1 {
    public static final DateTimeFormatter FORMATTER_DATE_TIME = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATE =
     DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_TIME = 
    DateTimeFormatter.ofPattern("HH:mm:ss");
    public static void main(String[] args) {
        LocalDate now = LocalDate.now();
        String format = now.format(FORMATTER_DATE);
        System.out.println("当前日期:"+format);
        // 日期计算
        // 减法
        System.out.println();
        System.out.println("当前日期减3天:"+now.minusDays(3));
        System.out.println("当前日期减3周:"+now.minusWeeks(3));
        System.out.println("当前日期减3月:"+now.minusMonths(3));
        System.out.println("当前日期减3年:"+now.minusYears(3));
        // 加法
        System.out.println();
        System.out.println("当前日期加3天:"+now.plusDays(3));
        System.out.println("当前日期加3周:"+now.plusWeeks(3));
        System.out.println("当前日期加3月:"+now.plusMonths(3));
        System.out.println("当前日期加3年:"+now.plusYears(3));

        // 设置
        System.out.println();
        System.out.println("设置为当前月的第10天:"+now.withDayOfMonth(10));
        System.out.println("设置当前月为8月:"+now.withMonth(8));
        System.out.println("设置当前年为2018年:"+now.withYear(2018));
    }
}

--------------结果值-----------
当前日期:2019-11-05

当前日期减3天:2019-11-02
当前日期减3周:2019-10-15
当前日期减3月:2019-08-05
当前日期减3年:2016-11-05

当前日期加3天:2019-11-08
当前日期加3周:2019-11-26
当前日期加3月:2020-02-05
当前日期加3年:2022-11-05

设置为当前月的第10天:2019-11-10
设置当前月为8月:2019-08-05
设置当前年为2018年:2018-11-05

```

## LocalTime

`LocalTime`是一个不可变的日期时间对象，代表一个时间，通常被用于小时 - 秒.
![2]($resource/2.jpg)

### 常用方法
获取时间的方法
int [getHour](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#getHour--)() 获取小时字段
int  [getMinute](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#getMinute--)() 获取分钟字段
int [getSecond](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#getSecond--)() 获取秒

```java
public class Demo1 {
    public static final DateTimeFormatter FORMATTER_DATE_TIME = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATE = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_TIME = 
    DateTimeFormatter.ofPattern("HH:mm:ss");
    public static void main(String[] args) {
        LocalTime now =LocalTime.now();
        String format = now.format(FORMATTER_TIME);
        System.out.println("当前时间:"+format);
        System.out.println("获取小时:"+now.getHour());
        System.out.println("获取分:"+now.getMinute());
        System.out.println("获取秒:"+now.getSecond());

    }
}
----------------结果-------------
当前时间:10:53:32
获取小时:10
获取分:53
获取秒:32
```
比较时间的方法
[isAfter](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#isAfter-java.time.LocalTime-)(LocalTime other) other是否在指定时间之后
[isBefore](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#isBefore-java.time.LocalTime-)(LocalTime other) other是否在指定时间之前

```java
public class Demo1 {
    public static final DateTimeFormatter FORMATTER_DATE_TIME = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATE = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_TIME = 
    DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        LocalTime time1 = LocalTime.now();
        LocalTime time2 = LocalTime.parse("10:20:30", FORMATTER_TIME);
        System.out.println("time1:"+time1.format(FORMATTER_TIME));
        System.out.println("time2:"+time2.format(FORMATTER_TIME));
        System.out.println("是否在time1之前:" + time1.isBefore(time2));
        System.out.println("是否在time2之前:" + time1.isAfter(time2));
    }
}
------------------结果值----------------------------
time1:06:36:49
time2:10:20:30
是否在time1之前:true
是否在time2之前:false
```
时间加减法

LocalTime [minus](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#minus-long-java.time.temporal.TemporalUnit-)(long amountToSubstract,Temporalunit unit) 减去指定的时间 unit时间单位
LocalTime [minusHours](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#minusHours-long-)(long hoursToSubtract) 减去小时
LocalTime  [minusMinutes](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#minusMinutes-long-)(long minutesToSubtract) 减去分钟
LocalTime  [minusSeconds](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#minusSeconds-long-)(long secondsToSubtract) 减去秒

```java
public class Demo1 {
    public static final DateTimeFormatter FORMATTER_DATE_TIME = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATE = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_TIME = 
    DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        LocalTime time1 = LocalTime.now();
        LocalTime time2 = LocalTime.parse("10:20:30", FORMATTER_TIME);

        System.out.println("time1减去两秒  "+time1.minusSeconds(2));
        // 另一种方法
        System.out.println("time1减去两秒  "+time1.minus(2, ChronoUnit.SECONDS));
        System.out.println("time1减去2分  "+time1.minusMinutes(2));
        System.out.println("time1减去2小时  "+time1.minusHours(2));

        System.out.println();
        System.out.println("time1加2小时  "+time1.plusHours(2));
        System.out.println("time1加2分  "+time1.plusMinutes(2));
        System.out.println("time1加2秒  "+time1.plusSeconds(2));

    }
}
------------- 结果-------------------------
time1减去两秒  06:42:06.362
time1减去两秒  06:42:06.362
time1减去2分  06:40:08.362
time1减去2小时  04:42:08.362

time1加2小时  08:42:08.362
time1加2分  06:44:08.362
time1加2秒  06:42:10.362

```

设置时间
static LocalTime [of](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#of-int-int-)(int hour, int minute, int second, int nanoOfSecond) 从小时 分钟 秒中获取时间
LocalTime  [withHour](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#withHour-int-)(int hour) 设置小时
LocalTime  [withMinute](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#withMinute-int-)(int minute) 设置分钟
LocalTime  [withSecond](http://www.matools.com/file/manual/jdk_api_1.8_google/java/time/LocalTime.html#withSecond-int-)(int second) 设置秒
```java
public class Demo1 {
    public static final DateTimeFormatter FORMATTER_DATE_TIME = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATE =
     DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_TIME = 
    DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        LocalTime time3 = LocalTime.of(10, 10, 11);
        System.out.println(time3.format(FORMATTER_TIME));
        System.out.println();
        System.out.println("设置小时为下午1点："+time3.withHour(13));
        System.out.println("设置分钟为15分:"+time3.withMinute(15));
        System.out.println("设置秒为16秒:"+time3.withMinute(15));

    }
}
---------------------结果值-------------------------------
10:10:11

设置小时为下午1点：13:10:11
设置分钟为15分:10:15:11
设置秒为16秒:10:15:11

```

## LocalDateTime 这个类是包含年月日时分秒,并且方法包含LocalDate和LocalTime.
转化方法

```java
public class Demo1 {
    public static final DateTimeFormatter FORMATTER_DATE_TIME =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATE =
     DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_TIME = 
    DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now.format(FORMATTER_DATE_TIME));

        System.out.println("LocalDateTime 转 LocalDate: 
        "+now.toLocalDate().format(FORMATTER_DATE));
        System.out.println("LocalDateTime 转 LocalTime: 
        "+now.toLocalTime().format(FORMATTER_TIME));
        System.out.println("LocalDate 转 LocalDateTime:"+ 
        LocalDate.now().atStartOfDay());
        System.out.println("LocalTime 转 LocalDateTime:"+ 
        LocalTime.now().atDate(LocalDate.now()));
        
    }
}
-----------结果值-------------------------
2019-11-06 07:02:51
LocalDateTime 转 LocalDate: 2019-11-06
LocalDateTime 转 LocalTime: 07:02:51
LocalDate 转 LocalDateTime:2019-11-06T00:00
LocalTime 转 LocalDateTime:2019-11-06T07:02:51.327

```

## Instant 时间线上的时间戳
Instant获取的时间都是带偏移量的时间,方法和上面基本相同.
LcoalDate,LocalDatTime,LocalTime 转 Date 所需的中间量

```java
public class Demo1 {
    public static final DateTimeFormatter FORMATTER_DATE_TIME = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATE =
     DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_TIME = 
    DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        // 获取instant
        Instant now = Instant.now();
        Instant from = Instant.from(ZonedDateTime.now());
        Instant parse = Instant.parse("2017-02-03T10:37:30.00Z");
        System.out.println(now);
        System.out.println(from);
        System.out.println(parse);
    }
}
-------------------结果-----------------
2019-11-05T23:09:42.962Z
2019-11-05T23:09:42.970Z
2017-02-03T10:37:30Z

```
## Duration 时间范围

```java
public class Demo1 {
    public static final DateTimeFormatter FORMATTER_DATE_TIME = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATE =
     DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_TIME = 
    DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        // 获取instant
        LocalDateTime dateTime = LocalDateTime.of(2018, 11, 12, 11, 11, 11);
        LocalDateTime dateTime1 = LocalDateTime.of(2018, 11, 12, 11, 11, 30);
        Duration between = Duration.between(dateTime, dateTime1);
        // 间隔多少秒
        System.out.println(between.getSeconds());
        System.out.println(between.get(ChronoUnit.SECONDS));
    }
}
------结果值-----
19
19
```
## 其他与Date相关转化

```java
public class Demo1 {
    public static final DateTimeFormatter FORMATTER_DATE_TIME = 
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATE =
     DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_TIME =
     DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void main(String[] args) {
        //Date与Instant的相互转化
        Instant instant  = Instant.now();
        Date date = Date.from(instant);
        Instant instant2 = date.toInstant();

        //Date转为LocalDateTime
        Date date2 = new Date();
        LocalDateTime localDateTime2 = LocalDateTime.ofInstant(date2.toInstant(),
        ZoneId.systemDefault());

        //LocalDateTime转Date
        LocalDateTime localDateTime3 = LocalDateTime.now();
        Instant instant3 = localDateTime3.atZone(ZoneId.systemDefault()).toInstant();
        Date date3 = Date.from(instant);

        //LocalDate转Date
        //因为LocalDate不包含时间，所以转Date时，会默认转为当天的起始时间，00:00:00
        LocalDate localDate4 = LocalDate.now();
        Instant instant4 =localDate4.atStartOfDay()
        .atZone(ZoneId.systemDefault()).toInstant();
        Date date4 = Date.from(instant);
    }
}

```



### 工作总结

1. 获取默认的ZoneOffset

```java
  ZoneOffset offset = OffsetDateTime.now().getOffset();
```

