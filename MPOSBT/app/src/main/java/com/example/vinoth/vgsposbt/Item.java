package com.example.vinoth.vgsposbt;

public class Item {
    private Integer Item_No;
    private String Item_Name;
    private double Price;

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public Integer getItem_No() {
        return Item_No;
    }

    public void setItem_No(Integer item_No) {
        Item_No = item_No;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }
}
