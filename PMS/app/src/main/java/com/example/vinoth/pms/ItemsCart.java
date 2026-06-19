package com.example.vinoth.pms;

public class ItemsCart {
    public double Qty;
    private String Item_No;
    private String Item_Name;
    private double Price;
    private double MRP;
    private double VehicleNo;

    public double getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(double vehicleNo) {
        VehicleNo = vehicleNo;
    }

    public double getMRP() {
        return MRP;
    }

    public void setMRP(double MRP) {
        this.MRP = MRP;
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

    public String getJsonObject(){
        return "{ItemNo:"+getItem_No()+",ItemName:"+getItem_Name()+",Quantity:"+Qty+"}";
    }
}
