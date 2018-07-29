package hub.com.stc.stc;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText hubCode;
    private EditText password;
    private Button btnLogin;
    private Dialog progressBar;
    ArrayList hubList;
    TelephonyManager telephonyManager;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String PostgreSQLHost = "PostgreSQLHost";
    public static final String PostgreSQLUserName = "PostgreSQLUserName";
    public static final String PostgreSQLPassword = "PostgreSQLPassword";
    public static final String PostgreSQLDB = "PostgreSQLDB";
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static String DB_URL = "jdbc:postgresql://192.168.43.107:5432/HUB";

    //  Database credentials
    static String USER = "postgres";
    static String PASS = "1@Vinothkmr";
    static String IMEI = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.stcourrierlogo);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        try {
            telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                showCustomDialog("Warning","Enable Permission to Check IMEI Number");
                ActivityCompat.requestPermissions(LoginActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        1);

            }
            else {
                IMEI = telephonyManager.getDeviceId();
            }
            if(IMEI.isEmpty()){
                showCustomDialog("Error","Permission issue to Read IMEI Details.");
                return;
            }
            hubCode = (EditText) findViewById(R.id.username);
            password = (EditText)findViewById(R.id.password);
            btnLogin = (Button) findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(this);
            progressBar = new Dialog(LoginActivity.this);
            progressBar.setContentView(R.layout.custom_progress_dialog);
            progressBar.setTitle("Loading");
            hubList = new ArrayList();
            sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
            //sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String hostname = sharedpreferences.getString(PostgreSQLHost, "");
            String SQLUser = sharedpreferences.getString(PostgreSQLUserName, "");
            String SQLPassword = sharedpreferences.getString(PostgreSQLPassword, "");
            String Databasename = sharedpreferences.getString(PostgreSQLDB, "");
            if (getIntent().getBooleanExtra("EXIT", false)) {
                finish();
            }
            if (hostname.isEmpty() || SQLUser.isEmpty() || Databasename.isEmpty()) {
                StartSettingsActivity();
            } else {
                DB_URL = "jdbc:postgresql://" + hostname + ":5432/" + Databasename;
                USER = SQLUser;
                PASS = SQLPassword;
            }
        } catch (Exception ex) {
            showCustomDialog("Error", ex.getMessage());
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                  showCustomDialog("Warning","Permission denied to Read IMEI Details");
                    //  Toast.makeText(LoginActivity.this, "Permission denied to read your IMEI Number", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void StartSettingsActivity() {
        showCustomDialog("Warning", "Host / Username / Dbname should not be Empty");
        Intent st = new Intent(this, Settings.class);
        st.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(st);
    }

    @Override
    public void onClick(View v) {
        String hub = hubCode.getText().toString();
        if (hub.isEmpty()) {
            showCustomDialog("Warnings", "Please User Name.");
        } else {
            try {
                //PostgreSqlJDBC pr = new PostgreSqlJDBC();
                // new WCFCall().execute("0");
//ArrayList srList = webCall.hubs;
                new Login().execute("");

            } catch (Exception ex) {
                showCustomDialog("Error", ex.getMessage());
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                return true;
            case R.id.logout:
                CommonUtil.LoggedInHUB = "";
                CommonUtil.isLoggedIn = false;
                Intent loginPage = new Intent(this, LoginActivity.class);
                loginPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginPage);
                return true;
            case R.id.Settings:
                Intent settingsPage = new Intent(this, Settings.class);
                settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
                return true;
            case R.id.homemenu:
                if (CommonUtil.isLoggedIn) {
                    Intent page = new Intent(this, MainActivity.class);
                    page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(page);
                } else {
                    Intent loginage = new Intent(this, LoginActivity.class);
                    loginage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginage);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void StartMainActivity() {
        Intent mass = new Intent(this, MainActivity.class);
        mass.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mass);
    }

    class CheckIMEI extends AsyncTask<String, Void, String> {

        //  ArrayList hubList = new ArrayList();

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        public void onPostExecute(String result) {
            progressBar.cancel();
            if (result.startsWith("ERROR")) {
                showCustomDialog("Error", result);
            } else {
                StartMainActivity();
            }

        }


        @Override
        protected String doInBackground(String... strings) {
            String msg = "";
            Connection conn = null;
            Statement st = null;
            try {
                //STEP 2: Register JDBC driver
                Class.forName(JDBC_DRIVER);

                //STEP 3: Open a connection
                System.out.println("Connecting to database...");
                if (PASS.isEmpty()) {
                    conn = DriverManager.getConnection(DB_URL, USER, null);
                } else {
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                }
                String currentDeviceIMEI = strings[0];

                //STEP 4: Execute a query
                System.out.println("Creating statement...");
                st = conn.createStatement();
                String sql;
                sql = "SELECT * FROM config_device where DEVICE_IMEI='"+IMEI+"'";
                ResultSet rs = st.executeQuery(sql);
                boolean foundimei = false;
                //STEP 5: Extract data from result set
                while (rs.next()) {
                    String device_imei = rs.getString("DEVICE_IMEI");
                    boolean isactive = rs.getBoolean("IS_ACTIVE");
                    device_imei = device_imei.trim();
                    if (device_imei.equals(currentDeviceIMEI)) {
                        foundimei = true;
                        if (!isactive) {
                            msg = "ERROR: Your Device is Locked. Please Contact Administrators";
                            break;
                        }

                    }

                }
                if (!foundimei) {
                    msg = "ERROR: Your Device is not yet Activated. Please Contact Administrators";
                }
                //STEP 6: Clean-up environment
                rs.close();
                st.close();
                conn.close();
            } catch (Exception e) {
                //Handle errors for Class.forName
                //showCustomDialog("Exception",e.getMessage());
                e.printStackTrace();
                System.out.println(e.getMessage());
                msg = "ERROR:" + e.getMessage();
            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                    //showCustomDialog("Exception",se2.getMessage());
                    msg = "ERROR:" + se2.getMessage();
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    //showCustomDialog("Exception",se.getMessage());
                    msg = "ERROR:" + se.getMessage();
                }//end finally try
                return msg;
            }
        }
    }

    public void showCustomDialog(String title, String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n" + Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        //dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog, int whichButton) {
        //   //pass
        //}
        //});
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
    class DBConnection extends AsyncTask<String, Void, String> {

        //  ArrayList hubList = new ArrayList();

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        public void onPostExecute(String result) {
            progressBar.cancel();
            if (result.startsWith("ERROR")) {
               showCustomDialog("Error",result);
            } else {
                new CheckIMEI().execute(IMEI);
            }


        }


        @Override
        protected String doInBackground(String... strings) {
         String msg = "";
            Connection conn = null;
            Statement st = null;
            try {
                //STEP 2: Register JDBC driver
                Class.forName(JDBC_DRIVER);

                //STEP 3: Open a connection
                System.out.println("Connecting to database...");
                if(PASS.isEmpty()){
                    conn = DriverManager.getConnection(DB_URL,USER,null);
                }
                else {
                    conn = DriverManager.getConnection(DB_URL,USER,PASS);
                }


                //STEP 4: Execute a query
                System.out.println("Creating statement...");
                st = conn.createStatement();
                String sql;
                sql = "SELECT  hub_code,hub_name,hub_state FROM OPN_HUBS";
                ResultSet rs = st.executeQuery(sql);

                //STEP 5: Extract data from result set
                while (rs.next()) {
                    //Retrieve by column name
                    String hub = rs.getString("hub_code").trim();
                    String hubname = rs.getString("hub_name").trim();
                    String state = rs.getString("hub_state").trim();
                    Hubs h = new Hubs();
                    h.setState_code(state);
                    h.setHub_name(hubname);
                    h.setHub_code(hub);
                    CommonUtil.hubsArrayList.add(h);
                    hubList.add(hub);
                }
                //STEP 6: Clean-up environment
                rs.close();
                st.close();
                st = conn.createStatement();
                String querey = "SELECT * FROM config where CONFIG_NAME IN ('Hub','State') ORDER BY config_name";
                rs = st.executeQuery(querey);
                Hubs hub = null;
                while (rs.next()){
                    String value= rs.getString("config_value");
                    String code = rs.getString("config_name");
                    if(code.trim().equals("State")){
                        for (int i = 0; i < CommonUtil.hubsArrayList.size(); i++) {
                            Hubs h = CommonUtil.hubsArrayList.get(i);
                            if (h.getHub_code().trim().equals(CommonUtil.LoggedInHUB.trim())) {
                                hub = h;
                                break;
                            }
                        }
                        CommonUtil.selectedHub.setState_code(hub.getState_code());
                        CommonUtil.selectedHub.setHub_code(hub.getHub_code());
                        CommonUtil.selectedHub.setHub_name(hub.getHub_name());
                    }
                    else {
                        CommonUtil.LoggedInHUB = value;
                    }

                }
                rs.close();
                st.close();
                conn.close();
            }  catch (Exception e) {
                //Handle errors for Class.forName
               // showCustomDialog("Exception",e.getMessage());
                msg = "ERROR: "+e.getMessage();
                e.printStackTrace();
                System.out.println(e.getMessage());
            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                    msg = "ERROR: "+se2.getMessage();
                    //  showCustomDialog("Exception",se2.getMessage());
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    msg = "ERROR: "+se.getMessage();
                    //showCustomDialog("Exception",se.getMessage());
                }//end finally try
                return msg;
            }
        }
    }

    class Login extends AsyncTask<String, Void, String> {

        //  ArrayList hubList = new ArrayList();

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        public void onPostExecute(String result) {
            progressBar.cancel();
            if(result.startsWith("ERROR")){
                CommonUtil.isLoggedIn = false;
                showCustomDialog("Error",result);
            }
            else {
                CommonUtil.isLoggedIn = true;
                new DBConnection().execute("");
            }


        }


        @Override
        protected String doInBackground(String... strings) {
            String msg = "";
            Connection conn = null;
            Statement st = null;
            try {
                //STEP 2: Register JDBC driver
                Class.forName(JDBC_DRIVER);

                //STEP 3: Open a connection
                System.out.println("Connecting to database...");
                if(PASS.isEmpty()){
                    conn = DriverManager.getConnection(DB_URL,USER,null);
                }
                else {
                    conn = DriverManager.getConnection(DB_URL,USER,PASS);
                }


                //STEP 4: Execute a query
                System.out.println("Creating statement...");
                st = conn.createStatement();
                String usr_Input = hubCode.getText().toString();
                String pass_Input = password.getText().toString();
                String sql;
                sql = "SELECT * FROM sys_users where user_name ='"+usr_Input+"'";
                ResultSet rs = st.executeQuery(sql);

                //STEP 5: Extract data from result set
                boolean userFound = false;
                while (rs.next()) {
                    //Retrieve by column name
                   String userName = rs.getString("user_name");
                   String password_temp = rs.getString("user_pass");

                   if(userName.equals(usr_Input)){
                       userFound = true;
                       if(!pass_Input.equals(password_temp)){
                           msg = "ERROR: Invalid Password.";
                        }
                        else {
                           CommonUtil.UserName = userName;
                           CommonUtil.Password = password_temp;
                       }
                        break;
                   }
                }
                if(!userFound){
                    msg = "ERROR: User Not Found";
                }
                //STEP 6: Clean-up environment
                rs.close();
                st.close();
                conn.close();
            }  catch (Exception e) {
                //Handle errors for Class.forName
              //  showCustomDialog("Exception",e.getMessage());
                e.printStackTrace();
                System.out.println(e.getMessage());
                msg = "ERROR:"+e.getMessage();
            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                   // showCustomDialog("Exception",se2.getMessage());
                    msg = "ERROR:"+se2.getMessage();
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    msg = "ERROR:"+se.getMessage();
                    //showCustomDialog("Exception",se.getMessage());
                }//end finally try
                return msg;
            }
        }
    }
}
