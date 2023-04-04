package _models;

import org.noear.snack.annotation.ONodeAttr;

import java.util.Date;

/**
 * @author noear 2021/6/13 created
 */
public class DateModel3 {
    public Date date1;
    @ONodeAttr(format = "yyyy-MM-dd HH:mm:ss")
    public Date date2;
    @ONodeAttr(format = "yyyy-MM-dd HH:mm:ss", timezone = "+07")
    public Date date3;
}
