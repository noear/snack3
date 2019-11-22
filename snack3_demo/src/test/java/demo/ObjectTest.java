package demo;

import demo.models.*;
import org.junit.Test;
import org.noear.snack.core.Constants;
import org.noear.snack.core.Context;
import org.noear.snack.from.ObjectFromer;
import org.noear.snack.to.ObjectToer;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * 2019.01.30
 *
 * @author cjl
 */
public class ObjectTest {


    /**
     * 无限递归
     * @throws IllegalAccessException
     */
    @Test
    public void test000() throws IllegalAccessException {

        AModel a = new AModel();
        BModel b = new BModel();

        a.b = b;
        b.a = a;

        Context context = new Context(Constants.def, a);

        new ObjectFromer().handle(context);

        System.out.println(context.node.toJson());

        assert "{\"b\":{}}".equals(context.node.toJson());
    }

    @Test
    public void test1() throws Exception {

        UserModel user = new UserModel();
        user.id = 1111;
        user.name = "张三";
        user.note = null;
        OrderModel order = new OrderModel();
        order.user = user;
        order.order_id = 2222;
        order.order_num = "ddddd";

        Context context = new Context(Constants.def, order);

        new ObjectFromer().handle(context);

        System.out.println(context.node.toJson());

        context = new Context(Constants.def,context.node,OrderModel.class);
        new ObjectToer().handle(context);

        OrderModel order2 = (OrderModel)context.target;

        assert 1111 == order2.user.id;

    }

    @Test
    public void test2() throws IllegalAccessException {

        UserGroupModel group = new UserGroupModel();
        group.id = 9999;
        group.users = new ArrayList<>();
        group.names = new String[5];
        group.ids = new short[5];
        group.iids = new Integer[5];

        for (short i = 0; i < 5 ; i++) {
            UserModel user = new UserModel();
            user.id = i;
            user.name = "张三" + i;
            user.note = null;
            group.users.add(user);
            group.names[i] = "李四" + i;
            group.ids[i] = i;
            group.iids[i] = (int) i;
        }

        Context context = new Context(Constants.serialize, group);

        new ObjectFromer().handle(context);

        System.out.println(context.node.toJson());

        assert 1 == context.node.get("users").get(1).get(1).get("id").getInt();

        UserGroupModel g = context.node.toObject(UserGroupModel.class);

        assert g.id == 9999;

    }

    @Test
    public void test3() {
        List<Object> d = new ArrayList<>();
        final TypeVariable<? extends Class<? extends List>>[] typeParameters = d.getClass().getTypeParameters();
        for (TypeVariable<? extends Class<? extends List>> t : typeParameters){
            System.out.println(t.getName());
        }
    }

}
