package features;

import _model5.TypeAImpl;
import _model5.TypeBImpl;
import _model5.TypeC;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;

/**
 * @author noear 2025/5/9 created
 */
public class ClassType {
    @Test
    public void case1(){
        TypeC c = new TypeC();
        c.typeA = new TypeAImpl();
        c.typeB = new TypeBImpl();

       String rst = ONode.serialize(c);
       System.out.println(rst);

       assert "{\"@type\":\"_model5.TypeC\",\"typeA\":{\"@type\":\"_model5.TypeAImpl\"},\"typeB\":{\"@type\":\"_model5.TypeBImpl\"}}".equals(rst);
    }

    @Test
    public void case2(){
        TypeC c = new TypeC();
        c.typeA = new TypeAImpl();
        c.typeB = new TypeBImpl();

        String rst = ONode.load(c, Options.serialize().add(Feature.NotWriteRootClassName)).toJson();
        System.out.println(rst);

        assert "{\"typeA\":{\"@type\":\"_model5.TypeAImpl\"},\"typeB\":{\"@type\":\"_model5.TypeBImpl\"}}".equals(rst);
    }
}
