package com.example.mpos_orders;

import com.paytm.pg.merchant.PaytmChecksum;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PaytmInitiateTransaction {
    private String TxnID;
    private String Mid;
    private String WebsiteName;
    private String orderID;
    private String Amount;
    private Member memberDetails;

    public String getTxnID() {
        return TxnID;
    }

    public void setTxnID(String txnID) {
        TxnID = txnID;
    }

    public String getMid() {
        return Mid;
    }

    public void setMid(String mid) {
        Mid = mid;
    }

    public String getWebsiteName() {
        return WebsiteName;
    }

    public void setWebsiteName(String websiteName) {
        WebsiteName = websiteName;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public Member getMemberDetails() {
        return memberDetails;
    }

    public void setMemberDetails(Member memberDetails) {
        this.memberDetails = memberDetails;
    }
    public void GenerateTransactionID() throws Exception {
        try {



        }
        catch (Exception exception){
            throw  exception;
        }
    }
}
