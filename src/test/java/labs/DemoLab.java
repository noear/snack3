package labs;

import org.noear.snack.ONode;
import org.noear.snack.core.Options;

/**
 * @author noear 2025/3/17 created
 */
public class DemoLab {
    public void case1() {
        ONode oNode = new ONode();
        oNode.set("id", 1);
        oNode.getOrNew("layout").build(o -> {
            o.addNew().set("title", "开始").set("type", "start");
            o.addNew().set("title", "结束").set("type", "end");
        });
    }

    public void case2() {
        String store = "{}";
        ONode.loadBean(store).select("$..book[?(@.tags contains 'war')]").toBean(Book.class);
        ONode.loadJson(store).select("$.store.book.count()");

        ONode.loadBean(store).create("$.store.book[0].category").toJson();

        ONode.loadBean(store).delete("$..book[-1]");
    }

    public void case3() {
        ONode schemaNode = ONode.loadJson("{user:{name:''}}"); //定义架构
        Options options = Options.builder().schema(schemaNode).build();
        ONode.loadJson("{}", options);
    }

    static class Book {
    }
}
