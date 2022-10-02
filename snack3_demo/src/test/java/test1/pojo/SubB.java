package test1.pojo;


import test1.enums.EnumComp;
import test1.enums.EnumMulti;
import test1.enums.EnumSub;

/**
 * @Author kevin
 * @Date 2022-10-02 20:19
 * @Description
 */
public class SubB extends SuperA{


  private CompC compC;
  private String subName;

  private EnumComp compEnum;

  private EnumSub subbEnum;

  private EnumMulti multiEnum;

  public EnumMulti getMultiEnum() {
    return multiEnum;
  }

  public void setMultiEnum(EnumMulti multiEnum) {
    this.multiEnum = multiEnum;
  }

  public String getSubName() {
    return subName;
  }

  public CompC getCompC() {
    return compC;
  }

  public void setCompC(CompC compC) {
    this.compC = compC;
  }

  public void setSubName(String subName) {
    this.subName = subName;
  }

  public EnumComp getCompEnum() {
    return compEnum;
  }

  public void setCompEnum(EnumComp compEnum) {
    this.compEnum = compEnum;
  }

  public EnumSub getSubbEnum() {
    return subbEnum;
  }

  public void setSubbEnum(EnumSub subbEnum) {
    this.subbEnum = subbEnum;
  }

}
