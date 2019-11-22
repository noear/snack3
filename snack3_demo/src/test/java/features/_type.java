package features;

import _models.UserModel;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class _type {

    @Test
    public void test2() {
        Type type1 = UserModel.class;
        Type type2 = (new ArrayList<UserModel>() {
        }).getClass().getGenericSuperclass();

        return;
    }
}
