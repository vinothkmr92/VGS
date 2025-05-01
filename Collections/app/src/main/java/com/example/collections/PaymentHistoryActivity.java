package com.example.collections;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaymentHistoryActivity extends AppCompatActivity {

    TextView memNametxtView;
    TextView loanNotxtView;
    LinearLayout viewHistory;
    LinearLayout payoptionlayout;
    TextView totalpaid;
    private MaterialRadioButton principalRadioBtn;
    private MaterialRadioButton interestRadioBtn;
    public static PaymentHistoryActivity instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment_history);
        try{
            memNametxtView = findViewById(R.id.memNameViewHist);
            loanNotxtView = findViewById(R.id.loanNoViewHist);
            viewHistory = findViewById(R.id.viewHist);
            payoptionlayout = findViewById(R.id.payoptionLayout);
            principalRadioBtn = findViewById(R.id.principalPay);
            interestRadioBtn = findViewById(R.id.intesetPay);
            totalpaid = findViewById(R.id.totalpaidAmt);
            memNametxtView.setText(CommonUtil.memberName);
            loanNotxtView.setText(CommonUtil.loanNo);
            payoptionlayout.setVisibility(CommonUtil.isinterestonly?View.VISIBLE:View.GONE);
            interestRadioBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    principalRadioBtn.setChecked(!interestRadioBtn.isChecked());
                    List<Payment> payms = CommonUtil.payments.stream().filter(k->k.isInterest==interestRadioBtn.isChecked()).collect(Collectors.toList());
                    LoadPaymentsCart(payms);
                }
            });
            principalRadioBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    interestRadioBtn.setChecked(!principalRadioBtn.isChecked());
                    List<Payment> payms = CommonUtil.payments.stream().filter(k->k.isInterest==interestRadioBtn.isChecked()).collect(Collectors.toList());
                    LoadPaymentsCart(payms);
                }
            });
            for (Payment p:CommonUtil.payments) {
                addCard(p);
            }
            Double paidAmt = CommonUtil.payments.size()>0 ? CommonUtil.payments.stream().mapToDouble(k->k.getPaidAmount()).sum():0;
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            formatter.setMaximumFractionDigits(0);
            String symbol = formatter.getCurrency().getSymbol();
            totalpaid.setText(formatter.format(paidAmt).replace(symbol,symbol+" "));
            instance = this;
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void LoadPaymentsCart(List<Payment> payments){
        viewHistory.removeAllViews();
        for(Payment p :payments){
            addCard(p);
        }
        Double paidAmt = payments.size()>0 ? payments.stream().mapToDouble(k->k.getPaidAmount()).sum():0;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        totalpaid.setText(formatter.format(paidAmt).replace(symbol,symbol+" "));
    }
    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }

    private void addCard(Payment sr){
        View view = getLayoutInflater().inflate(R.layout.cart_payments,null);
        TextView payDate = view.findViewById(R.id.paidDate);
        TextView agent = view.findViewById(R.id.agentName);
        TextView paidAmt = view.findViewById(R.id.paidAmt);
        TextView payMode = view.findViewById(R.id.paymentModeHist);
        MaterialButton btnReprint = view.findViewById(R.id.btnReprint);
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm aaa",Locale.getDefault());
        payDate.setText(format.format(sr.getPaymentDate()));
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        String moneyString = formatter.format(sr.getPaidAmount()).replace(symbol,symbol+" ");
        paidAmt.setText(moneyString);
        agent.setText(sr.getCollectedPerson());
        payMode.setText(sr.getPaymentMode());
        btnReprint.setTag(sr.getPaymentID());
        btnReprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer paymentID = (Integer)v.getTag();
                try {
                    Payment payments = GetPayment(paymentID);
                    ReprintPaymentReceipt(payments);
                }catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage().toString());
                }
            }
        });
        viewHistory.addView(view);
    }
    public void  ReprintPaymentReceipt(Payment paymentm){
        try{
            Double outstanding = CommonUtil.Outstanding+paymentm.getPaidAmount();
            Double bal = CommonUtil.Outstanding;
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            formatter.setMaximumFractionDigits(0);
            String symbol = formatter.getCurrency().getSymbol();
            String outstandingstr = formatter.format(outstanding).replace(symbol,"Rs. ");
            String paidAmStr = formatter.format(paymentm.getPaidAmount()).replace(symbol,"Rs. ");
            String balStr = formatter.format(bal).replace(symbol,"Rs. ");
            ReceiptDtl rptDtl = new ReceiptDtl();
            rptDtl.MemberName = CommonUtil.memberName;
            rptDtl.LoanNo = paymentm.MemberID;
            rptDtl.Outstanding = outstandingstr;
            rptDtl.Paid = paidAmStr;
            rptDtl.Balance = balStr;
            rptDtl.PaymentMode = paymentm.getPaymentMode();
            rptDtl.PaymentID = String.valueOf(paymentm.getPaymentID());
            rptDtl.PaidDate = paymentm.getPaymentDate();
            rptDtl.EndDate = paymentm.EndDate;
            PrinterUtil printerUtil = new PrinterUtil(this,rptDtl,null,null,true,false,false);
            printerUtil.Print();
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }

    }
    private Payment GetPayment(Integer paymentID){
        Payment payment =null;
        for (Payment p:CommonUtil.payments) {
            if(p.getPaymentID() == paymentID){
                payment = p;
            }
        }
        return  payment;
    }
}