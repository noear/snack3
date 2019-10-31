package demo;

import org.junit.Test;
import org.noear.snack.ONode;

public class _test {
    @Test
    public void test(){
        String str = "{\"g_udid\":\"1EFB07BFE0D98F8BF9EAF276C92C95FA4BEA3423\",\"g_imid\":\"864499040824376\",\"g_lkey\":\"d359a30a239e9e17daa8f8367ef35422\",\"g_encode\":\"1\",\"g_time\":1572511666,\"g_platform\":\"Android\",\"g_system\":\"8.1.0\",\"g_model\":\"PACM00\",\"g_brand\":\"OPPO\"}";
        ONode n = ONode.load(str);

        String g_lkey = n.get("g_lkey").getString();
        long g_time = n.get("g_time").getLong();
        int g_encode = n.get("g_encode").getInt();
        String g_platform = n.get("g_platform").getString();
        String g_system = n.get("g_system").getString();
        String g_brand = n.get("g_brand").getString();
        String g_model = n.get("g_model").getString();
        String g_udid = n.get("g_udid").getString();
        String g_imid = n.get("g_imid").getString();
        double g_lng = n.get("g_lng").getDouble();
        double  g_lat = n.get("g_lat").getDouble();
        String g_adr = n.get("g_adr").getString();

        String str2 = n.toJson();

        System.out.println(str);
        System.out.println(str2);

        assert str.equals(str2);
    }
}
