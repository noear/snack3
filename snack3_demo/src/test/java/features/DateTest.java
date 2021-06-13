package features;

import _models.DateModel;
import _models.DateModel2;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.utils.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * @author noear 2021/6/13 created
 */
public class DateTest {
    @Test
    public void test1() throws ParseException{
        Date date = new Date();

        test1One(date, DateUtil.FORMAT_24_ISO08601);
        test1One(date, DateUtil.FORMAT_19_ISO);
        test1One(date, DateUtil.FORMAT_19);
        test1One(date, DateUtil.FORMAT_22);
        test1One(date, DateUtil.FORMAT_10);
        test1One(date, DateUtil.FORMAT_29);
        test1One(date, DateUtil.FORMAT_23_a);
        test1One(date, DateUtil.FORMAT_23_b);
    }

    private void test1One(Date date, DateFormat format) throws ParseException {
        String str = format.format(date);

        System.out.println(str + " == " + str.length());

        System.out.println(format.parse(str).getTime());
    }

    @Test
    public void test2(){
        String json = "{date1:'2021-06-13T20:54:51.566Z', date2:'2021-06-13T20:54:51', date3:'2021-06-13 20:54:51', date4:'20210613205451566+0800', date5:'2021-06-13', date6:'2021-06-13T20:54:51.566+08:00', date7:'2021-06-13 20:54:51,566', date8:'2021-06-13 20:54:51.566'}";
        DateModel dateModel = ONode.deserialize(json,DateModel.class);

        assert dateModel.date1.getTime() == 1623588891566L;
        assert dateModel.date2.getTime() == 1623588891000L;
        assert dateModel.date3.getTime() == 1623588891000L;
        assert dateModel.date4.getTime() == 1623588891566L;
        assert dateModel.date5.getTime() == 1623513600000L;
        assert dateModel.date6.getTime() == 1623588891566L;
        assert dateModel.date7.getTime() == 1623588891566L;
        assert dateModel.date8.getTime() == 1623588891566L;

    }

    @Test
    public void test3(){
        String json = "{date1:'2021-06-13T20:54:51.566Z', date2:'2021-06-13T20:54:51', date3:'2021-06-13 20:54:51', date4:'20210613205451566+0800', date5:'2021-06-13', date6:'2021-06-13T20:54:51.566+08:00', date7:'2021-06-13 20:54:51,566', date8:'2021-06-13 20:54:51.566'}";
        DateModel2 dateModel = ONode.deserialize(json,DateModel2.class);

        System.out.println(ONode.stringify(dateModel));
    }
}
