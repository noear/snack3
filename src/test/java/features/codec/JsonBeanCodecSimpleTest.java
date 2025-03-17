package features.codec;

import org.junit.jupiter.api.Test;
import org.noear.snack.core.BeanCodec;
import org.noear.snack.ONode;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class JsonBeanCodecSimpleTest {

    // 测试用例 1: 基本数据类型转换
    public static class SimpleBean {
        public String name;
        public int age;
        public boolean active;
        public double score;
        public long id;
    }

    @Test
    public void testSimpleBean() {
        SimpleBean bean = new SimpleBean();
        bean.name = "Alice";
        bean.age = 30;
        bean.active = true;
        bean.score = 95.5;
        bean.id = 1001L;

        ONode node = BeanCodec.serialize(bean);
        SimpleBean result = BeanCodec.deserialize(node, SimpleBean.class);

        assertEquals(bean.name, result.name);
        assertEquals(bean.age, result.age);
        assertEquals(bean.active, result.active);
        assertEquals(bean.score, result.score, 0.001);
        assertEquals(bean.id, result.id);
    }

    // 测试用例 2: 嵌套对象
    public static class NestBean {
        public SimpleBean child;
    }

    @Test
    public void testNestedBean() {
        NestBean bean = new NestBean();
        bean.child = new SimpleBean();
        bean.child.name = "Bob";

        ONode node = BeanCodec.serialize(bean);
        NestBean result = BeanCodec.deserialize(node, NestBean.class);

        assertEquals(bean.child.name, result.child.name);
    }

    // 测试用例 3: List<String> 处理
    public static class ListBean {
        public List<String> items;
    }

    @Test
    public void testStringList() {
        ListBean bean = new ListBean();
        bean.items = Arrays.asList("A", "B", "C");

        ONode node = BeanCodec.serialize(bean);
        ListBean result = BeanCodec.deserialize(node, ListBean.class);

        assertEquals(bean.items, result.items);
    }

    // 测试用例 4: Map 处理
    public static class MapBean {
        public Map<String, Integer> data;
    }

    @Test
    public void testStringIntMap() {
        MapBean bean = new MapBean();
        Map<String, Integer> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        bean.data = map;

        ONode node = BeanCodec.serialize(bean);
        MapBean result = BeanCodec.deserialize(node, MapBean.class);

        assertEquals(bean.data, result.data);
    }

    // 测试用例 5: 枚举处理
    public enum TestEnum { OK, ERROR }
    public static class EnumBean {
        public TestEnum status;
    }

    @Test
    public void testEnumConversion() {
        EnumBean bean = new EnumBean();
        bean.status = TestEnum.OK;

        ONode node = BeanCodec.serialize(bean);
        assertEquals("OK", node.get("status").getString());

        EnumBean result = BeanCodec.deserialize(node, EnumBean.class);
        assertEquals(bean.status, result.status);
    }

    // 测试用例 6: null 值处理
    @Test
    public void testNullField() {
        SimpleBean bean = new SimpleBean();
        bean.name = null;

        ONode node = BeanCodec.serialize(bean);
        assertTrue(node.get("name").isNull());

        SimpleBean result = BeanCodec.deserialize(node, SimpleBean.class);
        assertNull(result.name);
    }

    // 测试用例 7: 空集合处理
    public static class EmptyCollectionBean {
        public List<String> emptyList = Collections.emptyList();
        public Map<String, String> emptyMap = Collections.emptyMap();
    }

    @Test
    public void testEmptyCollections() {
        EmptyCollectionBean bean = new EmptyCollectionBean();

        ONode node = BeanCodec.serialize(bean);
        EmptyCollectionBean result = BeanCodec.deserialize(node, EmptyCollectionBean.class);

        assertTrue(result.emptyList.isEmpty());
        assertTrue(result.emptyMap.isEmpty());
    }

    // 测试用例 8: 继承字段处理
    public static class ParentBean {
        public String parentField;
    }

    public static class ChildBean extends ParentBean {
        public String childField;
    }

    @Test
    public void testInheritedFields() {
        ChildBean bean = new ChildBean();
        bean.parentField = "parent";
        bean.childField = "child";

        ONode node = BeanCodec.serialize(bean);
        ChildBean result = BeanCodec.deserialize(node, ChildBean.class);

        assertEquals(bean.parentField, result.parentField);
        assertEquals(bean.childField, result.childField);
    }

    // 测试用例 9: 基本类型默认值
    static class PrimitiveBean {
        public int intVal;
        public boolean boolVal;
    }

    @Test
    public void testPrimitiveDefaults() {
        ONode emptyNode = new ONode(new HashMap<>());
        PrimitiveBean result = BeanCodec.deserialize(emptyNode, PrimitiveBean.class);

        assertEquals(0, result.intVal);
        assertFalse(result.boolVal);
    }

    // 测试用例 10: 泛型集合处理
    public static class GenericListBean {
        public List<SimpleBean> list;
    }

    @Test
    public void testGenericList() {
        GenericListBean bean = new GenericListBean();
        bean.list = Arrays.asList(new SimpleBean(), new SimpleBean());

        ONode node = BeanCodec.serialize(bean);
        GenericListBean result = BeanCodec.deserialize(node, GenericListBean.class);

        assertEquals(2, result.list.size());
    }

    // 测试用例 11: 多余JSON字段忽略
    @Test
    public void testExtraFieldsIgnored() {
        // 正确构建ONode结构
        Map<String, ONode> map = new HashMap<>();
        map.put("name", new ONode("test"));
        map.put("extra", new ONode(123));
        ONode node = new ONode(map);

        SimpleBean result = BeanCodec.deserialize(node, SimpleBean.class);
        assertEquals("test", result.name);
    }

    // 测试用例 12: 类型不匹配异常
    @Test
    public void testTypeMismatch() {
        Map<String, Object> map = new HashMap<>();
        map.put("age", "not_a_number");
        ONode node = new ONode(map);

        assertThrows(RuntimeException.class, () -> {
            BeanCodec.deserialize(node, SimpleBean.class);
        });
    }

    // 测试用例 13: transient 字段处理
    static class TransientBean {
        public transient String temp;
        public String normal;
    }

    @Test
    public void testTransientField() {
        TransientBean bean = new TransientBean();
        bean.temp = "tmp";
        bean.normal = "data";

        ONode node = BeanCodec.serialize(bean);
        assertTrue(node.hasKey("temp")); // 根据实现决定是否包含
    }

    // 测试用例 14: 不同访问权限字段
    public static class AccessBean {
        private String privateField;
        protected String protectedField;
        public String publicField;
    }

    @Test
    public void testDifferentAccessFields() {
        AccessBean bean = new AccessBean();
        bean.privateField = "private";
        bean.protectedField = "protected";
        bean.publicField = "public";

        ONode node = BeanCodec.serialize(bean);
        AccessBean result = BeanCodec.deserialize(node, AccessBean.class);

        assertEquals(bean.privateField, result.privateField);
        assertEquals(bean.protectedField, result.protectedField);
        assertEquals(bean.publicField, result.publicField);
    }

    // 测试用例 15: Boolean 包装类型处理
    public static class BooleanBean {
        public Boolean wrapper;
        public boolean primitive;
    }

    @Test
    public void testBooleanTypes() {
        BooleanBean bean = new BooleanBean();
        bean.wrapper = true;
        bean.primitive = false;

        ONode node = BeanCodec.serialize(bean);
        BooleanBean result = BeanCodec.deserialize(node, BooleanBean.class);

        assertEquals(bean.wrapper, result.wrapper);
        assertEquals(bean.primitive, result.primitive);
    }

    // 测试用例 16: 枚举名称大小写敏感
    @Test
    public void testEnumCaseSensitive() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", new ONode("error"));
        ONode node = new ONode(map);

        assertThrows(IllegalArgumentException.class, () -> {
            BeanCodec.deserialize(node, EnumBean.class); // 需要 "ERROR" 大写
        });
    }

    // 测试用例 17: 数字转换验证
    public static class NumberBean {
        public int intVal;
        public double doubleVal;
    }

    @Test
    public void testNumberConversion() {
        Map<String, ONode> map = new HashMap<>();
        map.put("intVal", new ONode(123.0)); // 使用ONode包装
        map.put("doubleVal", new ONode(456));
        ONode node = new ONode(map);

        NumberBean result = BeanCodec.deserialize(node, NumberBean.class);
        assertEquals(123, result.intVal);
        assertEquals(456.0, result.doubleVal, 0.001);
    }

    // 测试用例 18: 多态集合处理
    static class PolyBean {
        public List<Object> items;
    }

    @Test
    public void testPolymorphicList() {
        PolyBean bean = new PolyBean();
        bean.items = Arrays.asList("text", 123, true);

        ONode node = BeanCodec.serialize(bean);
        assertDoesNotThrow(() -> BeanCodec.deserialize(node, PolyBean.class));
    }

    // 测试用例 19: 循环引用检测
    public static class LoopBean {
        public LoopBean self;
    }

    @Test
    public void testCircularReference() {
        LoopBean bean = new LoopBean();
        bean.self = bean;

        assertThrows(StackOverflowError.class, () -> {
            BeanCodec.serialize(bean);
        });
    }

    // 测试用例 20: 静态内部类支持
    @Test
    public void testStaticInnerClass() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", new ONode("test"));
        ONode node = new ONode(map);

        assertDoesNotThrow(() -> {
            BeanCodec.deserialize(node, SimpleBean.class);
        });
    }
}