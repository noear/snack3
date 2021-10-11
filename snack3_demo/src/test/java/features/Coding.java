package features;

import _models.OrderModel;
import _models.UserModel;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Constants;

import java.util.Date;

/**
 * @author noear 2021/10/11 created
 */
public class Coding {
    @Test
    public void test0() {
        OrderModel orderModel = new OrderModel();
        orderModel.order_id = 1;
        Constants cfg = Constants.def();
        cfg.addEncoder(OrderModel.class, (source, target) -> {
            target.set("id", source.order_id);
        });

        String json = ONode.loadObj(orderModel, cfg).toJson();
        System.out.println(json);
        assert json.contains("1");

        cfg.addDecoder(OrderModel.class, (source, type) -> {
            OrderModel tmp = new OrderModel();
            tmp.order_id = source.get("id").getInt();
            return tmp;
        });

        OrderModel rst = ONode.loadStr(json).toObject(OrderModel.class);
        System.out.println(rst);
        assert rst.order_id == 0;

        rst = ONode.loadStr(json, cfg).toObject(OrderModel.class);
        System.out.println(rst);
        assert rst.order_id == 1;
    }

    @Test
    public void test1() {
        OrderModel orderModel = new OrderModel();
        orderModel.order_id = 1;
        Constants cfg = Constants.def();
        cfg.addEncoder(OrderModel.class, (source, target) -> {
            target.set("user", new ONode().set("uid","1001"));
            target.set("order_time", null);
        });

        String json = ONode.loadObj(orderModel, cfg).toJson();
        System.out.println(json);
        assert json.contains("1001");

        cfg.addDecoder(Date.class, (source, type) -> {
            if(source.isNull()){
                return new Date();
            }else{
                return source.getDate();
            }
        });

        cfg.addDecoder(UserModel.class, (source, type) -> {
            UserModel tmp = new UserModel();
            tmp.id = source.get("uid").getInt();
            return tmp;
        });

        OrderModel rst = ONode.loadStr(json).toObject(OrderModel.class);
        System.out.println(rst);
        assert rst.user.id == 0;

        rst = ONode.loadStr(json, cfg).toObject(OrderModel.class);
        System.out.println(rst);
        assert rst.user.id == 1001;
    }
}
