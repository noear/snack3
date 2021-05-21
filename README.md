[![Maven Central](https://img.shields.io/maven-central/v/org.noear/snack3.svg)](https://mvnrepository.com/search?q=snack3)


` QQ交流群：22200020 `

# Snack3 for java
A miniature JSON + JSONPath framework

Based on JDK8, 70Kb. Support: serialization and deserialization, parsing and transformation, JSON PATH query.

```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack3</artifactId>
  <version>3.1.17</version>
</dependency>
```

Snack3 borrows from 'JavaScript' where all variables are declared by 'var' and 'XML DOM' where everything is' Node '. All data under it is represented by 'ONode'. 'ONode', which means' One node ', represents any type and can be converted to any type。
* Emphasize the ability to manipulate and build the document tree
* As an intermediate medium, it is convenient to transfer between different formats
* High performance 'JSON path' queries (compatibility and performance are good)
* Support for serialization and deserialization
* Implementation based on no-argument constructors + field operations (risk of triggering an action due to injection, no)

## Just a couple of random examples

```java
//demo0::
String json = ONode.stringify(user); 

//demo1::
// -- Output with @ type
String json = ONode.serialize(user); 

//demo2::
// -- json Have already take @ type
UserModel user = ONode.deserialize(json); 
// -- json You can do without @type (CLZ declared)
UserModel user = ONode.deserialize(json, UserModel.class); 
// -- json You can print it generically without @type (the type is known)
List<UserModel> list = ONode.deserialize(json, (new ArrayList<UserModel>(){}).getClass()); 

//demo3::
ONode o = ONode.loadStr(json); //将json String 转为 ONode
ONode o = ONode.loadObj(user); //将java Object 转为 ONode

//demo3.1::
ONode o = ONode.loadStr(json);
UserModel user = o.get("user").toObject(UserModel.class);


//demo4:Building JSON data (REST API calls for Aurora push)
public static void push(Collection<String> alias_ary, String text)  {
    ONode data = new ONode().build((d)->{
        d.getOrNew("platform").val("all");

        d.getOrNew("audience").getOrNew("alias").addAll(alias_ary);

        d.getOrNew("options")
                .set("apns_production",false);

        d.getOrNew("notification").build(n->{
            n.getOrNew("ios")
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

//demo5:
o.get("name").getString();
o.get("num").getInt();
o.get("list").get(0).get("lev").getInt();

//demo5.1::
UserModel user = o.get("user").toObject(UserModel.class); //取user节点，并转为UserModel

//demo5.2::
o.get("list2").fill("[1,2,3,4,5,5,6]");


//demo6::json path //If the number is not certain, the array type is returned
//
o.select("$..mobile[?(@ =~ /^187/)]").forEach(n->n.val("186")).toJson();
//
o.select("$.data.list[1].mobile").getLong();

//
List<String> list = o.select("$..mobile").toObject(List.class);

List<String> list = o.select("$.data.list[*].mobile").toObject(List.class);
//
List<UserModel> list = o.select("$.data.list[?(@.mobile =~ /^187/)]")
                        .toObjectList(UserModel.class);
//
List<UserModel> list = o.select("$.data.list[?(@.mobile =~ /^187/)]")
                        .toObjectList(UserModel.class);


//demo7:traverse
//If it's an Object
o.forEach((k,v)->{
  //...
});
//If it's an Array
o.forEach((v)->{
  //...
});

//demo8:Transfers between
String xml = "<xml>....</xml>";
XmlFromer xmlFromer = new XmlFromer();
YmalToer  ymalToer  = new YmalToer();

//Load the XML and print YMAL
String ymal = ONode.load(xml,Constants.def(),xmlFromer).to(ymalToer);

//Load the XML, remove the phone number, and turn it into a Java Object
ONode tmp =ONode.load(xml,Constants.def(),xmlFromer);
tmp.select("$..[?(@.mobile)]").forEach(n->n.remove("mobile"));
XxxModel m =tmp.toObject(XxxModel.class);
```

## Features about serialization
#### Object (with type possible)
```json
{"a":1,"b":"2"}
//or
{"@type":"...","a":1,"b":"2"}
```
#### Array
```json
[1,2,3]
//fo
[{"@type":"...","a":1,"b":"2"},{"@type":"...","a":2,"b":"10"}]
```

## About JSON Path support
* String using single quotes, for example：\['name']
* Filtering operations are separated by a space mark, for example：\[?(@.type == 1)]

| Support the operation |	description |
| --- | --- |
| `$`	| Represents the root element |
| `@`	| Current node (used as a predicate for a filter expression) |
| `*`	| Generic card character that can represent a name or number. |
| `..`	| Deep scanning. Think of it as a recursive search. |
| `.<name>`	| Represents a child node |
| `['<name>' (, '<name>')]` | Represents one or more child nodes |
| `[<number> (, <number>)]`	| Represents one or more array subscripts (the negative sign is the inverse)|
| `[start:end]`	| Array fragment, interval \[start,end), without end (negative sign is reciprocal) |
| `[?(<expression>)]`	| Filter expressions. The result of the expression must be a Boolean value. |

| Support for filtering operators (' Space between operators')|	description |
| --- | --- |
| `==`	| Left equals right (note that 1 does not equal '1') |
| `!=`	| Is not equal to |
| `<`	| Less than |
| `<=`	| Less than or equal to |
| `>`	| Is greater than |
| `>=`	| Greater than or equal to |
| `=~`	| Matching regular expressions [?(@.name =~ /foo.*?/i)] |
| `in`	| The left exists on the right [?(@.size in ['S', 'M'])] |
| `nin`	| The left does not exist on the right |

| Support for tail functions |	description |
| --- | --- |
| `min()`	| Calculates the minimum value of an array of numbers |
| `max()`	| Computes the maximum value of an array of numbers |
| `avg()`	| Calculates the average value of an array of numbers |
| `sum()`	| Computes the summary value of an array of numbers |

case：`n.select("$.store.book[0].title")` or `n.select("$['store']['book'][0]['title']")`

case：`n.select("$..book.price.min()") //Find the lowest price` 

# Snack3 Interface dictionary
```swift
//Quickly build
//
+newValue()  -> new:ONode 创建值类型节点
+newObject() -> new:ONode 创建对象类型节点
+newArray()  -> new:ONode 创建数组类型节点

//Initialization operation
//
-asObject() -> self:ONode  //将当前节点切换为对象
-asArray()  -> self:ONode  //将当前节点切换为数组
-asValue()  -> self:ONode  //将当前节点切换为值
-asNull()   -> self:ONode  //将当前节点切换为null

//Test operation
//
-isObject() -> bool  //检查当前节点是否为对象
-isArray()  -> bool  //检查当前节点是否为数组
-isValue()  -> bool  //检查当前节点是否为值
-isNull()   -> bool  //检查当前节点是否为null

//common
//
-nodeData() -> ONodeData //获取当前节点数据
-nodeType() -> ONodeType //获取当前节点类型

-cfg(cfg:Constants) -> self:ONode   //切换配置
-cfg() -> Constants 				//获取配置

-build(n->..) -> self:ONode     	//节点构建表达式
-select(jpath:String) -> new:ONode 	                    //使用JsonPath表达式选择节点（默认缓存路径编译）
-select(jpath:String, useStandard:boolean)-> new:ONode  //useStandard:使用标准模式,默认非标准
-select(jpath:String, useStandard:boolean, cacheJpath:boolean)-> new:ONode   //cacheJpath:是否缓存javaPath编译成果，默认缓存

-clear()                    //清除子节点，对象或数组有效
-count() -> int             //子节点数量，对象或数组有效
-readonly() -> self:ONode   //只读形态（get时，不添加子节点）

//Value operation
//
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

//Object operation
//
-obj() -> Map<String,ONode>                     //获取节点对象数据结构体（如果不是对象类型，会自动转换）
-contains(key:String) -> bool                   //是否存在对象子节点?
-rename(key:String,newKey:String) -> self:ONode //重命名子节点并返回自己
-get(key:String) -> child:ONode                 //获取对象子节点（不存在，生成新的子节点并返回）
-getOrNew(key:String) -> child:ONode            //获取对象子节点（不存在，生成新的子节点并返回）
-getOrNull(key:String) -> child:ONode           //获取对象子节点（不存在，返回null）
-getNew(key:String) -> child:ONode              //生成新的对象子节点，会清除之前的数据
-set(key:String,val:Object) -> self:ONode           //设置对象的子节点（会自动处理类型）
-setNode(key:String,val:ONode) -> self:ONode        //设置对象的子节点，值为ONode类型（需要在外部初始化类型，建议用set(k,v)）
-setAll(obj:ONode) -> self:ONode                    //设置对象的子节点，将obj的子节点搬过来
-setAll(map:Map<String,T>) ->self:ONode             //设置对象的子节点，将map的成员搬过来
-setAll(map:Map<String,T>, (n,t)->..) ->self:ONode  //设置对象的子节点，将map的成员搬过来，并交由代理处置
-remove(key:String)                   //移除对象的子节点
-forEach((k,v)->..) -> self:ONode     //遍历对象的子节点

//Array operation
//
-ary() -> List<ONode>                   //获取节点数组数据结构体（如果不是数组，会自动转换）
-get(index:int)  -> child:ONode                 //获取数组子节点（超界，返回空节点）
-getOrNew(index:int)  -> child:ONode            //获取数组子节点（不存在，生成新的子节点并返回）
-getOrNull(index:int)  -> child:ONode           //获取数组子节点（超界，返回null）
-addNew() -> child:ONode                        //生成新的数组子节点
-add(val) -> self:ONode                         //添加数组子节点 //val:为常规类型或ONode
-addNode(val:ONode) -> self:ONode               //添加数组子节点，值为ONode类型（需要在外部初始化类型，建议用add(v)）
-addAll(ary:ONode)  -> self:ONode               //添加数组子节点，将ary的子节点搬过来
-addAll(ary:Collection<T>) -> self:ONode                //添加数组子节点，将ary的成员点搬过来
-addAll(ary:Collection<T>,(n,t)->..) -> self:ONode      //添加数组子节点，将ary的成员点搬过来，并交由代理处置
-removeAt(index:int)                 //移除数组的子节点
-forEach(v->..) -> self:ONode        //遍历数组的子节点

//Attrs operation（不破坏数据的情况上，添加数据；或用于构建xml dom）
//
-attrGet(key:String) -> String                  //获取特性
-attrSet(key:String,val:String) -> self:ONode   //设置特性
-attrForeach((k,v)->..) -> self:ONode           //遍历特性

//Conversion operations
//
-toString() -> String               //转为string （由字符串转换器决定，默认为json）
-toJson() -> String                 //转为json string
-toData() -> Object 			    //转为数据结构体（Map,List,Value）
-toObject(clz:Class<T>) -> T        //转为java object（clz=Object.class：自动输出类型）
-toObjectList(clz:Class<T>) -> List<T>   //转为java list

-to(toer:Toer, clz:Class<T>) -> T   //将当前节点通过toer进行转换
-to(toer:Toer) -> T                 //将当前节点通过toer进行转换

//Filling operation（为当前节点填充数据；source 为 String 或 java object）
-fill(source:Object)    -> self:ONode  //填充数据
-fill(source:Object, Feature... features)    -> self:ONode //填充数据
-fillObj(source:Object, Feature... features)    -> self:ONode //填充数据
-fillStr(source:String, Feature... features)    -> self:ONode //填充数据

/**
 * The following is a static operation
**/

//Load operation（source 为 String 或 java object）
//
+load(source:Object) -> new:ONode    //加载数据
+load(source:Object, Feature... features) -> new:ONode
+load(source:Object, cfg:Constants) -> new:ONode
+load(source:Object, cfg:Constants, fromer:Fromer) -> new:ONode

//Load string
+loadStr(source:String) -> new:ONode	//仅String
+loadStr(source:String, Feature... features) -> new:ONode	//仅String
//加载 java object
+loadObj(source:Object) -> new:ONode	//仅java object
+loadObj(source:Object, Feature... features) -> new:ONode	//仅java object

//Stringing operation
//
+stringify(source:Object) -> String                   //字符串化；

//Serialization operation
//
+serialize(source:Object) -> String                   //序列化（带@type属性）
+deserialize(source:String) -> T                      //反序列化
+deserialize(source:String, clz:Class<?>) -> T        //反序列化

```
