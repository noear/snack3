package features;

import demo.Book;
import demo.enums.BookType;
import org.junit.jupiter.api.Test;
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
    public void case1() {

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
    public void case2() {
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
    public void case3() {
        String json = "{name:'demo',dict:'9'}";

        try {
            ONode.deserialize(json, Book.class);
            assert false;
        } catch (SnackException e) {
            assert true;
        }
    }

    @Test
    public void case4() {
        String s1 = "'input'";
        String s2 = "'number'";
        String s3 = "'select'";
        String s4 = "'switcher'";
        ConfigControlType type1 = ONode.deserialize(s1, ConfigControlType.class);
        ConfigControlType type2 = ONode.deserialize(s2, ConfigControlType.class);
        ConfigControlType type3 = ONode.deserialize(s3, ConfigControlType.class);
        ConfigControlType type4 = ONode.deserialize(s4, ConfigControlType.class);
        System.out.println(type1);
        System.out.println(type2);
        System.out.println(type3);
        System.out.println(type4);

        assert type1 == ConfigControlType.input;
        assert type2 == ConfigControlType.number;
        assert type3 == ConfigControlType.select;
        assert type4 == ConfigControlType.switcher;
    }

    @Test
    public void case5() {
        String s1 = "input";
        String s2 = "number";
        String s3 = "select";
        String s4 = "switcher";
        ConfigControlType type1 = ONode.deserialize(s1, ConfigControlType.class);
        ConfigControlType type2 = ONode.deserialize(s2, ConfigControlType.class);
        ConfigControlType type3 = ONode.deserialize(s3, ConfigControlType.class);
        ConfigControlType type4 = ONode.deserialize(s4, ConfigControlType.class);
        System.out.println(type1);
        System.out.println(type2);
        System.out.println(type3);
        System.out.println(type4);

        assert type1 == ConfigControlType.input;
        assert type2 == ConfigControlType.number;
        assert type3 == ConfigControlType.select;
        assert type4 == ConfigControlType.switcher;
    }

    public static enum ConfigControlType {
        input,
        number,
        select,
        switcher,
    }
}
