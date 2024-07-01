package features;

import _models.BookModel;
import _models.BookViewModel;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2021/1/1 created
 */
public class NameTest {
    @Test
    public void test() {
        BookModel user = new BookModel();
        user.id = 12;
        user.bookname = "noear";
        user.note = "test";

        String json = ONode.stringify(user);
        System.out.println(json);

        assert json.contains("name");


        BookModel user2 = ONode.deserialize(json, BookModel.class);
        System.out.println(user2.bookname);

        assert "noear".equals(user2.bookname);
    }


    @Test
    public void test2() {
        BookViewModel vm = new BookViewModel();
        vm.list = new ArrayList<>();

        BookModel user = new BookModel();
        user.id = 12;
        user.bookname = "noear";
        user.note = "test";

        vm.list.add(user);


        user = new BookModel();
        user.id = 13;
        user.bookname = "ddd";
        user.note = "test";

        vm.list.add(user);

        String json = ONode.stringify(vm);
        System.out.println(json);

        assert json.contains("name");


        BookViewModel vm2 = ONode.deserialize(json, BookViewModel.class);
        System.out.println(vm2.list.get(0).bookname);

        assert "noear".equals(vm2.list.get(0).bookname);
    }

    @Test
    public void test3() {
        Options options = Options.of(Feature.OrderedField,
                Feature.WriteDateUseTicks,
                Feature.QuoteFieldNames);

        String val = new ONode(options).get("name").getString();
        System.out.println(val);
        assert val == null;


        String val2 = new ONode().get("name").getRawString();
        System.out.println(val2);
        assert val2 == null;
    }

    @Test
    public void test4() {
        Map<String, Object> data = new HashMap<>();
        data.put("c:\\", "c:\\");

        String json = ONode.stringify(data, options);
        System.out.println(json);

        String json2 = ONode.load(json).toJson();
        System.out.println(json2);
        assert json2.equals(json);
    }

    private static final Options options = Options.def()
            .add(Feature.EnumUsingName);
}
