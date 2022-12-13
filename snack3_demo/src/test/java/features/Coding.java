package features;

import _models.OrderModel;
import _models.UserModel;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.snack.core.DEFAULTS;
import org.noear.snack.core.utils.DateUtil;

import java.time.LocalDateTime;
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
        opts.addEncoder(OrderModel.class, (data, node) -> {
            node.set("id", data.order_id);
        });

        String json = ONode.loadObj(orderModel, opts).toJson();
        System.out.println(json);
        assert json.contains("1");

        //添加解码器
        opts.addDecoder(OrderModel.class, (node, type) -> {
            OrderModel tmp = new OrderModel();
            tmp.order_id = node.get("id").getInt();
            return tmp;
        });

        OrderModel rst = ONode.loadStr(json).toObject(OrderModel.class);
        System.out.println(rst);
        assert rst.order_id == 0;

        rst = ONode.loadStr(json, opts).toObject(OrderModel.class);
        System.out.println(rst);
        assert rst.order_id == 1;
    }

    public void demo0(){
        String json = "";

        Options opts = Options.def();
        opts.addDecoder(LocalDateTime.class, (node,type)->{
            //我随手写的，具体要自己解析下格式
            return LocalDateTime.parse(node.getString());
        });

        OrderModel tmp = ONode.load(json, opts).toObject(OrderModel.class);
    }

    @Test
    public void test1() {
        OrderModel orderModel = new OrderModel();
        orderModel.order_id = 1;

        Options options = Options.def();
        options.addEncoder(Date.class, (data, node) -> {
            node.val().setString(DateUtil.format(data, "yyyy-MM-dd"));
        });

        //添加编码器
        options.addEncoder(OrderModel.class, (data, node) -> {
            node.set("user", new ONode().set("uid", "1001"));
            node.set("order_time", null);
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
        options.addDecoder(UserModel.class, (node, type) -> {
            UserModel tmp = new UserModel();
            tmp.id = node.get("uid").getInt();
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
