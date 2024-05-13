package org.noear.snack.core.exts;

import org.noear.snack.core.utils.IOUtil;

/**
 * 字符阅读器
 * */
public class CharReader {

    private String chars;
    private int _length;
    private int _next = 0;
    private char _val;
    private char _last=0;

    public CharReader(String s) {
        this.chars  = s;
        this._length = s.length();
    }

    public boolean read() {
        if (_next >= _length) {
            return false;
        }else {
            _last = _val;
            _val = chars.charAt(_next++);
            return true;
        }
    }

    public char last() {
        return _last;
    }

    public char next(){
        if(read()){
            return _val;
        }else {
            return IOUtil.EOI;
        }
    }

    public int length(){
        return _length;
    }

    public char value(){
        return _val;
    }
}
