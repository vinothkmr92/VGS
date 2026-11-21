package com.example.vgposrpt;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Locale;


public class CollectionFragment extends Fragment implements View.OnClickListener {


    TextView searchCustomer;
    TextView pendingAmtView;
    private Button btnHistory;
    private Button btnPay;
    Dialog dialog;
    Double outAmount;
    public static CollectionFragment collectionFragmentInstance;

    public static CollectionFragment getInstance(){
        return collectionFragmentInstance;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        Context cn = getContext();
        View view = inflater.inflate(R.layout.fragment_collection, container, false);
        try{
            searchCustomer = view.findViewById(R.id.collectionMember);
            pendingAmtView = view.findViewById(R.id.outAmt);
            btnPay = view.findViewById(R.id.btnPay);
            btnHistory = view.findViewById(R.id.btnHist);
            collectionFragmentInstance = this;
            btnHistory.setOnClickListener(this);
            btnPay.setOnClickListener(this);
            searchCustomer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Initialize dialog
                    dialog=new Dialog(CollectionFragment.getInstance().getContext());

                    // set custom dialog
                    dialog.setContentView(R.layout.dialog_searchable_spinner);

                    // set custom height and width
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);

                    // set transparent background
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    // show dialog
                    dialog.show();

                    // Initialize and assign variable
                    EditText editText=dialog.findViewById(R.id.edit_text);
                    ListView listView=dialog.findViewById(R.id.list_view);

                    // Initialize array adapter
                    ArrayAdapter<Customer> adapter=new ArrayAdapter<>(CollectionFragment.getInstance().getContext(), android.R.layout.simple_list_item_1,CommonUtil.customers);

                    // set adapter
                    listView.setAdapter(adapter);
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            adapter.getFilter().filter(s);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // when item selected from list
                            // set selected item on textView
                            searchCustomer.setText(adapter.getItem(position).toString());
                            String member = searchCustomer.getText().toString();
                            if(!member.isEmpty()){
                                String[] mc = member.split("-");
                                int mid = GetMemberID(mc[0],mc[1]);
                                new GetOutStandingAmt().execute(mid);
                            }
                            // Dismiss dialog
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
        catch (Exception e){
            showCustomDialog("Error",e.getMessage());
        }
        return view;

    }
    public void showCustomDialog(String title,String Message) {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle(title)
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton("Ok", (dialog, which) -> {
                    // Positive action
                })
                .show();
    }
    private Integer GetMemberID(String memberName,String mobileNumber){
        Customer cn = CommonUtil.customers.stream().filter(c->c.MobileNumber.equals(mobileNumber) && c.MemberName.equals(memberName)).findFirst().orElse(null);
        Integer memid = cn!=null ? cn.MemberID:0;
        return memid;
    }

    public void getPaymentInfo(Integer memberID,String paymentMode,double amount){
        try{

        }
        catch (Exception e){
            showCustomDialog("Error",e.getMessage());
        }
    }
    @Override
    public void onClick(View view) {
         switch (view.getId()){

             case R.id.btnPay:
                 String member = searchCustomer.getText().toString();
                 int mid = 0;
                 if(!member.isEmpty()){
                     String[] mc = member.split("-");
                     mid = GetMemberID(mc[0],mc[1]);
                 }
                 if(mid==0){
                     showCustomDialog("Warning","No valid member selected. please select valid member.");
                     return;
                 }
                 if(outAmount==0){
                     showCustomDialog("Warning","No Outstanding amount to Pay.");
                     return;
                 }
                 try {
                     GetPaymentDetails getPayment = new GetPaymentDetails();
                     getPayment.memID = mid;
                     getPayment.show(getChildFragmentManager(),"Payment Details");
                 }catch (Exception ex){
                     showCustomDialog("Error",ex.getMessage().toString());
                 }

         }
    }

    public class GetOutStandingAmt extends AsyncTask<Integer,String,Double> {
        Boolean isSuccess = false;
        String error = "";
        private final ProgressDialog dialog = new ProgressDialog(getContext(),R.style.CustomProgressStyle);
        ConnectionClass connectionClass = null;
        Connection con = null;
        @Override
        protected void onPreExecute() {
            connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
            con = connectionClass.CONN();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setTitle("Loading");
            dialog.setMessage("Please Wait...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Double outAmt) {
            if(isSuccess){
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                formatter.setMaximumFractionDigits(0);
                String symbol = formatter.getCurrency().getSymbol();
                pendingAmtView.setText(formatter.format(outAmt).replace(symbol,symbol+" "));
                outAmount = outAmt;
            }
            else {
                if(dialog.isShowing()){
                    dialog.hide();
                }
                if(!error.isEmpty()){
                    showCustomDialog("Status",error);
                }
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }
        @Override
        protected Double doInBackground(Integer... params) {
            Double outAmt = 0d;
            try {
                if (con == null) {
                    error = "Database Connection Failed";
                } else {
                    String query = String.format("SELECT PENDING_AMT FROM MEMBERS WHERE MEMBER_ID=%d",params[0]);
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery(query);
                    if(rs.next()){
                        outAmt = rs.getDouble("PENDING_AMT");
                    }
                    rs.close();
                    isSuccess = true;
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions"+ex.getMessage();
            }
            return outAmt;
        }
    }
}