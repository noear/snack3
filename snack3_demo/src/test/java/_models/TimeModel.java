package _models;

import org.noear.snack.annotation.ONodeAttr;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author noear 2024/5/9 created
 */
public class TimeModel {
    @ONodeAttr(format = "yyyy-MM-dd")
    public LocalDate date;
    @ONodeAttr(format = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime time;
    @ONodeAttr(format = "yyyy-MM-dd")
    public Date date2;
}
