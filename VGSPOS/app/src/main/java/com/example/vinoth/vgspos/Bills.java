package com.example.vinoth.vgspos;

public class Bills {
    private int Bill_No;
    private String Bill_Date;
    private double Sale_Amt;
    private String user;

    private double Final_Amt;
    private double Discount;

    public double getFinal_Amt() {
        Final_Amt = Sale_Amt-Discount;
        return Final_Amt;
    }

    public double getDiscount() {
        return Discount;
    }

    public void setDiscount(double discount) {
        Discount = discount;
    }

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
