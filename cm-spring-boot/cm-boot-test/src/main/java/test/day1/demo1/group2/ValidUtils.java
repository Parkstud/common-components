package test.day1.demo1.group2;


import java.util.Set;

import javax.annotation.Nullable;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

/**
 * 校验器
 *
 * @author parkstud@qq.com 2020-09-15
 */
public class ValidUtils {
    /**
     * 默认结果实现
     */
    @SuppressWarnings("all")
    private static final ValidationResult DEFAULT_PROCESS = new ValidationResult();

    private ValidUtils() {
    }

    /**
     * 校验方法
     *
     * @param validator 校验器
     * @param object    校验对象
     * @param groups    校验组
     * @param <T>       参数泛型
     */
    public static <T> void valid(@Nullable Validator validator, @Nullable ValidationResult<T> process, T object, @Nullable Class<?>... groups) {
        if (validator == null) {
            validator = Validation.buildDefaultValidatorFactory().getValidator();
        }
        if (groups == null) {
            groups = new Class[0];
        }
        Set<ConstraintViolation<T>> result = validator.validate(object, groups);
        if (process == null) {
            process = DEFAULT_PROCESS;
        }
        process.process(result);


    }
}
