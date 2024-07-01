package features;

import _models.OffsetDateTimeModel;
import _models.OffsetTimeModel;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Context;
import org.noear.snack.core.DEFAULTS;
import org.noear.snack.core.Options;
import org.noear.snack.from.ObjectFromer;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * OffsetTime序列化测试
 * @author hans
 */
public class OffsetTimeTest {


    /**
     * 反序列化测试(不带时区偏移)
     */
    @Test
    public void deserialize() {
        String poc = "{\"time\":\"20:54:51\"}";
        ONode oNode = ONode.loadStr(poc);
//        //解析
        OffsetTimeModel model = oNode.toObject(OffsetTimeModel.class);
        OffsetTime time0 = model.time;
        // 转到0时区
        OffsetTime time1 = time0.withOffsetSameInstant(ZoneOffset.of("Z"));
        assert time0.isEqual(time1);
    }

    /**
     * 反序列化测试(带时区偏移)
     */
    @Test
    public void deserializeOffset() {
        String poc = "{\"time\":\"20:54:51+08:00\"}";
        ONode oNode = ONode.loadStr(poc);
//        //解析
        OffsetTimeModel model = oNode.toObject(OffsetTimeModel.class);
        OffsetTime time0 = model.time;
        // 转到0时区
        OffsetTime time1 = time0.withOffsetSameInstant(ZoneOffset.of("Z"));
        assert time0.isEqual(time1);
    }


    /**
     * 序列化测试
     */
    @Test
    public void serialize() {
        OffsetTimeModel data = new OffsetTimeModel();
        data.time = OffsetTime.of(2,3,1,0,ZoneOffset.of("+03:00"));
        ObjectFromer objectFromer = new ObjectFromer();
        Context context = new Context(Options.def(), data);
        objectFromer.handle(context);
        System.out.println(data.time);
        assert context.source == data;
    }
}