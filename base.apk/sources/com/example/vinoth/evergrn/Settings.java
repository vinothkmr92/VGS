package com.example.vinoth.evergrn;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends AppCompatActivity implements OnClickListener {
    public static final String BTNAME = "BTNAME";
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String SQLDB = "SQLDB";
    public static final String SQLPASSWORD = "SQLPASSWORD";
    public static final String SQLSERVER = "SQLSERVER";
    public static final String SQLUSERNAME = "SQLUSERNAME";
    EditText btname;
    ConnectionClass connectionClass;
    EditText dbname;
    EditText host;
    EditText password;
    private Dialog progressBar;
    private MySharedPreferences sharedpreferences;
    Button testConnection;
    EditText username;

    class CheckDBConnection extends AsyncTask<String, Void, String> {
        CheckDBConnection() {
        }

        public void onPreExecute() {
            super.onPreExecute();
            Settings.this.progressBar.show();
        }

        public void onPostExecute(String str) {
            Settings.this.progressBar.cancel();
            if (str.startsWith("ERROR")) {
                Settings.this.showCustomDialog("Error", str, false);
                return;
            }
            Settings.this.showCustomDialog("Message", str, false);
            Settings.this.SaveSettings();
        }

        protected java.lang.String doInBackground(java.lang.String... r7) {
            /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
            /*
            r6 = this;
            r0 = "";
            r1 = com.example.vinoth.evergrn.Settings.this;	 Catch:{ Exception -> 0x001e }
            r1 = r1.connectionClass;	 Catch:{ Exception -> 0x001e }
            r2 = 0;	 Catch:{ Exception -> 0x001e }
            r2 = r7[r2];	 Catch:{ Exception -> 0x001e }
            r3 = 1;	 Catch:{ Exception -> 0x001e }
            r3 = r7[r3];	 Catch:{ Exception -> 0x001e }
            r4 = 2;	 Catch:{ Exception -> 0x001e }
            r4 = r7[r4];	 Catch:{ Exception -> 0x001e }
            r5 = 3;	 Catch:{ Exception -> 0x001e }
            r7 = r7[r5];	 Catch:{ Exception -> 0x001e }
            r7 = r1.CONN(r2, r3, r4, r7);	 Catch:{ Exception -> 0x001e }
            if (r7 == 0) goto L_0x001b;	 Catch:{ Exception -> 0x001e }
        L_0x0018:
            r7 = "DB Connection Succesfull.";	 Catch:{ Exception -> 0x001e }
            goto L_0x001d;	 Catch:{ Exception -> 0x001e }
        L_0x001b:
            r7 = "ERROR: INVALID CONNECTION STRING.";	 Catch:{ Exception -> 0x001e }
        L_0x001d:
            return r7;
        L_0x001e:
            r7 = move-exception;
            r7.printStackTrace();	 Catch:{ all -> 0x0038 }
            r1 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0038 }
            r1.<init>();	 Catch:{ all -> 0x0038 }
            r2 = "ERROR: ";	 Catch:{ all -> 0x0038 }
            r1.append(r2);	 Catch:{ all -> 0x0038 }
            r7 = r7.getMessage();	 Catch:{ all -> 0x0038 }
            r1.append(r7);	 Catch:{ all -> 0x0038 }
            r7 = r1.toString();	 Catch:{ all -> 0x0038 }
            return r7;
        L_0x0038:
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.example.vinoth.evergrn.Settings.CheckDBConnection.doInBackground(java.lang.String[]):java.lang.String");
        }
    }

    protected void onCreate(Bundle bundle) {
        try {
            super.onCreate(bundle);
            setContentView((int) R.layout.activity_settings);
            this.host = (EditText) findViewById(R.id.host);
            this.username = (EditText) findViewById(R.id.username);
            this.password = (EditText) findViewById(R.id.password);
            this.dbname = (EditText) findViewById(R.id.dbname);
            this.testConnection = (Button) findViewById(R.id.testButton);
            this.btname = (EditText) findViewById(R.id.btname);
            this.progressBar = new Dialog(this);
            this.progressBar.setContentView(R.layout.custom_progress_dialog);
            this.progressBar.setTitle("Loading");
            this.sharedpreferences = MySharedPreferences.getInstance(this, "MyPrefs");
            bundle = this.sharedpreferences.getString("SQLSERVER", "");
            CharSequence string = this.sharedpreferences.getString("SQLUSERNAME", "");
            CharSequence string2 = this.sharedpreferences.getString("SQLPASSWORD", "");
            CharSequence string3 = this.sharedpreferences.getString("SQLDB", "");
            CharSequence string4 = this.sharedpreferences.getString("BTNAME", "");
            this.host.setText(bundle);
            this.username.setText(string);
            this.password.setText(string2);
            this.dbname.setText(string3);
            this.btname.setText(string4);
            this.connectionClass = new ConnectionClass();
            this.testConnection.setOnClickListener(this);
        } catch (Bundle bundle2) {
            showCustomDialog("Exception", bundle2.getMessage(), true);
        }
    }

    public void SaveSettings() {
        this.progressBar.show();
        String obj = this.host.getText().toString();
        String obj2 = this.username.getText().toString();
        String obj3 = this.password.getText().toString();
        String obj4 = this.dbname.getText().toString();
        String obj5 = this.btname.getText().toString();
        if (!(obj.isEmpty() || obj2.isEmpty())) {
            if (!obj4.isEmpty()) {
                this.sharedpreferences.putString("SQLSERVER", obj);
                this.sharedpreferences.putString("SQLUSERNAME", obj2);
                this.sharedpreferences.putString("SQLPASSWORD", obj3);
                this.sharedpreferences.putString("SQLDB", obj4);
                this.sharedpreferences.putString("BTNAME", obj5);
                this.sharedpreferences.commit();
                this.progressBar.cancel();
                showCustomDialog("Saved", "Settings Saved Sucessfully", true);
                return;
            }
        }
        this.progressBar.cancel();
        showCustomDialog("Warning", "Host / Username / Dbname should not be Empty.", false);
    }

    public void onClick(View view) {
        view = this.host.getText().toString();
        String obj = this.username.getText().toString();
        String obj2 = this.password.getText().toString();
        String obj3 = this.dbname.getText().toString();
        new CheckDBConnection().execute(new String[]{view, obj3, obj, obj2});
    }

    public void GoHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(67108864);
        startActivity(intent);
    }

    public void showCustomDialog(String str, String str2, final boolean z) {
        Builder builder = new Builder(this);
        builder.setView(getLayoutInflater().inflate(R.layout.custom_dialog, null));
        builder.setTitle(str);
        str = new StringBuilder();
        str.append("\n");
        str.append(str2);
        builder.setMessage(str.toString());
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (z != null) {
                    Settings.this.GoHome();
                }
            }
        });
        builder.create().show();
    }
}
