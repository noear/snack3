# 更强的 JsonPath 兼容性及性能测试

最近给自己的`json`框架`snack3`添加了`json path`支持。搞好之后，找了两个市面上流行框架比较性测试，以助自己改进框架的性能和兼容性。



测了一圈之后，发现`snack3`兼容性良好，性能更是最好的。晚上要喝点小酒庆祝一下！！！

免责声明：我也是个java新手，可能测试得方式不对而造成不科学的结果，可以留言指出来。以下测试数值只对我的电脑有效（配置：Macbook pro 13 2018款 i7+16G+512G）。



Snack3 借鉴了 `Javascript` 所有变量由 `var` 申明，及 `Xml dom` 一切都是 `Node` 的设计。其下一切数据都以`ONode`表示，`ONode`也即 `One node` 之意，代表任何类型，也可以转换为任何类型。
* 强调文档树的操控和构建能力
* 做为中间媒体，方便不同格式互转
* 高性能`Json path`查询（兼容性和性能很赞）
* 支持`序列化、反序列化`



##### 本案测试用的三个框架及版本：
* com.alibaba:fastjson:1.2.29
* com.jayway.jsonpath:json-path:2.2.0
* org.noear:snack3:3.1.5.9

##### 三份测试数据样本：
* A:`{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}`
* B:`[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]`
* C:`[{c:'aaaa'}, {b:'cccc'}, {c:'cccaa'}]`

## 测试方案：
#### 1.测试伪代码
```javascript
var text = "...";

//1.预解析json
var obj = JSON.parse(text);

long start = System.currentTimeMillis();
for(int i=0,len=1000000; i<len; i++) {
    //2.执行json path
    JSONPath.eval(obj,"$..."); 
}
//3.100万次的消耗时间（记录的数值就是这个）
long times = System.currentTimeMillis() - start;

System.out.println(times);
```
#### 2.每个框架、每个表达式各执行4次，并记录后3次消耗的毫秒数（例：225ms; 225ms; 232ms）
#### 3.最后制成对比表格

#### 4.测试结果：

| Json path 表达式                     | 数据 | fastjson            | json-path              | snack3              |
|-----------------------------------| --- |---------------------|------------------------|---------------------|
| `$..a`                            | A | 529ms; 546ms; 539ms | 2658ms; 2633ms; 2590ms | 225ms; 225ms; 232ms |
| `$..*`                            | A | (不兼容1)              | 3227ms; 3220ms; 3156ms | 306ms; 315ms; 325ms |
| `data.list[1,4]`                  | A | 85ms; 90ms; 86ms    | 782ms; 798ms; 776ms    | 133ms; 137ms; 131ms |
| `data.list[1:4]`                  | A | 105ms; 109ms; 109ms | 941ms; 899ms; 947ms    | 143ms; 145ms; 146ms |
| `data.ary2[1].b.c`                | A | 60ms; 58ms; 58ms    | 929ms; 826ms; 837ms    | 84ms; 86ms; 80ms    |
| `data.ary2[*].b.c`                | A | (不兼容2)              | 1105ms; 1025ms; 1050ms | 173ms; 152ms; 155ms |
| `$..b[?(@.c == 12)]`              | B | (不兼容2)              | 5628ms; 5739ms; 5636ms | 580ms; 535ms; 532ms |
| `$..c.min()`                      | B | (不兼容2)              | (不兼容2)                 | 279ms; 282ms; 285ms |
| `$[?(@.c =~ /a+/)]`               | C | (不兼容2)              | 3575ms; 3591ms; 3813ms | 444ms; 423ms; 429ms |
| `$..ary2[0].a`                    | A | 325ms; 321ms; 319ms   | 2522ms; 2551ms; 2591ms | 310ms; 311ms; 314ms |
| `data.list[?(@ in $..ary2[0].a)]` | A | (不兼容2)              | 5494ms; 5326ms; 5483ms   | 678ms; 674ms; 667ms   |

注：
* 不兼容1 : 直接返回了根节点自己
* 不兼容2 : 直接异常

## 总结

* fastjson 毫无兼容性可言
* json-path 性能不理想，函数使用局限性大
* snack3 性能最好，支持两种策略：1.标准模式,保持与json-path兼容效果；2.非标准模式,函数使用余地更大 

#### 附1：snack3项目地址：

* https://github.com/noear/snack3
* https://gitee.com/noear/snack3

#### 附2：..及函数表达式的兼容性说明
##### json-path：（snack3的标准模式同），处理策略如下：
1. 选择器的执行顺序：（括号里的表达式处理后再聚合）
    * `$..(ary2[0].a)`
    * `$..(c.min())`   //如果c不是数组，此处会出错 
    * `$..(ary2[0][0])`  
2. 只能：`data.list[?(@ in $..ary2[0].a)]`
3. 只能：在原数组节点上执行函数

##### snack3：（snack3的非标准模式），处理策略如下：
1. 选择器的执行顺序：（括号里的表达式处理后再聚合）
    * `($..ary2[0]).a` 
    * `($..c).min()`    //c是不是数组都正常 //实际使用中，这种会更方便，同时也兼容其它表达式 
    * `($..ary2[0])[0]` //这种会引起不同的结果 //不过实际中极少出现
2. 可以：`data.list[?(@ in $..ary2[0].a)]` 或 `data.list[?(@ == $..ary2[0].a[0])]`
3. 可以：在原数组节点上执行函数 或 查询结果上执行

#### 附3：测试代码

* `com.alibaba:fastjson` 测试代码：https://gitee.com/noear/snack3/blob/master/snack3_demo/src/test/java/benchmark/jsonpath/SpeedFastjsonJsonPathTest.java
* `com.jayway.jsonpath:json-path`  测试代码：https://gitee.com/noear/snack3/blob/master/snack3_demo/src/test/java/benchmark/jsonpath/SpeedJaywayJsonPathTest.java
* `org.noear:snack3`  测试代码：https://gitee.com/noear/snack3/blob/master/snack3_demo/src/test/java/benchmark/jsonpath/SpeedJsonPathTest.java