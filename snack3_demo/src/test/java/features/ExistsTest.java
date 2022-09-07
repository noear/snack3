package features;

import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.ONodeType;

/**
 * @author noear 2022/9/7 created
 */
public class ExistsTest {
    @Test
    public void test1(){
        ONode oNode = new ONode();
        assert  oNode.select("$.user").isNull();
        assert  oNode.select("$.user").nodeType() == ONodeType.Null;

        ONode oNode2 = ONode.loadStr("{user:1}");
        assert  oNode2.select("$.user").isNull() == false;
        assert  oNode2.select("$.user").nodeType() != ONodeType.Null;

        ONode oNode3 = ONode.loadStr("{user:null}");
        assert  oNode3.select("$.user").isNull();
        assert  oNode3.select("$.user").nodeType() != ONodeType.Null;
    }

    @Test
    public void test2(){
        ONode oNode = new ONode();
        assert  oNode.select("$.user").isNull();
        assert  oNode.select("$.user").isUndefined();

        ONode oNode2 = ONode.loadStr("{user:1}");
        assert  oNode2.select("$.user").isNull() == false;
        assert  oNode2.select("$.user").isUndefined() == false;

        ONode oNode3 = ONode.loadStr("{user:null}");
        assert  oNode3.select("$.user").isNull();
        assert  oNode3.select("$.user").isUndefined() == false;
    }

    @Test
    public void test3(){
        ONode oNode4 = ONode.loadStr("[{user:null}]");
        assert  oNode4.select("$[0].user").isNull();
        assert  oNode4.select("$[0].user").isUndefined() == false;
        assert  oNode4.exists("$[0].user");

        ONode oNode5 = ONode.loadStr("[{user:1}]");
        assert  oNode5.select("$[0].user").isNull() == false;
        assert  oNode5.select("$[0].user").isUndefined() == false;
        assert  oNode5.exists("$[0].user");
        assert  oNode5.select("$[0].user").getInt() == 1;
    }

    @Test
    public void test4(){
        ONode oNode4 = ONode.loadStr("[{user:null}]");
        assert  oNode4.select("$[0].user.name").isNull();
        assert  oNode4.select("$[0].user.name").isUndefined();
        assert  oNode4.exists("$[0].user.name") == false;

        ONode oNode5 = ONode.loadStr("[{user:{}}}]");
        assert  oNode5.select("$[0].user.name").isNull();
        assert  oNode5.select("$[0].user.name").isUndefined();
        assert  oNode5.exists("$[0].user.name") == false;
    }

    @Test
    public void test5(){
        ONode oNode4 = ONode.loadStr("[{user:null}]");
        assert  oNode4.select("$[0].user.name.first").isNull();
        assert  oNode4.select("$[0].user.name.first").isUndefined();
        assert  oNode4.exists("$[0].user.name.first") == false;

        ONode oNode5 = ONode.loadStr("[{user:{}}}]");
        assert  oNode5.select("$[0].user.name.first").isNull();
        assert  oNode5.select("$[0].user.name.first").isUndefined();
        assert  oNode5.exists("$[0].user.name.first") == false;
    }
}
