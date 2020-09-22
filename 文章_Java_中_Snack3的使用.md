# Java 中 Snack3的使用
> 网上看了一篇Java 中 Gson的使用，所以也跟着写篇Java 中 Snack3的使用

JSON 是一种文本形式的数据交换格式，从Ajax的时候开始流行，它比XML更轻量、比二进制容易阅读和编写；解析和生成的方式很多，Java中最常用的类库有：JSON-Java、Gson、Jackson、FastJson、Snack3等。

Snack3 基于jdk8，60kb大小，非常小巧。

```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack3</artifactId>
  <version>3.1.11</version>
</dependency>
```

Snack3 借鉴了 `Javascript` 所有变量由 `var` 申明，及 `Xml dom` 一切都是 `Node` 的设计。其下一切数据都以`ONode`表示，`ONode`也即 `One node` 之意，代表任何类型，也可以转换为任何类型。

- 强调文档树的操控和构建能力
- 做为中间媒体，方便不同格式互转
- 高性能`Json path`查询（兼容性和性能很赞）
- 支持`序列化、反序列化`

### 一、Snack3的基本用法

`Snack3`提供了几个快捷函数：

* `load(strOrObj)`, `loadStr(str)`, `loadObj(obj)` 用于解析和加载；
* `stringify(obj)`, `serialize(obj)`, `deserialize(str,clz)` 用于序列化和反序列化

#### （1）基本数据类型的解析
```java
int i = ONode.load("100").getInt(); //100
double d = ONode.load("\"99.99\"").getDouble();  //99.99
boolean b = ONode.load("true").getBoolean();     // true
String str = ONode.load("String").getString();   // String
```

#### （2）基本数据类型的生成　
```java
String jsonNumber = ONode.load(100).toJson();       // 100
String jsonBoolean = ONode.load(false).toJson();    // false
String jsonString = ONode.load("String").toString(); //"String"
```

#### （3）POJO类的生成与解析
```java
public class User {
    public String name;
    public int age;
    public String emailAddress;
}
```

###### 生成JSON：　
```java
User user = new User("张三",24);

//输出： {"name":"张三","age":24}
String json = ONode.stringify(user); //JSON字符化

//输出： {"@type":"demo.User","name":"\u5F20\u4E09","age":24}
String json2 = ONode.serialize(user); //JSON序列化
```

###### 解析并反序列化JSON：　
```java
String json = "{name:'张三',age:24}";
User user = ONode.deserialize(json, User.class);//JSON反序列化
```

### 二、序列化事项补充说明

从上面示例可以看出json的字段和值是的名称和类型是一一对应的，Snack3不支持直接改名称，但可以通过`transient`关键字进行排序，例：
```java
public class User {
    public String name;
    public int age;
    public String emailAddress;
    public transient Date date;
}
```

或者，加载后再重改名称，例：

```java
User user = new User("name", 12, "xxx@mail.cn");

//输出： {"name":"name","age":12,"email":"xxx@mail.cn"}
ONode.load(user).rename("emailAddress","email").toJson();

//o.rename(key,newKey); //重命名子节点，并返回自己
```

Snack3在序列化和反序列化时需要使用反射，且只对字段进行序列化（不管属性）。

###### 特性总结：

* 只对字段进行序列化（包括私有）
* 使用`transient` 对字段排序
* 加载后可进行重命名字段


### 三、Snack3中使用泛型

例如：JSON字符串数组：`["Android","Java","PHP"]`

当要通过Snack3解析这个json时，一般有三种方式：使用ONode，使用数组，使用List；而List对于增删都是比较方便的，所以实际使用是还是List比较多

数组比较简单：
```java
String jsonArray = "[\"Android\",\"Java\",\"PHP\"]";
String[] strings = ONode.deserialize(jsonArray,String[].class);
```

对于List将上面的代码中的 String[].class 直接改为 `List<String>.class` 是不行的，对于Java来说`List<String>` 和`List<User>` 这俩个的字节码文件只一个那就是`List.class`，这是Java泛型使用时要注意的问题 泛型擦除。

为了解决的上面的问题，Snack3提供了2种方式来实现对泛型的支持，所以将以上的数据解析为`List<String>`时需要这样写：

