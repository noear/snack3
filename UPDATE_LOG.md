### 4.0.57

* 修复 JsonReader 解析深度无限制，超深嵌套会导致 StackOverflowError 的问题；添加 Options.maxNestingDepth 配置（默认 1000）
* 修复 InetSocketAddressDecoder 反序列化时会主动触发 DNS 解析的问题
* 修复 JsonPath、Expression、RegexUtil 内部缓存无上限，可能导致内存耗尽的问题

### 4.0.56

* 调整 LocalDate, LocalTime, OffsetDateTime, OffsetTime 的编解码处理（默认用 toString 作为编码）

### 4.0.55

* 添加 JsonReader.readTry

### 4.0.54

* 修复：JsonReader.parseValue() AutoRepair 模式下 readLast() 可能会死循环

### 4.0.53

* 添加 Duration、File、Path JsonSchema 编码
* 添加 Options.readonly 方法（把当前实例转为只读，禁止外部修改）
* 添加 JsonSchema.options 可选配置（方便定制序列化格式）
* 调整 jsonschema 内部包名

### 4.0.52

* 添加 ONode.getDate(def) 方法
* 添加 ONode.getString(def) 方法
* 修复 ONodeAttr.creator 从类到字段类型传递失败的问题 

### 4.0.51

* 添加 CodeLib Path 类型编解码
* 添加 Feature.Write_DurationUsingSimple 特性
* 添加 ONodeAttr.creator 属性（通过注解控制生成）
* eggg 升为 1.1.3

### 4.0.50

* 新增 JsonPath.subPath 方法（获取子段）
* 添加 JsonPath.getSegmentCount 方法
* 添加 Segment.getOriginalText 方法
* eggg 升为 1.1.0

### 4.0.49

* 修复 `2026-03-25 11:00:00.152636324` 时间解析问题

### 4.0.48

* 优化 BeanDecoder 能根根据属性配置，自动选择最适合的构造方便
* eggg 升为 1.0.13

### 4.0.47

* 添加 Options.copy 方法

### 4.0.46

* 修复 BeanDecoder Map 解码时，会把内嵌 jsonString 自动转为对象的问题（v4.0.45 引起）

### 4.0.45

* 优化 BeanDecoder 对 className 的识别处理
* 添加 JsonReader Read_AutoRepair 对非规范转义控制符的自动修正处理

### 4.0.44

* 添加 BeanDecoder 内嵌 json 字符串，在解码时自动解包处理（方便处理 llm 不稳定情况）

### 4.0.43

* 添加 JsonSchema.generate, validate 快捷方法

### 4.0.42

* 添加 ONodeAttrHolder 新的构造方法，可以传入 format
* 添加 BeanDecoder.decode(node, parameter) 方法
* eggg 升为 1.0.11

### 4.0.41

* 添加 Decode_IgnoreError （新特性，解析时乎略错误）
* 添加 Decode_OnlyUseSetter ，替代 Write_OnlyUseSetter （后者标为弃用，新名更表意）
* 添加 Decode_AllowUseSetter ，替代 Write_AllowUseSetter （后者标为弃用，新名更表意）
* 添加 Encode_OnlyUseGetter ，替代 Read_OnlyUseGetter
* 添加 Encode_AllowUseGetter ，替代 Read_AllowUseGetter
* 修复 Json 反序列化时，string 自动转 list 会多出双引号的问题（并优化自动去掉空隔）

### 4.0.40

* 优化 Feature.Read_AutoRepair 特性

### 4.0.39

* 优化 jsonpath.exists 实现

### 4.0.38

* 添加 Feature.Read_AutoRepair 特性

### 4.0.37

* 优化 JsonPath 逻辑表达式处理，兼容 RFC9535 `\r\n\t` 空隔符

### 4.0.36

* 优化 JsonPath 逻辑表达式兼容性支持无空隔的 "a=='a'"
* 修复 JsonSchema TypeRule.getSchemaTypeName 没有把初始节点识别为 null 的问题（识别为 undefined 了）

### 4.0.35

* 调整 JsonPath.delete 改为返回 bool（之前为 void）

### 4.0.34

* 添加 Json Feature.Read_TrimString 特性

### 4.0.33

* 添加 JsonSchema 默认值生成

### 4.0.32

* 添加 JsonReader.iterableNext 方法
* 添加 JsonReader.readLast 方法
* 调整 JsonReader.streamRead 更名为 readNext（前者标为弃用）

### 4.0.31

* 添加 JsonReader.streamRead 方法
* 修复 ONode.ofBean 和 ofJson 传入 null 会异常的问题


### 4.0.30

* 修复 Json BeanDecoder array.item 为 null 不能原还的问题（补过滤了）

### 4.0.29

* 添加 ONode 字段类型反序列化支持

### 4.0.28

