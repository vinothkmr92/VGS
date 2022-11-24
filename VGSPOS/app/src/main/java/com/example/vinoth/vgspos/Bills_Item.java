package com.example.vinoth.vgspos;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Bills_Item {
    private int Bill_No;
    private String Bill_DateStr;
    private Date Bill_Date;
    private String Item_Name;
    private double Qty;
    private String Waiter;
    private int Item_No;
    private double Price;

    public void setBill_Date(Date bill_Date) {
        Bill_Date = bill_Date;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getItem_No() {
        return Item_No;
    }

    public void setItem_No(int item_No) {
        Item_No = item_No;
    }

    public String getWaiter() {
        return Waiter;
    }

    public void setWaiter(String waiter) {
        Waiter = waiter;
    }

    public int getBill_No() {
        return Bill_No;
    }

    public void setBill_No(int bill_No) {
        Bill_No = bill_No;
    }

    public String getBill_DateStr() {
        return Bill_DateStr;
    }

    public void setBill_DateStr(String bill_DateStr) {
        Bill_DateStr = bill_DateStr;
    }

    public Date getBill_Date() {
        try{
            Bill_Date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(getBill_DateStr());
            return Bill_Date;
        }
        catch (Exception ex){
            return new Date();
        }

    }

    public void setBill_Date(String bill_Date) {
        try
        {
            Bill_Date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(bill_Date);
        }
        catch (Exception ex){
            Bill_Date = new Date();
        }

    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_name) {
        Item_Name = item_name;
    }

    public double getQty() {
        return Qty;
    }

    public void setQty(double qty) {
        Qty = qty;
    }

}
