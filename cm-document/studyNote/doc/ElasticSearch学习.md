---
author: chen miao
time : 2019/11/09
email:  parkstud@qq.com
---
[TOC]



# ElasticSearch

Elasticsearch是一个高度可扩展的开源全文本搜索和分析引擎。它使您可以快速，近乎实时地存储，搜索和分析大量数据。它通常用作支持具有复杂搜索功能和要求的应用程序的基础引擎/技术.本身扩展性很好，可以扩展到上百台服务器，处理PB级别的数据。

Elasticsearch也使用Java开发并使用Lucene作为其核心来实现所有索引和搜索的功能，通过简单的RESTful API来隐藏Lucene的复杂性，从而让全文搜索变得简单。

**ES的使用场景:**

*   网上商场,搜索商品和提出建议
*   ElasticSearch 配合logstash,kibana,收集日志或交易数据，并且要分析和挖掘此数据以查找趋势，统计信息，摘要或异常.
* 分析/业务智能需求，并且想要快速调查，分析，可视化并针对大量数据（即数百万或数十亿条记录）提出特别问题
##  基本概念
### Near Reaktime(NRT)(近实时)
Elasticsearch是近实时搜索平台。也就是说从索引(创建)文档到可搜索到这段时间之间存在延迟（通常为一秒钟）。
### Cluster (集群)
群集是一个或多个节点（服务器）的集合，这些节点一起保存数据，并在所有节点之间提供联合索引和搜索功能。集群由唯一名称标识，默认情况下为“ elasticsearch”。此名称很重要，因为如果节点被设置为通过其名称加入群集，则该节点只能是群集的一部分。
### Node (节点)
节点是单个服务器，它是群集的一部分，存储数据并参与群集的索引和搜索功能。
### Index (索引)
索引是具有相似特征的文档的集合(类似Mysql的数据库)。您可以为客户数据创建索引，为产品目录创建另一个索引，为订单数据创建另一个索引。索引由名称标识（必须全为小写），并且对该索引中的文档执行索引，搜索，更新和删除操作时，该名称用于引用索引。
### Type (类型) es6弃用
一种类型曾经是索引的逻辑类别/分区，它使可以在同一索引中存储不同类型的文档，例如，一种用于用户，另一种用于博客文章。
### Document (文档)
文件是可以建立索引的基本信息单位。例如，可以拥有一个针对单个客户的文档，一个针对单个产品的文档，以及另一个针对单个订单的文档。文档使用JSON表示
### Shards & Replicas (分片和副本)
索引可能会存储大量数据，这些数据可能超过单个节点的硬件限制。为了解决此问题，Elasticsearch提供了将索引细分为多个分片的功能。创建索引时，只需定义所需的分片数量即可。
在随时可能发生故障的网络/云环境中，非常有用，强烈建议您使用故障转移机制，以防碎片/节点因某种原因脱机或消失。为此，Elasticsearch允许您将索引分片的一个或多个副本制作为所谓的副本分片（简称副本）

## 安装、配置和使用
### 配置java

