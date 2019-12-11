# Snack3 一个新的微型JSON框架
一个作品，一般表达作者的一个想法。因为大家想法不同，所有作品会有区别。就做技术而言，因为有很多有区别的框架，所以大家可以选择的框架很丰富。



snack3。基于`jdk8`，`60kb`，无其它依赖，非常小巧。
* 强调文档树的链式操控和构建能力
* 强调中间媒体，方便不同格式互转
* 支持`序列化、反序列化`
* 支持`Json path`查询

ONode 即 `One node` 之意；借签了 `Javascript` 所有变量由 `var` 申明，及 `Xml dom` 一切都是 `Node` 的设计。这应该也是与别的框架不同之处。



#### 设计思路
* `ONode`是一个接口界面，内部由`nodeType`和`nodeData`组成。通过`nodeType`进行类型识别，通过`nodeData`容纳所有的类型数据。

* 采用`Fromer`->`ONode`->`Toer`的架构设计。这是重要的一个设计，可使转换具有很强的扩展能力，`Fromer` 和 `Toer` 各做一半工作，也可各自切换（有好处也会有坏处）。

* 强调非null操作，通过o.isNull()进行判断（留意后面`-get(key | idx)`接口的注释；可通过`getOrNull(key | idx)` 获取 `null` ）

  

