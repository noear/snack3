package test1.pojo;


import test1.enums.EnumComp;

import java.io.Serializable;

/**
 * @Author kevin
 * @Date 2022-10-02 20:19
 * @Description
 */
public class CompC implements Serializable {

  private String compName;

  private EnumComp compEnumComp;


  public String getCompName() {
    return compName;
  }

  public void setCompName(String compName) {
    this.compName = compName;
  }

  public EnumComp getCompEnumOne() {
    return compEnumComp;
  }

  public void setCompEnumOne(EnumComp compEnumComp) {
    this.compEnumComp = compEnumComp;
  }

}
