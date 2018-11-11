package com.example.vinoth.baklfin;

import android.provider.ContactsContract;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Grp_Payment_Details {
    private int Payment_ID;
    private String Payment_Date_str;
    private Date Payment_Date;
    private int Customer_ID;
    private int GroupID;
    private double Paid_Amt;
    private String ReceivedBy;
    private double Balance_Amt;
    private String Payment_Type;

    private int SyncSts;

    public int getSyncSts() {
        return SyncSts;
    }

    public void setSyncSts(int syncSts) {
        SyncSts = syncSts;
    }

    public int getPayment_ID() {
        return Payment_ID;
    }

    public void setPayment_ID(int payment_ID) {
        Payment_ID = payment_ID;
    }

    public String getPayment_Date_str() {
        return Payment_Date_str;
    }

    public void setPayment_Date_str(String payment_Date_str) {
        Payment_Date_str = payment_Date_str;
    }

    public Date getPayment_Date() {
        try{
            Payment_Date=new SimpleDateFormat("dd-MMM-yyyy").parse(getPayment_Date_str());

        }
        catch (Exception ex){

        }

        return Payment_Date;
    }

    public void setPayment_Date(Date payment_Date) {
        Payment_Date = payment_Date;
    }

    public int getCustomer_ID() {
        return Customer_ID;
    }

    public void setCustomer_ID(int customer_ID) {
        Customer_ID = customer_ID;
    }

    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }

    public double getPaid_Amt() {
        return Paid_Amt;
    }

    public void setPaid_Amt(double paid_Amt) {
        Paid_Amt = paid_Amt;
    }

    public String getReceivedBy() {
        return ReceivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        ReceivedBy = receivedBy;
    }

    public double getBalance_Amt() {
        return Balance_Amt;
    }

    public void setBalance_Amt(double balance_Amt) {
        Balance_Amt = balance_Amt;
    }

    public String getPayment_Type() {
        return Payment_Type;
    }

    public void setPayment_Type(String payment_Type) {
        Payment_Type = payment_Type;
    }
}
