package features;

import _models.UserModel2;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.TypeRef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2021/1/1 created
 */
public class NameTest {
    @Test
    public void test() {
        UserModel2 user = new UserModel2();
        user.id = 12;
        user.nickname = "noear";
        user.note = "test";

        String json = ONode.stringify(user);
        System.out.println(json);

        assert json.contains("name");


        UserModel2 user2 = ONode.deserialize(json, UserModel2.class);
        System.out.println(user2.nickname);

        assert "noear".equals(user2.nickname);
    }


    @Test
    public void test2() {
        List<UserModel2> list = new ArrayList<>();
        UserModel2 user = new UserModel2();
        user.id = 12;
        user.nickname = "noear";
        user.note = "test";

        list.add(user);


        user = new UserModel2();
        user.id = 13;
        user.nickname = "ddd";
        user.note = "test";

        list.add(user);

        String json = ONode.stringify(list);
        System.out.println(json);

        assert json.contains("name");


        List<UserModel2> list2 = ONode.deserialize(json, new TypeRef<List<UserModel2>>(){}.getClass());
        System.out.println(list2.get(0).nickname);

        assert "noear".equals(list2.get(0).nickname);
    }
}
