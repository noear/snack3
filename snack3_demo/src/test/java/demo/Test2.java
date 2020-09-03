package demo;

import com.alibaba.fastjson.JSONObject;
import org.noear.snack.ONode;

public class Test2 {
    public void demo1(String json){

        String str = "";

        //加载json
        ONode oNode = ONode.loadStr(json);

        //找到resultData节点
        ONode resultData = oNode.get("resultData");
        if(resultData.isValue()){
            //如果它是value类型，则转换它的值（此处加载，会重新解析 resultData 里的字符串）
            resultData.val(ONode.loadStr(resultData.getString()));
        }

        //oNode，此时内部数据已变成期望的了
    }

    public void demo2(String json){

        String str = "";

        //加载json
        ONode oNode = ONode.loadStr(json);

        //找到resultData节点
        ONode select = oNode.get("resultData");

        //::之里做一次string解析；
        JSONObject parse = JSONObject.parseObject(select.val().toString());

        ONode strToJsonPrse = select.fill(parse);

        oNode.set("xxx",strToJsonPrse);
    }

    public void demo21(String json){

        String str = "";

        //加载json
        ONode oNode = ONode.loadStr(json);

        //找到resultData节点
        ONode select = oNode.get("resultData");

        //::之里做一次string解析；
        JSONObject parse = JSONObject.parseObject(select.val().toString());

        //代码，到这一部就可以了  // ONode 是个 树形结构，子节点变了。。。整个树就会变了。
        select.fill(parse);

        //但 fill 需要做类型解析；不是很好；改用：select.val(parse); 性能更好。
        //
        //fill(obj) 处理复杂的 java 类型；
        //
        //val(obj) 可以接收基础类型，如：Object, List<Object>, Map<String,Object>
        // 这个Object ，只能是基础类型。
    }
}
