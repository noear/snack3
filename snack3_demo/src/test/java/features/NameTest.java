package features;

import _models.BookModel;
import _models.BookViewModel;
import org.junit.Test;
import org.noear.snack.ONode;

import java.util.ArrayList;

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
}
