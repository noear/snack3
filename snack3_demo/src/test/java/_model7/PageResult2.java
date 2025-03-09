package _model7;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 表格分页数据对象
 *
 * @author chengliang
 * @date 2024/08/14
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PageResult2<E> extends R2<PageInfo2<E>> implements Serializable {

    private static final long serialVersionUID = 3L;

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public PageResult2(List<E> list, long total) {
        setData(PageInfo2.build(list, total));
    }

}
