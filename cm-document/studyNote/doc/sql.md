---
author: 陈苗
time: 2019/12/25
email: parkstud@qq.com
---

## select中返回boolean

```xml
 <select id="isLeafCategory" resultType="java.lang.Boolean">
        select case when count(path.category_tree_id) = 1 then 1 else 0 end
        from o2pcm_category_tree path
        where path.ancestor_category_id = #{categoryId}
 </select>
```

