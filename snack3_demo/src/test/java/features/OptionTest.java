package features;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.NodeDecoder;
import org.noear.snack.core.NodeEncoder;
import org.noear.snack.core.Options;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author noear 2025/6/25 created
 */
public class OptionTest {
    @Test
    public void case1() {
        Options options = Options.def();
        options.remove(Feature.WriteDateUseTicks);
        options.add(Feature.WriteNumberUseString);
        options.add(Feature.WriteDateUseFormat);
        options.add(Feature.SerializeNulls);
        options.add(Feature.EnumUsingName);
        options.setDateFormat("yyyy-MM-dd");
        options.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        options.addDecoder(BigDecimal.class, new NodeDecoder<BigDecimal>() {
            @Override
            public BigDecimal decode(ONode node, Type type) {
                return null;
            }
        });
        options.addEncoder(BigDecimal.class, new NodeEncoder<BigDecimal>() {
            @Override
            public void encode(BigDecimal data, ONode node) {

            }
        });

        ONode oNode = ONode.load("{}", options);

        oNode.selectOrNew("$.num").val(10000L);
        oNode.selectOrNew("$.date").val(new Date());

        String json = oNode.toJson();
        System.out.println(json);

        assert "{\"num\":\"10000\",\"date\":\"2025-06-25\"}".equals(json);
    }
}
