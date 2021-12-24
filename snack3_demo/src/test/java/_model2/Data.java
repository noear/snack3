package _model2;

import java.util.List;

/**
 * @author noear 2021/12/23 created
 */
@lombok.Data
public class Data<X> {
    private List<X> content;
    private X obj;
    private int pageNum;
    private int pageSize;
    private int totalElements;
    private int pages;
}
