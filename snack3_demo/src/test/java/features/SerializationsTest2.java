package features;

import _models.UserGroupModel;
import _models.UserGroupModel2;
import _models.UserModel;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.TypeRef;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class SerializationsTest2 {

    public Object buildObj(){
        UserGroupModel group = new UserGroupModel();
        group.id = 9999;
        group.users = new ArrayList<>();
        group.users2 = new LinkedHashMap<>();
        group.users3 = new TreeSet<>();
        group.names = new String[5];
        group.ids = new short[5];
        group.iids = new Integer[5];
        group.dd = new BigDecimal(12);
        group.tt1 = new Timestamp(new Date().getTime());
        group.tt2 = new Date();

        for (short i = 0; i < 5 ; i++) {
            UserModel user = new UserModel();
            user.id = i;
            user.name = "张三" + i;
            user.note = null;
            group.users.add(user);
            group.users2.put(Integer.valueOf(i),user);
            group.names[i] = "李四" + i;
            group.ids[i] = i;
        }

        return group;
    }

    public String buildJson(){
        return ONode.loadObj(buildObj()).toJson();
    }

    @Test
    public void test01(){
        String tmp = ONode.serialize(buildObj());
        System.out.println(tmp);
    }

    @Test
    public void test02(){
        String tmp = ONode.serialize(buildObj());
        tmp = tmp.replaceAll("UserGroupModel", "UserGroupModel2");
        UserGroupModel2 tmp2 = ONode.deserialize(tmp, UserGroupModel2.class);

        assert tmp2.users != null;
        assert tmp2.users.length > 2;
        System.out.println(tmp2);
    }

    @Test
    public void test10() throws Exception {
        String json0 = buildJson();

        System.out.println(json0);
        UserGroupModel group0 = ONode.loadStr(json0)
                .toObject((new TypeRef<UserGroupModel>(){}).getClass());

        assert group0.id == 9999;
    }

    @Test
    public void test11() throws Exception {
        String json0 = buildJson();

        System.out.println(json0);
        UserGroupModel group0 = ONode.loadStr(json0)
                .toObject(UserGroupModel.class);

        assert group0.id == 9999;
    }

    @Test
    public void test20() throws Exception {
        String json0 = buildJson();

        System.out.println(json0);
        List<UserModel> group0 = ONode.loadStr(json0).get("users")
                                      .toObject((new ArrayList<UserModel>(){}).getClass());

        assert group0.size() == 5;
    }

    @Test
    public void test21() throws Exception {
        String json0 = buildJson();

        System.out.println(json0);
        List<UserModel> group0 = ONode.loadStr(json0).get("users")
                                      .toObject((new TypeRef<List<UserModel>>(){}).getClass());

        assert group0.size() == 5;
    }
}
