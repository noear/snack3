package _models;

import org.noear.snack.annotation.ONodeAttr;
import org.noear.snack.core.NodeEncoder;

import java.util.Date;

public class UserModel2 {
    public NodeEncoder nodeEncoder;

    public int id;
    @ONodeAttr(serialize = false)
    public String name;
    @ONodeAttr(deserialize = false)
    public String note;

    @ONodeAttr(format = "yyyyMMdd")
    public Date date;

    @Override
    public String toString() {
        return "UserModel2{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", note='" + note + '\'' +
                ", date=" + date +
                '}';
    }
}
