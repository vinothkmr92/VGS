package com.example.vgposrpt;

public class ProductsSummary {
    private Double SoldQty;
    private String ProductName;
    private Double SoldAmount;
    public Double getSoldQty() {
        return SoldQty;
    }

    public void setSoldQty(Double soldQty) {
        SoldQty = soldQty;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public Double getSoldAmount() {
        return SoldAmount;
    }

    public void setSoldAmount(Double soldAmount) {
        SoldAmount = soldAmount;
    }
}
