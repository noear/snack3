package _models;

/**
 * @author noear 2024/5/29 created
 */
public class SModel {
    public String name;
    public int age;

    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "SModel{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
