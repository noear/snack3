package _model2;

import java.util.List;

/**
 * @author noear 2021/12/23 created
 */
@lombok.Data
public class Data<T> {
    private List<T> content;
    private int pageNum;
    private int pageSize;
    private int totalElements;
    private int pages;
}
