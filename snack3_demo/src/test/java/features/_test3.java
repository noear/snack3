package features;

import _models.PanOcrModel;
import _models.ShanYunResModel;
import org.junit.Test;
import org.noear.snack.ONode;

import java.util.ArrayList;
import java.util.List;

public class _test3 {
    @Test
    public void test() {
        String json = "{\"reportId\":\"N4293fAVK86Jq1Mf465B\",\"statusCode\":0,\"statusMessage\":\"E_SUCCESS\",\"IP\":\"122.224.92.122\",\"data\":{\"errorCode\":2000,\"errMessage\":\"E_OCR_SUCCESS\",\"cardType\":\"PAN_FRONT\",\"panCode\":\"MEUPS2579N\",\"panName\":\"YEKKALADEVI SUBRAMANYAM\",\"dateOfBirth\":\"05/03/1988\",\"fatherName\":\"YEKKALADEVI RAGHUNANDANA RAO\"}}";


        ONode oNode = ONode.loadStr(json);
        oNode.get("data").set("@type", PanOcrModel.class.getName());

        ShanYunResModel<PanOcrModel> tmp = oNode.toObject(ShanYunResModel.class);

        assert tmp.data instanceof PanOcrModel;
    }

    @Test
    public void test2(){
        String json = "{\"request\":{\"taskId\":\"6713480756628292224\",\"action\":1},\"yqn_groovy_debug_431\":\"F:\\\\Mr.zhang\\\\idea\\\\company\\\\api-composer\\\\groovys\\\\api_32839_完成海运进口保险任务\\\\431.groovy\",\"Activity_18dy13k\":{},\"yqn_groovy_debug_432\":\"F:\\\\Mr.zhang\\\\idea\\\\company\\\\api-composer\\\\groovys\\\\api_32839_完成海运进口保险任务\\\\432.groovy\",\"yqn_groovy_debug_440\":\"F:\\\\Mr.zhang\\\\idea\\\\company\\\\api-composer\\\\groovys\\\\api_32839_完成海运进口保险任务\\\\440.groovy\",\"yqn_groovy_debug_430\":\"F:\\\\Mr.zhang\\\\idea\\\\company\\\\api-composer\\\\groovys\\\\api_32839_完成海运进口保险任务\\\\430.groovy\",\"yqn_groovy_debug_\":\"true\",\"yqn_groovy_debug_435\":\"F:\\\\Mr.zhang\\\\idea\\\\company\\\\api-composer\\\\groovys\\\\api_32839_完成海运进口保险任务\\\\435.groovy\",\"header\":{\"accessToken\":null,\"viewAll\":null,\"xUserId\":null,\"xUserName\":null,\"xBPId\":null,\"xLangCode\":null,\"xSystemLangCode\":null,\"xAppId\":\"42061\",\"xSourceAppId\":null,\"xClientIp\":\"192.168.50.80\",\"xOpenId\":null,\"xOpenPlatform\":null,\"xDeviceId\":null,\"xToken\":null,\"xIsTest\":null,\"xTestFlag\":null,\"xTraceId\":null,\"xCallerId\":null,\"xJsFinger\":null,\"xSession\":null,\"xPushToken\":null,\"xConnectTimeoutMillis\":0,\"xReadTimeoutMillis\":0,\"yqnCatContext\":null},\"_baseUtil\":{\"PROJECT_TYPE\":\"42060_project_type\",\"APPLICATION_LIST\":\"42060_application_list\",\"applicationItemMap\":{\"1\":{\"id\":17014,\"categoryId\":226,\"key\":null,\"sort\":1,\"isDefault\":true,\"chineseName\":\"订单服务\",\"englishName\":\"Order-Service\",\"shortCode\":\"ORDER-SERVICE\",\"description\":null,\"selectedValue\":1},\"2\":{\"id\":17015,\"categoryId\":226,\"key\":null,\"sort\":2,\"isDefault\":false,\"chineseName\":\"用户服务\",\"englishName\":\"User-Service\",\"shortCode\":\"USER-SERVICE\",\"description\":null,\"selectedValue\":2},\"3\":{\"id\":17016,\"categoryId\":226,\"key\":null,\"sort\":3,\"isDefault\":false,\"chineseName\":\"客户服务\",\"englishName\":\"CRM-Service\",\"shortCode\":\"CRM-SERVICE\",\"description\":null,\"selectedValue\":3},\"4\":{\"id\":17095,\"categoryId\":226,\"key\":null,\"sort\":4,\"isDefault\":false,\"chineseName\":\"基础数据服务\",\"englishName\":\"CPF-FACADE\",\"shortCode\":\"CPF-FACADE\",\"description\":null,\"selectedValue\":4},\"5\":{\"id\":17099,\"categoryId\":226,\"key\":null,\"sort\":5,\"isDefault\":false,\"chineseName\":\"订单中心服务\",\"englishName\":\"ORDER-CENTER-SERVICE\",\"shortCode\":\"ORDER-CENTER-SERVICE\",\"description\":null,\"selectedValue\":5},\"6\":{\"id\":17107,\"categoryId\":226,\"key\":null,\"sort\":6,\"isDefault\":false,\"chineseName\":\"基础后台服务\",\"englishName\":\"CPF-SERVICE\",\"shortCode\":\"CPF-SERVICE\",\"description\":null,\"selectedValue\":6},\"7\":{\"id\":17108,\"categoryId\":226,\"key\":null,\"sort\":7,\"isDefault\":false,\"chineseName\":\"保险履约服务\",\"englishName\":\"INSURANCE-SERVICE\",\"shortCode\":\"INSURANCE-SERVICE\",\"description\":null,\"selectedValue\":7},\"8\":{\"id\":17154,\"categoryId\":226,\"key\":null,\"sort\":8,\"isDefault\":false,\"chineseName\":\"供应商服务\",\"englishName\":\"SUPPLIER-SERVICE\",\"shortCode\":\"SUPPLIER-SERVICE\",\"description\":null,\"selectedValue\":8},\"9\":{\"id\":17157,\"categoryId\":226,\"key\":null,\"sort\":9,\"isDefault\":false,\"chineseName\":\"用户中心-原子服务\",\"englishName\":\"YQN-USER-SERVICE\",\"shortCode\":\"YQN-USER-SERVICE\",\"description\":null,\"selectedValue\":9},\"10\":{\"id\":17158,\"categoryId\":226,\"key\":null,\"sort\":10,\"isDefault\":false,\"chineseName\":\"工作流服务\",\"englishName\":\"WF-SERVICE\",\"shortCode\":\"WF-SERVICE\",\"description\":null,\"selectedValue\":10},\"11\":{\"id\":17159,\"categoryId\":226,\"key\":null,\"sort\":11,\"isDefault\":false,\"chineseName\":\"bpm服务\",\"englishName\":\"BPM-SERVICE\",\"shortCode\":\"BPM-SERVICE\",\"description\":null,\"selectedValue\":11}},\"projectTypeMap\":{\"1\":\"专项\",\"2\":\"迭代\",\"3\":\"hotfix\"},\"applicationMap\":{\"1\":\"订单服务\",\"2\":\"用户服务\",\"3\":\"客户服务\",\"4\":\"基础数据服务\",\"5\":\"订单中心服务\",\"6\":\"基础后台服务\",\"7\":\"保险履约服务\",\"8\":\"供应商服务\",\"9\":\"用户中心-原子服务\",\"10\":\"工作流服务\",\"11\":\"bpm服务\"}},\"yqn_groovy_debug_428\":\"F:\\\\Mr.zhang\\\\idea\\\\company\\\\api-composer\\\\groovys\\\\api_32839_完成海运进口保险任务\\\\428.groovy\",\"yqn_groovy_debug_426\":\"F:\\\\Mr.zhang\\\\idea\\\\company\\\\api-composer\\\\groovys\\\\api_32839_完成海运进口保险任务\\\\426.groovy\"}";


        ONode oNode = ONode.load(json);

        assert oNode.isObject();

        String jsonPath= "$.request.taskId";

        ONode tmp = oNode.select(jsonPath);

        System.out.print(tmp.toJson());
    }

