package com.example.vgposrpt;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.icu.text.NumberFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class SaleActivity extends AppCompatActivity implements View.OnClickListener {

    TextView totalAmt;
    LinearLayout prView;
    LinearLayout categoryView;
    LinearLayout productsView;
    ArrayList<Product> productCart;
    ImageButton btnViewCart;
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;
    GmsBarcodeScanner scanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sale);
        try{
            categoryView = findViewById(R.id.catView);
            productsView = findViewById(R.id.PdView);
            totalAmt = findViewById(R.id.billAmt);
            prView = findViewById(R.id.prView);
            btnViewCart = findViewById(R.id.btnViewCart);
            btnViewCart.setOnClickListener(this);
            productCart = new ArrayList<>();
            if(CommonUtil.cartItems.size()>0){
                productCart = CommonUtil.cartItems;
                for (Product p:productCart) {
                    LoadPRView(p);
                }
                Double billAmt = productCart.size()>0 ? productCart.stream().mapToDouble(c->c.getAmount()).sum() :0d;
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
                formatter.setMaximumFractionDigits(0);
                String symbol = formatter.getCurrency().getSymbol();
                totalAmt.setText(formatter.format(billAmt).replace(symbol,symbol+" "));
            }
            GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE,Barcode.FORMAT_CODE_128)
                    .build();
            scanner = GmsBarcodeScanning.getClient(SaleActivity.this,options);
            if(CommonUtil.categories.size()>0){
               LoadCategoryMenu();
               LoadProductsMenu(CommonUtil.categories.get(0));
            }
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
    private void LoadCategoryMenu(){
        for (String ct:
             CommonUtil.categories) {
            BuildCatButtons(ct);
        }
    }
    private void LoadProductsMenu(String catName){
        productsView.removeAllViews();


        List<Product> products = CommonUtil.productsFull.stream().filter(c->c.getCategory().equals(catName)).collect(Collectors.toList());
        int itemcount = products.size();
        while (itemcount>0){
            LinearLayout ln = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            ln.setLayoutParams(layoutParams);
            ln.setOrientation(LinearLayout.HORIZONTAL);
            ln.setPadding(10,10,10,10);
            List<Product> splic = products.stream().limit(6).collect(Collectors.toList());
            for (Product ic:splic) {
                Button btn = BuildProductsButtons(ic.getProductName(),ic.getProductID());
                ln.addView(btn);
                products.remove(ic);
            }
            itemcount = products.size();
            if(splic.size()>0){
                productsView.addView(ln);
            }
        }
       // productsView.setStretchAllColumns(true);
    }
    private Button BuildProductsButtons(String prName,String prID){
        // Create a new Button
        Button dynamicButton = new Button(this);
        dynamicButton.setText(prName); // Set button text
        dynamicButton.setTag(prID);
        dynamicButton.setTextColor(Color.WHITE);
        dynamicButton.setTypeface(null, Typeface.BOLD);
        // Generate a random color
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        dynamicButton.setBackgroundColor(color);

        // Optional: Set layout parameters for the button
        //LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(120,120);
        layoutParams.setMargins(10, 0, 0, 0); // Add some margin if desired
        dynamicButton.setLayoutParams(layoutParams);

        // Add the button to the LinearLayout


        // Optional: Set an OnClickListener for the dynamic button
        dynamicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event here
                // For example, change its color again or show a Toast
                String prid = v.getTag().toString();
                Product pr  = CommonUtil.productsFull.stream().filter(c->c.getProductID().equals(prid)).findFirst().get();
                if(pr!=null){
                    AddItemToCart(pr);
                    LoadPRView(pr);
                }
            }
        });
        return  dynamicButton;
    }
    private void BuildCatButtons(String cat) {
        // Create a new Button
        Button dynamicButton = new Button(this);
        dynamicButton.setText(cat); // Set button text
        dynamicButton.setTag(cat);
        dynamicButton.setTextColor(Color.WHITE);
        dynamicButton.setTypeface(null, Typeface.BOLD);
        // Generate a random color
        Random random = new Random();
        int color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        dynamicButton.setBackgroundColor(color);

        // Optional: Set layout parameters for the button
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 10, 0, 0); // Add some margin if desired
        dynamicButton.setLayoutParams(layoutParams);

        // Add the button to the LinearLayout
        categoryView.addView(dynamicButton);

        // Optional: Set an OnClickListener for the dynamic button
        dynamicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click event here
                // For example, change its color again or show a Toast
                String catName = v.getTag().toString();
                LoadProductsMenu(catName);
            }
        });
    }


    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(SaleActivity.this);
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private int AddItemToCart(Product pr){
        int latestQty = 0;
        Product alreadyExits = null;
        for (Product p:productCart) {
            if(p.getProductID()==pr.getProductID()){
                alreadyExits = p;
                break;
            }
        }
        if(alreadyExits!=null){
            Integer qty = alreadyExits.getQty();
            qty+=1;
            pr.setQty(qty);
            productCart.remove(alreadyExits);
        }
        productCart.add(pr);
        Double billAmt = productCart.size()>0 ? productCart.stream().mapToDouble(c->c.getAmount()).sum() :0d;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        totalAmt.setText(formatter.format(billAmt).replace(symbol,symbol+" "));
        latestQty = pr.getQty();
        return  latestQty;
    }

    private int RemoveItemToCart(Product pr){
        int latestQty = 0;
        Product alreadyExits = null;
        for (Product p:productCart) {
            if(p.getProductID()==pr.getProductID()){
                alreadyExits = p;
                break;
            }
        }
        if(alreadyExits!=null){
            Integer qty = alreadyExits.getQty();
            if(qty==1){
                productCart.remove(alreadyExits);
                pr.setQty(0);
            }
            else {
                qty-=1;
                pr.setQty(qty);
                productCart.remove(alreadyExits);
                productCart.add(pr);
            }
        }
        Double billAmt = productCart.size()>0 ? productCart.stream().mapToDouble(c->c.getAmount()).sum() :0d;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        totalAmt.setText(formatter.format(billAmt).replace(symbol,symbol+" "));
        latestQty = pr.getQty();
        return latestQty;
    }

    private void LoadPRView(Product pr){
        View view = getLayoutInflater().inflate(R.layout.items,null);
        TextView prName = view.findViewById(R.id.saleItemName);
        TextView prPrice = view.findViewById(R.id.saleItemRate);
        TextView prQty = view.findViewById(R.id.saleItemQty);
        prName.setText(pr.getProductName());
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        prPrice.setText(formatter.format(pr.getPrice()).replace(symbol,symbol+" "));
        Integer qty = pr.getQty()!=null ? pr.getQty():0;
        prQty.setText(String.valueOf(qty));
        ImageButton btnRemove = view.findViewById(R.id.btnRemoveItem);
        ImageButton btnAdd = view.findViewById(R.id.btnAddItem);
        btnAdd.setTag(pr.getProductID());
        btnRemove.setTag(pr.getProductID());
        if(qty>0){
            btnRemove.setVisibility(View.VISIBLE);
        }
        else {
            btnRemove.setVisibility(View.INVISIBLE);
        }
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prid = (String) v.getTag();
                Product pn = CommonUtil.productsFull.stream().filter(c->c.getProductID().equals(prid)).findFirst().get();
                if(pn!=null){
                    if(pn.getQty()==null){
                        pn.setQty(1);
                    }
                    int qty = RemoveItemToCart(pn);
                    prQty.setText(String.valueOf(qty));
                    btnRemove.setVisibility(qty>0 ? View.VISIBLE:View.INVISIBLE);
                }
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prid = (String) v.getTag();
                Product pn = CommonUtil.productsFull.stream().filter(c->c.getProductID().equals(prid)).findFirst().get();
                if(pn!=null){
                    if(pn.getQty()==null || pn.getQty()==0){
                        pn.setQty(1);
                    }
                    int qty = AddItemToCart(pn);
                    prQty.setText(String.valueOf(qty));
                    btnRemove.setVisibility(qty>0 ? View.VISIBLE:View.INVISIBLE);
                }
            }
        });
        prView.addView(view);
    }
    @Override
    public void onBackPressed(){
        Intent rptPage = new Intent(this,SalesReportActivity.class);
        rptPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(rptPage);
        super.onBackPressed();
    }
    public static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnViewCart:
                if(productCart.size()>0){
                    Intent cartPage = new Intent(this,CartViewActivity.class);
                    cartPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    cartPage.putExtra("CART", (Serializable) productCart);
                    startActivity(cartPage);
                }
                else {
                    showCustomDialog("Warning","Please add products into cart.");
                }
                break;
        }
    }
}