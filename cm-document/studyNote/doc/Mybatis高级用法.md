---

author: 陈苗
time: 2019/12/23
email: parkstud@qq.com
---



[TOC]

## Mybatis 符号处理

```xml
<if test="registerDateFrom!=null">
    AND register_date <![CDATA[>]]>= #{registerDateFrom}
</if>
<if test="registerDateTo!=null">
    AND register_date <![CDATA[<]]>= #{registerDateTo}
</if>

create_date_time <![CDATA[ >= ]]> #{startTime} and create_date_time <![CDATA[ <= ]]> #{endTime}
```



## Mybatis遍历

```java
 List<Category> listAllAncestors(@Param("categoryIds") List<Long> categoryIds);
```

```xml
 <select id="listAllAncestors" resultType="org.o2.product.core.domain.entity.Category">
        SELECT DISTINCT
        tree.ancestor_category_id,
        <include refid="BaseColumn">
            <property name="tb" value="pc"/>
        </include>
        FROM
        o2pcm_category pc
        JOIN o2pcm_category_tree tree ON pc.category_id = tree.ancestor_category_id
        WHERE
        tree.descendant_category_id IN
        <foreach collection="categoryIds" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </select>
```



## 简化Mybatis

```xml
<sql id="BaseColumn">
        ${tb}.template_id,
        ${tb}_tl.template_name,
        ${tb}.condition_code,
        ${tb}.action_code,
        ${tb}.object_version_number,
        ${tb}.creation_date,
        ${tb}.created_by,
        ${tb}.last_updated_by,
        ${tb}.last_update_date
    </sql>
<!--  使用 -->
 <include refid="BaseColumn">
         <property name="tb" value="oct"/>
  </include>
```

## 多语言

```xml
 <sql id="MultiLanguage">
        <bind name="__lang" value="@io.choerodon.mybatis.helper.LanguageHelper@language()"/>
        LEFT JOIN ${tb}_tl AS ${as}_tl
        ON ( ${as}_tl.${id} = ${as}.${id}
        AND ${as}_tl.lang = #{__lang}
        )
    </sql>
   <!--  使用 -->
 <include refid="org.o2.mkt.promotion.core.infra.mapper.UtilMapper.MultiLanguage">
        <property name="tb" value="o2mkt_customer_template"/>
        <property name="id" value="template_id"/>
        <property name="as" value="oct"/>
 </include>
    
```

## Like查询

```xml
 <if test="@org.apache.commons.lang3.StringUtils@isNotBlank(conditionCode)">
     <bind name="conditionCodeLike" value="'%' + conditionCode + '%'"/>
     and condition_code like #{conditionCodeLike}
 </if>
```

## 部分修改实体

```java
  
// 按照字段修改数据实体 基础tk Mybatis
masterSkuRepository.updateOptional(masterSku,
                    MasterSku.FIELD_SKU_NAME,
                    MasterSku.FIELD_WEIGHT,
                    MasterSku.FIELD_WEIGHT_UOM,
                    MasterSku.FIELD_VOLUME,
                    MasterSku.FIELD_VOLUME_UOM_ID,
                    MasterSku.FIELD_TAG_PRICE,
                    MasterSku.FIELD_SKU_TYPE_CODE);
```

