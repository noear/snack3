package _models;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author noear 2021/5/20 created
 */
@Data
public class NumberModel {
    private boolean num01;
    private byte num02;
    private short num11;
    private Short num11_2;
    private int num12;
    private long num13;
    private Long num13_2;
    private float num14 ;
    private double num15;
    private Double num15_2;
    private BigInteger num21;
    private BigDecimal num22;
}
