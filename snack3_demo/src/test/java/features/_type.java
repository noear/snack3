package features;

import _models.UserModel;
import org.junit.Test;
import org.noear.snack.core.TypeRef;

import java.lang.reflect.Type;
import java.util.List;

public class _type {
    @Test
    public void test1() {
        Type type1 = (new TypeRef<List<UserModel>>() {
        }).getType(); //class

        Type type2 = (new TypeRef<UserModel>() {
        }).getType(); //class

        return;
    }
}
