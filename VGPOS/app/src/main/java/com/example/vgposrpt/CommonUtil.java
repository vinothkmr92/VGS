package com.example.vgposrpt;

import java.util.ArrayList;
import java.util.List;

public class CommonUtil {
    public static String SQL_SERVER = "";
    public static String DB = "";
    public static String PASSWORD = "";
    public static String USERNAME = "";
    public static String loggedinUser = "";
    public static Integer loggedinUserRoleID = 0;
    public static ArrayList<Branch> branchList=new ArrayList<Branch>();
    public static ArrayList<Counters> countersList = new ArrayList<>();
    public static ArrayList<Tables> tablesList = new ArrayList<>();
    public static Double totalRevenueAmt;
    public static Branch selectedBarnch = new Branch(0,"ALL");
    public static String frmDate = "";
    public static String toDate="";
    public static String printer="";
    public static String printerKot="";
    public static ArrayList<Product> cartItems=new ArrayList<>();
    public static ArrayList<Product> productsFull = new ArrayList<>();
    public static ArrayList<String> categories = new ArrayList<>();
    public static Integer defBranch=1;
    public static ArrayList<Branch> branches=new ArrayList<>();
    public static ArrayList<String> users = new ArrayList<>();
    public static String PrintOption="None";
    public static String PrintOptionKot="None";
    public static String ReceiptSize = "2";
    public static String PrinterIP = "";
    public static String PrinterIPKot = "";
    public static String ReceiptHeader = "";
    public static String ReceiptFooter = "";
    public static String ReceiptAddress = "";
    public static Boolean includeMRP= false;
    public static Boolean MultiLang = false;
    public static Integer kotNo=1;
    public static String usbDeviceName = "";
    public static String usbDeviceNameKot = "";
}
