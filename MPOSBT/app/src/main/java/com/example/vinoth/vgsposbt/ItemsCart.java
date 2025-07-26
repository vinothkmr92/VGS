package com.example.vinoth.vgsposbt;

public class ItemsCart {
    private Integer Item_No;
    private String Item_Name;
    private double Qty;
    private double Price;
    private double Deduction;
    public double BinWeight;
    public double NetWeight(){
        return  getQty()-BinWeight-getDeduction();
    }
    public double getDeduction() {
        return Deduction;
    }

    public void setDeduction(double deduction) {
        Deduction = deduction;
    }

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

    public double getQty() {
        return Qty;
    }

    public void setQty(double qty) {
        Qty = qty;
    }
    public String getJsonObject(){
        return "{ItemNo:"+getItem_No()+",ItemName:"+getItem_Name()+",Quantity:"+getQty()+"}";
    }
}
