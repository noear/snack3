package test1.enums;

/**
 * @Author kevin
 * @Date 2022-10-02 20:29
 * @Description
 */
public enum EnumMulti implements DictEnum{

  A(1,"A","复杂测试A"){
    @Override
    public String getComments() {
      return "这是A";
    }
  },
  B(2, "B","复杂测试B") {
    @Override
    public String getComments() {
      return "这是B";
    }
  };
  private String code;

  private String display;

  private int type;


  EnumMulti(int type, String code,  String display) {
    this.code = code;
    this.display = display;
    this.type = type;
  }

  @Override
  public String getDisplay() {
    return this.display;
  }

  @Override
  public String getCode() {
    return this.code;
  }

  public abstract String getComments();


}
