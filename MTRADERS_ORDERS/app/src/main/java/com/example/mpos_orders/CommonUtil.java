package com.example.mpos_orders;

import java.util.ArrayList;

public class CommonUtil {
    public static String SQL_SERVER = "";
    public static String DB = "";
    public static String PASSWORD = "";
    public static String USERNAME = "";
    public static ArrayList<Product> productsList = new ArrayList<Product>();
    public static String loggedUserName = "";
    public static Order order;
    public static Member member;
    public static Double pendingAmt=0d;
    public static Member paymentMember;
    public static ArrayList<OrderProducts> orderProducts=new ArrayList<OrderProducts>();
}
