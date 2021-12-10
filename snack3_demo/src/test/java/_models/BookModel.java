package _models;

import org.noear.snack.annotation.ONodeAttr;

public class BookModel {
    public int id;
    @ONodeAttr(name = "name")
    public String bookname;
    public String note;
}
