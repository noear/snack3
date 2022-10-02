package test1.pojo;


import test1.enums.EnumSuper;

import java.io.Serializable;

/**
 * @Author kevin
 * @Date 2022-10-02 20:18
 * @Description
 */
public class SuperA implements Serializable {


  private String name;

  private EnumSuper superEnum;

  public EnumSuper getSuperEnum() {
    return superEnum;
  }

  public void setSuperEnum(EnumSuper superEnum) {
    this.superEnum = superEnum;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


}
