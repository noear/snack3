package features;

import _models.OrderModel;
import _models.UserModel;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.snack.core.DEFAULTS;

import java.time.LocalTime;
import java.util.Date;

/**
 * @author noear 2021/10/11 created
 */
public class Coding {
    @Test
    public void test0() {
        OrderModel orderModel = new OrderModel();
        orderModel.order_id = 1;
        Options opts = Options.def();
        //添加编码器
        opts.addEncoder(OrderModel.class, (source, target) -> {
            target.set("id", source.order_id);
        });

        String json = ONode.loadObj(orderModel, opts).toJson();
        System.out.println(json);
        assert json.contains("1");

        //添加解码器
        opts.addDecoder(OrderModel.class, (source, type) -> {
            OrderModel tmp = new OrderModel();
            tmp.order_id = source.get("id").getInt();
            return tmp;
        });

        OrderModel rst = ONode.loadStr(json).toObject(OrderModel.class);
        System.out.println(rst);
        assert rst.order_id == 0;

        rst = ONode.loadStr(json, opts).toObject(OrderModel.class);
        System.out.println(rst);
        assert rst.order_id == 1;
    }

    @Test
    public void test1() {
        OrderModel orderModel = new OrderModel();
        orderModel.order_id = 1;
        Options options = Options.def();
        //添加编码器
        options.addEncoder(OrderModel.class, (data, node) -> {
            node.set("user", new ONode().set("uid", "1001"));
            node.set("order_time", null);
        });
        options.addEncoder(Date.class, (data, node) -> {
            node.val().setNumber(data.getTime());
        });

        String json = ONode.loadObj(orderModel, options).toJson();
        System.out.println(json);
        assert json.contains("1001");

        //添加解码器
        options.addDecoder(Date.class, (node, type) -> {
            if (node.isNull()) {
                return new Date();
            } else {
                return node.getDate();
            }
        });


        //添加解码器
        options.addDecoder(LocalTime.class, (node, type) -> {
            if (node.isNull()) {
                return LocalTime.now();
            } else {
                return node.getDate()
                        .toInstant()
                        .atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId())
                        .toLocalTime();
            }
        });

        //添加解码器
        options.addDecoder(UserModel.class, (source, type) -> {
            UserModel tmp = new UserModel();
            tmp.id = source.get("uid").getInt();
            return tmp;
        });

        OrderModel rst = ONode.loadStr(json).toObject(OrderModel.class);
        System.out.println(rst);
        assert rst.user.id == 0;

        rst = ONode.loadStr(json, options).toObject(OrderModel.class);
        System.out.println(rst);
        assert rst.user.id == 1001;
    }
}
