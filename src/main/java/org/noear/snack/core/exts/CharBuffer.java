package org.noear.snack.core.exts;

/**
 * 字符缓冲器
 * */
public class CharBuffer {

    private char[] buffer;
    private int length;
    public boolean isString = false;

    public CharBuffer(){
        this(1024 * 5);
    }
    public CharBuffer(int capacity) {
        this.buffer  = new char[capacity];
        this.length = 0;
    }

    public void append(char c){
        if (length == buffer.length) {
            char[] newbuf = new char[buffer.length * 2];
            System.arraycopy(buffer, 0, newbuf, 0, buffer.length);
            buffer = newbuf;
        }
        buffer[length++] = c;
    }

    public char charAt(int idx){
        return buffer[idx];
    }

    public int length(){
        return this.length;
    }

    public void setLength(int len){
        this.length = len;
        this.isString = false;
    }

    public void clear(){
        this.length = 0;
    }

    public String toString(){
        return new String(buffer, 0, length);
    }

    public void trimLast(){
        while (length>0) {
            if (buffer[length - 1] != 32) {
                break;
            }
            length--;
        }
    }

}
