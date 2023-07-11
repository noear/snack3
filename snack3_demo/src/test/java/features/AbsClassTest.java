package features;

import _models.AnnoTreeDTO;
import _models.UserModel2;
import org.junit.Test;
import org.noear.snack.ONode;

import java.util.Arrays;

/**
 * @author noear 2023/7/11 created
 */
public class AbsClassTest {
    @Test
    public void test(){
        UserModel2 data = new UserModel2(){{
            this.id = 1;
            this.name = "noear";
            this.note  ="test";
        }};

        String json= ONode.stringify(data);

        System.out.println(json);

        assert json.contains("noear") == false;
        assert json.contains("test") == true;
    }

    @Test
    public void test2(){
        AnnoTreeDTO<String> data = new AnnoTreeDTO<String>(){{
            setId("1");
            setParentId("0");
            setLabel("test");
        }};

        String json= ONode.stringify(data);

        System.out.println(json);

        assert json.contains("noear") == false;
        assert json.contains("test") == true;
    }

    @Test
    public void test3(){
        AnnoTreeDTO<String> node1 = new AnnoTreeDTO<String>(){{
            setId("2");
            setParentId("1");
            setLabel("test");
        }};

        AnnoTreeDTO<String> data = new AnnoTreeDTO<String>(){{
            setId("1");
            setParentId("0");
            setLabel("test");
            setChildren(Arrays.asList(node1));
        }};

        String json= ONode.stringify(data);

        System.out.println(json);

        assert json.contains("noear") == false;
        assert json.contains("test") == true;
    }
}
