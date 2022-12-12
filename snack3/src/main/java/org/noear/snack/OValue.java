package org.noear.snack;

import org.noear.snack.core.Feature;
import org.noear.snack.core.utils.DateUtil;
import org.noear.snack.exception.SnackException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Date;

/**
 * 节点值
 *
 * @author noear
 * */
public class OValue {
    protected String _string; //字符串
    protected boolean _bool; //布尔值
    protected Date _date; //日期
    protected Number _number;//数字
    protected ONode _n;

    public OValue(ONode n) {
        _n = n;
    }

    private OValueType _type = OValueType.Null;

    /**
     * 获取值类型
     */
    public OValueType type() {
        return _type;
    }


    /**
     * 设置值
     */
    public void set(Object val) {
        if (val == null) {
            _type = OValueType.Null;
            return;
        }

        if (val instanceof String) { //string
            setString((String) val);
            return;
        }

        if (val instanceof Date) { //date
            setDate((Date) val);
            return;
        }

        if (val instanceof Number) { //number
            setNumber((Number) val);
            return;
        }

        if (val instanceof Boolean) { //bool
            setBool((Boolean) val);
            return;
        }

        throw new SnackException("unsupport type class" + val.getClass().getName());
    }

    public void setNull() {
        _type = OValueType.Null;
    }


    public void setNumber(Number val) {
        _type = OValueType.Number;
        _number = val;
    }

    public void setString(String val) {
        _type = OValueType.String;
        _string = val;
    }

    public void setBool(boolean val) {
        _type = OValueType.Boolean;
        _bool = val;
    }

    public void setDate(Date val) {
        _type = OValueType.DateTime;
        _date = val;
    }

    //==================
    public Object getRaw() {
        switch (_type) {
            case String:
                return _string;
            case DateTime:
                return _date;
            case Boolean:
                return _bool;
            case Number:
                return _number;
            default:
                return null;
        }
    }

    /**
     * 获取真实的字符串
     */
    public String getRawString() {
        return _string;
    }

    /**
     * 获取真实的布尔值
     */
    public boolean getRawBoolean() {
        return _bool;
    }

    /**
     * 获取真实的日期
     */
    public Date getRawDate() {
        return _date;
    }

    /**
     * 获取真实的数字
     */
    public Number getRawNumber() {
        return _number;
    }

    //==================

    public boolean isNull() {
        return _type == OValueType.Null;
    }

    /**
     * 获取值为 char 类型（为序列化提供支持）
     */
    public char getChar() {
        switch (_type) {
            case Number:
                return (char) _number.longValue();
            case String:
                if (_string == null || _string.length() == 0) {
                    return 0;
                } else {
                    return _string.charAt(0);
                }
            case Boolean:
                return _bool ? '1' : '0';
            case DateTime:
                return 0;
            default:
                return 0;
        }
    }

    /**
     * 获取值为 short 类型
     */
    public short getShort() {
        return (short) getLong();
    }

    /**
     * 获取值为 int 类型
     */
    public int getInt() {
        return (int) getLong();
    }

    /**
     * 获取值为 long 类型
     */
    public long getLong() {
        switch (_type) {
            case Number:
                return _number.longValue();
            case String: {
                if (_string == null || _string.length() == 0) {
                    return 0;
                } else {
                    return Long.parseLong(_string);
                }
            }
            case Boolean:
                return _bool ? 1 : 0;
            case DateTime:
                return _date.getTime();
            default:
                return 0;
        }
    }

    public float getFloat() {
        return (float) getDouble();
    }

    /**
     * 获取值为 double 类型
     */
    public double getDouble() {
        switch (_type) {
            case Number:
                return _number.doubleValue();
            case String: {
                if (_string == null || _string.length() == 0) {
                    return 0;
                } else {
                    return Double.parseDouble(_string);
                }
            }
            case Boolean:
                return _bool ? 1 : 0;
            case DateTime:
                return _date.getTime();
            default:
                return 0;
        }
    }

    /**
     * 获取值为 string 类型
     */
    public String getString() {
        switch (_type) {
            case String:
                return _string;
            case Number: {
                if (_number instanceof BigInteger) {
                    return _number.toString();
                } else if (_number instanceof BigDecimal) {
                    return ((BigDecimal) _number).toPlainString();
                } else {
                    return String.valueOf(_number);
                }
            }
            case Boolean:
                return String.valueOf(_bool);
            case DateTime:
                return String.valueOf(_date);
            default: {
                if (_n._o.hasFeature(Feature.StringNullAsEmpty)) {
                    return "";
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * 获取值为 boolean 类型
     */
    public boolean getBoolean() {
        switch (_type) {
            case Boolean:
                return _bool;
            case Number:
                return _number.intValue() > 0;
            case String:
                return "true".equals(_string) || "True".equals(_string);
            case DateTime:
                return false;
            default:
                return false;
        }
    }

    /**
     * 获取值为 date 类型
     */
    public Date getDate() {
        switch (_type) {
            case DateTime:
                return _date;
            case String: {
                if (_string == null) {
                    return null;
                } else {
                    return parseDate(_string.trim());
                }
            }
            case Number: {
                return new Date(_number.longValue());
            }
            default:
                return null;
        }
    }

    /**
     * 尝试解析时间
     */
    private Date parseDate(String dateString) {
        try {
            return DateUtil.parse(dateString);
        } catch (ParseException ex) {
            return null;
        }
    }

    @Override
    public String toString() {
        return getString();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return isNull();
        }

        if (o instanceof OValue) {
            OValue o2 = (OValue) o;
            switch (_type) {
                case String:
                    return _string.equals(o2._string);
                case DateTime:
                    return _date.equals(o2._date);
                case Boolean:
                    return _bool == o2._bool;
                case Number: {
                    if (_number instanceof BigInteger) {
                        return toString().equals(o2.toString());
                    } else if (_number instanceof BigDecimal) {
                        return toString().equals(o2.toString());
                    } else if (_number instanceof Double || _number instanceof Float) {
                        return getDouble() == o2.getDouble();
                    } else {
                        return getLong() == o2.getLong();
                    }
                }
                default:
                    return isNull() && o2.isNull();
            }
        } else {

            switch (_type) {
                case String:
                    return _string.equals(o);
                case DateTime:
                    return _date.equals(o);
                case Boolean: {
                    if (o instanceof Boolean) {
                        return _bool == (Boolean) o;
                    } else {
                        return false;
                    }
                }
                case Number: {
                    if (o instanceof Number) {
                        Number o2 = (Number) o;

                        if (_number instanceof BigInteger) {
                            return toString().equals(o2.toString());
                        } else if (_number instanceof BigDecimal) {
                            return toString().equals(o2.toString());
                        } else if (_number instanceof Double || _number instanceof Float) {
                            return _number.doubleValue() == o2.doubleValue();
                        } else {
                            return _number.longValue() == o2.longValue();
                        }
                    } else {
                        return false;
                    }
                }
                default:
                    return false;
            }
        }
    }

    @Override
    public int hashCode() {
        switch (_type) {
            case String:
                return _string.hashCode();
            case DateTime:
                return _date.hashCode();
            case Boolean:
                return Boolean.hashCode(_bool);
            case Number:
                return _number.hashCode();
            default:
                return 0;
        }
    }
}
