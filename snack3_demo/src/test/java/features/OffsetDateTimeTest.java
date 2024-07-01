package features;

import _models.OffsetDateTimeModel;
import _models.ZonedDateTimeModel;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Context;
import org.noear.snack.core.Options;
import org.noear.snack.from.ObjectFromer;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * OffsetDateTime序列化测试
 * @author hans
 */
public class OffsetDateTimeTest {


    /**
     * 反序列化测试
     */
    @Test
    public void deserialize() {
        String poc = "{\"date\":\"2024-01-12T10:30:00.000+03:00\"}";
        ONode oNode = ONode.loadStr(poc);
        //解析
        OffsetDateTimeModel model = oNode.toObject(OffsetDateTimeModel.class);
        OffsetDateTime date = model.date;
        OffsetDateTime offsetDateTime = date.withOffsetSameInstant(ZoneOffset.of("+03:00"));
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println(df.format(offsetDateTime));
        System.out.println(df.format(date));
        assert date.toInstant().equals(offsetDateTime.toInstant());
    }

    /**
     * 序列化测试
     */
    @Test
    public void serialize() {
        OffsetDateTimeModel data = new OffsetDateTimeModel();
        data.date=OffsetDateTime.now();
        ObjectFromer objectFromer = new ObjectFromer();
        Context context = new Context(Options.def(), data);
        objectFromer.handle(context);
        assert context.source == data;
    }
}