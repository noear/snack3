package features;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.TypeRef;

import java.util.List;

/**
 * @author noear 2025/2/26 created
 */
public class GenericTest3 {
    @Test
    public void test() {
        String json = "{\"results\":[{\"uid\":\"1\"}],\"offset\":0,\"limit\":20,\"total\":0}";
        Results<Index> deserialize = ONode.deserialize(json, new TypeRef<Results<Index>>() {
        }.getType());

        System.out.println(deserialize);
        System.out.println(deserialize.getClass());
        System.out.println(deserialize.results.getClass());

        assert deserialize.results.get(0).getClass() == Index.class;
    }

    @Data
    public static class Results<T> {
        public List<T> results; //list
        public int offset;
        public int limit;
        public int total;
    }

    @Data
    public static class Index {
        public String uid;
    }
}
