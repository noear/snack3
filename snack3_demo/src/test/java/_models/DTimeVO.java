package _models;

import lombok.Data;
import lombok.experimental.Accessors;
import org.noear.snack.annotation.ONodeAttr;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author noear 2024/7/17 created
 */
@Data
@Accessors(chain = true)
public class DTimeVO {

    @ONodeAttr(format = "yyyy-MM-dd", timezone = "+08")
    private LocalDate date = LocalDate.now();

    @ONodeAttr(format = "yyyy-MM-dd HH:mm:ss", timezone = "+08")
    private LocalDateTime dateTime0 = LocalDateTime.now();

    @ONodeAttr(format = "yyyy-MM-dd HH:mm:ss", timezone = "+07")
    private LocalDateTime dateTime1 = LocalDateTime.now();

    @ONodeAttr(format = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "+08")
    private LocalDateTime dateTime2 = LocalDateTime.now();

    @ONodeAttr(format = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "+07")
    private LocalDateTime dateTime3 = LocalDateTime.now();

    @ONodeAttr(format = "yyyy-MM-dd HH:mm:ss", timezone = "+08")
    private Date date_a1 = new Date();

    @ONodeAttr(format = "yyyy-MM-dd HH:mm:ss", timezone = "+07")
    private Date date_a2 = new Date();

    @ONodeAttr(format = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "+08")
    private Date date_a3 = new Date();

    @ONodeAttr(format = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "+07")
    private Date date_a4 = new Date();
}
