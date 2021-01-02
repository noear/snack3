package features;

import _models.ComplexModel;
import _models.Person;
import _models.Point;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.TypeRef;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author noear 2021/1/2 created
 */
public class GenericTest {
    @Test
    public void test() {
        ComplexModel<Point> model = new ComplexModel<Point>();
        model.setId(1);
        Person person = new Person();
        person.setName("Tom");
        person.setAge(86);
        person.setBirthDay(new Date());
        person.setSensitiveInformation("This should be private over the wire");
        model.setPerson(person);

        List<Point> points = new ArrayList<Point>();
        Point point = new Point();
        point.setX(3);
        point.setY(4);
        points.add(point);

        point = new Point();
        point.setX(100);
        point.setY(100);
        points.add(point);

        //远程方法调用
        model.setPoints(points);

        String json = ONode.serialize(model);

        System.out.println(json);

        ComplexModel<Point> model1 = ONode.deserialize(json, new TypeRef<ComplexModel<Point>>() {
        }.getType());

        List<Point> points1 = model1.getPoints();
        for (Point p1 : points1) {
            System.out.println(p1.getX());
        }
    }
}
