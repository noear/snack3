package demo;

import _models.PanOcrModel;
import _models.ShanYunResModel;
import demo.enums.BookType;
import features.test5.A;
import lombok.Data;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Context;
import org.noear.snack.core.Handler;
import org.noear.snack.core.Options;
import org.noear.snack.from.JsonFromer;

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
