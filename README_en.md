[![Maven Central](https://img.shields.io/maven-central/v/org.noear/snack3.svg)](https://search.maven.org/artifact/org.noear/snack3)
[![Apache 2.0](https://img.shields.io/:license-Apache2-blue.svg)](https://license.coscl.org.cn/Apache2/)
[![JDK-8+](https://img.shields.io/badge/JDK-8+-green.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)
[![QQ交流群](https://img.shields.io/badge/QQ交流群-22200020-orange)](https://jq.qq.com/?_wv=1027&k=kjB5JNiC)

# Snack3 for java
A miniature JSON + JSONPath framework

Based on JDK8, 80Kb. Support: serialization and deserialization, parsing and transformation, JSON PATH query.

```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack3</artifactId>
  <version>3.2.23</version>
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
+newValue()  -> new:ONode //Create a value type node
+newObject() -> new:ONode //Create an object type node
+newArray()  -> new:ONode //Creates a node of array type

//Initialization operation
//
-asObject() -> self:ONode  //Switches the current node to an object
-asArray()  -> self:ONode  //Switches the current node to an array
-asValue()  -> self:ONode  //Switches the current node to a value
-asNull()   -> self:ONode  //Switch the current node to NULL

//Test operation
//
-isObject() -> bool  //Checks whether the current node is an object
-isArray()  -> bool  //Checks whether the current node is an array
-isValue()  -> bool  //Check whether the current node is a value
-isNull()   -> bool  //Check whether the current node is NULL

//common
//
-nodeData() -> ONodeData //Gets the current node data
-nodeType() -> ONodeType //Gets the current node type

-options(opts:Options) -> self:ONode   //Switch options
-options() -> Options 				//Access to the options

-build(n->..) -> self:ONode     	//Node build expression
-select(jpath:String) -> new:ONode 	                    //Select nodes using JSONPATH expressions (default cache path compilation)
-select(jpath:String, useStandard:boolean)-> new:ONode  //useStandard:Use standard mode, default non-standard
-select(jpath:String, useStandard:boolean, cacheJpath:boolean)-> new:ONode   //cacheJpath:Whether to cache javaPath compilation results, the default cache

-clear()                    //Clear child node, object or array valid
-count() -> int             //Number of child nodes, object or array valid
-readonly() -> self:ONode   //Read-only mode (when GET, no child nodes are added)

//Value operation
//
-val() -> OValue                //Gets the node value data structure (automatically converted if it is not a value type)
-val(val:Object) -> self:ONode  //Set the node value //val: to a regular type or ONode
-getString()    //Gets the value and prints it as a String //If the node is an object or an array, JSON is printed
-getShort()     //Get the value and print it as short... (The following is the same as...)
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
-obj() -> Map<String,ONode>                     //Gets the data structure of the node object (automatically converted if it is not the object type)
-contains(key:String) -> bool                   //Is there an object child node?
-rename(key:String,newKey:String) -> self:ONode //Rename the child node and return itself
-get(key:String) -> child:ONode                 //Gets object child node (does not exist, returns empty node)***
-getOrNew(key:String) -> child:ONode            //Gets object child node (does not exist, generates new child node and returns)
-getOrNull(key:String) -> child:ONode           //Gets the child node of the object (does not exist, returns null)
-getNew(key:String) -> child:ONode              //Generating a new child node of the object clears the previous data
-set(key:String,val:Object) -> self:ONode           //Sets the child nodes of the object (the type is handled automatically)
-setNode(key:String,val:ONode) -> self:ONode        //Set the child nodes of the object to type ONode (need to initialize the type externally, recommend set(k,v)).
-setAll(obj:ONode) -> self:ONode                    //Set the child node of the object, and move the child node of obj over
-setAll(map:Map<String,T>) ->self:ONode             //Set the child nodes of the object and move the members of the Map over
-setAll(map:Map<String,T>, (n,t)->..) ->self:ONode  //Set the child nodes of the object, move the members of the Map over, and hand them over to the proxy
-remove(key:String)                   //Removes the child nodes of the object
-forEach((k,v)->..) -> self:ONode     //Iterate through the child nodes of the object

//Array operation
//
-ary() -> List<ONode>                   //Gets the node array data structure (automatically converted if it is not an array)
-get(index:int)  -> child:ONode                 //Gets the array child node (does not exist, returns empty node)
-getOrNew(index:int)  -> child:ONode            //Gets array child node (does not exist, generates new child node and returns)
-getOrNull(index:int)  -> child:ONode           //Gets array child nodes (does not exist, returns null)
-addNew() -> child:ONode                        //Generates a new array child node
-add(val) -> self:ONode                         //Add array child node //val: for regular type or ONode
-addNode(val:ONode) -> self:ONode               //Add array child node with value of type ONode (need to initialize type externally, recommend add(v))
-addAll(ary:ONode)  -> self:ONode               //Add an array child node and move the children of ary
-addAll(ary:Collection<T>) -> self:ONode                //Add an array child node and move the member points of ARY
-addAll(ary:Collection<T>,(n,t)->..) -> self:ONode      //Add a child node of the array, and move the member points of ARY to the agent
-removeAt(index:int)                 //Removes the children of the array
-forEach(v->..) -> self:ONode        //Ignore the children of the array

//Attrs operation（Add data without damaging the data; Or it can be used to build the XML DOM）
//
-attrGet(key:String) -> String                  //Access to attr
-attrSet(key:String,val:String) -> self:ONode   //Set the attr
-attrForeach((k,v)->..) -> self:ONode           //Traversal attr

//Conversion operations
//
-toString() -> String               //To string (determined by a string converter, default is JSON)
-toJson() -> String                 //To json string
-toData() -> Object 			    //To a data structure（Map,List,Value）
-toObject(clz:Class<T>) -> T        //To java object（clz=Object.class：Automatic output type）
-toObjectList(clz:Class<T>) -> List<T>   //To java object list

-to(toer:Toer, clz:Class<T>) -> T   //Convert the current node through TOER
-to(toer:Toer) -> T                 //Convert the current node through TOER

//Filling operation（Populate the current node with data；source 为 String 或 java object）
-fill(source:Object)    -> self:ONode  //Fill in the data
-fill(source:Object, Feature... features)    -> self:ONode //Fill in the data
-fillObj(source:Object, Feature... features)    -> self:ONode //Fill in the data
-fillStr(source:String, Feature... features)    -> self:ONode //Fill in the data

/**
 * The following is a static operation
**/

//Load operation（source 为 String 或 java object）
//
+load(source:Object) -> new:ONode    //Load the data
+load(source:Object, Feature... features) -> new:ONode
+load(source:Object, opts:Constants) -> new:ONode
+load(source:Object, opts:Constants, fromer:Fromer) -> new:ONode

//Load string
+loadStr(source:String) -> new:ONode	//Only string
+loadStr(source:String, Feature... features) -> new:ONode	//Only string
//加载 java object
+loadObj(source:Object) -> new:ONode	//Only java object
+loadObj(source:Object, Feature... features) -> new:ONode	//Only java object

//Stringing operation
//
+stringify(source:Object) -> String                   //stringify；

//Serialization operation
//
+serialize(source:Object) -> String                   //serialize（@the type attribute）
+deserialize(source:String) -> T                      //deserialize
+deserialize(source:String, clz:Class<?>) -> T        //deserialize

```_
