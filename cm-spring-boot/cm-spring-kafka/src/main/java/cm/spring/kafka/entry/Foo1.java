package cm.spring.kafka.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author parkstud@qq.com 2020-05-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Foo1 {
    private String foo;
    @Override
    public String toString() {
        return "Foo1 [foo=" + this.foo + "]";
    }
}
