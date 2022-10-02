package test1;

import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import test1.enums.EnumComp;
import test1.enums.EnumMulti;
import test1.enums.EnumSub;
import test1.enums.EnumSuper;
import test1.pojo.CompC;
import test1.pojo.SubB;

/**
 * @Author kevin
 * @Date 2022-10-02 20:21
 * @Description
 */
public class SnackTest {

  @Test
  public void testEnum() {
    CompC compC = new CompC();
    compC.setCompName("复合属性");
    compC.setCompEnumOne(EnumComp.A);


    SubB subB = new SubB();
    subB.setSubName("子类");
    subB.setCompEnum(EnumComp.A);
    subB.setSubbEnum(EnumSub.B);
    subB.setSuperEnum(EnumSuper.A);
    subB.setMultiEnum(EnumMulti.A);

    subB.setCompC(compC);


    System.out.println(ONode.stringify(subB));
    System.out.println("\n");
    System.out.println(ONode.load(subB, Feature.EnumUsingName));
  }
}
