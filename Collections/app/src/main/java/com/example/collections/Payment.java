package com.example.collections;

import java.util.Date;

public class Payment {
    private Integer PaymentID;
    private Date PaymentDate;
    private Double PaidAmount;
    private String CollectedPerson;
    private String PaymentMode;

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

    public Date getPaymentDate() {
        return PaymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        PaymentDate = paymentDate;
    }

    public Integer getPaymentID() {
        return PaymentID;
    }

    public void setPaymentID(Integer paymentID) {
        PaymentID = paymentID;
    }
}
