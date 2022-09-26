package com.example.vinoth.vgspos;

public class ItemsCart {
    private int Item_No;
    private String Item_Name;
    private int Qty;
    private double Price;

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

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }
    public String getJsonObject(){
        return "{ItemNo:"+getItem_No()+",ItemName:"+getItem_Name()+",Quantity:"+getQty()+"}";
    }
}
