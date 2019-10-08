` QQ交流群：22200020 `

# snack3 for java
轻量级JSON框架

这是snacks的重构项目（snacks为07年的项目），还在性能优化和特殊类型兼容适配中。。。

有序列化反序列化、解析和转换。才57Kb哦

```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack3</artifactId>
  <version>3.0.11</version>
</dependency>
```

## 随便放几个示例

```java
//demo1::序列化
String json = ONode.serialize(user);

//demo2::反序列化
UserModel user = ONode.deserialize(json, UserModel.class);

//demo3::转为ONode
ONode o = ONode.fromStr(json); //将json string 转为 ONode
ONode o = ONode.fromObj(user); //将bean 转为 ONode

//demo4:构建json数据(极光推送的rest api调用)
public static void push(Collection<String> alias_ary, String text)  {
    ONode data = new ONode().exp((d)->{
        d.get("platform").val("all");

        d.get("audience").get("alias").addAll(alias_ary);

        d.get("options")
                .set("apns_production",false);

        d.get("notification").exp(n->{
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

//demo5:取值
o.get("name").getString();
o.get("num").getInt();
o.get("list").get(0).get("lev").getInt();
```
