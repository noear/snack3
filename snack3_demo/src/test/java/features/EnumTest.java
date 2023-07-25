package features;

import demo.Book;
import demo.enums.BookType;
import org.junit.Test;
import org.noear.snack.ONode;

public class EnumTest {
    @Test
    public void demo() {

        String poc = "{\"name\":\"西游记\",\"dict\":" + BookType.CLASSICS.getCode() + ",}";
        ONode oNode = ONode.loadStr(poc);
        //解析
        Book tmp = oNode.toObject(Book.class);

        System.out.println(tmp);
        assert BookType.CLASSICS == tmp.getDict();
    }
}
