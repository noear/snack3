package features;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.TypeRef;

/**
 * @author noear 2025/2/26 created
 */
public class GenericTest2 {
    @Test
    public void test() {
        String json = "{\"results\":[{\"uid\":\"1\"}],\"offset\":0,\"limit\":20,\"total\":0}";
        Results<Index> deserialize = ONode.deserialize(json, new TypeRef<Results<Index>>() {
        }.getType());

        System.out.println(deserialize);
        System.out.println(deserialize.getClass());
        System.out.println(deserialize.results.getClass());
    }

    @Data
    public static class Results<T> {
        public T[] results; //ary
        public int offset;
        public int limit;
        public int total;
    }

    @Data
    public static class Index {
        public String uid;
    }
}
