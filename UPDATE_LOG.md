#### 3.2.10
* 增加 List<List<Interge>> 之类的泛型支持

#### 3.2.9
* 增加 toObject() 接口
* 去掉 ParameterizedTypeImpl ，改用自带的

#### 3.2.8
* 增加对 kotlin data class 和 jdk14+ record 的序列化与反序列化支持

#### 3.2.7
* 增加对成员类的反序列化支持

#### 3.2.6
* 增加对只读字段的写入过滤

#### 3.2.5
* 增加更复杂的泛型传导

#### 3.2.3

* Options 取消功能特性，只留配置特性
* 增加接口 ONode::options(ops->...);
* 取消 Act0, Act1, Fun0, Fun4 临时功能接口

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


