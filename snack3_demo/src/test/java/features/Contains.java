package features;

import com.alibaba.fastjson.JSONArray;
import org.junit.Test;
import org.noear.snack.ONode;

import java.util.Date;

public class Contains {
    @Test
    public void test1(){
        JSONArray tmp = (JSONArray)JSONArray.parse("[1,2,3,4,5]");
        assert  tmp.contains(2);

        tmp = (JSONArray)JSONArray.parse("[1,'2',3,4,5]");
        assert  tmp.contains("2");

        long times = System.currentTimeMillis();
        Date time = new Date(times);

        tmp = (JSONArray)JSONArray.parse("[1,'2',3,4,new Date("+times+")]");
        assert  tmp.contains(time);
    }

    @Test
    public void test2(){
        ONode tmp = ONode.loadStr("[1,2,3,4,5]");
        assert  tmp.contains(new ONode().val(2));

        tmp = ONode.loadStr("[1,'2',3,4,5]");
        assert  tmp.contains(new ONode().val("2"));

        long times = System.currentTimeMillis();
        Date time = new Date(times);

        tmp = ONode.loadStr("[1,'2',3,4,new Date("+times+")]");
        assert  tmp.contains(new ONode().val(time));
    }
}
