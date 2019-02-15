# snack3
轻量级JSON框架

这是snacks的重构项目（snacks为07年的项目），还在性能优化和特殊类型兼容适配中。。。

希望大小能控制在60KB以内

## 随便放几个示例

```java
//demo1::序列化
String json = ONode.serialize(user);

//demo2::反序列化
UserModel user = ONode.deserialize(json, UserModel.class);

//demo3::转为ONode
ONode o = ONode.map(json); //将json string 转为 ONode
ONode o = ONode.map(user); //将bean 转为 ONode

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
```
