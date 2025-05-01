package com.example.collections;

import java.sql.Timestamp;
import java.util.Date;

public class Payment {
    private Integer PaymentID;
    private Timestamp PaymentDate;
    private Double PaidAmount;
    private String CollectedPerson;
    private String PaymentMode;
    public String MemberID;
    public Date EndDate;
    public boolean isInterest;
    public String getPaymentMode() {
        return PaymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        PaymentMode = paymentMode;
    }

    public String getCollectedPerson() {
        return CollectedPerson;
    }

    public void setCollectedPerson(String collectedPerson) {
        CollectedPerson = collectedPerson;
    }

    public Double getPaidAmount() {
        return PaidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        PaidAmount = paidAmount;
    }

    public Timestamp getPaymentDate() {
        return PaymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        PaymentDate = paymentDate;
    }

    public Integer getPaymentID() {
        return PaymentID;
    }

    public void setPaymentID(Integer paymentID) {
        PaymentID = paymentID;
    }
}
