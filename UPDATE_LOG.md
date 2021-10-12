#### 3.2.0

* NodeName 注解更名为 NodeAttr 并增加能力

* Constants 更名为 Options

* 将部分 Options 的公开字段，调整为函数

* 增加自定义编码与解码支持

```java
import org.noear.snack.core.Options;

import java.util.Date;

public class DemoTest {
    public void test(UserDto user) {
        Options options = Options.def();
        options.addEncoder(Date.class, (s,t)->{
            t.val().setNumber(s.times());
        });
        
        ONode oNode = ONode.loadObj(user, options);
    }
}
```


