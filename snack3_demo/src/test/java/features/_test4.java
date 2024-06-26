package features;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;

/**
 * @author noear 2022/5/13 created
 */
public class _test4 {
    @Test
    public void testONodeToJson() {
        String jsonSomeFieldValueContainBackslash = "{\"abc\":\"\\abc\"}";
        ONode oNode = ONode.loadStr(jsonSomeFieldValueContainBackslash);
        String toJson = oNode.options(opt -> opt.remove(Feature.TransferCompatible)).toJson();

        System.out.println(jsonSomeFieldValueContainBackslash);
        System.out.println(toJson);

        assert jsonSomeFieldValueContainBackslash.equals(toJson);
    }

    @Test
    public void test2() {
        String s = "{\n" +
                "  \"StartDate\" : 1657520667367,\n" +
                "  \"RowData\" : {\n" +
                "    \"id\" : 5346021874511400542,\n" +
                "    \"state\" : 1,\n" +
                "    \"start_member_id\" : -8325008841882316909,\n" +
                "    \"start_date\" : 1657520667367,\n" +
                "    \"approve_member_id\" : 0,\n" +
                "    \"approve_date\" : null,\n" +
                "    \"finishedflag\" : 0,\n" +
                "    \"ratifyflag\" : 0,\n" +
                "    \"ratify_member_id\" : 0,\n" +
                "    \"ratify_date\" : null,\n" +
                "    \"sort\" : 0,\n" +
                "    \"modify_member_id\" : -8325008841882316909,\n" +
                "    \"modify_date\" : 1657520667367,\n" +
                "    \"field0001\" : \"NULLNULLNULL003\",\n" +
                "    \"field0002\" : null,\n" +
                "    \"field0003\" : null,\n" +
                "    \"field0004\" : null,\n" +
                "    \"field0005\" : null,\n" +
                "    \"field0006\" : null,\n" +
                "    \"field0007\" : null,\n" +
                "    \"field0008\" : null,\n" +
                "    \"field0009\" : null,\n" +
                "    \"field0010\" : null,\n" +
                "    \"field0011\" : null,\n" +
                "    \"field0012\" : null,\n" +
                "    \"field0013\" : null,\n" +
                "    \"field0014\" : null,\n" +
                "    \"field0015\" : null,\n" +
                "    \"field0016\" : null,\n" +
                "    \"field0017\" : null,\n" +
                "    \"field0018\" : null,\n" +
                "    \"field0019\" : null,\n" +
                "    \"field0020\" : null,\n" +
                "    \"field0021\" : null,\n" +
                "    \"field0022\" : null,\n" +
                "    \"field0023\" : null,\n" +
                "    \"field0024\" : null,\n" +
                "    \"field0025\" : null,\n" +
                "    \"field0026\" : null,\n" +
                "    \"field0027\" : null,\n" +
                "    \"field0028\" : null,\n" +
                "    \"field0029\" : null,\n" +
                "    \"field0030\" : null,\n" +
                "    \"field0031\" : null,\n" +
                "    \"field0032\" : null,\n" +
                "    \"field0033\" : null,\n" +
                "    \"field0034\" : null,\n" +
                "    \"field0036\" : null,\n" +
                "    \"field0038\" : null,\n" +
                "    \"field0039\" : null,\n" +
                "    \"field0040\" : null,\n" +
                "    \"field0041\" : null,\n" +
                "    \"field0042\" : null,\n" +
                "    \"field0043\" : null,\n" +
                "    \"field0044\" : null,\n" +
                "    \"field0045\" : null,\n" +
                "    \"field0046\" : null,\n" +
                "    \"field0047\" : null,\n" +
                "    \"field0048\" : null,\n" +
                "    \"field0049\" : null,\n" +
                "    \"field0050\" : null,\n" +
                "    \"field0051\" : null,\n" +
                "    \"field0052\" : null,\n" +
                "    \"field0053\" : null,\n" +
                "    \"field0054\" : null,\n" +
                "    \"field0055\" : null,\n" +
                "    \"field0057\" : null,\n" +
                "    \"field0058\" : null,\n" +
                "    \"field0059\" : null,\n" +
                "    \"field0060\" : null,\n" +
                "    \"field0061\" : null,\n" +
                "    \"field0062\" : null,\n" +
                "    \"field0063\" : null,\n" +
                "    \"field0064\" : null,\n" +
                "    \"field0065\" : null,\n" +
                "    \"field0066\" : null,\n" +
                "    \"field0067\" : null,\n" +
                "    \"field0068\" : null,\n" +
                "    \"field0069\" : null,\n" +
                "    \"field0070\" : null,\n" +
                "    \"field0071\" : null,\n" +
                "    \"field0072\" : null,\n" +
                "    \"field0073\" : null,\n" +
                "    \"field0074\" : null,\n" +
                "    \"field0075\" : null,\n" +
                "    \"field0076\" : null,\n" +
                "    \"field0077\" : null,\n" +
                "    \"field0078\" : null,\n" +
                "    \"field0079\" : null,\n" +
                "    \"field0080\" : null,\n" +
                "    \"field0081\" : null,\n" +
                "    \"field0082\" : null,\n" +
                "    \"field0083\" : null,\n" +
                "    \"field0084\" : null,\n" +
                "    \"field0085\" : null,\n" +
                "    \"field0086\" : null,\n" +
                "    \"field0087\" : null,\n" +
                "    \"field0088\" : null,\n" +
                "    \"field0089\" : null,\n" +
                "    \"field0090\" : null,\n" +
                "    \"field0091\" : null,\n" +
                "    \"field0092\" : null,\n" +
                "    \"field0093\" : null,\n" +
                "    \"field0094\" : null,\n" +
                "    \"field0095\" : null,\n" +
                "    \"field0096\" : null,\n" +
                "    \"field0097\" : null,\n" +
                "    \"field0098\" : null,\n" +
                "    \"field0099\" : null,\n" +
                "    \"field0100\" : null,\n" +
                "    \"field0101\" : null,\n" +
                "    \"field0102\" : null,\n" +
                "    \"field0103\" : null,\n" +
                "    \"field0104\" : null,\n" +
                "    \"field0105\" : null,\n" +
                "    \"field0106\" : null,\n" +
                "    \"field0107\" : null,\n" +
                "    \"field0108\" : null,\n" +
                "    \"field0109\" : null,\n" +
                "    \"field0110\" : null,\n" +
                "    \"field0111\" : null,\n" +
                "    \"field0112\" : null,\n" +
                "    \"field0113\" : null,\n" +
                "    \"field0114\" : null,\n" +
                "    \"field0115\" : null,\n" +
                "    \"field0116\" : null,\n" +
                "    \"field0117\" : null,\n" +
                "    \"field0118\" : null,\n" +
                "    \"field0119\" : null,\n" +
                "    \"field0120\" : null,\n" +
                "    \"field0121\" : null,\n" +
                "    \"field0122\" : null,\n" +
                "    \"field0123\" : null,\n" +
                "    \"field0124\" : null,\n" +
                "    \"field0125\" : null,\n" +
                "    \"field0126\" : null,\n" +
                "    \"field0127\" : null,\n" +
                "    \"field0128\" : null,\n" +
                "    \"field0129\" : null,\n" +
                "    \"field0130\" : null,\n" +
                "    \"field0131\" : null,\n" +
                "    \"field0132\" : null,\n" +
                "    \"field0133\" : null,\n" +
                "    \"field0134\" : null,\n" +
                "    \"field0135\" : null,\n" +
                "    \"field0136\" : \"-4205980728472351383\",\n" +
                "    \"field0137\" : null,\n" +
                "    \"field0138\" : null,\n" +
                "    \"field0139\" : null,\n" +
                "    \"field0140\" : null,\n" +
                "    \"field0141\" : null,\n" +
                "    \"field0142\" : null,\n" +
                "    \"field0143\" : null,\n" +
                "    \"field0144\" : null,\n" +
                "    \"field0145\" : null,\n" +
                "    \"field0146\" : null,\n" +
                "    \"field0147\" : null,\n" +
                "    \"field0148\" : \"NULL\",\n" +
                "    \"field0149\" : \"NULL\",\n" +
                "    \"field0150\" : \"NULL\",\n" +
                "    \"field0151\" : \"2022003\",\n" +
                "    \"field0152\" : \"003\",\n" +
                "    \"field0153\" : \"天津市区街道\",\n" +
                "    \"field0154\" : \"单人间元/间/月；双人间元/间/月；四人间元/间/月；六人间元/间/月；八人间元/间/月；十人间元/间/月；\",\n" +
                "    \"field0155\" : \"—\",\n" +
                "    \"field0156\" : \"元/间/月\",\n" +
                "    \"field0157\" : 0.00,\n" +
                "    \"field0158\" : \"租金：个月；物业费：个月。\",\n" +
                "    \"field0159\" : null,\n" +
                "    \"field0160\" : null\n" +
                "  },\n" +
                "  \"code\" : \"0\",\n" +
                "  \"ApproveDate\" : null,\n" +
                "  \"FinishedFlag\" : 0,\n" +
                "  \"State\" : 1,\n" +
                "  \"Sort\" : 0,\n" +
                "  \"DataJson\" : \"[{\\\"field0152\\\":\\\"003\\\",\\\"field0153\\\":\\\"天津市区街道\\\",\\\"field0154\\\":\\\"单人间元/间/月；双人间元/间/月；四人间元/间/月；六人间元/间/月；八人间元/间/月；十人间元/间/月；\\\",\\\"field0155\\\":\\\"—\\\",\\\"field0156\\\":\\\"元/间/月\\\",\\\"field0157\\\":\\\"0.00\\\",\\\"field0158\\\":\\\"租金：个月；物业费：个月。\\\",\\\"field0150\\\":\\\"NULL\\\",\\\"field0151\\\":\\\"2022003\\\",\\\"field0149\\\":\\\"NULL\\\",\\\"id\\\":\\\"5346021874511400542\\\",\\\"state\\\":1,\\\"ratify_member_id\\\":\\\"0\\\",\\\"finishedflag\\\":0,\\\"field0136\\\":\\\"-4205980728472351383\\\",\\\"modify_member_id\\\":\\\"-8325008841882316909\\\",\\\"start_date\\\":\\\"2022-07-11 14:24:27\\\",\\\"field0148\\\":\\\"NULL\\\",\\\"approve_member_id\\\":\\\"0\\\",\\\"ratifyflag\\\":0,\\\"field0001\\\":\\\"NULLNULLNULL003\\\",\\\"sort\\\":0,\\\"start_member_id\\\":\\\"-8325008841882316909\\\",\\\"modify_date\\\":\\\"2022-07-11 14:24:27\\\"},{}]\",\n" +
                "  \"id\" : 5346021874511400542,\n" +
                "  \"DataMap\" : {\n" +
                "    \"field0031\" : null,\n" +
                "    \"field0152\" : \"003\",\n" +
                "    \"field0032\" : null,\n" +
                "    \"field0153\" : \"天津市区街道\",\n" +
                "    \"field0033\" : null,\n" +
                "    \"field0154\" : \"单人间元/间/月；双人间元/间/月；四人间元/间/月；六人间元/间/月；八人间元/间/月；十人间元/间/月；\",\n" +
                "    \"field0034\" : null,\n" +
                "    \"field0155\" : \"—\",\n" +
                "    \"field0156\" : \"元/间/月\",\n" +
                "    \"field0036\" : null,\n" +
                "    \"field0157\" : 0.00,\n" +
                "    \"field0158\" : \"租金：个月；物业费：个月。\",\n" +
                "    \"field0038\" : null,\n" +
                "    \"field0159\" : null,\n" +
                "    \"field0150\" : \"NULL\",\n" +
                "    \"field0030\" : null,\n" +
                "    \"field0151\" : \"2022003\",\n" +
                "    \"field0028\" : null,\n" +
                "    \"field0149\" : \"NULL\",\n" +
                "    \"field0029\" : null,\n" +
                "    \"id\" : 5346021874511400542,\n" +
                "    \"state\" : 1,\n" +
                "    \"approve_date\" : null,\n" +
                "    \"field0042\" : null,\n" +
                "    \"field0043\" : null,\n" +
                "    \"field0044\" : null,\n" +
                "    \"field0045\" : null,\n" +
                "    \"field0046\" : null,\n" +
                "    \"field0047\" : null,\n" +
                "    \"field0048\" : null,\n" +
                "    \"field0049\" : null,\n" +
                "    \"field0160\" : null,\n" +
                "    \"field0040\" : null,\n" +
                "    \"ratify_member_id\" : 0,\n" +
                "    \"field0041\" : null,\n" +
                "    \"finishedflag\" : 0,\n" +
                "    \"field0039\" : null,\n" +
                "    \"field0097\" : null,\n" +
                "    \"field0130\" : null,\n" +
                "    \"field0010\" : null,\n" +
                "    \"field0098\" : null,\n" +
                "    \"field0131\" : null,\n" +
                "    \"field0011\" : null,\n" +
                "    \"field0099\" : null,\n" +
                "    \"field0132\" : null,\n" +
                "    \"field0012\" : null,\n" +
                "    \"field0133\" : null,\n" +
                "    \"field0013\" : null,\n" +
                "    \"field0134\" : null,\n" +
                "    \"field0014\" : null,\n" +
                "    \"field0135\" : null,\n" +
                "    \"field0015\" : null,\n" +
                "    \"field0136\" : \"-4205980728472351383\",\n" +
                "    \"field0016\" : null,\n" +
                "    \"field0137\" : null,\n" +
                "    \"field0090\" : null,\n" +
                "    \"field0091\" : null,\n" +
                "    \"field0092\" : null,\n" +
                "    \"field0093\" : null,\n" +
                "    \"field0094\" : null,\n" +
                "    \"field0095\" : null,\n" +
                "    \"field0096\" : null,\n" +
                "    \"field0006\" : null,\n" +
                "    \"field0127\" : null,\n" +
                "    \"field0007\" : null,\n" +
                "    \"field0128\" : null,\n" +
                "    \"field0008\" : null,\n" +
                "    \"field0129\" : null,\n" +
                "    \"modify_member_id\" : -8325008841882316909,\n" +
                "    \"field0009\" : null,\n" +
                "    \"start_date\" : 1657520667367,\n" +
                "    \"field0020\" : null,\n" +
                "    \"field0141\" : null,\n" +
                "    \"field0021\" : null,\n" +
                "    \"field0142\" : null,\n" +
                "    \"field0022\" : null,\n" +
                "    \"field0143\" : null,\n" +
                "    \"field0023\" : null,\n" +
                "    \"field0144\" : null,\n" +
                "    \"field0024\" : null,\n" +
                "    \"field0145\" : null,\n" +
                "    \"field0025\" : null,\n" +
                "    \"field0146\" : null,\n" +
                "    \"field0026\" : null,\n" +
                "    \"field0147\" : null,\n" +
                "    \"field0027\" : null,\n" +
                "    \"field0148\" : \"NULL\",\n" +
                "    \"field0140\" : null,\n" +
                "    \"approve_member_id\" : 0,\n" +
                "    \"field0017\" : null,\n" +
                "    \"field0138\" : null,\n" +
                "    \"field0018\" : null,\n" +
                "    \"field0139\" : null,\n" +
                "    \"field0019\" : null,\n" +
                "    \"field0075\" : null,\n" +
                "    \"field0076\" : null,\n" +
                "    \"field0077\" : null,\n" +
                "    \"field0110\" : null,\n" +
                "    \"field0078\" : null,\n" +
                "    \"field0111\" : null,\n" +
                "    \"field0079\" : null,\n" +
                "    \"field0112\" : null,\n" +
                "    \"field0113\" : null,\n" +
                "    \"field0114\" : null,\n" +
                "    \"field0115\" : null,\n" +
                "    \"field0070\" : null,\n" +
                "    \"field0071\" : null,\n" +
                "    \"field0072\" : null,\n" +
                "    \"field0073\" : null,\n" +
                "    \"field0074\" : null,\n" +
                "    \"field0105\" : null,\n" +
                "    \"field0106\" : null,\n" +
                "    \"field0107\" : null,\n" +
                "    \"field0108\" : null,\n" +
                "    \"field0109\" : null,\n" +
                "    \"ratify_date\" : null,\n" +
                "    \"field0086\" : null,\n" +
                "    \"field0087\" : null,\n" +
                "    \"field0120\" : null,\n" +
                "    \"field0088\" : null,\n" +
                "    \"field0121\" : null,\n" +
                "    \"ratifyflag\" : 0,\n" +
                "    \"field0001\" : \"NULLNULLNULL003\",\n" +
                "    \"field0089\" : null,\n" +
                "    \"field0122\" : null,\n" +
                "    \"field0002\" : null,\n" +
                "    \"field0123\" : null,\n" +
                "    \"field0003\" : null,\n" +
                "    \"field0124\" : null,\n" +
                "    \"field0004\" : null,\n" +
                "    \"field0125\" : null,\n" +
                "    \"field0005\" : null,\n" +
                "    \"field0126\" : null,\n" +
                "    \"field0080\" : null,\n" +
                "    \"sort\" : 0,\n" +
                "    \"field0081\" : null,\n" +
                "    \"field0082\" : null,\n" +
                "    \"field0083\" : null,\n" +
                "    \"field0084\" : null,\n" +
                "    \"field0085\" : null,\n" +
                "    \"field0116\" : null,\n" +
                "    \"field0117\" : null,\n" +
                "    \"field0118\" : null,\n" +
                "    \"field0119\" : null,\n" +
                "    \"field0053\" : null,\n" +
                "    \"field0054\" : null,\n" +
                "    \"field0055\" : null,\n" +
                "    \"field0057\" : null,\n" +
                "    \"field0058\" : null,\n" +
                "    \"field0059\" : null,\n" +
                "    \"field0050\" : null,\n" +
                "    \"field0051\" : null,\n" +
                "    \"field0052\" : null,\n" +
                "    \"field0064\" : null,\n" +
                "    \"field0065\" : null,\n" +
                "    \"field0066\" : null,\n" +
                "    \"field0067\" : null,\n" +
                "    \"field0100\" : null,\n" +
                "    \"field0068\" : null,\n" +
                "    \"field0101\" : null,\n" +
                "    \"field0069\" : null,\n" +
                "    \"field0102\" : null,\n" +
                "    \"field0103\" : null,\n" +
                "    \"field0104\" : null,\n" +
                "    \"field0060\" : null,\n" +
                "    \"field0061\" : null,\n" +
                "    \"field0062\" : null,\n" +
                "    \"field0063\" : null,\n" +
                "    \"start_member_id\" : -8325008841882316909,\n" +
                "    \"modify_date\" : 1657520667367\n" +
                "  }\n" +
                "}";

        //System.out.println(s);
        //System.out.println("\n\n\n\n\n");
        ONode oNode = ONode.loadStr(s);
        System.out.println(oNode.toJson());
        System.out.println("start_member_id:"+oNode.get("DataMap").get("start_member_id").getString());
        assert oNode.get("field0063").isNull();
        assert oNode.get("DataMap").get("start_member_id").getLong() == -8325008841882316909L;
    }
}
