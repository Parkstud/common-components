---
author: 陈苗
time: 2019/12/25
email: parkstud@qq.com
---

## 数据库sql

- 查看mysql执行语句,分析问题(cpu高)

  `show full processlist`


## 索引

是存储引擎快速找到记录的一种数据结构

### 理论

#### 设计原则

- 适合索引的列是出现在where子句中的列，或者连接子句中指定的列；

- 基数较小的类，索引效果较差，没有必要在此列建立索引；

- 使用短索引，如果对长字符串列进行索引，应该指定一个前缀长度，这样能够节省大量索引空间；

- 不要过度索引。索引需要额外的磁盘空间，并降低写操作的性能。在修改表内容的时候，索引会进行更新甚至重构，索引列越多，这个时间就会越长。所以只保持需要的索引有利于查询即可。

- 如果MySQL估计使用索引比全表扫描还慢，则不会使用索引。
  返回数据的比例是重要的指标，比例越低越容易命中索引。记住这个范围值——30%，后面所讲的内容都是建立在返回数据的比例在30%以内的基础上。

- 前导模糊查询不能命中索引。`like '%xxx'`

- 数据类型出现隐式转换的时候不会命中索引，特别是当列类型是字符串，一定要将字符常量值用引号引起来。例如 `EXPLAIN SELECT * FROM user WHERE name=1;`

- 复合索引的情况下，查询条件不包含索引列最左边部分（不满足**`最左原则`**），不会命中符合索引。

  ```sql
  name,age,status列创建复合索引：
  ALTER TABLE user ADD INDEX index_name (name,age,status);
  ```

  根据最左原则，可以命中复合索引index_name：
  `EXPLAIN SELECT * FROM user WHERE name='swj' AND status=1;`

  **最左原则并不是说是查询条件的顺序**,**而是查询条件中是否包含索引最左列字段**

- `union`、`in`、`or`都能够命中索引，建议使用`in`。查询的CPU消耗：or>in>union。

- **用or分割开的条件，如果or前的条件中列有索引，而后面的列中没有索引，那么涉及到的索引都不会被用到**。因为or后面的条件列中没有索引，那么后面的查询肯定要走全表扫描，在存在全表扫描的情况下，就没有必要多一次索引扫描增加IO访问。

- 负向条件查询不能使用索引，可以优化为in查询。
  负向条件有：`!=`、`<>`、`not in`、`not exists`、`not like`等。

- 范围条件查询可以命中索引。范围条件有：<、<=、>、>=、between等,范围列可以用到索引（联合索引必须是最左前缀），但是范围列后面的列无法用到索引，索引最多用于一个范围列，如果查询条件中有两个范围列则**无法全用到索引**,如果是范围查询和等值查询同时存在，**优先匹配等值查询列的索引**

- 数据库执行计算不会命中索引。

- 利用覆盖索引进行查询，避免回表。

- 建立索引的列，不允许为null。单列索引不存null值，复合索引不存全为null的值，如果列允许为null，可能会得到“不符合预期”的结果集，所以，请使用not null约束以及默认值。

#### 概念

##### 基数

单个列唯一键（distict_keys）的数量叫做基数。

##### 回表

普通索引查询条件,select获取非索引字段

### 索引分析

####  查看索引详情

`SHOW INDEX FROM table_name` 

#### 查看索引的使用情况

`SHOW STATUS LIKE 'Handler_read%';`

- **Handler_read_key**：如果索引正在工作，Handler_read_key的值将很高。
- **Handler_read_rnd_next**：数据文件中读取下一行的请求数，如果正在进行大量的表扫描，值将较高，则说明索引利用不理想。

#### 分析sql
`EXPLAIN` + sql语句

查看

- `id` 选择标识符

- `select_type` 表示查询的类型。

- `table`:输出结果集的表

- `partitions`匹配的分区

- **`type`** 表示表的连接类型(访问类型)

  常用类型有： **ALL、index、range、 ref、eq_ref、const、system、NULL **（从左到右，性能从差到好）

  **ALL**：Full Table Scan， MySQL将遍历全表以找到匹配的行

  **index**: Full Index Scan，index与ALL区别为index类型只遍历索引树

  **range**:只检索给定范围的行，使用一个索引来选择行

  **ref**: 表示上述表的连接匹配条件，即哪些列或常量被用于查找索引列上的值

  **eq_ref**: 类似ref，区别就在使用的索引是唯一索引，对于每个索引键值，表中只有一条记录匹配，简单来说，就是多表连接中使用primary key或者 unique key作为关联条件

  **const、system**: 当MySQL对查询某部分进行优化，并转换为一个常量时，使用这些类型访问。如将主键置于where列表中，MySQL就能将该查询转换为一个常量，system是const类型的特例，当查询的表只有一行的情况下，使用system

  **NULL**: MySQL在优化过程中分解语句，执行时甚至不用访问表或索引，例如从一个索引列里选取最小值可以通过单独索引查找完成。

- `possible_keys`表示查询时，可能使用的索引

- **`key`**表示实际使用的索引

- `key_len`索引字段的长度

- `ref`列与索引的比较

- `rows`扫描出的行数(估算的行数)

- `filtered`按表条件过滤的行百分比

