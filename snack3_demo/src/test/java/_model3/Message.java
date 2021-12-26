package _model3;

import lombok.Data;

/**
 * @author noear 2021/12/26 created
 */
@Data
public class Message {
    private long msg_id;
    private long id;
    private String sender;
    private int r_status;
    private int type;
    private String content;
    private long send_time;
}
