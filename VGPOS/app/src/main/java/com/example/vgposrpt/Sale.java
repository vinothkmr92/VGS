package com.example.vgposrpt;

import net.sourceforge.jtds.jdbc.DateTime;

import java.time.LocalDateTime;
import java.util.Date;

public class Sale {
    public int Bill_No;
    public Date Bill_Date;
    public Double Bill_Amount;
    public Double Cash_Amount;
    public Double Card_Amount;
    public Double Upi_Amount;
}
