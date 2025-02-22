package com.example.vinoth.vgspos;

import java.util.ArrayList;
import java.util.Date;

public class ReceiptData {
    public Date billDate;
    public Integer billno;
    public String waiter;
    public ArrayList<ItemsCart> itemsCarts;
    public Double discount;
    public Double advance;
}