```java
String jsonArray = "[\"Android\",\"Java\",\"PHP\"]";
ONode ary0 			  = ONode.load(jsonArray);
List<String> ary1 = ONode.deserialize(jsonArray,(new ArrayList<String>(){}).getClass());
List<String> ary2 = ONode.deserialize(jsonArray,(new TypeRef<List<String>>(){}).getClass());

//(new ArrayList<String>(){}).getClass() 			//方式1，通过临时类形（最终都是产生Class）
//(new TypeRef<List<String>>(){}).getClass() 	//方式2，通过TypeRef（最终都是产生Class）
```

泛型的引入还可以减少无关的代码：　
```json　
{"code":"0","message":"success","data":{}}
{"code":"0","message":"success","data":[]}
```

像上面这段代码，code只使用一次，message则几乎不用；我们真正需要的data所包含的数据，但它可能对象也可能是数组。如果Snack3不支持泛型或不知道Snack3支持泛型的同学一定会这么定义POJO：

```java
public class UserResponse {
    public int code;
    public String message;
    public User data;
}
```

当设计其它接口的时候又重新定义一个XxxResponse将data的类型改成Xxx，很明显code，和message被重复定义了多次，会产大量的POJO。。。通过泛型可以将code和message字段抽取到一个Result的类中，这样只需要编写data字段所对应的POJO即可：
```java
public class Result<T> {
    public int code;
    public String message;
    public T data;
} 
```

### 四、Snack3的序列化与反序列化

####（1）自动方式
Snack3提供了`serialize(obj)`和`deserialize(str,clz)` 前者实现序列化，后者实现了反序列化。
```java
ONode.serialize(obj);       //序列化
ONode.deserialize(str,clz); //反序列化
```

#### (2)手动方式：
手动的方式使用`load(obj)`,`toObject(clz)`,`toJson()` 来手动实现序列化和反序列化：
```java
String json = "{\"name\":\"张三\",\"age\":\"24\"}";

//反序列化
User user = ONode.load(json,Constants.serialize()).toObject(User.class);

//序列化
ONode.load(user,Constants.serialize()).toJson();
```

自动方式最终都是通过`load(obj)`,`toObject(clz)`,`toJson()` 进行操作。

内部代码：

```java
/**
 * 序列化为 string（由序列化器决定格式）
 *
 * @param source java object
 * @throws Exception
 */
public static String serialize(Object source) {
    //加载java object，须指定Fromer
    return load(source, Constants.serialize(), DEFAULTS.DEF_OBJECT_FROMER).toJson();
}


/**
 * 反序列化为 java object（由返序列化器决定格式）
 *
 * @param source string
 * @throws Exception
 */
public static <T> T deserialize(String source, Class<?> clz) {
    //加载String，不需指定Fromer
    return load(source,  Constants.serialize(), null).toObject(clz);
}
```

### 五、使用Snack3导出null值、格式化输出、日期时间
一般情况下ONode类提供的 API已经能满足大部分的使用场景，但有时需要更多特殊、强大的功能时，这时候就引入一个新的类 Constants。

Constants从名字上看它是一个提供配置的类，要想改变ONode默认的设置必须使用该类进行配置。用法：　
```java
Constants.of(..)                      //全新定义一份配置
Constants.def().add(..).sub(..)       //在默认配置基础上，添加或减少特性
Constants.serialize().add(..).sub(..) //在序列化配置基础上，添加或减少特性
```

#### （1）Snack3在默认情况下是不动导出值null的键的，如：
```java
User user = new User("张三", 24);
System.out.println(ONode.stringify(user)); //{"name":"张三","age":24}


Constants cfg = Constants.def().add(Feature.SerializeNulls); //导出null
System.out.println(ONode.load(user, cfg).toJson()); //{"name":"张三","age":24,"emailAddress":null}
```

#### （2）格式化输出、日期时间及其它：
```java
Date date = new Date();

Constants cfg = Constants.of(Feature.WriteDateUseFormat) //使用格式化特性
        .build(c-> c.date_format = new SimpleDateFormat("yyyy-MM-dd",c.locale)); //设置格式符（默认为："yyyy-MM-dd'T'HH:mm:ss"）

System.out.println(ONode.load(date, cfg).toJson()); //2019-12-06
```

