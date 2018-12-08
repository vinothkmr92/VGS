package com.example.vinoth.vgspos;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Bills_Item {
    private int Bill_No;
    private String Bill_DateStr;
    private Date Bill_Date;
    private int Item_No;
    private double Qty;
    private double Price;

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
            Bill_Date=new SimpleDateFormat("dd-MMM-yyyy").parse(getBill_DateStr());
            return Bill_Date;
        }
        catch (Exception ex){
            return new Date();
        }

    }

    public void setBill_Date(String bill_Date) {
        try
        {
            Bill_Date=new SimpleDateFormat("dd-MMM-yyyy").parse(bill_Date);
        }
        catch (Exception ex){
            Bill_Date = new Date();
        }

    }

    public int getItem_No() {
        return Item_No;
    }

    public void setItem_No(int item_No) {
        Item_No = item_No;
    }

    public double getQty() {
        return Qty;
    }

    public void setQty(double qty) {
        Qty = qty;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }
}
