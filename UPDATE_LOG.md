#### 3.2.83
* 解决 `$..[?(@.treePath)]` 表达式的兼容问题

#### 3.2.82
* 增加特性：Feature.DisableClassNameRead


#### 3.2.81
* 优化 recordable 识别，避免构造函数可能索引超界的问题

#### 3.2.80
* 优化枚举序列化处理

#### 3.2.78
* 修复根值为 string 时，没有做编码处理（偷懒了）

#### 3.2.78
* 冲掉 3.2.77 版本

#### 3.2.76

* 字段访问权限，移到执行时再请求
* 反序列化时大写的数字，当为 '' 时则为 null

#### 3.2.75

* 增加 jsonpath 对 keys(), length(), size() 函数的支持

#### 3.2.74

* 优化  结构型枚举自定义字段序列化处理

#### 3.2.73

* 增加 结构型枚举自定义字段序列化支持

#### 3.2.72
* 修复 bindTo(obj) 可能会返回 null 的问题

#### 3.2.71
* 优化 `$.[?(@.field == 'l1-field-1')].children[?(@.field == 'l2-field-1')]` 兼容性
* 优化 `$[?(@.field == 'l1-field-1')].children[?(@.field == 'l2-field-1')].fields[*]` 兼容性

#### 3.2.69
* 优化 `$.*.*.*` 的兼容性

#### 3.2.68
* 修复 `$..*[?(@.id)]` 可能出现 value 项

#### 3.2.67
* 增加 java record 的参数与字段去重处理

#### 3.2.66
* 增加 Charset 反序化支持

#### 3.2.65
* 增加 File 反序化支持

#### 3.2.64
* 增加泛型多层嵌套支持: List<List<Long>>、Map<String,List<Long>>

#### 3.2.63
* NodeAttr 增加时区支持

#### 3.2.62
* 修复 {names:null} ，转为 A{names:List} 时, 会变成 A{names:List=[null]}

#### 3.2.61
* 修复非静态成员类序列化时会死循环的问题

#### 3.2.60
* 添加特性，解析时可让所有整型为长整型

#### 3.2.59
* 添加漂亮格式化特性支持

#### 3.2.57
* 增加 selectOrNew() 接口;//只支持简单索引查询时orNew，不支持条件表达式和函数

#### 3.2.56
* 增加 ClassLoader 自动指定能力
* 增加 @NodeAttr 对 LocalTime,LocalDate,LocalDateTime 有效

#### 3.2.55
* 增加指定 ClassLoader 支持

#### 3.2.54
* 增加 kotlin data 类支持

#### 3.2.53
* 增加 getOrNew(int index, ONodeType newNodeType) 接口
* 增加 getOrNew(String key, ONodeType newNodeType) 接口

#### 3.2.52
* 增加 SerializeNulls 对 Map/nullValue 的控制（之前默认认输出）
* 增加 SerializeMapNullValues 特性

#### 3.2.51
* 增加特性 BooleanNullAsFalse, NumberNullAsZero, ArrayNullAsEmpty
* 默认 features_def，不再包括 StringNullAsEmpty（旧的作用，也就只有 OValue::getString 上）

#### 3.2.50
* ONode 在分析 date str 时，增加 trim() 处理

#### 3.2.49
* 增加新特性 StringDoubleToDecimal （可保持小数不变位数）

#### 3.2.47
* 修复反序列化时空字符转类的异常情况

#### 3.2.46
* rename 时，相同名字则不处理

#### 3.2.45
* 增加 string 转 int 的支持（之前只转 long）!!!
* 增加 任何数字可 转 date 的支持

#### 3.2.44 (2022-10-12)
* 增加特性 StringJsonToNode 对 ONode.loadObj() 的支持 

#### 3.2.43
* 修复反序列化时，只读保合不能被赋值的问题

#### 3.2.42
* 添加 UUID 输出时，自动转为字符串格式

#### 3.2.40
* 修复实现接口的枚举无法被识别为枚举的问题

#### 3.2.39
* 当类型为 虚拟类 时，支持将 string 自动转换为 object

#### 3.2.38
* 修复 jsonpath 出现 ._ 时，会出错的问题

#### 3.2.37
* 增加 ONode::isUndefined() 接口
* 增加 ONode::exists(jpath)接口
* 调整 当有 key 时，其 null 值统一为 valueType；用于区别 isUndefined

#### 3.2.35
* 修复 值为""时，转为 LocalDateTime 会出错的问题

#### 3.2.34
* 增加 val 可自动转为集合的一部分（如果接收的是集合字段）


#### 3.2.33
* 修复 name 特定情况下会出现空隔的问题

#### 3.2.32
* 增加 name 值的格式控制
* 调整 \\ 的解析方式

#### 3.2.31
* 增加 LongAdder，DoubleAdder 反序列化支持

#### 3.2.30
* 增加 jsonpath 内的选项传导

#### 3.2.29
* 增加对 Properties 数组的转换支持

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

#### 3.2.3 (2021-12-23)

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
2. 完全改造为基于 无参构函数 和 字段的操作。避免因数据注入而诱发恶意动作。

#### 3.1.15 
1. 修改OValue对数字的存储统一为Number

#### 3.1.11 (2020-11-10)
1. 增加 toObjectList接口新用法
2. 优化 jsonpath 处理

#### 3.1.2.1
1. 完善fill机制
2. 序列化通过过临时类，支持泛型
3. 新增：toObject()；
4. 置为过期：toBean()toData();

#### 3.1.2 (2019-11-22)
1. 添加json path


