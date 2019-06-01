package com.example.bwq;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Product {
    private String ProductName;
    private double Weight;
    private double Qty;
    private int ProductID;
    private Date PdDate;
    private String DtString;
    private String Supplier;

    public String getSupplier() {
        return Supplier;
    }

    public void setSupplier(String supplier) {
        Supplier = supplier;
    }

    public String getDtString() {
        return DtString;
    }

    public void setDtString(String dtString) {
        DtString = dtString;
    }

    public Date getPdDate() {
        try{
            Date ds = new SimpleDateFormat("dd/MM/yyyy hh:mm tt").parse(DtString);
            return ds;
        }
        catch(Exception ex){
            return  new Date();
        }
    }

    public void setPdDate(Date pdDate) {
        PdDate = pdDate;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        Weight = weight;
    }

    public double getQty() {
        return Qty;
    }

    public void setQty(double qty) {
        Qty = qty;
    }
}
