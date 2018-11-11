package com.example.vinoth.baklfin;

import java.util.ArrayList;

public class Groups {
    private int GroupID;
    private String GroupName;
    private double Amount;
    private int MaxCount;
    private double Interst;
    private int Tenure;
    private double TotalInterestAmt;

    public double getTotalInterestAmt() {
        double tt = Amount + (Amount * (Interst / 100));
        double interestper = tt - Amount;
        TotalInterestAmt =  Math.round((interestper * Tenure));
        return TotalInterestAmt;
    }

    public double getNetPayable() {
        NetPayable = getTotalInterestAmt()+Amount;
        return NetPayable;
    }

    public double getMonthlyAmt() {
        double mn = getNetPayable()/Tenure;
        MonthlyAmt = Math.round(mn);
        return MonthlyAmt;
    }
    private ArrayList<Customers> CustomerOnGrp;

    public ArrayList<Customers> getCustomerOnGrp() {
        return CustomerOnGrp;
    }

    public void setCustomerOnGrp(ArrayList<Customers> customerOnGrp) {
        CustomerOnGrp = customerOnGrp;
    }

    private double NetPayable;
    private double MonthlyAmt;



    public int getGroupID() {
        return GroupID;
    }

    public void setGroupID(int groupID) {
        GroupID = groupID;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public int getMaxCount() {
        return MaxCount;
    }

    public void setMaxCount(int maxCount) {
        MaxCount = maxCount;
    }

    public double getInterst() {
        return Interst;
    }

    public void setInterst(double interst) {
        Interst = interst;
    }

    public int getTenure() {
        return Tenure;
    }

    public void setTenure(int tenure) {
        Tenure = tenure;
    }

    @Override
    public String toString() {
        return GroupName;
    }
}
