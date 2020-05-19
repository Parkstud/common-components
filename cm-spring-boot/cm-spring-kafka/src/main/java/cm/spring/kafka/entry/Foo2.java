package cm.spring.kafka.entry;

import lombok.Data;

/**
 * @author parkstud@qq.com 2020-05-18
 */
@Data
public class Foo2 {
    private String foo;
    @Override
    public String toString() {
        return "Foo2 [foo=" + this.foo + "]";
    }
}
