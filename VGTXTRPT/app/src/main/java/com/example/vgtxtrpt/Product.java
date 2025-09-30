package com.example.vgtxtrpt;

import java.io.Serializable;

public class Product implements Serializable {
    private  String ProductName;
    private  Double Price;
    private  Integer Qty;
    private  String ProductID;
    private  Integer BranchCode;
    private  Double Amount;
    private String BatchNo="";
    private String SupplierName="";
    private String Category="";
    private Double MRP;
    private Integer SNo;
    private Double Stocks=0d;
    private Double PurchasedPrice;

    public Double getStocks() {
        return Stocks;
    }

    public void setStocks(Double stocks) {
        Stocks = stocks;
    }

    public Double getPurchasedPrice() {
        return PurchasedPrice;
    }

    public void setPurchasedPrice(Double purchasedPrice) {
        PurchasedPrice = purchasedPrice;
    }

    public Integer getSNo() {
        return SNo;
    }

    public void setSNo(Integer SNo) {
        this.SNo = SNo;
    }

    public Double getMRP() {
        return MRP;
    }

    public void setMRP(Double MRP) {
        this.MRP = MRP;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getSupplierName() {
        return SupplierName;
    }

    public void setSupplierName(String supplierName) {
        SupplierName = supplierName;
    }

    public void setAmount(Double amount) {
        Amount = amount;
    }

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

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
