package _model4;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class QueryParam extends Param implements Serializable, Cloneable {
    private static final long serialVersionUID = 7941767360194797891L;
    public static final int DEFAULT_FIRST_PAGE_INDEX = Integer.getInteger("easyorm.page.fist.index", 0);
    public static final int DEFAULT_PAGE_SIZE = Integer.getInteger("easyorm.page.size", 25);

    private boolean paging = true;

    private int firstPageIndex;

    private int pageIndex;

    private int pageSize;
    private List<Sort> sorts;
    private transient int pageIndexTmp;
    private boolean forUpdate;

    public QueryParam() {
        this.firstPageIndex = DEFAULT_FIRST_PAGE_INDEX;
        this.pageIndex = this.firstPageIndex;
        this.pageSize = DEFAULT_PAGE_SIZE;
        this.sorts = new LinkedList();
        this.pageIndexTmp = 0;
        this.forUpdate = false;
    }

    public Sort orderBy(String column) {
        Sort sort = new Sort(column);
        this.sorts.add(sort);
        return sort;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndexTmp = this.pageIndex;
        this.pageIndex = Math.max(pageIndex - this.firstPageIndex, 0);
    }

    public void setFirstPageIndex(int firstPageIndex) {
        this.firstPageIndex = firstPageIndex;
        this.pageIndex = Math.max(this.pageIndexTmp - this.firstPageIndex, 0);
    }

    public int getThinkPageIndex() {
        return this.pageIndex + this.firstPageIndex;
    }

    public boolean isPaging() {
        return this.paging;
    }

    public int getPageIndex() {
        return this.pageIndex;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public List<Sort> getSorts() {
        return this.sorts;
    }

    public int getPageIndexTmp() {
        return this.pageIndexTmp;
    }

    public boolean isForUpdate() {
        return this.forUpdate;
    }

    public void setPaging(boolean paging) {
        this.paging = paging;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setSorts(List<Sort> sorts) {
        this.sorts = sorts;
    }

    public void setPageIndexTmp(int pageIndexTmp) {
        this.pageIndexTmp = pageIndexTmp;
    }

    public void setForUpdate(boolean forUpdate) {
        this.forUpdate = forUpdate;
    }

    public int getFirstPageIndex() {
        return this.firstPageIndex;
    }
}

