# Snack3 之 Jsonpath使用

### 一、 Snack3 和 JSONPath 介绍

Snack3 是一个支持JSONPath的JSON框架。JSONPath是一个很强大的功能，也可以在Java框架中当作对象查询语言（OQL）来使用。


```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack3</artifactId>
  <version>3.1.6.3</version>
</dependency>
```

Snack3 借鉴了 `Javascript` 所有变量由 `var` 申明，及 `Xml dom` 一切都是 `Node` 的设计。其下一切数据都以`ONode`表示，`ONode`也即 `One node` 之意，代表任何类型，也可以转换为任何类型。
* 强调文档树的操控和构建能力
* 做为中间媒体，方便不同格式互转
* 高性能`Json path`查询（兼容性和性能很赞）
* 支持`序列化、反序列化`

### 二、接口

```java
public class ONode{
    //...
    /**
     * Json path select
     *
     * @param jpath json path express
     * @param useStandard use standard mode(default: false)
     * @param cacheJpath cache json path parsing results
     */
    public ONode select(String jpath,  boolean useStandard, boolean cacheJpath) {
        return JsonPath.eval(this, jpath, useStandard, cacheJpath);
    }

    public ONode select(String jpath,  boolean useStandard) {
        return select(jpath, useStandard, true);
    }

    public ONode select(String jpath) {
        return select(jpath, false);
    }
    //...  
}
```

默认使用缓存JSONPath解析对象，可提供几倍性能效果。

### 三、支持语法

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

### 四、语法示例说明

| JSONPath | 说明                           |
| ------------ | ------------------------------ |
| `$`      | 根对象           |
| `$[-1]`      | 最后元素           |
| `$[:-2]`      | 第0个至倒数第2个           |
| `$[1:]`      | 第1个之后所有元素（0为首个） |
| `$[1,2,3]`      | 集合中1,2,3个元素（0为首个） |

### 五、接口使用示例

#### 示例1:
读取对象的属性
```java
Entity entity = new Entity(123, new Object());
ONode n = ONode.load(entity);

assert n.select("$.id").getInt() == 123;
assert n.select("$.*").count() == 2;

public static class Entity {
   public int id;
   public String name;
   public Object value;
   public Entity(int id, Object value) { this.id = id; this.value = value; }
   public Entity(String name) { this.name = name; }
}

```

#### 示例2
读取集合多个元素的某个属性
```java
List<Entity> entities = new ArrayList<Entity>();
entities.add(new Entity("wenshao"));
entities.add(new Entity("ljw2083"));
ONode n = ONode.load(entities);

List<String> names = n.select("$.name").toObject(List.class); 
assert names.size() == 2;
```

#### 示例3
返回集合中多个元素
```java
List<Entity> entities = new ArrayList<Entity>();
entities.add(new Entity("wenshao"));
entities.add(new Entity("ljw2083"));
entities.add(new Entity("Yako"));
ONode n = ONode.load(entities);

List<Entity> result = n.select("$[1,2]").toObject((new ArrayList<Entity>() {}).getClass());
assert result.size() == 2;
```

#### 示例4
按范围返回集合的子集
```java
List<Entity> entities = new ArrayList<Entity>();
entities.add(new Entity("wenshao"));
entities.add(new Entity("ljw2083"));
entities.add(new Entity("Yako"));
ONode n = ONode.load(entities);

List<Entity> result = n.select("$[0:2]").toObject((new ArrayList<Entity>(){}).getClass());
assert result.size() == 2;
```

#### 示例5
通过条件过滤，返回集合的子集
```java
List<Entity> entities = new ArrayList<Entity>();
entities.add(new Entity(1001, "ljw2083"));
entities.add(new Entity(1002, "wenshao"));
entities.add(new Entity(1003, "yakolee"));
entities.add(new Entity(1004, null));
ONode n = ONode.load(entities);

ONode rst = n.select("$[?($.id in [1001,1002])]");
assert rst.count() == 2;
```

#### 示例6
根据属性值过滤条件判断是否返回对象，修改对象，数组属性添加元素
```java
Entity entity = new Entity(1001, "ljw2083");
ONode n = ONode.load(entity);

assert n.select("$[?(id == 1001)]").isObject();
assert n.select("$[?(id == 1002)]").isNull();

n.select("$").set("id",123456);
assert n.get("id").getInt() == 123456;

n.get("value").add(1).add(2).add(3);
assert n.get("value").count() == 3;

```

#### 示例7
```java
Map root = Collections.singletonMap("company",
        Collections.singletonMap("departs",
                Arrays.asList(
                        Collections.singletonMap("id",
                                1001),
                        Collections.singletonMap("id",
                                1002),
                        Collections.singletonMap("id", 1003)
                )
        ));

ONode n = ONode.load(root);

List<Object> ids = n.select("$..id").toObject(List.class);
assertEquals(3l, ids.size());
assertEquals(1001l, ids.get(0));
assertEquals(1002l, ids.get(1));
assertEquals(1003l, ids.get(2));
```
#### 具体用例测试请看下面:
```java
String jsonStr = "{\n" +
        "    \"store\": {\n" +
        "        \"bicycle\": {\n" +
        "            \"color\": \"red\",\n" +
        "            \"price\": 19.95\n" +
        "        },\n" +
        "        \"book\": [\n" +
        "            {\n" +
        "                \"author\": \"刘慈欣\",\n" +
        "                \"price\": 8.95,\n" +
        "                \"category\": \"科幻\",\n" +
        "                \"title\": \"三体\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"author\": \"itguang\",\n" +
        "                \"price\": 12.99,\n" +
        "                \"category\": \"编程语言\",\n" +
        "                \"title\": \"go语言实战\"\n" +
        "            }\n" +
        "        ]\n" +
        "    }\n" +
        "}";

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
打印结果
```
books=::[{"author":"刘慈欣","price":8.95,"category":"科幻","title":"三体"},{"author":"itguang","price":12.99,"category":"编程语言","title":"go语言实战"}]
titles=::["三体","go语言实战"]
title=::"三体"
price大于10元的book=::[{"author":"itguang","price":12.99,"category":"编程语言","title":"go语言实战"}]
price大于10元的title=::["go语言实战"]
category(类别)为科幻的book=::[{"author":"刘慈欣","price":8.95,"category":"科幻","title":"三体"}]
bicycle的所有属性值=::["red",19.95]
bicycle的color和price属性值=::["red",19.95]
```
