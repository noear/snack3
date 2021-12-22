#### 3.2.3


#### 3.2.2
* 增加 @ONodeAttr 注解，取代旧的 @NodeName

#### 3.2.1

* 增加接口 ONode::getRawString()
* 增加接口 ONode::getRawNumber()
* 增加接口 ONode::getRawBoolean()
* 增加接口 ONode::getRawDate()
* 增加接口 Options::getFeatures()

#### 3.2.0

* Constants 更名为 Options，并优化细节

* ONode::get(key) ，不再自动为文档树添加节点；如有需要改用 ONode::getOrNew(key)

* 增加自定义编码与解码支持

```java
import org.noear.snack.core.Options;

import java.util.Date;

public class DemoTest {
    public void test(UserDto user) {
        Options options = Options.def();
        options.addEncoder(Date.class, (data, node)->{
            node.val().setNumber(data.getTimes());
        });
        
        ONode oNode = ONode.loadObj(user, options);
    }
}
```


