package test.day1.demo1.group2;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;

import lombok.Data;

/**
 * @author parkstud@qq.com 2020-09-15
 */
@Data
public class Student {
    @NotBlank(groups = {Validate1.class})
    private String name;
    @Max(value = 200, groups = {Validate1.class})
    private Integer age;
    @Past(groups = {Validate1.class})
    private Date birthday;
    @DecimalMin(value = "0.00", groups = {Validate2.class})
    private BigDecimal price;
    @Email(groups = {Validate2.class})
    private String email;
    @Length(max = 20, groups = {Validate1.class, Validate2.class})
    private String phone;
    @Range(max = 150)
    private Integer score;


    interface Validate1 {

    }

    interface Validate2 {

    }

}
