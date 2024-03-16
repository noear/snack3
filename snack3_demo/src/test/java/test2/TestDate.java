package test2;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;

/**
 * @author lingkang
 * Created by 2024/3/16
 */
public class TestDate {
    @Test
    public void test01() {
        String payContent = "{\"mchid\":\"1623051071\",\"appid\":\"wx530baf52565c2828\",\"out_trade_no\":\"WX1710597479999990\",\"transaction_id\":\"4200002188202403160282491796\",\"trade_type\":\"NATIVE\",\"trade_state\":\"SUCCESS\",\"trade_state_desc\":\"支付成功\",\"bank_type\":\"OTHERS\",\"attach\":\"test\",\"success_time\":\"2024-03-16T21:58:25+08:00\",\"payer\":{\"openid\":\"oavVX6iIjDGt3Jpt6f7S3GoNjWJM\"},\"amount\":{\"total\":1,\"payer_total\":1,\"currency\":\"CNY\",\"payer_currency\":\"CNY\"}}";
        ONode node = ONode.loadStr(payContent);
        System.out.println(node.get("success_time").getDate());
        if (node.get("success_time").getDate() == null)
            throw new IllegalArgumentException("时间解析失败: " + "2024-03-16T21:58:25+08:00");
        JSONObject parse = (JSONObject) JSON.parse(payContent);
        System.out.println(parse.getDate("success_time"));
    }
}
