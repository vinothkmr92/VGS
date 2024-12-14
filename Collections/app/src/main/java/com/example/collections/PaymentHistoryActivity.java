package com.example.collections;

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

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PaymentHistoryActivity extends AppCompatActivity {

    TextView memNametxtView;
    TextView loanNotxtView;
    LinearLayout viewHistory;
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
            memNametxtView.setText(CommonUtil.memberName);
            loanNotxtView.setText(CommonUtil.loanNo);
            for (Payment p:CommonUtil.payments) {
                addCard(p);
            }
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

    public void showCustomDialog(String title,String Message) {
        MaterialAlertDialogBuilder dialog =  new MaterialAlertDialogBuilder(PaymentHistoryActivity.this,
                com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog_Centered);

        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
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
            PrinterUtil printerUtil = new PrinterUtil(this,CommonUtil.memberName,
                    CommonUtil.loanNo,outstandingstr,paidAmStr,balStr,
                    paymentm.getPaymentMode(),String.valueOf(paymentm.getPaymentID()),paymentm.getPaymentDate(),true);
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