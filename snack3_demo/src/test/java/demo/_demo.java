package demo;

import demo.models.UserModel;
import org.junit.Test;
import org.noear.snack.ONode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class _demo {


    @Test
    public void demo4() throws Exception{
        String tmp = "{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}";
        //1.加载json
        Object n = ONode.deserialize(tmp);

        assert n instanceof Map;
    }

    @Test
    public void demo10() throws Exception{
        UserModel user = new UserModel();
        user.id = 1;
        user.name = "x";

        //demo1::序列化
        String json = ONode.serialize(user);

        //demo2::反序列化
        UserModel user2 = ONode.deserialize(json, UserModel.class);

        assert user.id == user2.id;
    }

    @Test
    public void demo20()  {
        Collection<String>alias_ary  =new ArrayList<>();
        String text="Hello world!";

        ONode data = new ONode().build((d)->{
            d.get("platform").val("all");

            d.get("audience").get("alias").addAll(alias_ary);

            d.get("options")
                    .set("apns_production",false);

            d.get("notification").build(n->{
                n.get("ios")
                        .set("alert",text)
                        .set("badge",0)
                        .set("sound","happy");
            });
        });

        String message = data.toJson();

        assert message!=null;
    }

    @Test
    public void demo31() {
        UserModel user = new UserModel();
        user.id = 1;
        user.name = "x";

        //exp的写法，方便获得 root 节点
        String json = new ONode().build(n->{
            //load : 为当前节点加载数据
            //from : 加载数据并生成新节点
            n.fill("{a:1,b:2}").get("c").fill(user);
        }).toJson();

        //无exp写法，需要给根安排个变量
        ONode root = ONode.load("{a:1,b:2}");
        root.get("c").load(user);
        json = root.toJson();

        /*
         * {a:1,b:2,c:{id:1,name:"x",note:null}}
         * */
    }

    public void demo32() throws Exception{
        UserModel tmp = ONode.load("{id:1,name:'x'}").toBean(UserModel.class);
    }

    public void demo40(){

    }
}
