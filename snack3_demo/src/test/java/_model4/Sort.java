package _model4;

public class Sort extends Column {

    private String order = "asc";

    public Sort() {
    }

    public Sort(String column) {
        this.setName(column);
    }

    public String getOrder() {
        return "desc".equalsIgnoreCase(this.order) ? this.order : (this.order = "asc");
    }

    public void asc() {
        this.order = "asc";
    }

    public void desc() {
        this.order = "desc";
    }

    protected boolean canEqual(Object other) {
        return other instanceof Sort;
    }
    public void setOrder(String order) {
        this.order = order;
    }
}
