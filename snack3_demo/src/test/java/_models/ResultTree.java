package _models;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

/**
 * Describe: 前端 tree 结果封装数据
 */
@ToString
@Data
public class ResultTree implements Serializable {

    /**
     * 状态信息
     */
    private Status status = new Status();

    /**
     * 返回数据
     */
    private Object data;

    /**
     * 所需内部类
     */
    @Data
    public class Status {

        private Integer code = 200;

        private String message = "默认";
    }

}
