package com.example.vinoth.vgspos;

import java.util.Date;

public class SaleReport {
    private String BillDate;
    private String BillNo;
    private Double BillAmount;
    private Double Discount;
    private Double SaleAmount;
    private Date billDt;
    private String paymentMode;

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public Double getDiscount() {
        return Discount;
    }

    public void setDiscount(Double discount) {
        Discount = discount;
    }

    public Double getSaleAmount() {
        return SaleAmount;
    }

    public void setSaleAmount(Double saleAmount) {
        SaleAmount = saleAmount;
    }

    public Date getBillDt() {
        return billDt;
    }

    public void setBillDt(Date billDt) {
        this.billDt = billDt;
    }

    public String getBillDate() {
        return BillDate;
    }

    public void setBillDate(String billDate) {
        BillDate = billDate;
    }

    public String getBillNo() {
        return BillNo;
    }

    public void setBillNo(String billNo) {
        BillNo = billNo;
    }

    public Double getBillAmount() {
        return BillAmount;
    }

    public void setBillAmount(Double billAmount) {
        BillAmount = billAmount;
    }
}
