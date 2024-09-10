package features;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;

import java.time.Duration;

/**
 * @author noear 2024/9/10 created
 */
public class DurationTest {
    @Test
    public void test1() {
        Duration duration = ONode.load("'6s'").toObject(Duration.class);
        System.out.println(duration);

        duration = ONode.load("'6m'").toObject(Duration.class);
        System.out.println(duration);

        duration = ONode.load("'6d'").toObject(Duration.class);
        System.out.println(duration);
    }

    @Test
    public void test3() {
        Duration duration = ONode.load("'PT6S'").toObject(Duration.class);
        System.out.println(duration);

        duration = ONode.load("'PT6M'").toObject(Duration.class);
        System.out.println(duration);

        duration = ONode.load("'PT6H'").toObject(Duration.class);
        System.out.println(duration);
    }
}
