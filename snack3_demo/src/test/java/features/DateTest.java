package features;

import _models.DateModel;
import _models.DateModel2;
import _models.DateModel3;
import org.junit.Test;
import org.noear.snack.ONode;

import java.util.Date;

/**
 * @author noear 2021/6/13 created
 */
public class DateTest {

    @Test
    public void test2() {
        String json = "{date1:'2021-06-13T20:54:51.566Z', date2:'2021-06-13T20:54:51', date3:'2021-06-13 20:54:51', date4:'20210613205451566+0800', date5:'2021-06-13', date6:'2021-06-13T20:54:51.566+08:00', date7:'2021-06-13 20:54:51,566', date8:'2021-06-13 20:54:51.566', date9:'20:54:51'}";
        DateModel dateModel = ONode.deserialize(json, DateModel.class);

        assert dateModel.date1.getTime() == 1623588891566L;
        assert dateModel.date2.getTime() == 1623588891000L;
        assert dateModel.date3.getTime() == 1623588891000L;
        assert dateModel.date4.getTime() == 1623588891566L;
        assert dateModel.date5.getTime() == 1623513600000L;
        assert dateModel.date6.getTime() == 1623588891566L;
        assert dateModel.date7.getTime() == 1623588891566L;
        assert dateModel.date8.getTime() == 1623588891566L;
        assert dateModel.date9.getTime() == 46491000L;

    }

    @Test
    public void test3() {
        String json = "{date1:'2021-06-13T20:54:51.566Z', date2:'2021-06-13T20:54:51', date3:'2021-06-13 20:54:51', date4:'20210613205451566+0800', date5:'2021-06-13', date6:'2021-06-13T20:54:51.566+08:00', date7:'2021-06-13 20:54:51,566', date8:'2021-06-13 20:54:51.566', date9:'20:54:51', date10:'2021-06-13T20:54:51.566+08:00'}";

        DateModel dateModel0 = ONode.deserialize(json, DateModel.class);
        DateModel2 dateModel = ONode.deserialize(json, DateModel2.class);

        String json2 = ONode.stringify(dateModel);
        System.out.println(json2);

        DateModel2 dateModel2 = ONode.deserialize(json2, DateModel2.class);


        assert dateModel.date1.equals(dateModel2.date1);
        assert dateModel.date2.equals(dateModel2.date2);
        assert dateModel.date3.equals(dateModel2.date3);
        assert dateModel.date4.equals(dateModel2.date4);
        assert dateModel.date5.equals(dateModel2.date5);
        assert dateModel.date6.equals(dateModel2.date6);
        assert dateModel.date7.equals(dateModel2.date7);
        assert dateModel.date8.equals(dateModel2.date8);
        assert dateModel.date9.equals(dateModel2.date9);
        assert dateModel.date10.equals(dateModel2.date10);
    }

    @Test
    public void test4() {
        String json = "{date1:1670774400000}";

        DateModel dateModel0 = ONode.deserialize(json, DateModel.class);
        DateModel2 dateModel = ONode.deserialize(json, DateModel2.class);

        String json2 = ONode.stringify(dateModel);
        System.out.println(json2);

        DateModel2 dateModel2 = ONode.deserialize(json2, DateModel2.class);


        assert dateModel.date1.equals(dateModel2.date1);
    }

    @Test
    public void test5(){
        DateModel3  dateModel3 = new DateModel3();
        dateModel3.date1 = new Date(1680602276520L);
        dateModel3.date2 = dateModel3.date1 ;
        dateModel3.date3 = dateModel3.date1 ;


        String json = ONode.stringify(dateModel3);

        System.out.println(json);

        assert json.contains("2023-04-04 17:57:56");
        assert json.contains("2023-04-04 16:57:56");
    }
}
