package com.example.vinoth.vgspos;

import java.util.Date;

public class ItemsRpt {
    private String ItemID;
    private double Amount;

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    public double getAmount() {
        return Amount;
    }

    private String ItemName;
    private double Quantity;

    public void setAmount(double amount) {
        Amount = amount;
    }
    private Date billDate;

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public double getQuantity() {
        return Quantity;
    }

    public void setQuantity(double quantity) {
        Quantity = quantity;
    }
}
