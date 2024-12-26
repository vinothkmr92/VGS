package com.example.vgposrpt;

import java.io.Serializable;

import kotlin.jvm.internal.PropertyReference0Impl;

public class Product implements Serializable {
    private  String ProductName;
    private  Double Price;
    private  Integer Qty;
    private  String ProductID;
    private  Integer BranchCode;
    private  Double Amount;

    public Double getAmount() {
        return Price*Qty;
    }

    public Integer getBranchCode() {
        return BranchCode;
    }

    public void setBranchCode(Integer branchCode) {
        BranchCode = branchCode;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public Integer getQty() {
        return Qty;
    }

    public void setQty(Integer qty) {
        Qty = qty;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }
}
