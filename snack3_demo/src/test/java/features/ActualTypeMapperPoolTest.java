package features;

import _model2.House;
import _model2.Result;
import org.junit.Assert;
import org.junit.Test;
import org.noear.snack.core.TypeRef;
import org.noear.snack.core.utils.ActualTypeMapperPool;
import org.noear.snack.core.utils.BeanUtil;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author noear 2021/12/23 created
 */
public class ActualTypeMapperPoolTest {
    @Test
    public void getTypeArgumentTest(){
        final Map<Type, Type> typeTypeMap = ActualTypeMapperPool.get(FinalClass.class);
        typeTypeMap.forEach((key, value)->{
            if("A".equals(key.getTypeName())){
                Assert.assertEquals(Character.class, value);
            } else if("B".equals(key.getTypeName())){
                Assert.assertEquals(Boolean.class, value);
            } else if("C".equals(key.getTypeName())){
                Assert.assertEquals(String.class, value);
            } else if("D".equals(key.getTypeName())){
                Assert.assertEquals(Double.class, value);
            } else if("E".equals(key.getTypeName())){
                Assert.assertEquals(Integer.class, value);
            }
        });
    }

    @Test
    public void getTypeArgumentStrKeyTest(){
        final Map<String, Type> typeTypeMap = ActualTypeMapperPool.getStrKeyMap(FinalClass.class);
        typeTypeMap.forEach((key, value)->{
            if("A".equals(key)){
                Assert.assertEquals(Character.class, value);
            } else if("B".equals(key)){
                Assert.assertEquals(Boolean.class, value);
            } else if("C".equals(key)){
                Assert.assertEquals(String.class, value);
            } else if("D".equals(key)){
                Assert.assertEquals(Double.class, value);
            } else if("E".equals(key)){
                Assert.assertEquals(Integer.class, value);
            }
        });
    }

    @Test
    public void Test3(){
        final Map<Type, Type> typeTypeMap = ActualTypeMapperPool.get(Result.class);
        final Map<Type, Type> typeTypeMap2 = ActualTypeMapperPool.get(new Result<House>().getClass());

        Type type = new TypeRef<Result<House>>(){}.getType();
        final Map<Type, Type> typeTypeMap3 = ActualTypeMapperPool.get(type);

        final Map map4= BeanUtil.buildGenericInfo(FinalClass.class);


        return;
    }

    public interface BaseInterface<A, B, C> {}
    public interface FirstInterface<A, B, D, E> extends BaseInterface<A, B, String> {}
    public interface SecondInterface<A, B, F> extends BaseInterface<A, B, String> {}

    public static class BaseClass<A, D> implements FirstInterface<A, Boolean, D, Integer> {}
    public static class FirstClass extends BaseClass<Character, Double> implements SecondInterface<Character, Boolean, FirstClass> {}
    public static class SecondClass extends FirstClass {}
    public static class FinalClass extends SecondClass {}
}
