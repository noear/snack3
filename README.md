# snack3
轻量级json框架

这是snacks的重构项目，还在性能优化中：）

## dom 构建简单示例（先随便放个示例）

```java
//极光推送的数据构建示例
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
