package _models;

import org.noear.snack.ONodeType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public class CModel {
    //集合
    public List<Integer> list;
    //泛型继承
    public CArrayModel list2;

    //字典
    public Map<String,Class<?>> map;
    //字典继承（key:string）
    public CMap1Model map1;
    //字典继承（key:Int）
    public CMap2Model map2;

    //枚举
    public ONodeType nodeType;

    //日期
    public Date date = new Date();

    public BigDecimal num1 = new BigDecimal("1");
    public BigInteger num2 = null;

    //public Queue<OrderModel> queue; //fastjson 不能创建

    public void init() {
        list = new LinkedList<>();
        list2 = new CArrayModel();
        map = new LinkedHashMap<String, Class<?>>();

        map1 = new CMap1Model();
        map2 = new CMap2Model();

        nodeType = ONodeType.Null;

        //queue = new ArrayDeque<>();
    }

    public void build(){
        init();
        list.add(11);
        list.add(12);

        list2.add(21);
        list2.add(22);

        map.put("1",UserModel.class);
        map1.put(1,"1");
        map2.put("1",UserModel.class);

        nodeType = ONodeType.Object;

        num2 = new BigInteger("1");

        //queue.add(new OrderModel());
    }
}
