package com.example.vgtxtrpt;

import java.util.Date;

public class Sale {
    public int Bill_No;
    public Date Bill_Date;
    public Double Bill_Amount;
    public Double Cash_Amount;
    public Double Card_Amount;
    public Double Upi_Amount;
    public String PaymentMode(){
        if(Bill_Amount==Cash_Amount){
            return "CASH";
        }
        else if(Bill_Amount == Card_Amount){
            return "CARD";
        }
        else if(Bill_Amount==Upi_Amount){
            return "UPI";
        }
        else {
            return "MULTIPLE";
        }
    }
}
