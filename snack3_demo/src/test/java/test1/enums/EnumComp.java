package test1.enums;

/**
 * @Author kevin
 * @Date 2022-10-02 20:17
 * @Description
 */
public enum EnumComp implements DictEnum{
  A("1","复合属性测试A"),
  B("2","复合属性测试B"),
  ;

  private String code;

  private String display;

  EnumComp(String code, String display) {
    this.code = code;
    this.display = display;
  }

  @Override
  public String getDisplay() {
    return this.display;
  }

  @Override
  public String getCode() {
    return this.code;
  }
}
