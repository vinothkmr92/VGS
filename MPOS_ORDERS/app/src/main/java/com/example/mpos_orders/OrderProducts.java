package com.example.mpos_orders;

public class OrderProducts {
    private String ProductID;
    private String ProductName;
    private Double Quantity;
    private Double Amt;

    public Double getAmt() {
        return Amt;
    }

    public void setAmt(Double amt) {
        Amt = amt;
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

    public Double getQuantity() {
        return Quantity;
    }

    public void setQuantity(Double quantity) {
        Quantity = quantity;
    }
}
