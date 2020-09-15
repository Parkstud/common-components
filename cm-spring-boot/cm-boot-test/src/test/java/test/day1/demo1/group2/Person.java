package test.day1.demo1.group2;

import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author miao.chen01@hand-china.com 2020-07-31
 */

public class Person implements Cloneable {
    private String name = "陈苗";
    private Date birthday = new Date();
    private BigDecimal money = BigDecimal.ZERO;
    private List<String> love = new ArrayList<>();
    private List<Work> works;

    public Person() {
        works = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            works.add(new Work());
        }
    }

    @Override
    protected Person clone() throws CloneNotSupportedException {
        Person person = (Person) super.clone();
        List<Work> myWorks = new ArrayList<>(16);
        try {
            for (Work work : works) {
                myWorks.add(work.clone());
            }
        } catch (CloneNotSupportedException e) {
        }
        person.works = myWorks;
        return person;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public List<String> getLove() {
        return love;
    }

    public void setLove(List<String> love) {
        this.love = love;
    }
}

class Work implements Cloneable {
    String title = "哈哈";
    Date time = new Date();


    @Override
    protected Work clone() throws CloneNotSupportedException {
        return (Work) super.clone();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
