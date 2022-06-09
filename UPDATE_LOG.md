#### 3.2.28
* 增加 @ONodeAttr(ignore,incNull)

#### 3.2.26
* 增加特性：Feature.TransferCompatible （传输兼容处理）

#### 3.2.25
* 增加对 isFinal 字段的注入支持

#### 3.2.24
* 调整默认特性下 "\abc" 会输出 "\\abc" 的问题（转到浏览器编码特性下处理）

#### 3.2.22
* 增加与Properties 数组的相互转换能力，排序后再转换

#### 3.2.21
* 当类型为 interface 时，支持将 string 自动转换为 object

#### 3.2.20
* 优化异常处理

#### 3.2.18
* 增加 新特性 UseSetter（即允许使用 setXxx）

#### 3.2.16
* 枚举支持字符大小写

#### 3.2.14
* 增加字符串 "true" 转为 Boolean 

#### 3.2.13
* 增加与Properties 的相互转换能力

#### 3.2.12
* 增新加特性 Feature.DisThreadLocal

#### 3.2.11
* 加回自建 ParameterizedTypeImpl 类，不然在jdk17里麻烦

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

#### 3.1.20::
1. ObjectToer，Map 实例类型改为 LinkedHashMap

#### 3.1.19::
1. 增加类型 SnackException

#### 3.1.18::
1. 增加特性 WriteNumberUseString
2. 增加注解：Node

#### 3.1.17::
1. 改为完全基于字段操作（避免属性注入）

#### 3.1.2.1::
1. 完善fill机制
2. 序列化通过过临时类，支持泛型
3. 新增：toObject()；
4. 置为过期：toBean()toData();

#### 3.1.2::
1. 添加json path


