package com.example.vgposrpt;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class BillDetails {
    public ArrayList<Product> billProducts;
    public Integer branchCode;
    public String counter = "";
    public String billUser;
    public Integer BillAmount;
    public Integer CashAmt=0;
    public Integer CardAmt=0;
    public Integer UpiAmt=0;
    public Integer BillNo=0;
    public Integer MemberID = 0;
    public String MemberName="";
    public Date billDate;
}
