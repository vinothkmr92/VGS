package com.example.vinoth.baklfin;

public class Customers {
    private int CustomerID;
    private String CustomerName;
    private String MobileNumber;
    private String Address;
    private String ID_Proof;

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public String getCustomerName() {
        return CustomerName;
    }

    public void setCustomerName(String customerName) {
        CustomerName = customerName;
    }

    public String getMobileNumber() {
        return MobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        MobileNumber = mobileNumber;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getID_Proof() {
        return ID_Proof;
    }

    public void setID_Proof(String ID_Proof) {
        this.ID_Proof = ID_Proof;
    }

    @Override
    public String toString() {
        return CustomerID+"-"+CustomerName;
    }
}
