package xml;

import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Constants;
import org.noear.snack.from.XmlFromer;
import org.noear.snack.to.XmlToer;

import java.util.List;

public class _test {
    @Test
    public void test1(){
        String str ="<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                "<mapper namespace=\"weed3demo2.xmlsql2\" :db=\"db\">\n" +
                "    <sql id=\"appx_get\" return=\"int\" note=\"随便取条数据的ID\">\n" +
                "        select app_id from appx limit 1\n" +
                "    </sql>\n" +
                "\n" +
                "    <sql id=\"appx_get2\"\n" +
                "         return=\"webapp.model.AppxModel\"\n" +
                "         note=\"根据id取条数据\"\n" +
                "         caching=\"test\"\n" +
                "         usingCache=\"300\"\n" +
                "         cacheTag=\"app_${app_id}\">\n" +
                "        select * from appx where app_id = @{app_id:int} limit 1\n" +
                "    </sql>\n" +
                "\n" +
                "    <sql id=\"appx_get3\" return=\"Map\" note=\"取一批ID\" cacheClear=\"test\">\n" +
                "        select * from ${tb:String} where app_id = @{app_id:int} limit 1\n" +
                "    </sql>\n" +
                "\n" +
                "    <sql id=\"appx_getlist\" return=\"List[webapp.model.AppxModel]\">\n" +
                "        select * from appx where app_id>@{app_id:int} order by app_id asc limit 4\n" +
                "    </sql>\n" +
                "\n" +
                "    <sql id=\"appx_getids\" return=\"List[Integer]\">\n" +
                "        select app_id from appx limit 4\n" +
                "    </sql>\n" +
                "</mapper>";

        ONode tmp = ONode.load(str, Constants.def(), new XmlFromer());

        String str2 = tmp.to(new XmlToer());

        System.out.println(str2);

        String str3 = tmp.toJson();

        System.out.println(str3);
    }

    @Test
    public void test2(){
        String json = "[{\"code\":0,\"name\":\"缺陷\",\"icon\":\"fa-bug\"},{\"code\":1,\"name\":\"改进\",\"icon\":\"fa-twitter\"},{\"code\":2,\"name\":\"需求\",\"icon\":\"fa-circle-o\"}]";

        Object tmp = ONode.loadStr(json).to(new XmlToer());

        System.out.println(tmp);
    }
}
