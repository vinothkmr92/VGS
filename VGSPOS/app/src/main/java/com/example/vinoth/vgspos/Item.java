package com.example.vinoth.vgspos;

public class Item {
    private int Item_No;
    private String Item_Name;
    private double N_Price;
    private double Ac_Price;
    private Tax tax;

    public int getItem_No() {
        return Item_No;
    }

    public void setItem_No(int item_No) {
        Item_No = item_No;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }

    public double getN_Price() {
        return N_Price;
    }

    public void setN_Price(double n_Price) {
        N_Price = n_Price;
    }

    public double getAc_Price() {
        return Ac_Price;
    }

    public void setAc_Price(double ac_Price) {
        Ac_Price = ac_Price;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }
}
