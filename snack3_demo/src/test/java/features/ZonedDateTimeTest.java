package features;

import _models.ZonedDateTimeModel;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Context;
import org.noear.snack.core.Options;
import org.noear.snack.from.ObjectFromer;

import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * ZoneDateTime序列化测试
 * @author hans
 */
public class ZonedDateTimeTest {


    /**
     * 反序列化测试
     */
    @Test
    public void deserialize() {
        String poc = "{\"date\":\"2024-01-12T10:30:00.000+03:00\"}";
        ONode oNode = ONode.loadStr(poc);
        //解析
        ZonedDateTimeModel model = oNode.toObject(ZonedDateTimeModel.class);
        ZonedDateTime date = model.date;
        ZonedDateTime zonedDateTime = date.withZoneSameInstant(ZoneId.of("+03:00"));
        assert date.toInstant().equals(zonedDateTime.toInstant());
    }

    /**
     * 序列化测试
     */
    @Test
    public void serialize() {
        ZonedDateTimeModel data = new ZonedDateTimeModel();
        data.date=ZonedDateTime.now();
        ObjectFromer objectFromer = new ObjectFromer();
        Context context = new Context(Options.def(), data);
        objectFromer.handle(context);
        assert context.source == data;
    }
}