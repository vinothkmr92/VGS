package com.example.vinoth.vgspos;

import java.util.ArrayList;
import java.util.Date;

public class ReceiptData {
    public Date billDate;
    public Integer billno;
    public String waiter;
    public ArrayList<ItemsCart> itemsCarts;
    public Double discount=0d;
    public Double advance=0d;
}
