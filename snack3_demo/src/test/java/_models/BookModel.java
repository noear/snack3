package _models;

import org.noear.snack.annotation.NodeName;

public class BookModel {
    public int id;
    @NodeName(value = "name")
    public String bookname;
    public String note;
}
