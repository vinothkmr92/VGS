package com.example.vgposrpt;

import java.util.ArrayList;

public class BillDetails {
    public ArrayList<Product> billProducts;
    public Integer branchCode;
    public String counter = "CD1";
    public String billUser;
    public Double BillAmount;
    public Double CashAmt=0d;
    public Double CardAmt=0d;
    public Double UpiAmt=0d;
}