![配置java](https://i.loli.net/2019/12/12/Rt68iZTFhDfVp4r.png)

### 安装Elasticsearch
[官网文档](https://www.elastic.co/guide/en/elasticsearch/reference/7.4/getting-started-install.html) 
Linux 安装
```
下载
curl -L -O https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.4.2-linux-x86_64.tar.gz
解压
tar -xvf elasticsearch-7.4.2-linux-x86_64.tar.gz
启动
cd elasticsearch-7.4.2/bin 
./elasticsearch 
(后台启动 ./elasticsearch -d)
```
启动成功访问  http://ip:9200/ 如图
![es启动成功](https://i.loli.net/2019/12/12/LyugO1SMpITFUJY.png)

**启动失败一**
elasticsearch 7.2默认启用机器学习功能,需要关闭.
```
org.elasticsearch.ElasticsearchException: Failed to create native process factories for Machine Learning
    at org.elasticsearch.xpack.ml.MachineLearning.createComponents(MachineLearning.java:455) ~[?:?]
    at org.elasticsearch.node.Node.lambda$new$9(Node.java:438) ~[elasticsearch-7.2.0.jar:7.2.0]
    at java.util.stream.ReferencePipeline$7$1.accept(ReferencePipeline.java:267) ~[?:1.8.0_162]
    at java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1382) ~[?:1.8.0_162]
    at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:481) ~[?:1.8.0_162]
    at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471) ~[?:1.8.0_162]
    at java.util.stream.ReduceOps$ReduceOp.evaluateSequential(ReduceOps.java:708) ~[?:1.8.0_162]
    at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234) ~[?:1.8.0_162]
    at java.util.stream.ReferencePipeline.collect(ReferencePipeline.java:499) ~[?:1.8.0_162]
    at org.elasticsearch.node.Node.<init>(Node.java:441) ~[elasticsearch-7.2.0.jar:7.2.0]
    at org.elasticsearch.node.Node.<init>(Node.java:251) ~[elasticsearch-7.2.0.jar:7.2.0]
    at org.elasticsearch.bootstrap.Bootstrap$5.<init>(Bootstrap.java:221) ~[elasticsearch-7.2.0.jar:7.2.0]
    at org.elasticsearch.bootstrap.Bootstrap.setup(Bootstrap.java:221) ~[elasticsearch-7.2.0.jar:7.2.0]
    at org.elasticsearch.bootstrap.Bootstrap.init(Bootstrap.java:349) [elasticsearch-7.2.0.jar:7.2.0]
    at org.elasticsearch.bootstrap.Elasticsearch.init(Elasticsearch.java:159) [elasticsearch-7.2.0.jar:7.2.0]
    at org.elasticsearch.bootstrap.Elasticsearch.execute(Elasticsearch.java:150) [elasticsearch-7.2.0.jar:7.2.0]
    at org.elasticsearch.cli.EnvironmentAwareCommand.execute(EnvironmentAwareCommand.java:86) [elasticsearch-7.2.0.jar:7.2.0]
    at org.elasticsearch.cli.Command.mainWithoutErrorHandling(Command.java:124) [elasticsearch-cli-7.2.0.jar:7.2.0]
    at org.elasticsearch.cli.Command.main(Command.java:90) [elasticsearch-cli-7.2.0.jar:7.2.0]
    at org.elasticsearch.bootstrap.Elasticsearch.main(Elasticsearch.java:115) [elasticsearch-7.2.0.jar:7.2.0]
    at org.elasticsearch.bootstrap.Elasticsearch.main(Elasticsearch.java:92) [elasticsearch-7.2.0.jar:7.2.0]
Caused by: java.io.FileNotFoundException: \\.\pipe\controller_log_4652 (系统找不到指定的文件。)
    at java.io.FileInputStream.open0(Native Method) ~[?:1.8.0_162]
    at java.io.FileInputStream.open(FileInputStream.java:195) ~[?:1.8.0_162]
    at java.io.FileInputStream.<init>(FileInputStream.java:138) ~[?:1.8.0_162]
    at java.io.FileInputStream.<init>(FileInputStream.java:93) ~[?:1.8.0_162]
    at org.elasticsearch.xpack.ml.utils.NamedPipeHelper$PrivilegedInputPipeOpener.run(NamedPipeHelper.java:288) ~[?:?]
    at org.elasticsearch.xpack.ml.utils.NamedPipeHelper$PrivilegedInputPipeOpener.run(NamedPipeHelper.java:277) ~[?:?]
    at java.security.AccessController.doPrivileged(Native Method) ~[?:1.8.0_162]
    at org.elasticsearch.xpack.ml.utils.NamedPipeHelper.openNamedPipeInputStream(NamedPipeHelper.java:130) ~[?:?]
    at org.elasticsearch.xpack.ml.utils.NamedPipeHelper.openNamedPipeInputStream(NamedPipeHelper.java:97) ~[?:?]
    at org.elasticsearch.xpack.ml.process.ProcessPipes.connectStreams(ProcessPipes.java:131) ~[?:?]
    at org.elasticsearch.xpack.ml.process.NativeController.<init>(NativeController.java:62) ~[?:?]
    at org.elasticsearch.xpack.ml.process.NativeControllerHolder.getNativeController(NativeControllerHolder.java:40) ~[?:?]
    at org.elasticsearch.xpack.ml.MachineLearning.createComponents(MachineLearning.java:440) ~[?:?]
    ... 20 more
```
解决方法
编辑 `config\elasticsearch.yml` 文件 添加

```
xpack.ml.enabled: false
```

**启动失败二**
启动es 使用了root用户启动

```
Exception in thread "main" java.lang.RuntimeException: don't run elasticsearch as root.
    at org.elasticsearch.bootstrap.Bootstrap.initializeNatives(Bootstrap.java:94)
    at org.elasticsearch.bootstrap.Bootstrap.setup(Bootstrap.java:160)
    at org.elasticsearch.bootstrap.Bootstrap.init(Bootstrap.java:286)
    at org.elasticsearch.bootstrap.Elasticsearch.main(Elasticsearch.java:35)
Refer to the log for complete error details.
```

解决方法

```
# 创建es账户
adduser es
# 修改密码
passwd es
# 授权
chown -R es /opt/elasticsearch-7.4.2/
# 使用 es账户
su es
```

**外网无法访问**
在`/opt/elasticsearch-7.4.2/config/elasticsearch.yml`配置如下

```
network.bind_host: 0.0.0.0
```

### 使用Kibana
#### 简介
Kibana 是一款开源的数据分析和可视化平台，它是 Elastic Stack 成员之一，设计用于和 Elasticsearch 协作。您可以使用 Kibana 对 Elasticsearch 索引中的数据进行搜索、查看、交互操作。您可以很方便的利用图表、表格及地图对数据进行多元化的分析和呈现。
[Kibana官方文档](https://www.elastic.co/guide/cn/kibana/current/index.html)
#### Windows 安装Kibana
下载
[https://artifacts.elastic.co/downloads/kibana/kibana-6.0.0-windows-x86_64.zip](https://artifacts.elastic.co/downloads/kibana/kibana-6.0.0-windows-x86_64.zip)

配置中配置 

```
elasticsearch.hosts: ["http://esip地址:9200"] 
```

[Kibana完整配置](https://www.elastic.co/guide/cn/kibana/current/settings.html)

![kibana配置文件](https://i.loli.net/2019/12/12/Yw5RfLIgEh2COvk.png)

解压后使用 双击启动

![双击使用Kibana](https://i.loli.net/2019/12/12/eJ1fwClGZv9xbyF.png)

启动后访问地址`[http://localhost:5601/](http://localhost:5601/)`

打开开发界面,进行es测试
![t5.png](https://i.loli.net/2019/12/12/DtxTnVk7rKB6jFw.png)


## Elasticsearch API学习
### PUT API
请求
```
PUT /website/_doc/123
{
  "title": "My first blog entry",
  "text": "Just trying this out...",
  "date": "2014/01/01"
}
```
响应

```
{
  "_index" : "website",
  "_type" : "_doc",
  "_id" : "123",
  "_version" : 3,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 27,
  "_primary_term" : 1
}
```

`_shard`提供了关于索引操作的复制过程的信息。

`total`表示多少分片需要被执行该索引操作（包含主分片和副本分片）。

`successful`表示所有分片中有多少成功的。

`failed`表示有多少分片是执行失败的。

如果索引操作成功，则`successful`至少为1。

### GET API
请求
```
GET /website
```
响应

```
{
  "website" : {
    "aliases" : { },
    "mappings" : {
      "properties" : {
        "date" : {
          "type" : "date",
          "format" : "yyyy/MM/dd HH:mm:ss||yyyy/MM/dd||epoch_millis"
        },
        "tags" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "test" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "text" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "title" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "views" : {
          "type" : "long"
        }
      }
    },
    "settings" : {
      "index" : {
        "creation_date" : "1574407109631",
        "number_of_shards" : "1",
        "number_of_replicas" : "1",
        "uuid" : "JsAxoAdBRc6X_3PMpfBgOg",
        "version" : {
          "created" : "7040299"
        },
        "provided_name" : "website"
      }
    }
  }
}

```

返回website索引的全部定义信息
`website` 表示索引
`aliases` 别名
`mappings`  是类似于数据库中的表结构定义，主要作用如下
* 定义index下的字段名
* 定义字段类型，比如数值型、浮点型、布尔型等
* 定义倒排索引相关的设置，比如是否索引、记录position等
`properties` 文档字段
`type` [字段类型详解](#字段类型)
`settings` 更新集群级别的设置。更新分为持久和临时两种，如果是持久的，则集群重启后仍然生效；如果是临时的，在完全重启集群后会失效。通过将值设置为null可以重置持久或临时设置。如果临时设置被重置，则会按持久设置、配置文件中的设置和默认值的顺序将第一个找到的值作为设置的值。集群设置的优先级是：临时设置、持久设置、配置文件elasticsearch.yml中的设置。
### Delete API
请求

```
DELETE /website/_doc/123
```
响应
```
{
  "_index" : "website",
  "_type" : "_doc",
  "_id" : "123",
  "_version" : 4,
  "result" : "deleted",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 28,
  "_primary_term" : 1
}

```

**使用Query API删除**
请求
```
POST /website/_delete_by_query
{
  # 查询必须传递一个值给`query`,方法与SearchAPI相同，你也可以使用q参数。
  "query":{
    "match":{
      "title":"first"
    }
  }
}
```
响应
```
{
  "took" : 111,
  "timed_out" : false,
  "total" : 1,
  "deleted" : 1,
  "batches" : 1,
  "version_conflicts" : 0,
  "noops" : 0,
  "retries" : {
    "bulk" : 0,
    "search" : 0
  },
  "throttled_millis" : 0,
  "requests_per_second" : -1.0,
  "throttled_until_millis" : 0,
  "failures" : [ ]
}

```

### Search API
请求
```
GET /website/_search
```
#### 请求URI案例
1. 详细查看相关性得分的计算过程
`GET /{idx}/{type}/_search?explain&format=yaml`
2. 测试查看分析器是如何工作的
`GET /_anzlyze`
3. 查看mapping
`GET /{idx}/_mapping`
4. 只返回source，不要元数据（`_index`，`_id`，`_shards`）
`GET /{idx}/{type}/{id}/_source`
5. 只返回`_source`中某些字段
`GET /{idx}/{type}/{id}?_source=name,age`
6. multi-get，获取所有文档
`GET /_mget`
7. 轻量搜索
`GET /{idx}/{type}?q=name:value`
8. +：必须匹配，-必须不匹配
`GET /{idx}/{type}?q=+name:liyl age:>30 -gender:male`
9. 分页
`GET /_search?from=5&size=10`

响应
```
{
  "took" : 0,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 2,
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "website",
        "_type" : "blog",
        "_id" : "0876kW4BOzACapDQ5Q3A",
        "_score" : 1.0,
        "_source" : {
          "title" : "My second blog entry",
          "text" : "Still trying this out...",
          "date" : "2014/01/01"
        }
      },
      {
        "_index" : "website",
        "_type" : "blog",
        "_id" : "1",
        "_score" : 1.0,
        "_source" : {
          "views" : 6
        }
      }
    ]
  }
}
```
#### 响应信息
`took`  整个操作从开始到结束的毫秒数。
`time_out`  执行期间执行的任何请求超时，则将此标志设置为true。
`total`​ 成功执行的文档总数
`_index`文档所在的索引名
`_type` 文档所在的类型名
`_id` 文档id
`_version` 文档的版本
`_shards` _shards表示索引操作的复制过程的信息。
`total`指示应在其上执行索引操作的分片副本（主分片和副本分片）的数量。
`successful`表示索引操作成功的分片副本数。
`failed`在副本分片上索引操作失败的情况下包含复制相关错误。
### Query DSL
案例
```
GET /_search
{
  "query": { 
    "bool": { 
      "must": [
        { "match": { "title":   "Search"        }},
        { "match": { "content": "Elasticsearch" }}
      ],
      "filter": [ 
        { "term":  { "status": "published" }},
        { "range": { "publish_date": { "gte": "2015-01-01" }}}
      ]
    }
  }
}
```
* 该`title`字段包含单词`search`
* 该`content`字段包含单词`elasticsearch`
* 该`status`字段包含确切的单词`published`
* 该`publish_date`字段包含从2015年1月1日开始的日期。
#### bool查询
可以理解成通过布尔逻辑将较小的查询组合成较大的查询。
特点
1. 子查询可以任意顺序出现
2. 可以嵌套多个查询，包括bool查询
3. 如果bool查询中没有must条件，should中必须至少满足一条才会返回结果。

```
must： 必须匹配。贡献算分
must_not：过滤子句，必须不能匹配，但不贡献算分
should： 选择性匹配，至少满足一条。贡献算分
filter： 过滤子句，必须匹配，但不贡献算分
```

```
{
  "query": {
    "bool" : {
      "must" : {
        "term" : { "user" : "kimchy" }
      },
      "filter": {
        "term" : { "tag" : "tech" }
      },
      "must_not" : {
        "range" : {
          "age" : { "gte" : 10, "lte" : 20 }
        }
      },
      "should" : [
        { "term" : { "tag" : "wow" } },
        { "term" : { "tag" : "elasticsearch" } }
      ],
      "minimum_should_match" : 1,
      "boost" : 1.0
    }
  }
}
```
在filter元素下指定的查询对评分没有影响 , 评分返回为0。分数仅受已指定查询的影响。

```
# 嵌套，实现了 should not 逻辑 
POST /products/_search
{
  "query": {
    "bool": {
      "must": {
        "term": {
          "price": "30"
        }
      },
      "should": [
        {
          "bool": {
            "must_not": {
              "term": {
                "avaliable": "false"
              }
            }
          }
        }
      ],
      "minimum_should_match": 1
    }
  }
}
```

#### boosting 查询
在上面的复合查询我们可以通过`must_not+must` 先剔除不想匹配的文档，再获取匹配的文档，但是有一种场景就是我并不需要完全剔除，而是把需要剔除的那部分文档的分数降低。这个时候就可以使用boosting query

```
POST news/_search
{
  "query": {
    "boosting": {
      "positive": {
        "match": {
          "content": "apple"
        }
      },
      "negative": {
        "match": {
          "content": "pie"
        }
      },
      "negative_boost": 0.5
    }
  }
}
```
`positive`  要运行的查询。任何返回的文档都必须与此查询匹配
`negative` 查询用于降低匹配文档的相关性得分 获取positive分数 * `negative_boost`
`negative_boost`  0到1.0之间的浮点数，用于降低与否定查询匹配的文档的相关性得分
#### constant_score(固定分数查询)
常量分值查询，目的就是返回指定的`score`，一般都结合`filter`使用，因为filter context忽略score。

```
POST news/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "match": {
         "content":"apple"
        }
      },
      "boost": 2.5
    }
  }
}
```
#### dis_max(最佳匹配查询）
`dis_max` : 只是取分数最高的那个query的分数。

```
GET /_search
{
    "query": {
        "dis_max" : {
            "queries" : [
                { "term" : { "title" : "Quick pets" }},
                { "term" : { "body" : "Quick pets" }}
            ],
            "tie_breaker" : 0.7
        }
    }
}
```
假设一条文档的'title'查询得分是 1，'body'查询得分是1.6。那么总得分为：1.6+1*0.7 = 2.3。
如果我们去掉`"tie_breaker" : 0.7` ，那么tie_breaker默认为0，那么这条文档的得分就是 1.6 + 1*0 = 1.6
#### function_score(函数查询）
function_score是处理分值计算过程的终极工具。它让你能够对所有匹配了主查询的每份文档`调用一个函数来调整甚至是完全替换原来的_score。`
`weight` 对每份文档适用一个简单的提升，且该提升不会被归约：当weight为2时，结果为2 * _score。
`field_value_factor` 使用文档中某个字段的值来改变_score，比如将受欢迎程度或者投票数量考虑在内。
`random_score` 使用一致性随机分值计算来对每个用户采用不同的结果排序方式，对相同用户仍然使用相同的排序方式。
`衰减函数(Decay Function) - linear，exp，gauss`
将像`publish_date`，`geo_location`或者price这类浮动值考虑到_score中，偏好最近发布的文档，邻近于某个地理位置(译注：其中的某个字段)的文档或者价格(译注：其中的某个字段)靠近某一点的文档。
`script_score`
使用自定义的脚本来完全控制分值计算逻辑。如果你需要以上预定义函数之外的功能，可以根据需要通过脚本进行实现。

```
GET /_search
{
    "query": {
        "function_score": {
          "query": { "match_all": {} },
          "boost": "5", 
          "functions": [
              {
                  "filter": { "match": { "test": "bar" } },
                  "random_score": {}, 
                  "weight": 23
              },
              {
                  "filter": { "match": { "test": "cat" } },
                  "weight": 42
              }
          ],
          "max_boost": 42,
          "score_mode": "max",
          "boost_mode": "multiply",
          "min_score" : 42
        }
    }
}
```
#### Match Query
全文查询返回与提供的文本，数字，日期或布尔值匹配的文档。匹配之前分析提供的文本。

```
GET /_search
{
    "query": {
        "match" : {
            "message" : {
                "query" : "this is a test",
                "operator" : "and"
            }
        }
    }
}
```
### Aggregations 聚合
聚合分析是数据库中重要的功能特性，完成对一个查询的数据集中数据的聚合计算，如：找出某字段（或计算表达式的结果）的最大值、最小值，计算和、平均值等。ES作为搜索引擎兼数据库，同样提供了强大的聚合分析能力。
#### 聚合语法
```
"aggregations" : {
    "<aggregation_name>" : { <!--聚合的名字 -->
        "<aggregation_type>" : { <!--聚合的类型 -->
            <aggregation_body> <!--聚合体：对哪些字段进行聚合 -->
        }
        [,"meta" : {  [<meta_data_body>] } ]? <!--元 -->
        [,"aggregations" : { [<sub_aggregation>]+ } ]? <!--在聚合里面在定义子聚合 -->
    }
    [,"<aggregation_name_2>" : { ... } ]*<!--聚合的名字 -->
}
```
**聚合可以嵌套**
#### buckets 桶聚合
关系型数据库中除了有聚合函数外，还可以对查询出的数据进行分组group by，再在组上进行指标聚合。在 ES 中group by 称为**分桶**，**桶聚合 bucketing**
##### Terms Aggregation  根据字段值项分组聚合

```
POST /bank/_search?size=0
{
  "aggs": {
    "age_terms": {
      "terms": {
        "field": "age",
         #指定分组数
         "size": 20
         #每个分组上显示偏差值
         "show_term_doc_count_error": true
         #指定每个分片上返回多少个分组
         "shard_size":20
         #根据文档计数排序
         "order" : { "_count" : "asc" }
      }
    }
  }
}
```

```
{
  "took": 2000,
  "timed_out": false,
  "_shards": {
    "total": 5,
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": 1000,
    "max_score": 0,
    "hits": []
  },
  "aggregations": {
    "age_terms": {
      "doc_count_error_upper_bound": 0,
      "sum_other_doc_count": 463,
      "buckets": [
        {
          "key": 31,
          "doc_count": 61
        },
        {
          "key": 39,
          "doc_count": 60
        },
        {
          "key": 26,
          "doc_count": 59
        },
        {
          "key": 32,
          "doc_count": 52
        },
        {
          "key": 35,
          "doc_count": 52
        },
        {
          "key": 36,
          "doc_count": 52
        },
        {
          "key": 22,
          "doc_count": 51
        },
        {
          "key": 28,
          "doc_count": 51
        },
        {
          "key": 33,
          "doc_count": 50
        },
        {
          "key": 34,
          "doc_count": 49
        }
      ]
    }
  }
}
```
`doc_count_error_upper_bound` 文档计数的最大偏差值
`sum_other_doc_count` 未返回的其他项的文档数
默认情况下返回按文档计数从高到低的前10个分组

##### filter Aggregation  对满足过滤查询的文档进行聚合计算

```
POST /bank/_search?size=0
{
  "aggs": {
    "age_terms": {
      "filter": {"match":{"gender":"F"}},
      "aggs": {
        "avg_age": {
          "avg": {
            "field": "age"
          }
        }
      }
    }
  }
}
```
##### Filters Aggregation  多个过滤组聚合计算

```
GET logs/_search
{
  "size": 0,
  "aggs": {
    "messages": {
      "filters": {
        "filters": {
          "errors": {
            "match": {
              "body": "error"
            }
          },
          "warnings": {
            "match": {
              "body": "warning"
            }
          }
        }
      }
    }
  }
}
```
##### Range Aggregation 范围分组聚合

```
POST /bank/_search?size=0
{
  "aggs": {
    "age_range": {
      "range": {
        "field": "age",
        "ranges": [
          {
            "to": 25
          },
          {
            "from": 25,
            "to": 35
          },
          {
            "from": 35
          }
        ]
      },
      "aggs": {
        "bmax": {
          "max": {
            "field": "balance"
          }
        }
      }
    }
  }
}
```

#### Metric 指标聚合
对一个数据集求最大、最小、和、平均值等指标的聚合，在ES中称为**指标聚合   metric**
##### Avg
请求
```
POST /exams/_search?size=0
{
    "aggs" : {
        "avg_grade" : { "avg" : { "field" : "grade" ,  "missing":  10 } }
    }
}
```
missing 表示处理缺少值为10
响应

```
{
    ...
    "aggregations": {
        "avg_grade": {
            "value": 75.0
        }
    }
}
```
平局分75.0
##### cardinality 去重

```
POST /sales/_search?size=0
{
    "aggs" : {
        "type_count" : {
            "cardinality" : {
                "field" : "type"
            }
        }
    }
}
```
##### Extended Stats(扩展统计汇总)

```
GET /exams/_search
{
    "size": 0,
    "aggs" : {
        "grades_stats" : { "extended_stats" : { "field" : "grade" } }
    }
}
```

```
{
    ...

    "aggregations": {
        "grades_stats": {
           "count": 2,
           "min": 50.0,
           "max": 100.0,
           "avg": 75.0,
           "sum": 150.0,
           "sum_of_squares": 12500.0,
           "variance": 625.0,
           "std_deviation": 25.0,
           "std_deviation_bounds": {
            "upper": 125.0,
            "lower": 25.0
           }
        }
    }
}
```
#####  MAX

```
POST /sales/_search?size=0
{
    "aggs" : {
        "max_price" : { "max" : { "field" : "price" } }
    }
}
```
##### MIN

```
POST /sales/_search?size=0
{
    "aggs" : {
        "min_price" : { "min" : { "field" : "price" } }
    }
}
```
##### SUM

```
POST /sales/_search?size=0
{
    "query" : {
        "constant_score" : {
            "filter" : {
                "match" : { "type" : "hat" }
            }
        }
    },
    "aggs" : {
        "hat_prices" : { "sum" : { "field" : "price" } }
    }
}
```

#### Matrix 矩阵聚合
类聚合，可在多个字段上进行操作，并根据从请求的文档字段中提取的值生成矩阵结果。

#### Pipeline 管道聚合
汇总其他聚合及其相关指标的输出的聚合

###  Update API
请求
```
PUT /website/_doc/1
{
   "title" : "My second blog entry1",
   "text" : "Still trying this out...1"
}

```
响应
```
{
  "_index" : "website",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 6,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 30,
  "_primary_term" : 1
}

```

使用脚本
```
POST /test/_update/1
{
    "script" : {
        "source": "ctx._source.counter += params.count",
        "lang": "painless",
        "params" : {
            "count" : 4
        }
    }
}
```
_提示_
`ctx`是一个map可以获得`_source`，`_index`，`_type`，`_id`，`_version`，`_routing`和`_now`(当前时间)
脚本参数
`lang` 指定编写脚本的语言 默认`painless ` 可以是`groovy`
`source,id`指定脚本的来源，`inline`脚本是指定`source`，如上例所示，存储的脚本是指定的`id`，并从群集状态中检索（请参阅存储的脚本）。
`params` 指定作为变量传递到脚本的任何命名参数。
脚本案例
```
 "script" : "ctx._source.new_field = '添加一个新字段'"

#删除一个字段
  "script" : "ctx._source.remove('new_field')"
```
#### Upserts 使用
如果文档不存在，upsert元素的内容将作为新文档插入。如果文档确实存在，则执行脚本:
```
POST /test/_update/1
{
 "script" : {
        "source": "ctx._source.counter += params.count",
        "lang": "painless",
        "params" : {
            "count" : 4
        }
    },
    "upsert" : {
        "counter" : 1
    }
}
```
如果您希望脚本运行，无论文档是否存在 - 即脚本处理初始化文档而不是upsert元素 - 将`scripted_upsert`设置为`true`
将`doc_as_upsert`设置为`true`将使用doc的内容作为upsert值，而不是发送部分doc加上upsert文档
###  Multi GET API
Multi get API基于索引，类型，（可选）和id（或者路由）返回多个文档。响应包括一个`docs`数组，其中包含所有获取的文档，按照multi-get请求对应的顺序排列(如果某个特定get出现失败，则在响应中包含一个包含此错误的对象)。
```
POST /us/_mget
{
   "ids" : [ "2", "1" ]
}
```

```
GET /_mget
{
    "docs" : [
        {
            "_index" : "test",
            "_type" : "_doc",
            "_id" : "1"
        },
        {
            "_index" : "test",
            "_type" : "_doc",
            "_id" : "2"
        }
    ]
}
```
可以为每个要获取的文档指定要检索的特定存储字段，类似于get API的`stored_fields`参数
```
{
    "docs" : [
        {
            "_index" : "test",
            "_type" : "_doc",
            "_id" : "1",
            "stored_fields" : ["field1", "field2"]
        },
        {
            "_index" : "test",
            "_type" : "_doc",
            "_id" : "2",
            "stored_fields" : ["field3", "field4"]
        }
    ]
}
```

### Bulk API
批量API可以在一个API调用中执行许多索引/删除操作，这可以大大提高索引速度。

```
POST /_bulk
{ "index" : { "_index" : "test", "_id" : "1" } }
{ "field1" : "value1" }
{ "delete" : { "_index" : "test", "_id" : "2" } }
{ "create" : { "_index" : "test", "_id" : "3" } }
{ "field1" : "value3" }
{ "update" : {"_id" : "1", "_index" : "test"} }
{ "doc" : {"field2" : "value2"} }
```

## ElasticSearch 字段类型
##### 字符串类型
1. `string`
ElasticSearch 5.x开始不再支持string，由text和keyword类型替代
2. `text`
需要被全文搜索的字段,如商品描述,设置text类型以后，字段内容会被分析，在生成倒排索引以前，字符串会被分析器分成一个一个词项。text类型的字段**不用于排序，很少用于聚合**。
3. `keyword`
keyword类型适用于索引结构化的字段,**keyword类型的字段只能通过精确值搜索到。**
##### 整数

| 类型      | 取值范围          |
| --------- | --------         |
| byte      | -128~127         |
| short     | -32768~32767     |
| integer   | -2^31~2^31-1     |
| long      | -2^63~2^63-1     |
#####  浮点类型
`double`，`float` ，`half_float`，`scaled_float`
##### date 类型
ElasticSearch 内部会将日期数据转换为UTC，并存储为milliseconds-since-the-epoch的long型整数。
##### boolean类型
逻辑类型（布尔类型）可以接受true/false/”true”/”false”值
##### binary类型
二进制字段是指用base64来表示索引中存储的二进制数据，可用来存储二进制形式的数据，例如图像。默认情况下，该类型的字段只存储不索引。二进制类型只支持index_name属性。
##### array类型
在ElasticSearch中，没有专门的数组（Array）数据类型，
但是，在默认情况下，任意一个字段都可以包含0或多个值，这意味着每个字段默认都是数组类型
在同一个数组中，数组元素的数据类型是相同的，ElasticSearch不支持元素为多个数据类型：[ 10, “some string” ]，常用的数组类型是：
1. 字符数组: [ “one”, “two” ]
2. 整数数组: productid:[ 1, 2 ]
3. 对象（文档）数组: “user”:[ { “name”: “Mary”, “age”: 12 }, { “name”: “John”, “age”: 10 }]，ElasticSearch内部把对象数组展开为 {“user.name”: [“Mary”, “John”], “user.age”: [12,10]}
##### object类型
JSON天生具有层级关系，文档会包含嵌套的对象
##### ip类型
ip类型的字段用于存储IPv4或者IPv6的地址
## ElasticSearch 脚本
### 脚本语法格式
```
  "script": {
    "lang":   "...",  
    "source" | "id": "...", 
    "params": { ... } 
  }
```
*   脚本编写的语言，默认为`painless`。
*   脚本本身可以指定为内联脚本的`source`或存储脚本的`id`。
*   应传递给脚本的任何命名参数。

### 案例

```
GET my_index/_search
{
  "script_fields": {
    "my_doubled_field": {
      "script": {
        "lang":   "expression",
        "source": "doc['my_field'] * multiplier",
        "params": {
          "multiplier": 2
        }
      }
    }
  }
}
```
## JAVA客户端
### Spring Data Elasticsearch集成
1. 引入依赖

```
  <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-elasticsearch</artifactId>
            <version>${version}</version>
  </dependency>
```
2. 配置

```
##es地址
spring.data.elasticsearch.cluster-nodes = {ip}:9300
```
3. 使用
```java
// 创建实体
@Document(indexName = "testgoods")
//indexName索引名称 可以理解为数据库名 必须为小写 不然会报org.elasticsearch.indices.InvalidIndexNameException异常
@Data
public class GoodsInfo implements Serializable {
    private Long id;
    private String name;
    private String description;
}
//设置Repository 操作
@Component public interface GoodsRepository extends ElasticsearchRepository<GoodsInfo,Long> { }
```

```java
@RestController
public class GoodsController {

    @Autowired
    private GoodsRepository goodsRepository;

    @GetMapping("save")
    public String save(){
        GoodsInfo goodsInfo = new GoodsInfo(System.currentTimeMillis(),
                "商品"+System.currentTimeMillis(),"这是一个测试商品");
        goodsRepository.save(goodsInfo);
        return "success";
    }

    @GetMapping("delete")
    public String delete(long id){
        goodsRepository.delete(id);
        return "success";
    }

    @GetMapping("update")
    public String update(long id,String name,String description){
        GoodsInfo goodsInfo = new GoodsInfo(id,
                name,description);
        goodsRepository.save(goodsInfo);
        return "success";
    }

    @GetMapping("getOne")
    public GoodsInfo getOne(long id){
        GoodsInfo goodsInfo = goodsRepository.findOne(id);
        return goodsInfo;
    }


    //每页数量
    private Integer PAGESIZE=10;


    //根据关键字"商品"去查询列表，name或者description包含的都查询
    @GetMapping("getGoodsList")
    public List<GoodsInfo> getList(Integer pageNumber,String query){
        if(pageNumber==null) pageNumber = 0;
        //es搜索默认第一页页码是0
        SearchQuery searchQuery=getEntitySearchQuery(pageNumber,PAGESIZE,query);
        Page<GoodsInfo> goodsPage = goodsRepository.search(searchQuery);
        return goodsPage.getContent();
    }


    private SearchQuery getEntitySearchQuery(int pageNumber, int pageSize, String searchContent) {
        FunctionScoreQueryBuilder functionScoreQueryBuilder = QueryBuilders.functionScoreQuery()
                .add(QueryBuilders.matchPhraseQuery("name", searchContent),
                        ScoreFunctionBuilders.weightFactorFunction(100))
                .add(QueryBuilders.matchPhraseQuery("description", searchContent),
                        ScoreFunctionBuilders.weightFactorFunction(100))
                //设置权重分 求和模式
                .scoreMode("sum")
                //设置权重分最低分
                .setMinScore(10);

        // 设置分页
        Pageable pageable = new PageRequest(pageNumber, pageSize);
        return new NativeSearchQueryBuilder()
                .withPageable(pageable)
                .withQuery(functionScoreQueryBuilder).build();
    }

}
```

#### 注解说明
*   `@Id`：在字段级别应用，以标记用于标识目的的字段。

*   `@Document`：在类级别应用，以指示该类是映射到数据库的候选对象。最重要的属性是：

    *   `indexName`：用于存储此实体的索引的名称

    *   `type`：映射类型。如果未设置，则使用小写的类的简单名称。

    *   `shards`：索引的分片数。

    *   `replicas`：索引的副本数。

    *   `refreshIntervall`：索引的刷新间隔。用于索引创建。默认值为_“ 1s”_。

    *   `indexStoreType`：索引的索引存储类型。用于索引创建。默认值为_“ fs”_。

    *   `createIndex`：配置是否在存储库引导中创建索引。默认值为_true_。

    *   `versionType`：版本管理的配置。默认值为_EXTERNAL_。

*   `@Transient`：默认情况下，所有私有字段都映射到文档，此注释将应用该字段的字段从数据库中存储出来

*   `@PersistenceConstructor`：标记从数据库实例化对象时要使用的给定构造函数，甚至是受保护的程序包。构造函数参数按名称映射到检索到的Document中的键值。

*   `@Field`：在字段级别应用并定义字段的属性，大多数属性映射到各自的[Elasticsearch映射](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping.html)定义：

    *   `name`：字段名称，将在Elasticsearch文档中表示，如果未设置，则使用Java字段名称。

    *   `type`：字段类型，可以是_Text，Integer，Long，Date，Float，Double，Boolean，Object，Auto，Nested，Ip，Attachment，Keyword之一_。

    *   `format`和_日期_类型的`pattern`自定义定义。

    *   `store`：标记是否将原始字段值存储在Elasticsearch中，默认值为_false_。

    *   `analyzer`，`searchAnalyzer`，`normalizer`用于指定自定义自定义分析和正规化。

    *   `copy_to`：将多个文档字段复制到的目标字段。

*   `@GeoPoint`：将字段标记为_geo_point_数据类型。如果字段是`GeoPoint`类的实例，则可以省略。
#### 查询详解
```java
interface BookRepository extends Repository<Book, String> {
  List<Book> findByNameAndPrice(String name, Integer price);
}
```
转化为

```
{ "bool" :
    { "must" :
        [
            { "field" : {"name" : "?"} },
            { "field" : {"price" : "?"} }
        ]
    }
}
```
![t6.jpg](https://i.loli.net/2019/12/12/XfvClNQSiyOE5qJ.png)
![t7.jpg](https://i.loli.net/2019/12/12/2b9RXINUBPdQLDw.png)
[官方链接](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.clients)

### Java Rest Client集成
产品上没有使用 上面的方法进行集成,采用的是本方法
1. 依赖

```
      <!-- Java High Level REST Client -->
        <!-- https://mvnrepository.com/artifact/org.elasticsearch.client/elasticsearch-rest-high-level-client -->
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>elasticsearch-rest-high-level-client</artifactId>
            <version>7.3.2</version>
        </dependency>

        <!-- 工具类 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.8.1</version>
        </dependency>

```
2. 配置
yml文件
```
#可以是多个
elasticsearch.ip={ip}:9200
```
配置类

```java
@Slf4j
@Configuration
public class ElasticsearchRestClient {
    private static final int ADDRESS_LENGTH = 2;
    private static final String HTTP_SCHEME = "http";

    /**
     * 使用冒号隔开ip和端口1
     */
    @Value("${elasticsearch.ip}")
    String[] ipAddress;

    @Bean
    public RestClientBuilder restClientBuilder() {
        HttpHost[] hosts = Arrays.stream(ipAddress)
                .map(this::makeHttpHost)
                .filter(Objects::nonNull)
                .toArray(HttpHost[]::new);
        log.debug("hosts:{}", Arrays.toString(hosts));
        return RestClient.builder(hosts);
    }


    @Bean(name = "highLevelClient")
    public RestHighLevelClient highLevelClient(@Autowired RestClientBuilder restClientBuilder) {
        restClientBuilder.setMaxRetryTimeoutMillis(60000);
        return new RestHighLevelClient(restClientBuilder);
    }


    private HttpHost makeHttpHost(String s) {
        assert StringUtils.isNotEmpty(s);
        String[] address = s.split(":");
        if (address.length == ADDRESS_LENGTH) {
            String ip = address[0];
            int port = Integer.parseInt(address[1]);
            return new HttpHost(ip, port, HTTP_SCHEME);
        } else {
            return null;
        }
    }
}

```
使用

```java
@Service
public class XXXServiceImpl implements XXXService {
    @Autowired
    RestHighLevelClient highLevelClient;

    @Override
    public boolean testEsRestClient(){
        SearchRequest searchRequest = new SearchRequest("gdp_tops*");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("city", "北京市"));
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse response = highLevelClient.search(searchRequest);
            Arrays.stream(response.getHits().getHits())
                    .forEach(i -> {
                        System.out.println(i.getIndex());
                        System.out.println(i.getSource());
                        System.out.println(i.getType());
                    });
            System.out.println(response.getHits().totalHits);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
```
#### 各种API查询Demo
##### IndexRequest

```
 @Autowired
    private RestHighLevelClient client;

    public void methodIndex1() {
        IndexRequest request = new IndexRequest("posts");
        request.id("1");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        request.source(jsonString);
    }

    public void methodIndex2() {
        HashMap<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "kimchy");
        jsonMap.put("postDate", new Date());
        jsonMap.put("message", "tring out elasticsearch");
        IndexRequest posts = new IndexRequest("posts").id("1").source(jsonMap);
    }

    public void methodIndex3() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        builder.field("user", "kimchy");
        builder.timeField("postDate", new Date());
        builder.field("message", "trying out elasticsearch");
        builder.endObject();
        IndexRequest indexRequest = new IndexRequest("posts").id("1").source(builder);
    }

    public void methodIndex4() throws IOException {
        IndexRequest indexRequest = new IndexRequest("posts").id("1").source(
                "user", "kimchy",
                "postDate", new Date(),
                "message", "trying out elasticearch"
        );
        // 控制分片路由
        indexRequest.routing("routing");
        // 设置主分片时间
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        indexRequest.timeout("1s");
        // 设置刷新策略
        indexRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        indexRequest.setRefreshPolicy("wait_for");
        // 设置版本号
        indexRequest.versionType(VersionType.EXTERNAL);
        //版本类型
        indexRequest.opType(DocWriteRequest.OpType.CREATE);
        indexRequest.opType("create");
        // 设置管道
        indexRequest.setPipeline("pipeline");
        // 同步执行
        IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
        //异步执行
        client.indexAsync(indexRequest, RequestOptions.DEFAULT, new ActionListener<IndexResponse>() {
            @Override
            public void onResponse(IndexResponse indexResponse) {
                // 执行成功的回调
            }

            @Override
            public void onFailure(Exception e) {
                // 执行失败的回调

            }
        });

        String index = response.getIndex();
        String id = response.getId();
        if (DocWriteResponse.Result.CREATED.equals(response.getResult())) {
            // 处理create
        } else if (DocWriteResponse.Result.UPDATED.equals(response.getResult())) {
            //处理update

        }
        ReplicationResponse.ShardInfo shardInfo = response.getShardInfo();
        if (!Objects.equals(shardInfo.getSuccessful(), shardInfo.getTotal())) {
            // 处理成功数小于总数
        }

        if (shardInfo.getFailed() > 0) {
            for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
                // 获取失败信息
                String reason = failure.reason();
            }
        }
    }

    public void methodIndex5() {
        IndexRequest request = new IndexRequest("posts")
                .id("1")
                .source("field", "value")
                // 处理版本问题
                .setIfSeqNo(10L)
                .setIfPrimaryTerm(20);
    }

```
##### GetRequest

```java
  public void methodGet1() throws IOException {
        GetRequest request = new GetRequest("posts", "1");
        //禁用_source
        request.fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);

        // 包含字段
        String[] includes = {"message", "*Date"};
        // 不包含字段
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        request.fetchSourceContext(fetchSourceContext);

        //需要返回的字段,默认所有_source
        request.storedFields("message");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        Object message = response.getField("message").getValue();


        // 设置节点偏好
        request.preference();
        // 设置实时标志,默认true
        request.realtime(false);

        String index = response.getIndex();
        String id = response.getId();
        if (response.isExists()) {
            // 包含文档
            long version = response.getVersion();
            String sourceAsString = response.getSourceAsString();
            Map<String, Object> sourceAsMap = response.getSourceAsMap();
            byte[] sourceAsBytes = response.getSourceAsBytes();
        }
    }

    public void methodExist1() throws IOException {
        GetRequest request = new GetRequest("posts", "1");
        request.fetchSourceContext(new FetchSourceContext(false));
        // 不获取存储的字段
        request.storedFields("_none_");
        // 是否存在
        boolean exists = client.exists(request, RequestOptions.DEFAULT);
    }

```
##### DeleteRequest

```
 public void methodDelete() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("posts", "1");
        DeleteResponse delete = client.delete(deleteRequest, RequestOptions.DEFAULT);
    }
```
##### UpdateRequest

```
 public void methodUpdate() throws IOException {
        UpdateRequest request = new UpdateRequest(
                "posts",
                "1").doc("updated", new Date(), "reason", "daily update");
        Map<String, Object> parameters = Collections.singletonMap("count", 4);
        // 使用脚本
        Script inline = new Script(ScriptType.INLINE, "painless", "ctx.source.field+=params.count", parameters);
        request.script(inline);
        // 脚本创建不存在的文档
        request.scriptedUpsert(true);

        // 设置更新前必须活动的分片副本数
        request.waitForActiveShards(2);
        // 设置提供的分片数
        request.waitForActiveShards(ActiveShardCount.ALL);
        UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
        String index = updateResponse.getIndex();
        String id = updateResponse.getId();
        long version = updateResponse.getVersion();
        if (updateResponse.getResult() == DocWriteResponse.Result.CREATED) {

        } else if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {

        } else if (updateResponse.getResult() == DocWriteResponse.Result.DELETED) {

        } else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
            // 对文档没有处理
        }
    }
```
##### TermVectorsRequest

```
  public void MethodTermVector() throws IOException {
        TermVectorsRequest request = new TermVectorsRequest("authors", "1");
        request.setFields("user");

        XContentBuilder docBuilder = XContentFactory.jsonBuilder();
        docBuilder.startObject().field("user", "guest-user")
                .endObject();
        TermVectorsRequest request1 = new TermVectorsRequest("authors", docBuilder);
        //将字段统计设置为false(默认为true)以忽略文档统计信息。
        request.setFieldStatistics(false);
        //将术语统计设置为true(默认为false)，以显示总术语频率和文档频率。
        request.setTermStatistics(true);
        //将位置设置为false(默认为true)，以忽略位置输出。
        request.setPositions(false);
        //将偏移量设置为false(默认为true)，以忽略偏移量的输出。
        request.setOffsets(false);
        //将有效载荷设置为false(默认为true)，以忽略有效载荷的输出。
        request.setPayloads(false);

        Map<String, Integer> filterSettings = new HashMap<>();
        filterSettings.put("max_num_terms", 3);
        filterSettings.put("min_term_freq", 1);
        filterSettings.put("max_term_freq", 10);
        filterSettings.put("min_doc_freq", 1);
        filterSettings.put("max_doc_freq", 100);
        filterSettings.put("min_word_length", 1);
        filterSettings.put("max_word_length", 10);
        //设置过滤器设置，根据tf-idf分数过滤可返回的术语。
        request.setFilterSettings(filterSettings);
        //将perFieldAnalyzer设置为指定与该字段不同的分析仪。
        Map<String, String> perFieldAnalyzer = new HashMap<>();
        perFieldAnalyzer.put("user", "keyword");
        request.setPerFieldAnalyzer(perFieldAnalyzer);
        //将实时设置为假(默认值为真)以接近实时地检索术语向量。
        request.setRealtime(false);
        //设置路由参数
        request.setRouting("routing");
    }
```
##### BulkRequest

```java
 public void methodBulk() throws IOException {
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest("posts").id("1").source(XContentType.JSON, "field", "foo"));
        request.add(new IndexRequest("posts").id("2").source(XContentType.JSON, "field", "bar"));
        request.add(new IndexRequest("posts").id("3").source(XContentType.JSON, "field", "baz"));
        request.add(new DeleteRequest("posts", "3"));
        request.add(new UpdateRequest("posts", "2").doc(XContentType.JSON, "other", "test"));
        request.add(new IndexRequest("posts").id("4").source(XContentType.JSON, "field", "baz"));

        BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
        // 如果至少有一个操作失败，此方法返回true
        if (bulkResponse.hasFailures()) {

        }
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
            //指示给定操作是否失败
            if (bulkItemResponse.isFailed()) {
                //检索失败操作的失败
                BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
            }
        }
        //迭代所有操作的结果
        for (BulkItemResponse bulkItemResponse : bulkResponse) {
            //检索操作的响应(成功与否)，可以是索引响应、更新响应或删除响应，它们都可以被视为DocWriteResponse实例
            DocWriteResponse itemResponse = bulkItemResponse.getResponse();
            switch (bulkItemResponse.getOpType()) {
                //处理索引操作的响应
                case INDEX:
                    break;
                case CREATE:
                    IndexResponse indexResponse = (IndexResponse) itemResponse;
                    break;
                //处理更新操作的响应
                case UPDATE:
                    UpdateResponse updateResponse = (UpdateResponse) itemResponse;
                    break;
                //处理删除操作的响应
                case DELETE:
                    DeleteResponse deleteResponse = (DeleteResponse) itemResponse;
                    break;
                default:
                    break;
            }
        }
    }
```

##### MultiGetRequest

```java
    public void methodMutilGet() throws IOException {
        MultiGetRequest request = new MultiGetRequest();
        request.add(new MultiGetRequest.Item("index", "example_id")
                //禁用源检索,默认情况下启用
                .fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE)
        );
        request.add(new MultiGetRequest.Item("index", "another_id"));

        String[] includes = {"index", "example_id"};
        String[] excludes = Strings.EMPTY_ARRAY;
        FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
        //为特定字段配置源包含
        request.add(new MultiGetRequest.Item("index", "example_id").fetchSourceContext(fetchSourceContext));


        request.add(new MultiGetRequest.Item("index", "example_id")
                //配置特定存储字段的检索(要求字段在映射中单独存储)
                .storedFields("foo"));
        MultiGetResponse response = client.mget(request, RequestOptions.DEFAULT);
        MultiGetItemResponse item = response.getResponses()[0];
        //检索foo存储字段(要求该字段在映射中单独存储)
        String value = item.getResponse().getField("foo").getValue();


        MultiGetItemResponse firstItem = response.getResponses()[0];
        //getResponse返回GetResponse。
        GetResponse firstGet = firstItem.getResponse();
        String index = firstItem.getIndex();
        String id = firstItem.getId();
        if (firstGet.isExists()) {
            long version = firstGet.getVersion();
            //以字符串形式检索文档
            String sourceAsString = firstGet.getSourceAsString();
            //以Map<String, Object>的形式检索文档
            Map<String, Object> sourceAsMap = firstGet.getSourceAsMap();
            //以 byte[]形式检索文档
            byte[] sourceAsBytes = firstGet.getSourceAsBytes();
        } else {
            //处理找不到文档的情况。请注意，虽然返回的响应有404个状态代码，
            // 但返回的是有效的GetResponse，而不是引发的异常。这种响应不包含任何源文档，其isExists方法返回false。
        }
        //getFailure不是并且包含异常。
        Exception e = firstItem.getFailure().getFailure();
        //  这个异常是一个ElasticsearchException
        ElasticsearchException ee = (ElasticsearchException) e;
        //它的状态为“未找到”。如果不是多重获取，它应该是一个HTTP 404。
        // assertEquals(RestStatus.NOT_FOUND, ee.status());
    }

```
#####  ReindexRequest

```java
 /**
     * ReindexRequest可用于将文档从一个或多个索引复制到目标索引。
     * 它要求在请求之前可能存在或可能不存在的现有源索引和目标索引。
     * Reindex不会尝试设置目标索引。它不会复制源索引的设置。
     * 您应该在运行_reindex操作之前设置目标索引，包括设置映射，分片计数，副本等
     */
    public void methodReindex() throws IOException {
        ReindexRequest request = new ReindexRequest();
        // 添加来源列表
        request.setSourceIndices("source1", "source2");
        // 添加目标索引
        request.setDestIndex("dest");

        //设置versionType为exiternal(外部版本 , 版本号大ok)
        request.setDestVersionType(VersionType.EXTERNAL);
        //创建丢失的文档,已有文档会冲突
        request.setDestOpType("create");
        //冲突计数(默认终止)
        request.setConflicts("proceed");

        // 只复制包含user 和 kimchy字段文档
        request.setSourceQuery(new TermQueryBuilder("user", "kimchy"));
        // 设置文档复制限制10
        request.setMaxDocs(10);
        // 更改批次大小
        request.setSourceBatchSize(100);
        // 指定管道
        request.setDestPipeline("my_pipeline");

        // 使用脚本
        request.setScript(
                new Script(
                        ScriptType.INLINE, "painless",
                        "if (ctx._source.user == 'kimchy') {ctx._source.likes++;}",
                        Collections.emptyMap()));

        BulkByScrollResponse bulkResponse =
                client.reindex(request, RequestOptions.DEFAULT);


        // 使用task api执行
        ReindexRequest reindexRequest = new ReindexRequest();
        reindexRequest.setSourceIndices("sourceIndex");
        reindexRequest.setDestIndex("destinationIndex");
        reindexRequest.setRefresh(true);

        TaskSubmissionResponse reindexSubmission = client
                .submitReindexTask(reindexRequest, RequestOptions.DEFAULT);
        String taskId = reindexSubmission.getTask();

        //获取总时间
        TimeValue timeTaken = bulkResponse.getTook();
        //检查请求是否超时
        boolean timedOut = bulkResponse.isTimedOut();
        //获取处理的文档总数
        long totalDocs = bulkResponse.getTotal();
        //已更新的文档数
        long updatedDocs = bulkResponse.getUpdated();
        //创建的文档数
        long createdDocs = bulkResponse.getCreated();
        //已删除的文档数
        long deletedDocs = bulkResponse.getDeleted();
        //已执行的批次数量
        long batches = bulkResponse.getBatches();
        //跳过的文档数
        long noops = bulkResponse.getNoops();
        //版本冲突的数量
        long versionConflicts = bulkResponse.getVersionConflicts();
        //请求必须重试批量索引操作的次数
        long bulkRetries = bulkResponse.getBulkRetries();
        //请求必须重试搜索操作的次数
        long searchRetries = bulkResponse.getSearchRetries();
        //如果当前处于睡眠状态，此请求限制自身的总时间不包括当前限制时间
        TimeValue throttledMillis = bulkResponse.getStatus().getThrottled();
        //任何当前油门休眠的剩余延迟，或者如果没有休眠，则为0
        TimeValue throttledUntilMillis =
                bulkResponse.getStatus().getThrottledUntil();
        //搜索阶段失败
        List<ScrollableHitSource.SearchFailure> searchFailures =
                bulkResponse.getSearchFailures();
        //批量索引操作期间失败
        List<BulkItemResponse.Failure> bulkFailures =
                bulkResponse.getBulkFailures();
    }
```
##### UpdateByQueryRequest

```java
  /**
     * UpdateByQueryRequest可用于更新索引中的文档。
     * 它需要执行更新的现有索引(或一组索引)。
     * 更新查询的最简单形式如下:
     */
    public void methodUpdateByQuery() throws IOException {
        //在一组索引上创建UpdateByQueryRequest。
        UpdateByQueryRequest request = new UpdateByQueryRequest("source1", "source2");
        //设置版本冲突时继续
        request.setConflicts("proceed");
        //仅复制字段用户设置为kimchy的文档
        request.setQuery(new TermQueryBuilder("user", "kimchy"));
        BulkByScrollResponse bulkResponse =
                client.updateByQuery(request, RequestOptions.DEFAULT);

        //在一组索引上创建DeleteByQueryRequest。
        //可用于从索引中删除文档。它需要一个要执行删除的现有索引(或一组索引)
        // 用法与上面类似
        DeleteByQueryRequest request1 =
                new DeleteByQueryRequest("source1", "source2");
    }

```
##### SearchRequest

```java

    /**
     * SearchRequest用于与搜索文档，聚集，建议有关的任何操作，
     * 并且还提供了在突出显示的文档上请求突出显示的方式。
     */
    public void methodSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        // 请求参数添加到searchSourceBuilder中
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("user", "kimchy"));
        sourceBuilder.from(0);
        sourceBuilder.size(5);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //排序
        sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        sourceBuilder.sort(new FieldSortBuilder("_id").order(SortOrder.ASC));

        /**
         * 要将“建议”添加到搜索请求，请使用“SuggestionBuilder”工厂类中易于访问的“SuggestBuilders ”实现之一。
         */
        SuggestionBuilder termSuggestionBuilder =
                SuggestBuilders.termSuggestion("user").text("kmichy");
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("suggest_user", termSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);

        // 执行
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        // 获取hits
        SearchHits hits = searchResponse.getHits();

        for (SearchHit hit : hits) {
            // do something with the SearchHit
            String sourceAsString = hit.getSourceAsString();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String documentTitle = (String) sourceAsMap.get("title");
            List<Object> users = (List<Object>) sourceAsMap.get("user");
            Map<String, Object> innerObject =
                    (Map<String, Object>) sourceAsMap.get("innerObject");
        }


        /**
         * 获取聚合信息
         */
        Aggregations aggregations = searchResponse.getAggregations();
        Terms byCompanyAggregation = aggregations.get("by_company");
        MultiBucketsAggregation.Bucket elasticBucket = byCompanyAggregation.getBucketByKey("Elastic");
        Avg averageAge = elasticBucket.getAggregations().get("average_age");
        double avg = averageAge.getValue();

        Map<String, Aggregation> aggregationMap = aggregations.getAsMap();
        Terms companyAggregation = (Terms) aggregationMap.get("by_company");
        List<Aggregation> aggregationList = aggregations.asList();
        for (Aggregation agg : aggregationList) {
            String type = agg.getType();
            if (TermsAggregationBuilder.NAME.equals(type)) {
                Terms.Bucket elasticBucket1 = ((Terms) agg).getBucketByKey("Elastic");
                long numberOfDocs = elasticBucket1.getDocCount();
            }
        }

        /**
         * 响应信息状态
         */
        RestStatus status = searchResponse.status();
        TimeValue took = searchResponse.getTook();
        Boolean terminatedEarly = searchResponse.isTerminatedEarly();
        boolean timedOut = searchResponse.isTimedOut();

        /**
         * 分片信息
         */
        int totalShards = searchResponse.getTotalShards();
        int successfulShards = searchResponse.getSuccessfulShards();
        int failedShards = searchResponse.getFailedShards();
        for (ShardSearchFailure failure : searchResponse.getShardFailures()) {
            // failures should be handled here
        }

        /**
         * 获取建议
         */
        Suggest suggest = searchResponse.getSuggest();
        TermSuggestion termSuggestion = suggest.getSuggestion("suggest_user");
        for (TermSuggestion.Entry entry : termSuggestion.getEntries()) {
            for (TermSuggestion.Entry.Option option : entry) {
                String suggestText = option.getText().string();
            }
        }

    }

```
## 参考资料
[Elasticsearch 权威指南(中文版)](https://es.xiaoleilu.com/)
[Java Rest Client7.3](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.3/index.html)
[Elasticsearch文档](https://www.elastic.co/guide/en/elasticsearch/reference/7.4/index.html)
[SpringBoot 集成Elasticsearch官方文档](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#preface)
[elasticsearch7.3 doc](https://static.javadoc.io/org.elasticsearch/elasticsearch/7.3.2/index.html)
[elasticsearch-rest高级客户端7.3](https://artifacts.elastic.co/javadoc/org/elasticsearch/client/elasticsearch-rest-high-level-client/7.3.2/index.html)
[Elasticsearch--建议器](https://www.cnblogs.com/51zone/p/9841009.html)
[ElasticSearch字段类型](https://segmentfault.com/a/1190000016686631)
[elasticsearch7.x clusterAPI之settings](https://blog.csdn.net/asty9000/article/details/100752309)
[Elasticsearch7.X 入门学习第五课笔记---- - Mapping设定介绍](https://blog.csdn.net/qq_36697880/article/details/100660867)
[Elasticsearch 参考指南（如何使用脚本）](https://segmentfault.com/a/1190000016869041)
[Elasticsearch学习之图解Elasticsearch中的_source、_all、store和index属性](https://blog.csdn.net/napoay/article/details/62233031)
[elasticsearch系列六：聚合分析（聚合分析简介、指标聚合、桶聚合）](https://www.cnblogs.com/leeSmall/p/9215909.html)
[SpringBoot整合elasticsearch](https://juejin.im/post/5aec0b386fb9a07abb23784d)
[SpringBoot整合Elasticsearch的Java Rest Client](https://www.jianshu.com/p/0b4f5e41405e)
