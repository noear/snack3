package features;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2021/9/11 created
 */
public class TemporaryClassTest {
    @Test
    public void test() {
        List<Map> list = new ArrayList();
        list.add(new HashMap<>());
        list.add(new HashMap<>());

        List userServices = new ArrayList();

        list.forEach(tmp -> {
            tmp.put("service", new HashMap<String, Object>() {{ //这是个临时类
                put("name", "noear");
                put("icon", "");
            }});

            userServices.add(tmp);
        });

        String json = ONode.serialize(userServices);
        System.out.println(json);

        try {
            Object obj = ONode.deserialize(json);
            assert false;
        } catch (Exception e) {
            e.printStackTrace();
            assert true;
        }
    }


    @Test
    public void test2(){
        List<Map> list = new ArrayList();
        list.add(new HashMap<>());
        list.add(new HashMap<>());

        List userServices = new ArrayList();

        list.forEach(tmp->{
            Map<String,Object> service = new HashMap<>();
            service.put("name","noear");
            service.put("icon","");

            tmp.put("service", service);

            userServices.add(tmp);
        });

        String json = ONode.serialize(userServices);
        System.out.println(json);

        Object obj = ONode.deserialize(json);
        assert obj != null;
    }
}