#### 项目源码
* github：[https://github.com/noear/snack3](https://github.com/noear/snack3)
* gitee：[https://gitee.com/noear/snack3](https://gitee.com/noear/snack3)



#### Meven配置

```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack3</artifactId>
  <version>3.1.5.12</version>
</dependency>
```



#### 一、简单的代码演示

* 简单操控
```java
//1.加载json
// name支持没有引号
// 字符串支持单引号（在java里写代码方便点；输出是标准的引号）
ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5]}}");

//2.1.取一个属性的值
String msg = n.get("msg").getString();

//2.2.取列表里的一项
int li2  = n.get("data").get("list").get(2).getInt();
int li2  = n.select("data.list[2]").getInt(); //使用josn path 

//2.3.获取一个数组
List<Integer> ary = n.get("data").get("list").toObject(ArrayList.class);
List<Integer> ary = n.select("data.list").toObject(ArrayList.class); //使用josn path 

//3.1.设置值
n.set("msg","Hello world2");
n.get("msg").val("Hello world2"); //另一种设置方式

//3.2.设置列表里的一项值为12
n.get("data").get("list").get(2).val(12);
n.select("data.list[2]").val(12); //使用josn path 

//3.3.清掉一个数组
n.get("data").get("list").clear();
n.select("data.list").clear(); //使用josn path 
```



#### 二、更多代码演示

* 1.字符串化
```java
//将 java object 转为 json
String json = ONode.stringify(user);
```
* 2.序列化
```java
//1.序列化（通过@type申明类型）
String json = ONode.serialize(user);

//2.反序列化
UserModel user = ONode.deserialize(json, UserModel.class);
```
* 3.转换
```java
//1.1.将json string 转为 ONode
ONode o = ONode.load(json); 

//1.2.将java bean 转为 ONode
ONode o = ONode.load(user); 

//2.1.将 ONode 转为 string（由转换器决定，默认转换器为json）
o.toString();

//2.2.将 ONode 转为 json
o.toJson();

//2.3.将 ONode 转为XxxModel (例：UserModel)
o.toObject(UserModel.class);

//>将 ONode 转为 Map 或 List 或 常规值
o.toObject(null);

//>将 ONode 转为 自动类型的 Java Object
o.toObject(Object.class);

```
* 4.填充
```java
ONode o = ONode.load("{code:1,msg:'Hello world!'}");

//填充java object
List<UserModel> list = new ArrayList<>();
//...
o.get("data").get("list").fill(list);

//填充string json
o.get("data").get("list").fill("[{a:1,b:2},{a:2,c:3}]");
```

* 5.一个应用示例（极光推送的rest api调用）
```java
public static void push(Collection<String> alias_ary, String text)  {
    ONode data = new ONode().build((d)->{
        d.get("platform").val("all");

        d.get("audience").get("alias").addAll(alias_ary);

        d.get("options")
                .set("apns_production",false);

        d.get("notification").build(n->{
            n.get("ios")
                    .set("alert",text)
                    .set("badge",0)
                    .set("sound","happy");
        });
    });

    String message = data.toJson();
    String author = Base64Util.encode(appKey+":"+masterSecret);

    Map<String,String> headers = new HashMap<>();
    headers.put("Content-Type","application/json");
    headers.put("Authorization","Basic "+author);

    HttpUtil.postString(apiUrl, message, headers);
}
```
* 5.数据遍历
```java
//遍历Object数据 方案1
o.forEach((k,v)->{
  //...
});
//遍历Object数据 方案2
for(Map.Entry<String,ONode> kv : n.obj().entrySet()){
  //...    
}

//遍历Array数据 方案1
o.forEach((v)->{
  //...
});
//遍历Array数据 方案2
for(ONode v : o.ary()){
  //..
}
```



#### 三、混合加载与转换代码演示

```java
List<UserModel> list = new ArrayList<>();
//..
ONode o = ONode.load("{code:1,msg:'succeed'}");
o.get("data").get("list").fill(list);

```



#### 关于三组类型接口的设计（Json  object,array,value）

* 关于`json object`的操作
```swift
-obj() -> Map<String,ONode>             //获取节点对象数据结构体（如果不是对象类型，会自动转换）
-contains(key:String) -> bool           //是否存在对象子节点?
-get(key:String) -> child:ONode         //获取对象子节点（不存在，生成新的子节点并返回）
-getOrNull(key:String) -> child:ONode   //获取对象子节点（不存在，返回null）
-getNew(key:String) -> child:ONode      //生成新的对象子节点，会清除之前的数据
-set(key:String,val:Object) -> self:ONode           //设置对象的子节点（会自动处理类型）//val:为常规类型或ONode
-setNode(key:String,val:ONode) -> self:ONode        //设置对象的子节点，值为ONode类型
-setAll(obj:ONode) -> self:ONode                    //设置对象的子节点，将obj的子节点搬过来
-setAll(map:Map<String,T>) ->self:ONode             //设置对象的子节点，将map的成员搬过来
-setAll(map:Map<String,T>, (n,t)->..) ->self:ONode  //设置对象的子节点，将map的成员搬过来，并交由代理处置
-remove(key:String)     //移除对象的子节点
-forEach((k,v)->..)     //遍历对象的子节点
```

* 关于`json array`的操作
```swift
-ary() -> List<ONode>                   //获取节点数组数据结构体（如果不是数组，会自动转换）
-get(index:int)  -> child:ONode                 //获取数组子节点（超界，返回空节点）
-getOrNull(index:int)  -> child:ONode           //获取数组子节点（超界，返回null）
-addNew() -> child:ONode                        //生成新的数组子节点
-add(val) -> self:ONode                         //添加数组子节点 //val:为常规类型或ONode
-addNode(val:ONode) -> self:ONode               //添加数组子节点，值为ONode类型
-addAll(ary:ONode)  -> self:ONode               //添加数组子节点，将ary的子节点搬过来
-addAll(ary:Collection<T>) -> self:ONode                //添加数组子节点，将ary的成员点搬过来
-addAll(ary:Collection<T>,(n,t)->..) -> self:ONode      //添加数组子节点，将ary的成员点搬过来，并交由代理处置
-removeAt(index:int)    //移除数组的子节点
-forEach(v->..)         //遍历数组的子节点
```

* 关于`json value`的操作
```swift
-val() -> OValue                //获取节点值数据结构体（如果不是值类型，会自动转换）
-val(val:Object) -> self:ONode  //设置节点值 //val:为常规类型或ONode
-getString()    //获取值并以string输出 //如果节点为对象或数组，则输出json
-getShort()     //获取值并以short输出...(以下同...)
-getInt()
-getBoolean()
-getLong()
-getDate()
-getFloat()
-getDouble()
-getDouble(scale:int)
-getChar()
```



#### 关于序列化的特点

* 对象
```json
{"@type":"...","a":1,"b":"2"}
```
* 数组（可以精准反序列化列表类型；需要特性开启）
```json
[{"@type":"..."},[1,2,3]]  
```

