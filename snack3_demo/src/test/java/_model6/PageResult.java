package _model6;

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
public class PageResult<T> extends R<PageInfo<T>> implements Serializable {

    private static final long serialVersionUID = 3L;

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public PageResult(List<T> list, long total) {
        setData(PageInfo.build(list, total));
    }

}