* 添加 自定义注解获取支持
* 取消 ONodeAttr.masking 标记（扩展性不好）

### 4.0.27

* 添加 ONodeAttr.masking 标记（方便脱敏定制）

### 4.0.26

* 添加 Options.then 方法，用于链式构建
* 添加 CodecLib.patternCreators,patternDecoders,patternEncoders 去重处理
* 添加 MapperLib.schemaPatternMappers,typePatternMappers 去重处理

### 4.0.25

* 添加 AtomicBoolean,AtomicLong,AtomicInteger 支持

### 4.0.24

* 添加 snack4 Optional 内置编解码器支持（也可以自定义扩展）
* 优化 snack4-jsonschema Optional 类型处理

### 4.0.23

* 添加 snack4 ONode:delete 方法，协助 jsonpath 删除
* 修复 snack4-jsonpath JsonPathProvider:delete(root, path) 删除多个 arrray index 时会超界的问题

### 4.0.22

* 优化 snack4 Iterable 支持（替代之前的 Collection）

### 4.0.21

* 优化 snack4 反序列化自动移除 '@type' 属性申明

### 4.0.20

* 添加 snack4-jsonschema 类型映射机制，支持 Future,Optional 等包装或传递类型

### 4.0.19

* 添加 ONode:getByte 方法
* 优化 与 ascii 不可见码(lt 32)的兼容处理
  * cluade llm，输出的 json 可能会有不可见码
* 修复 空字符串（'''）反序列化为枚举时会出错的问题
* 修复 字符串反序列化为 byte 时会出错的问题

### 4.0.18

* 修复 options:zoneId 没有传导到 JsonWriter 的问题

### 4.0.17

* 优化 DateUtil

### 4.0.16

* eggg 升为 1.0.10

### 4.0.15

* eggg 升为 1.0.9

### 4.0.14

* 修复 BigIntegerDecoder，BigDecimalDecoder 不能转数字的问题（4.0.13 出现的）

### 4.0.13

* 优化 snack4 空字符串的解码处理
* eggg 升为 1.0.8

### 4.0.12

* 修复 `List<? extend Xxx>` 反序列化时泛型识别出错的问题
* eggg 升为 1.0.7

### 4.0.11

* 添加 ONodeAttr 注解到类支持

### 4.0.10

* 添加 _EnumPatternEncoder 支持 Write_EnumShapeAsObject 特性（可以把 Enum 转为 Json Object）//只适合小范围使用
* 优化 _EnumPatternDecoder 添加 `ONodeCreator` 表态方法
* eggg 升为 1.0.5

### 4.0.9

* 修复 issue-ID5NQL parseKeyword 可能越界的问题

### 4.0.8

* 移除 ONode:hasNestedJson 方法

### 4.0.7

* 添加 TypeRef:listOf, setOf,mapOf 方法
* 添加 EgggUtil:getClassEggg 方法
* 优化 ONode:setAll, addAll 允许传入 null（兼容 snack3）
* eggg 升为 1.0.3

### 4.0.6

* 调整 Read_ConvertSnakeToCamel 特性更名为 Read_ConvertSnakeToSmlCamel
* 调整 Write_UseSnakeStyle 特性更名为 Write_UseSmlSnakeStyle
* 添加 Read_ConvertCamelToSmlSnake 特性
* 添加 Write_UseSmlCamelStyle 特性
* eggg 升为 1.0.2

### 4.0.5

* eggg 升为 1.0.1

### 4.0.4

* 优化 与 snack3 的效果兼容性

### 4.0.3

* 调整 泛型处理切抱为 eggg

### 4.0.2

* 添加 Write_BigDecimalAsPlain 特性
* 调整 ONode:nodeType,getType 合并为 `type()` 与 `options()` 保持相同风格
* 调整 QueryContext:isInFilter 更名为 `isFiltered()`
* 调整 Write_BigNumbersAsString 更名为 Write_DoubleAsString
* 优化 与 snack3 的效果兼容性

### 4.0.1

* 添加 ONodeCreator 静态方法的支持（普通类）
* 添加 ONodeAttr:ignore 注解属性支持
* 添加 Write_BooleanAsNumber 新特性
* 添加 Read_UseBigDecimalMode 新特性
* 添加 Read_UseBigIntegerMode 新特性
* 添加 DecodeContext:hasFeature, EncodeContext:hasFeature 新特性
* 优化 Write_Nulls 完善对 Map 输出的控制
* 优化 Write_BrowserCompatible 写入性能


### 4.0.0

* 重构整个项目（除了名字没变，其它都变了） 
* 单测覆盖 98%，历时小半年 
* 支持 IETF JSONPath (RFC 9535) 标准（全球首个支持该标准的 Java 框架），同时兼容 `jayway.jsonpath`
* 添加 json-schema 支持

