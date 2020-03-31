package org.cm.boot.starter.util;

import com.google.common.base.Joiner;

import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;
import org.springframework.objenesis.ObjenesisStd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * BeanCopier 属性拷贝封装
 *
 * @author parkstud@qq.com 2020-03-31
 */
public abstract class BaseBeanCopierUtil {
    /**
     * 缓存BeanCopier
     */
    private static final Map<String, BeanCopier> BEAN_COPIER_CACHE = new ConcurrentHashMap<>();

    private BaseBeanCopierUtil() {
    }


    /**
     * 拷贝对象
     *
     * @param source 源对象
     * @param target 目标对象
     * @param <S>    源对象泛型
     * @param <T>    目标对象泛型
     */
    public static <S, T> void copy(S source, T target) {
        copyObject(source, target, null);
    }

    /**
     * 拷贝对象
     *
     * @param source   源对象
     * @param target   目标对象
     * @param consumer 对转化后对象操作
     * @param <S>      源对象泛型
     * @param <T>      目标对象泛型
     */
    public static <S, T> void copy(S source, T target, Consumer<T> consumer) {
        copyObject(source, target, null);
        consumer.accept(target);
    }

    /**
     * 拷贝对象
     *
     * @param source    源对象
     * @param target    目标对象
     * @param converter 转化器,Converter定义的规则去拷贝属性
     * @param <S>       源对象泛型
     * @param <T>       目标对象泛型
     */
    public static <S, T> void copy(S source, T target, Converter converter) {
        copyObject(source, target, converter);
    }

    /**
     * 通过类型拷贝对象
     *
     * @param source      源对象
     * @param targetClass 目标类
     * @param <S>         源对象泛型
     * @param <T>         目标类泛型
     * @return 目标对象
     */
    public static <S, T> T copy(S source, Class<T> targetClass) {
        return copyClass(source, targetClass, null);
    }

    /**
     * 通过类型拷贝对象
     *
     * @param source      源对象
     * @param targetClass 目标类
     * @param converter   转换器
     * @param <S>         源对象泛型
     * @param <T>         目标类泛型
     * @return 目标对象
     */
    public static <S, T> T copy(S source, Class<T> targetClass, Converter converter) {
        return copyClass(source, targetClass, converter);
    }

    /**
     * 拷贝对象集合
     *
     * @param sourceList  源对象集合
     * @param targetClass 目标类
     * @param <S>         源对象泛型
     * @param <T>         目标对象泛型
     * @return 目标对象集合
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass) {
        ArrayList<T> targetList = new ArrayList<>();
        sourceList.forEach(s -> targetList.add(copy(s, targetClass)));
        return targetList;
    }

    /**
     * 拷贝对象集合
     *
     * @param sourceList  源对象集合
     * @param targetClass 目标类
     * @param converter   转换器
     * @param <S>         源对象泛型
     * @param <T>         目标对象泛型
     * @return 目标对象集合
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetClass, Converter converter) {
        ArrayList<T> targetList = new ArrayList<>();
        sourceList.forEach(s -> targetList.add(copy(s, targetClass, converter)));
        return targetList;
    }


    /**
     * 拷贝Class
     *
     * @param source    源对象
     * @param target    目标类
     * @param converter 转换器
     * @param <S>       源对象泛型
     * @param <T>       目标对象泛型
     */
    private static <S, T> void copyObject(S source, T target,
                                          Converter converter) {
        String beanKey = generateKey(source.getClass(), target.getClass(), converter);
        BeanCopier copier;
        if (!BEAN_COPIER_CACHE.containsKey(beanKey)) {
            if (converter == null) {
                copier = BeanCopier.create(source.getClass(), target.getClass(), false);
            } else {
                copier = BeanCopier.create(source.getClass(), target.getClass(), true);
            }
        } else {
            copier = BEAN_COPIER_CACHE.get(beanKey);
        }
        copier.copy(source, target, converter);
    }

    /**
     * 拷贝Class
     *
     * @param source      源对象
     * @param targetClass 目标类
     * @param converter   转换器
     * @param <S>         源对象泛型
     * @param <T>         目标类泛型
     * @return 对象集合
     */
    private static <S, T> T copyClass(S source, Class<T> targetClass,
                                      Converter converter) {
        String beanKey = generateKey(source.getClass(), targetClass, converter);
        BeanCopier copier;
        if (!BEAN_COPIER_CACHE.containsKey(beanKey)) {
            if (converter == null) {
                copier = BeanCopier.create(source.getClass(), targetClass, false);
            } else {
                copier = BeanCopier.create(source.getClass(), targetClass, true);
            }
            BEAN_COPIER_CACHE.put(beanKey, copier);
        } else {
            copier = BEAN_COPIER_CACHE.get(beanKey);
        }

        T targetInstance = new ObjenesisStd().newInstance(targetClass);
        copier.copy(source, targetInstance, converter);
        return targetInstance;
    }

    /**
     * 生成唯一key
     *
     * @param sourceClass 源类
     * @param targetClass 目标类
     * @param converter   转换器
     * @return 唯一字符串
     */
    private static String generateKey(final Class<?> sourceClass, final Class<?> targetClass,
                                      final Converter converter) {
        return Joiner.on(".").skipNulls().join(sourceClass.toString(), targetClass.toString(), converter);
    }

}
