2022年了，重新做了一份`json path`的兼容性与性能测试。三个市面上流行框架比较性测试。


免责声明：可能测试得方式不对而造成不科学的结果（另外，机器不同结果会有不同），可以留言指出来。以下测试数值只对我的电脑有效（配置：Macbook pro 13 2020款 i7+32G+1T）。


##### 本案测试用的三个框架及版本：
* com.alibaba.fastjson2:fastjson2:2.0.4
* com.jayway.jsonpath:json-path:2.2.0
* org.noear:snack3:3.2.31

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
#### 2.每个框架、每个表达式各执行4次，并记录后3次消耗时间
#### 3.最后制成对比表格

#### 4.测试结果：

| Json path 表达式                       | 数据 | fastjson2   | json-path | snack3 |
|-------------------------------------| --- |-------------| --- | --- |
| `$..a`                              | A | 872;764;715 | 2658;2633;2590 | 225;225;232 |
| `$..*`                              | A | (不兼容1)      | 3227;3220;3156 | 306;315;325 |
| `$.data.list[1,4]`                  | A | 577;524;419 | 782;798;776 | 133;137;131 |
| `$.data.list[1:4]`                  | A | 332;367;391 | 941;899;947 | 143;145;146 |
| `$.data.ary2[1].a`                  | A | 315;339;329 | 704;663;655 | 84;86;80 |
| `$.data.ary2[*].b.c`                | A | 642;645;660 | 1105;1025;1050 | 239;235;237 |
| `$..b[?(@.c == 12)]`                | B | (不兼容2)      | 5628;5739;5636 | 580;535;532 |
| `$..c.min()`                        | B | (不兼容2)      | (不兼容2) | 279;282;285 |
| `$[?(@.c =~ /a+/)]`                 | C | (不兼容2)      | 3575;3591;3813 | 444;423;429 |
| `$..ary2[0].a`                      | A | 735;728;736 | 2522;2551;2591 | 310;311;314 |
| `$.data.list[?(@ in $..ary2[0].a)]` | A | (不兼容2)      | 5494;5326;5483 | 678;674;667 |

注：
* 不兼容1 : 直接返回空数组
* 不兼容2 : 直接异常

## 总结

* fastjson2 兼容性差了些
* json-path 性能不理想，函数使用局限性大
* snack3 性能最好，支持两种策略：1.标准模式,保持与json-path兼容效果；2.非标准模式,函数使用余地更大

#### 附1：snack3项目地址：

* [https://github.com/noear/snack3](https://github.com/noear/snack3)
* [https://gitee.com/noear/snack3](https://gitee.com/noear/snack3)

#### 附2：..及函数表达式的兼容性说明
##### json-path：（snack3的标准模式同），处理策略如下：
1.  选择器的执行顺序：（括号里的表达式处理后再聚合）
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

* `com.alibaba.fastjson2:fastjson2` 测试代码：https://gitee.com/noear/snack3/blob/master/snack3_demo/src/test/java/benchmark/jsonpath/SpeedFastjson2JsonPathTest.java
* `com.jayway.jsonpath:json-path`  测试代码：https://gitee.com/noear/snack3/blob/master/snack3_demo/src/test/java/benchmark/jsonpath/SpeedJaywayJsonPathTest.java
* `org.noear:snack3`  测试代码：https://gitee.com/noear/snack3/blob/master/snack3_demo/src/test/java/benchmark/jsonpath/SpeedJsonPathTest.java