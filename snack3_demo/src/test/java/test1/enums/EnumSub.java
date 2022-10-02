package test1.enums;

/**
 * @Author kevin
 * @Date 2022-10-02 20:17
 * @Description
 */
public enum EnumSub implements DictEnum{
  A("1","测试A"),
  B("2","测试B"),
  ;

  private String code;

  private String display;

  EnumSub(String code, String display) {
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
