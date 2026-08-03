package org.noear.snack4.codec;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author noear 2025/10/9 created
 *
 */
public class OptionsDemo {
    public static void main(String[] args) {
        Options options = Options.of();

        options.addChecker(clzName -> {
            if(clzName.startsWith("com.demo")) {
                return TypeChecker.ALLOW;
            } else {
                return TypeChecker.DENY;
            }
        });

        //编码：使用类的名字作为数据
        options.addEncoder(Class.class, ((ctx, value, target) -> {
            return target.setValue(value.getName());
        }));

        //解码：把字符串作为类名加载（成为类）
        options.addDecoder(Class.class, (ctx, node) -> {
            return ctx.getOptions().loadClass(node.getString());
        });

        //options.addCreator()

        //测试：序列化
        Map<String, Class<?>> data = new HashMap<>();
        data.put("list", ArrayList.class);

        String json = ONode.ofBean(data, options).toJson();
        System.out.println(json);  // {"list":"java.util.ArrayList"}
        assert "{\"list\":\"java.util.ArrayList\"}".equals(json);

        //测试：反序列化
        data = ONode.ofJson(json, options).toBean(new TypeRef<Map<String, Class<?>>>() {
        });
        System.out.println(data.get("list")); // class java.util.ArrayList
        assert ArrayList.class.equals(data.get("list"));
    }
}
