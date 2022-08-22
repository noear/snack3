package _model4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QueryParamEntity extends QueryParam {
    private static final Logger log = LoggerFactory.getLogger(QueryParamEntity.class);
    private static final long serialVersionUID = 8097500947924037523L;

    private String where;
    private String orderBy;
    private Integer total;

    public QueryParamEntity() {
    }

    public boolean isForUpdate() {
        return super.isForUpdate();
    }

    public int getThinkPageIndex() {
        return super.getThinkPageIndex();
    }

    public int getPageIndexTmp() {
        return super.getPageIndexTmp();
    }

    public QueryParamEntity noPaging() {
        this.setPaging(false);
        return this;
    }
    public String getWhere() {
        return this.where;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public Integer getTotal() {
        return this.total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
