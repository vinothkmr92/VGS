package com.example.vinoth.vgsposbt;

import java.util.Date;
import java.util.Map;

public class SaleReport {
    private String BillDate;
    private String BillNo;
    private Double BillAmount;
    private Double SaleAmount;
    private Date billDt;
    public String CustomerName;
    public String CustomerID;
    public Double totalWt;
    public String ward;
    public String zone;
    public Map<String,Double> itemwiseqty;
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
