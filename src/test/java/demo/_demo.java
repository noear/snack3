package demo;

import demo.models.UserModel;
import org.junit.Test;
import org.noear.snack.ONode;

import java.util.ArrayList;
import java.util.Collection;

public class _demo {
    @Test
    public void demo1()  {
        Collection<String>alias_ary  =new ArrayList<>();
        String text="Hello world!";

        ONode data = new ONode().exp((d)->{
            d.get("platform").val("all");

            d.get("audience").get("alias").addAll(alias_ary);

            d.get("options")
                    .set("apns_production",false);

            d.get("notification").exp(n->{
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
    public void demo2() {
        UserModel user = new UserModel();
        user.id = 1;
        user.name = "x";

        //exp的写法，方便获得 root 节点
        String json = new ONode().exp(n->{
            n.from("{a:1,b:2}").get("c").from(user);
        }).toJson();

        //无exp写法，需要给根安排个变量
        ONode root = ONode.map("{a:1,b:2}");
        root.get("c").from(user);
        json = root.toJson();

        /*
         * {a:1,b:2,c:{id:1,name:"x",note:null}}
         * */
    }

    public void demo3() throws Exception{
        UserModel tmp = ONode.map("{id:1,name:'x'}").toBean(UserModel.class);
    }
}
