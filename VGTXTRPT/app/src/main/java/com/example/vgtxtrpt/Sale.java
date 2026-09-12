package com.example.vgtxtrpt;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Sale {
    public int Bill_No;
    public Date Bill_Date;
    public Double Bill_Amount;
    public Double Cash_Amount;
    public Double Card_Amount;
    public Double Upi_Amount;
    public String CounterID;
    public String BillURL()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        String bd = formatter.format(Bill_Date);
        String billURL = String.format("%s%s/%s/%s/%d.pdf",CommonUtil.FTP_URL.toLowerCase().replace("ftp", "https").replace("8085", "8443"),CommonUtil.FTP_PATH.toLowerCase(),CounterID,bd,Bill_No);
        return billURL;
    }
    public String PaymentMode(){
        if(Bill_Amount.equals(Cash_Amount)){
            return "CASH";
        }
        else if(Bill_Amount.equals(Card_Amount)){
            return "CARD";
        }
        else if(Bill_Amount.equals(Upi_Amount)){
            return "UPI";
        }
        else {
            return "MULTIPLE";
        }
    }
}
