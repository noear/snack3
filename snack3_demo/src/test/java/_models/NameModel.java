package _models;


public class NameModel {
    private String userName;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "NameModel{" +
                "userName='" + userName + '\'' +
                '}';
    }
}