- **`Extra`** :执行情况的描述和说明
  
  - **index skip scan** :会探测出索引前导列的唯一值个数，每个唯一值都会作为常规扫描的入口，在此基础上做一次查找，最后合并这些查询.
  - **Bacckward index scan** : 降序索引，如果一个查询，需要对多个列进行排序，且顺序要求不一致。在这种场景下，要想避免数据库额外的排序-“filesort”，只能使用降序索引
  - **use mrr**：目的是减少磁盘随机访问，将随机访问转化为较为顺序的访问。适用于 range/ref/eq_ref 类型的查询。
  - **Using where**： 表示优化器需要通过索引**回表**查询数据；
  - **Using index**：表示直接访问索引就足够获取到所需要的数据，不需要通过索引回表
  - **Using index condition**：会先条件过滤索引，过滤完索引后找到所有符合索引条件的数据行，随后用 WHERE 子句中的其他条件去过滤这些数据行；

**出现以下问题需要优化**

- extra中 出现了`Using temporary`；
- `rows`过多，或者几乎是全表的记录数；
- `key` 是 (NULL)；
- `possible_keys` 出现过多（待选）索引。

### 索引类型

#### 聚集索引

![image-20200328000203860](C:\Users\chen\AppData\Roaming\Typora\typora-user-images\image-20200328000203860.png)

叶子节点存储行记录

- 如果表定义了PK，则PK就是聚集索引；
- 如果表没有定义PK，则第一个not NULL unique列是聚集索引；
- 否则，InnoDB会创建一个隐藏的row-id作为聚集索引；

主键定义的长度越小，二级索引的大小就越小，这样每个磁盘块存储的索引数据越多，查询效率就越高

#### 普通索引

叶子节点存储主键值,*MyISAM的索引叶子节点存储记录指针*

![image-20200328000251632](C:\Users\chen\AppData\Roaming\Typora\typora-user-images\image-20200328000251632.png)

```sql
create table user (
    id int primary key,
    name varchar(20),
    sex varchar(5),
    index(name)
)engine=innodb;
```

如下记录

| 1    | *shenjian* | *m*  | *A*  |
| ---- | ---------- | ---- | ---- |
| 3    | *zhangsan* | *m*  | *A*  |
| 5    | *lisi*     | *m*  | *A*  |
| 6    | *wangwu*   | *F*  | *B*  |

![img](https://img2018.cnblogs.com/blog/885859/201907/885859-20190729184808306-758660222.png)

两个B+tree 索引如上图

1. id为PK，聚集索引，叶子节点存储行记录；
2. name为KEY，普通索引，叶子节点存储PK值，即id；

普通索引无法直接定位行记录`,需要扫码两遍索引树`。

```sql
select * from t where name='lisi';　
```

执行步骤

![img](https://img2018.cnblogs.com/blog/885859/201907/885859-20190729184911699-676257427.png)

1. 先通过普通索引定位到主键值id=5；
2. 在通过聚集索引定位到行记录；

这就是所谓的**回表查询**，先定位主键值，再定位行记录，它的性能较扫一遍索引树更低。

**Mysql 只需要在一棵索引树上就能获取SQL所需的所有列数据，无需回表，速度更快。**

下面sql 无需回表

```sql
select` `id,``name` `from` `user` `where` `name``=``'shenjian'``;　
```

下面sql 需要建立`联合索引(name, sex)`才能避免回表

```sql
select id,name,sex ... where name='shenjian';
```

#### 主键索引(PRIMARY KEY)

它是一种特殊的唯一索引，不允许有空值。一般是在建表的时候同时创建主键索引。

**删除**

```
alter table table_name drop primary key ;
```



#### 唯一索引(UNIQUE)

唯一索引列的值必须唯一，但允许有空值。如果是组合索引，则列值的组合必须唯一。

`ALTER TABLE table_name ADD UNIQUE (column);`创建唯一索引：

**普通索引(INDEX) **

这是最基本的索引，它没有任何限制。

可以通过`ALTER TABLE table_name ADD INDEX index_name (column);`创建普通索引：

**组合索引 INDEX**

即一个索引包含多个列，多用于避免回表查询。

可以通过`ALTER TABLE table_name ADD INDEX index_name(column1,column2, column3)`;创建组合索引：

#### 全文索引(5.6 MyISAM 5.6之後InnoDB也支持)

对于大量文本检索使用,速度快,但是有精度损失,在测试的时候表中至少存在4条以上的记录,看到这两个变量在 MyISAM 和 InnoDB 两种存储引擎下的变量名和默认值.

**中文Mysql 5.6.7 才能用 或者使用插件**

```
show variables like '%ft%';

// MyISAM
ft_min_word_len = 4;
ft_max_word_len = 84;

// InnoDB
innodb_ft_min_token_size = 3;
innodb_ft_max_token_size = 84;
```

**创建全文索引**

```sql
#方法一
create fulltext index content_tag_fulltext on fulltext_test(content,tag);
#方法二
alter table fulltext_test add fulltext index content_tag_fulltext(content,tag);
```

**删除**

```sql
drop index content_tag_fulltext on fulltext_test;

alter table fulltext_test drop index content_tag_fulltext;
```

**使用**

```sql
select * from test where match(content) against('a');
```

包含两种全全文索引

- 自然语言全文索引
- 布尔全文索引
  - `+`必须包含该词
  - `-`必须不包含该词
  - `>`提高该词的相关性,查询结果靠前
  - `<`降低该词的相关性，查询的结果靠后
  - `*`配符，只能接在词后面

**使用**

```sql
select * test where match(content) against('a*' in boolean mode);
```



## 常用sql

### select中返回boolean

```xml
 <select id="isLeafCategory" resultType="java.lang.Boolean">
        select case when count(path.category_tree_id) = 1 then 1 else 0 end
        from o2pcm_category_tree path
        where path.ancestor_category_id = #{categoryId}
 </select>
```

