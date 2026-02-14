package com.example.collections;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
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
    public Date StartDate;
    public Date EndDate;
    public long GetLoanDays(){
        Instant stin = StartDate.toInstant();
        ZoneId defaultZone = ZoneId.systemDefault();
        LocalDate startDate = stin.atZone(defaultZone).toLocalDate();
        Date today = new Date();
        LocalDate todayDate = today.toInstant().atZone(defaultZone).toLocalDate();
        long days = ChronoUnit.DAYS.between(startDate, todayDate);
        days++;
        return days;
    }
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
