package org.near.snack3;

/**
 * 节点值类型（7个）
 * */
public enum OValueType {
    /** null 类型 */
    Null,
    /** string 类型 */
    String,
    /** 整数 类型 */
    Integer,
    /** 小数 类型 */
    Decimal,
    /** boolean 类型 */
    Boolean,
    /** datetime 类型 */
    DateTime,
    /** 大数字（为序列化做准备） */
    Bignumber,
}
