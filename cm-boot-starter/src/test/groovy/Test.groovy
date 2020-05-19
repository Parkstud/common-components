import org.cm.boot.starter.util.BaseBeanCopierUtil
import org.springframework.beans.BeanUtils

import static org.cm.boot.starter.util.BaseSystemUtil.computeRuntime

class User {
    String name;
    Integer age;

    User() {

    }

    User(String name, Integer age) {
        this.name = name
        this.age = age
    }

    String getName() {
        return name
    }

    void setName(String name) {
        this.name = name
    }

    Integer getAge() {
        return age
    }

    void setAge(Integer age) {
        this.age = age
    }
}

def runtime = computeRuntime(() -> {
    for (int i = 0; i < 10; i++) {
        def user = new User("name" + i, i)
        BeanUtils.copyProperties(user, new User())
    }
})
println(runtime)
def runtime1 = computeRuntime(() -> {
    for (int i = 0; i < 10; i++) {
        def user = new User("name" + i, i)
        BaseBeanCopierUtil.copy(user, new User())
    }
})
println(runtime1)
