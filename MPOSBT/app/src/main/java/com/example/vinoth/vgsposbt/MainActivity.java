package com.example.vinoth.vgsposbt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String EXPIRE_DT = "EXPIRE_DT";
    public static MainActivity instance;
    String android_id;
    Spinner zoneSpinner;
    Spinner wardSpinner;
    ImageButton loginBtn;
    DatabaseHelper dbHelper;
    private MySharedPreferences sharedpreferences;

    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            dbHelper = new DatabaseHelper(this);
            sharedpreferences = MySharedPreferences.getInstance(this, MyPREFERENCES);
            zoneSpinner = findViewById(R.id.zoneNo);
            wardSpinner = findViewById(R.id.wardNo);
            loginBtn = findViewById(R.id.loginbtn);
            instance = this;
            Date dt = new Date();
            Date yesterday = getYesterday();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String expiredtstr = sharedpreferences.getString(EXPIRE_DT, simpleDateFormat.format(yesterday));
            Date expireDt = simpleDateFormat.parse(expiredtstr);
            Date compare = new Date(dt.getYear(), dt.getMonth(), dt.getDate());
            Common.isActivated = expireDt.compareTo(compare) >= 0;
            Common.expireDate = expireDt;
            android_id = android.provider.Settings.Secure.getString(MainActivity.this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            if (!Common.isActivated) {
                Common.isActivated = false;
                AppActivation appActivation = new AppActivation(MainActivity.this, android_id, this);
                appActivation.CheckActivationStatus();
            }
            ArrayList<String> zoneslist = dbHelper.GetZones();
            ArrayAdapter scadapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,zoneslist);
            scadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            zoneSpinner.setAdapter(scadapter);
            ArrayList<String> wards = dbHelper.GetWards();
            ArrayAdapter wardAdaptor = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,wards);
            wardAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            wardSpinner.setAdapter(wardAdaptor);
            loginBtn.setOnClickListener(this);
        }
        catch (Exception ex){
            showCustomDialog("Error", ex.getMessage());
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void ValidateActivationResponse(String response) {
        if (!Common.isActivated) {
            showCustomDialog("Msg", "Your Android device " + android_id + " is not activated\n" + response, true, true);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainmenu, menu);//Menu Resource, Menu
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            case R.id.uploadExcel:
                Intent dcpage = new Intent(this, UploadActivity.class);
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private Date getYesterday() {
        return new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
    }
    public void showCustomDialog(String title, String Message, boolean... closeapp) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        TextView titleview = new TextView(this);
        titleview.setText(title);
        titleview.setBackgroundColor(Color.WHITE);
        titleview.setPadding(10, 10, 10, 10);
        titleview.setGravity(Gravity.CENTER);
        titleview.setTextColor(Color.BLACK);
        titleview.setTypeface(titleview.getTypeface(), Typeface.BOLD_ITALIC);
        titleview.setTextSize(20);
        dialogBuilder.setCustomTitle(titleview);
        dialogBuilder.setMessage("\n" + Message);
        if (closeapp.length > 1 && closeapp[1]) {
            dialogBuilder.setNeutralButton("Share Device ID", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    String shareBody = android_id;
                    String shareSub = "Share Device ID";
                    whatsappIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    whatsappIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    try {
                        startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(MainActivity.this, "Whatsapp have not been installed.", Toast.LENGTH_LONG);
                    } finally {
                        finish();
                        System.exit(0);
                    }
                }
            });
        }
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (closeapp.length > 0 && closeapp[0]) {
                    finish();
                    System.exit(0);
                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.loginbtn:
                String zoneSelected = zoneSpinner.getSelectedItem().toString();
                String wardSelected = wardSpinner.getSelectedItem().toString();
                if(zoneSelected.isEmpty() || wardSelected.isEmpty()){
                    showCustomDialog("Warning","Please select valid Zone/Ward Details.");
                }
                else {
                    Common.zoneSelected = zoneSelected;
                    Common.wardSelected = wardSelected;
                    Intent homePage = new Intent(this, HomeActivity.class);
                    homePage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homePage);
                }
        }
    }
}