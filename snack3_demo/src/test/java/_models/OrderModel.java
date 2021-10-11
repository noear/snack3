package _models;

import java.util.Date;

/**
 * 2019.01.30
 *
 * @author cjl
 */
public class OrderModel {
    public UserModel user;
    public String order_num;
    public int order_id;
    public Date order_time;

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
