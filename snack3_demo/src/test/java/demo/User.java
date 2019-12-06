package demo;

import java.util.Date;

public class User {
    public String name;
    public int age;
    public String emailAddress;
    public transient Date date= new Date();

    public User(){}

    public User(String name, int age){
        this.name = name;
        this.age = age;
    }

    public User(String name, int age, String mail){
        this.name = name;
        this.age = age;
        this.emailAddress = mail;
    }
}
