package com.example.vinoth.vgspos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Bills {
    private int Bill_No;
    private String Bill_Date;
    private double Sale_Amt;
    private String user;

    public int getBill_No() {
        return Bill_No;
    }

    public void setBill_No(int bill_No) {
        Bill_No = bill_No;
    }

    public String getBill_Date() {
        return Bill_Date;

    }

    public void setBill_Date(String bill_Date) {
        Bill_Date = bill_Date;

    }

    public double getSale_Amt() {
        return Sale_Amt;
    }

    public void setSale_Amt(double sale_Amt) {
        Sale_Amt = sale_Amt;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
