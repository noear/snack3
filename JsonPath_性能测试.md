# JsonPath 兼容性及性能测试

最近给自己的`json`框架`snack3`添加了`json path`支持。搞好之后，找了两个市面上流行框架比较性测试，以改进自己框架的性能和兼容性。

##### 测试目标框架：
* com.alibaba:fastjson:1.2.29
* com.jayway.jsonpath:json-path:2.2.0
* org.noear:snack3:3.1.5.2

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
//3.消耗时间
long times = System.currentTimeMillis() - start;

System.out.println(times);
```
#### 2.每个方案各执行3次以上，并记录3次数消耗时间
#### 3.最后制成对比表格

#### 4.测试结果：

| Json path表达式 | 数据 | fastjson | json-path | snack3 |
| --- | --- | ---| --- | --- |
| `$..a` | A | 529,546,539 | 2658,2633,2590 | 225,225,232 |
| `$..*` | A | (不兼容1) | 3227,3220,3156 | 306,315,325 |
| `data.list[1,4]` | A | 85,90,86 | 782,798,776 | 133,137,131 |
| `data.list[1:4]` | A | 105,109,109 | 941,899,947 | 143,145,146 |
| `data.ary2[1].b.c` | A | 60,58,58 | 929,826,837 | 84,86,80 |
| `data.ary2[*].b.c` | A | (不兼容3) | 1105,1025,1050 | 173,152,155 |
| `$..b[?(@.c == 12)]` | B | (不兼容3) | 5628,5739,5636 | 580,535,532 |
| `$..c.min()` | B | (不兼容3) | (不兼容3) | 279,282,285 |
| `$[?(@.c =~ /a+/)]` | C | (不兼容3) | 3575,3591,3813 | 444,423,429 |
| `$..ary2[0].a` | A | 325,321,319 | 2522,2551,2591 | 310,311,314 |
| `data.list[?(@ in $..ary2[0].a)]` | A | (不兼容3) | 5494,5326,5483 | 678,674,667 |

注：
* 不兼容1 : 返回的是它自己
* 不兼容2 : 结果是个null
* 不兼容3 : 直接异常

## 总结

* fastjson 毫无兼容性可言
* json-path 性能不理想，函数使用局限性大

#### 附：..表达式的兼容性说明
##### json-path：从测试的情况看，处理策略如下：

* 1.带..输出必为数组;如果有[x]，合并为一维数组
* 2.优选顺序：
`$..(ary2[0].a)` 
`$..(c.min())`   //所以会出错
* 3.只能：`data.list[?(@ in $..ary2[0].a)]`
* 4.只能：在原数组节点上执行函数

##### snack3：从测试的情况看，处理策略如下：

* 1.带..输出必为数组;如果有[x]，合并为一维数组
* 2.优选顺序：
  `($..ary2[0]).a`    
  `($..c).min()`  //所以会有结果 //实际使用中，这种会更方便
* 3.可以：`data.list[?(@ in $..ary2[0].a)]` 或 `data.list[?(@ == $..ary2[0].a[0])]`

* 4.可以：在原数组节点上执行函数 或 查询结果上执行

#### 附：测试代码

* com.alibaba:fastjson 代码：https://github.com/noear/snack3/blob/master/snack3_demo/src/test/java/speed/SpeedFastjsonJsonPathTest.java
* com.jayway.jsonpath:json-path  代码：https://github.com/noear/snack3/blob/master/snack3_demo/src/test/java/speed/SpeedJaywayJsonPathTest.java
* org.noear:snack3  代码：https://github.com/noear/snack3/blob/master/snack3_demo/src/test/java/speed/SpeedJsonPathTest.java