### 六、使用Snack3进行JSONPath查询
在网上找了一份经典的JSON样本：
```json
{
    "store": {
        "bicycle": {
            "color": "red",
            "price": 19.95
        },
        "book": [
            {
                "author": "刘慈欣",
                "price": 8.95,
                "category": "科幻",
                "title": "三体"
            },
            {
                "author": "itguang",
                "price": 12.99,
                "category": "编程语言",
                "title": "go语言实战"
            }
        ]
    }
}
```
Snack3可以提供高速的JSONPath查询，JSONPath更给日常的查询节省了大量代码：
```java
ONode o = ONode.load(jsonStr);

//得到所有的书
ONode books = o.select("$.store.book");
System.out.println("books=::" + books);

//得到所有的书名
ONode titles = o.select("$.store.book.title");
System.out.println("titles=::" + titles);

//第一本书title
ONode title = o.select("$.store.book[0].title");
System.out.println("title=::" + title);

//price大于10元的book
ONode list = o.select("$.store.book[?(price > 10)]");
System.out.println("price大于10元的book=::" + list);

//price大于10元的title
ONode list2 = o.select("$.store.book[?(price > 10)].title");
System.out.println("price大于10元的title=::" + list2);

//category(类别)为科幻的book
ONode list3 = o.select("$.store.book[?(category == '科幻')]");
System.out.println("category(类别)为科幻的book=::" + list3);


//bicycle的所有属性值
ONode values = o.select("$.store.bicycle.*");
System.out.println("bicycle的所有属性值=::" + values);


//bicycle的color和price属性值
ONode read = o.select("$.store.bicycle['color','price']");
System.out.println("bicycle的color和price属性值=::" + read);
```

#### （1）支持的JSONPath语法

* 字符串使用单引号，例：\['name']
* 过滤操作用空隔号隔开，例：\[?(@.type == 1)]

| 支持操作                  | 说明                                                 |
| ------------------------- | ---------------------------------------------------- |
| `$`                       | 表示根元素                                           |
| `@`                       | 当前节点（做为过滤表达式的谓词使用）                 |
| `*`                       | 通用配配符，可以表示一个名字或数字。                 |
| `..`                      | 深层扫描。 可以理解为递归搜索。                      |
| `.<name>`                 | 表示一个子节点                                       |
| `['<name>' (, '<name>')]` | 表示一个或多个子节点                                 |
| `[<number> (, <number>)]` | 表示一个或多个数组下标（负号为倒数）                 |
| `[start:end]`             | 数组片段，区间为\[start,end),不包含end（负号为倒数） |
| `[?(<expression>)]`       | 过滤表达式。 表达式结果必须是一个布尔值。            |

| 支持过滤操作符 | 说明                                     |
| -------------- | ---------------------------------------- |
| `==`           | left等于right（注意1不等于'1'）          |
| `!=`           | 不等于                                   |
| `<`            | 小于                                     |
| `<=`           | 小于等于                                 |
| `>`            | 大于                                     |
| `>=`           | 大于等于                                 |
| `=~`           | 匹配正则表达式[?(@.name =~ /foo.*?/i)]   |
| `in`           | 左边存在于右边 [?(@.size in ['S', 'M'])] |
| `nin`          | 左边不存在于右边                         |

| 支持尾部函数 | 说明                           |
| ------------ | ------------------------------ |
| `min()`      | 计算数字数组的最小值           |
| `max()`      | 计算数字数组的最大值           |
| `avg()`      | 计算数字数组的平均值           |
| `sum()`      | 计算数字数组的汇总值（新加的） |

像这两种写法的语义是差不多：


```java 
$.store.book[0].title //建议使用这种
```
```java
$['store']['book'][0]['title']
```

#### （2）语法示例说明

| JSONPath | 说明                           |
| ------------ | ------------------------------ |
| `$`      | 根对象           |
| `$[-1]`      | 最后元素           |
| `$[:-2]`      | 第0个至倒数第2个           |
| `$[1:]`      | 第1个之后所有元素（0为首个） |
| `$[1,2,3]`      | 集合中1,2,3个元素（0为首个） |

### 七、数据格式互转

Snack3是采用`(Fromer)`->`(ONode)`->`(Toer)`的架构。非常适合格式的转换，开发时只需要完成与ONode的对接即可：

#### （1）将Xml转为Ymal
```java
String xml = "<xml>....</xml>";
XmlFromer xmlFromer = new XmlFromer();
YmalToer  ymalToer  = new YmalToer();

//加载xml，输出ymal
String ymal = ONode.load(xml,Constants.def(),xmlFromer).to(ymalToer);
```

#### （2）加载Xml，去掉手机号，转为java object
```java
ONode tmp =ONode.load(xml,Constants.def(),xmlFromer);

//找到有手机号的，然后移除手机号
tmp.select("$..[?(@.mobile)]").forEach(n->n.remove("mobile"));

XxxModel m =tmp.toObject(XxxModel.class);
```
