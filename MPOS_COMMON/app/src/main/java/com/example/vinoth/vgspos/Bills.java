package com.example.vinoth.vgspos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Bills {
    private int Bill_No;
    private String Bill_DtStr;
    private Date Bill_Date;
    private double Sale_Amt;
    private Users user;

    public int getBill_No() {
        return Bill_No;
    }

    public void setBill_No(int bill_No) {
        Bill_No = bill_No;
    }

    public String getBill_DtStr() {
        return Bill_DtStr;
    }

    public void setBill_DtStr(String bill_DtStr) {
        Bill_DtStr = bill_DtStr;
    }

    public Date getBill_Date() {
        try{
            Bill_Date=new SimpleDateFormat("dd-MMM-yyyy").parse(getBill_DtStr());
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

    public double getSale_Amt() {
        return Sale_Amt;
    }

    public void setSale_Amt(double sale_Amt) {
        Sale_Amt = sale_Amt;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
