package test.day1.demo1.group2;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;

import lombok.Data;

/**
 * 校验结果
 *
 * @author parkstud@qq.com 2020-09-15
 */
@Data
public class ValidationResult<T> {
    /**
     * 校验是否错误
     */
    private Boolean error;

    /**
     * 错误数据
     */
    private Set<ConstraintViolation<T>> resultSet;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 处理校验
     *
     * @param resultSet 校验参数
     */
    public void process(Set<ConstraintViolation<T>> resultSet) {
        if (CollectionUtils.isEmpty(resultSet)) {
            this.error = Boolean.FALSE;
            return;
        }
        this.error = Boolean.TRUE;
        this.errorMsg = resultSet.stream().map(item -> item.getPropertyPath() + " " + item.getMessage()).collect(Collectors.toList()).toString();

    }
}