    @Test
    public void test3(){
        String json = "{\n" +
                "\t\"fromBranch\": {\n" +
                "\t\t\"key1\": {\n" +
                "\t\t\t\"a\": \"a\"\n" +
                "\t\t},\n" +
                "\t\t\"key2\": {\n" +
                "\t\t\t\"b\": \"b\"\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"keyword\": \"key1\"\n" +
                "}";

        ONode oNode = ONode.loadStr(json);

        ONode tmp = oNode.select("$.fromBranch[$.keyword]");

        System.out.print(tmp.toJson());
        assert "{\"a\":\"a\"}".equals(tmp.toJson());
    }

    @Test
    public void test4(){
        String json = "['1','2']";

        //用静态函数反序列化（基础类型，不需要指明类型）
        List<String> list = ONode.deserialize(json);
        assert list.size() == 2;

        //用静态函数反序列化（也可以指下类型）
        List<String> list2 = ONode.deserialize(json, (new ArrayList<String>()).getClass());
        assert list2.size() == 2;
    }

    @Test
    public void test5(){
        String json = "['1','2']";

        ONode oNode = ONode.loadStr(json);

        List<String> list = oNode.toObject(null);
        assert list.size() == 2;

        list = oNode.toObject((new ArrayList<String>()).getClass());
        assert list.size() == 2;

        List<String> list2 = oNode.toObjectList(String.class);
        assert list2.size() == 2;
    }
}
