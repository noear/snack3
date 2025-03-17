# snack v4.0 lab

## 特性

支持 dom 操控

```java
ONode oNode = new ONode();
oNode.set("id", 1);
oNode.getOrNew("layout").build(o -> {
    o.addNew().set("title", "开始").set("type", "start");
    o.addNew().set("title", "结束").set("type", "end");
});
```

支持序列化、反序列化

```java
User user = new User();
ONode.loadBean(user).toBean(User.class);
ONode.loadBean(user).toJson();

ONode.loadJson("{}").toBean(User.class);
```

支持 jsonpath 查询、构建、删除

```java
ONode.loadBean(store).select("$..book[?(@.tags contains 'war')]").toBean(Book.class);
ONode.loadJson(store).select("$.store.book.count()");

ONode.loadBean(store).create("$.store.book[0].category").toJson();

ONode.loadBean(store).delete("$..book[-1]");
```

支持架构校验

```java
ONode schemaNode = ONode.loadJson("{user:{name:''}}"); //定义架构
Options options = Options.builder().schema(schemaNode).build();
ONode.loadJson("{}",options);
```