package com.example.vinoth.baklfin;

public class Users {
    String TABLE_NAME = "USERS";
    String USER_ID_COLUMN = "USER_ID";
    String USER_NAME_COLUMN = "USER_NAME";
    String MOBILE_NUMBER_COLUMN = "MOBILE_NUMBER";
    String PASSWORD_COLUMN = "PASSWORD";

    Integer User_Id;
    String User_Name;
    String Mobile_Number;
    String Password;

    public Integer getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(Integer user_Id) {
        User_Id = user_Id;
    }

    public String getUser_Name() {
        return User_Name;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public String getMobile_Number() {
        return Mobile_Number;
    }

    public void setMobile_Number(String mobile_Number) {
        Mobile_Number = mobile_Number;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
