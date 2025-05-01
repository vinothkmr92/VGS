package com.example.collections;

import java.util.Date;

public class Loan {
    private String LoanNo;
    private String LoanType;
    private double LoanAmount;
    private double Interest;
    private double InterestAmt;
    private double PaidAmt;
    private double BalanceAmt;
    private Integer Term;

    public Date EndDate;
    public double PenaltyAmt;
    public Integer getTerm() {
        return Term;
    }

    public void setTerm(Integer term) {
        Term = term;
    }

    public String getLoanNo() {
        return LoanNo;
    }

    public void setLoanNo(String loanNo) {
        LoanNo = loanNo;
    }

    public String getLoanType() {
        return LoanType;
    }

    public void setLoanType(String loanType) {
        LoanType = loanType;
    }

    public double getLoanAmount() {
        return LoanAmount;
    }

    public void setLoanAmount(double loanAmount) {
        LoanAmount = loanAmount;
    }

    public double getBalanceAmt() {
        if(LoanType.equals("Daily") || LoanType.equals("Weekly")){
            InterestAmt=0;
        }
        else {
           InterestAmt = Term*LoanAmount*(Interest/100);
           InterestAmt = Math.round(InterestAmt);
        }
        BalanceAmt = LoanAmount+InterestAmt+PenaltyAmt-PaidAmt;
        return BalanceAmt;
    }

    public double getPaidAmt() {
        return PaidAmt;
    }

    public void setPaidAmt(double paidAmt) {
        PaidAmt = paidAmt;
    }


    public double getInterest() {
        return Interest;
    }

    public void setInterest(double interest) {
        Interest = interest;
    }
}
