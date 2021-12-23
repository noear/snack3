package _model2;


/**
 * @author noear 2021/12/23 created
 */
@lombok.Data
public class Result<T> {
    private int code;
    private Data<T> data;
}
