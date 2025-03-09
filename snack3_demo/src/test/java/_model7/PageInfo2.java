package _model7;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 统一分页结果对象
 *
 * @author chengliang
 * @since 2024/04/17
 */
@Data
@NoArgsConstructor
public class PageInfo2<E> implements Serializable {

    private static final long serialVersionUID = 2L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<E> rows;

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public PageInfo2(List<E> list, long total) {
        this.rows = list;
        this.total = total;
    }

    public static <T> PageInfo2<T> build(List<T> list) {
        PageInfo2<T> pageInfo = new PageInfo2<>();
        pageInfo.setRows(list);
        pageInfo.setTotal(list.size());
        return pageInfo;
    }

    public static <T> PageInfo2<T> build() {
        return new PageInfo2<>();
    }

    public static <T> PageInfo2<T> build(List<T> data, long total) {
        return new PageInfo2<>(data, total);
    }

    public static <T> PageInfo2<T> build(long total) {
        return new PageInfo2<>(new ArrayList<>(), total);
    }

}
