package com.example.vinoth.vgspos;

public class Item {
    private String Item_No;
    private String Item_Name;
    private double Price;
    private double AcPrice;
    private double Stocks;

    public double getStocks() {
        return Stocks;
    }

    public void setStocks(double stocks) {
        Stocks = stocks;
    }

    public double getAcPrice() {
        return AcPrice;
    }

    public void setAcPrice(double acPrice) {
        AcPrice = acPrice;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getItem_No() {
        return Item_No;
    }

    public void setItem_No(String item_No) {
        Item_No = item_No;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }
}
