package features;

import demo.Book;
import demo.enums.BookType;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Context;
import org.noear.snack.core.Options;
import org.noear.snack.exception.SnackException;
import org.noear.snack.from.ObjectFromer;


/**
 * 枚举注解单元测试
 *
 * @author hans
 */
public class EnumTest {

    /**
     * 反序列化测试
     */
    @Test
    public void demo() {

        String poc = "{\"name\":\"西游记\",\"dict\":" + BookType.CLASSICS.getCode() + ",}";
        ONode oNode = ONode.loadStr(poc);
        //解析
        Book tmp = oNode.toObject(Book.class);

        System.out.println(tmp);
        assert BookType.CLASSICS == tmp.getDict();
    }

    /**
     * 序列化测试
     */
    @Test
    public void demo1() {
        Book book = new Book();
        book.setName("西游记");
        book.setDict(BookType.CLASSICS);
        ObjectFromer objectFromer = new ObjectFromer();
        Context context = new Context(Options.def(), book);
        objectFromer.handle(context);
        assert context.source == book;
    }

    /**
     * 序列化测试2
     */
    @Test
    public void demo2() {
        String json = "{name:'demo',dict:'9'}";

        try {
            ONode.deserialize(json, Book.class);
            assert false;
        } catch (SnackException e) {
            assert true;
        }
    }
}
