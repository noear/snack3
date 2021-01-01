package _models;

import org.noear.snack.annotation.NodeName;

public class UserModel2 {
    public int id;
    @NodeName("name")
    public String nickname;
    public String note;
}
