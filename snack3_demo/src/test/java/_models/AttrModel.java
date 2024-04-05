package _models;

import org.noear.snack.annotation.ONodeAttr;

public class AttrModel {
    public int id;
    @ONodeAttr(asString = true)
    public long traceId;
    public String name;

    @Override
    public String toString() {
        return "AttrModel{" +
                "id=" + id +
                ", traceId=" + traceId +
                ", name='" + name + '\'' +
                '}';
    }
}
