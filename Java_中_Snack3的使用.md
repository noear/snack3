# Java 中 Snacks3的使用
> 网上看一篇Java 中 Gson的使用，打算也写篇Java 中 Snacks3的使用


JSON 是一种文本形式的数据交换格式，从Ajax的时候开始流行，它比XML更轻量、比二进制容易阅读和编写；解析和生成的方式很多，Java中最常用的类库有：JSON-Java、Gson、Jackson、FastJson等。现在还有个小巧的Snacks3，此次就是关于它的。

### 一、Snack3的基本用法

`Snack3`提供了`load(strOrObj)` `loadStr(str)` `loadObj(obj)` 用于解析和加载；`stringify(obj)` `serialize(obj)` `deserialize(str,clz)` 用于序列化和反序列化

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
String json = ONode.stringify(user); // {"name":"张三","age":24}
```

###### 解析JSON：　
```java
String json = "{name:'张三',age:24}";
User user = ONode.deserialize(json, User.class);
```

### 二、序列化事项说明

从上面POJO的生成与解析可以看出json的字段和值是的名称和类型是一一对应的，Snack3不支持直接改名称，但可以通过`transient`关键字进行排序，例：
```java
public class User {
    public String name;
    public int age;
    public String emailAddress;
    public transient Date date;
}
```

Snack3在序列化和反序列化时需要使用反射，且只对POJO的字段进行序列化，不管属性。

###### 特性总结：

* 只对字段进行序列化（包括私有）
* 使用`transient` 对字段排序


### 三、Snack3中使用泛型

例如：JSON字符串数组：`["Android","Java","PHP"]`

当要通过Snack3解析这个json时，一般有两种方式：使用数组，使用List；而List对于增删都是比较方便的，所以实际使用是还是List比较多

数组比较简单：
```java
String jsonArray = "[\"Android\",\"Java\",\"PHP\"]";
String[] strings = ONode.load(jsonArray).toObject(String[].class);
```

对于List将上面的代码中的 String[].class 直接改为 `List<String>.class` 是不行的，对于Java来说`List<String>` 和`List<User>` 这俩个的字节码文件只一个那就是`List.class`，这是Java泛型使用时要注意的问题 泛型擦除。

为了解决的上面的问题，Snack3提供了2种方式来实现对泛型的支持，所以将以上的数据解析为`List<String>`时需要这样写：

```java
String jsonArray = "[\"Android\",\"Java\",\"PHP\"]";
List<String> ary1 = ONode.load(jsonArray).toObject((new ArrayList<String>(){}).getClass());
List<String> ary2 = ONode.load(jsonArray).toObject((new TypeRef<List<String>>(){}).getClass());

//(new ArrayList<String>(){}).getClass() 			//方式1，通过临时类形（最终都是产生Class）
//(new TypeRef<List<String>>(){}).getClass() 	//方式2，通过TypeRef（最终都是产生Class）
```

泛型解析对接口POJO的设计影响

泛型的引入可以减少无关的代码：　
```json　
{"code":"0","message":"success","data":{}}
{"code":"0","message":"success","data":[]}
```

我们真正需要的data所包含的数据，而code只使用一次，message则几乎不用，如果Snack3不支持泛型或不知道Snack3支持泛型的同学一定会这么定义POJO：
```java
public class UserResponse {
    public int code;
    public String message;
    public User data;
}
```

当其它接口的时候又重新定义一个XxxResponse将data的类型改成Xxx，很明显code，和message被重复定义了多次，通过泛型可以将code和message字段抽取到一个Result的类中，这样只需要编写data字段所对应的POJO即可：
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
User user = ONode.load(json).toObject(User.class);

//序列化
ONode.load(user).toJson();
```

自动方式最终都是通过`load(obj)`,`toObject(clz)`,`toJson()` 进行操作
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

Constants从名上看它是一个提供配置的类，要想改变ONode默认的设置必须使用该类进行配置。用法：　
```java
Constants.of(..)
```