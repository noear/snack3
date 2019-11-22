package _models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 2019.01.30
 *
 * @author cjl
 */
public class UserGroupModel {
    public long id;
    public List<UserModel> users;
    public Map<Integer,UserModel> users2;
    public Set<UserModel> users3;
    public String[] names;
    public short[] ids;
    public Integer[] iids;
    public BigDecimal dd;
    public Timestamp tt1;
    public Date tt2;
}
