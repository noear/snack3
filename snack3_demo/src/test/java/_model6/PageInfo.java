package _model6;

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
public class PageInfo<T> implements Serializable {

    private static final long serialVersionUID = 2L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<T> rows;

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public PageInfo(List<T> list, long total) {
        this.rows = list;
        this.total = total;
    }

    public static <T> PageInfo<T> build(List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setRows(list);
        pageInfo.setTotal(list.size());
        return pageInfo;
    }

    public static <T> PageInfo<T> build() {
        return new PageInfo<>();
    }

    public static <T> PageInfo<T> build(List<T> data, long total) {
        return new PageInfo<>(data, total);
    }

    public static <T> PageInfo<T> build(long total) {
        return new PageInfo<>(new ArrayList<>(), total);
    }

}
