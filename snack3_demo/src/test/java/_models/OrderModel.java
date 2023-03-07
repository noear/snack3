package _models;

import java.util.Date;
import java.util.List;

/**
 * 2019.01.30
 *
 * @author cjl
 */
public class OrderModel {
    public UserModel user;
    public List userList;
    public String order_num;
    public int order_id;
    public Date order_time;
    public List orderList;

    @Override
    public String toString() {
        return "OrderModel{" +
                "user=" + user +
                ", order_num='" + order_num + '\'' +
                ", order_id=" + order_id +
                ", order_time=" + order_time +
                '}';
    }
}
