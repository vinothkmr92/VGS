package hub.com.stc.stc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Settings extends AppCompatActivity implements View.OnClickListener{

    EditText host;
    EditText username;
    EditText password;
    EditText defaultWeight;
    EditText dbname;
    EditText dbname_fen;
    Button testConnection;
    private MySharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String PostgreSQLHost = "PostgreSQLHost";
    public static final String PostgreSQLUserName= "PostgreSQLUserName";
    public static final String PostgreSQLPassword = "PostgreSQLPassword";
    public static final String DefaultWeight = "DefaultWeight";
    public static final String PostgreSQLDB = "PostgreSQLDB";
    public static final String PostgreSQLDB_FEN = "PostgreSQLDB_FEN";
    private Dialog progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        host = (EditText) findViewById(R.id.host);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        defaultWeight = (EditText)findViewById(R.id.defaultweight);
        dbname= (EditText)findViewById(R.id.dbname);
        dbname_fen = (EditText)findViewById(R.id.dbname_fen);
        testConnection = (Button)findViewById(R.id.testButton);
        progressBar = new Dialog(Settings.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        sharedpreferences = MySharedPreferences.getInstance(this,MyPREFERENCES);
       // sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String hostname = sharedpreferences.getString(PostgreSQLHost,"");
        String SQLUser = sharedpreferences.getString(PostgreSQLUserName,"");
        String SQLPassword = sharedpreferences.getString(PostgreSQLPassword,"");
        String defWeight = sharedpreferences.getString(DefaultWeight,"0.250");
        String Databasename = sharedpreferences.getString(PostgreSQLDB,"");
        String fendb = sharedpreferences.getString(PostgreSQLDB_FEN,"");
        host.setText(hostname);
        username.setText(SQLUser);
        password.setText(SQLPassword);
        defaultWeight.setText(defWeight);
        dbname.setText(Databasename);
        dbname_fen.setText(fendb);
        testConnection.setOnClickListener(this);
    }
    public  void  SaveSettings(){
        progressBar.show();
        String hostname = host.getText().toString();
        String SQLUser = username.getText().toString();
        String SQLPassword = password.getText().toString();
        String defWeight = defaultWeight.getText().toString();
        String Databasename = dbname.getText().toString();
        String dbfen = dbname_fen.getText().toString();
        if(hostname.isEmpty() || SQLUser.isEmpty() || Databasename.isEmpty() || dbfen.isEmpty()){
            progressBar.cancel();
            showCustomDialog("Warning","Host / Username / Dbname should not be Empty.",false);

        }
        else{
            if(defWeight.isEmpty()){
                showCustomDialog("Warning","Default Weight will be taken as 0.250. Kindly Enter value if you want to Change",false);
                defWeight = "0.250";
            }
           // SharedPreferences.Editor editor = sharedpreferences.edit();
            sharedpreferences.putString(PostgreSQLHost,hostname);
            sharedpreferences.putString(PostgreSQLUserName,SQLUser);
            sharedpreferences.putString(PostgreSQLPassword,SQLPassword);
            sharedpreferences.putString(DefaultWeight,defWeight);
            sharedpreferences.putString(PostgreSQLDB,Databasename);
            sharedpreferences.putString(PostgreSQLDB_FEN,dbfen);
            sharedpreferences.commit();
            progressBar.cancel();
            showCustomDialog("Saved","Settings Saved Sucessfully",true);


        }
    }
    @Override
    public void onClick(View v) {

                String hostname = host.getText().toString();
                String SQLUser = username.getText().toString();
                String SQLPassword = password.getText().toString();
                String Databasename = dbname.getText().toString();
                new CheckDBConnection().execute(hostname,Databasename,SQLUser,SQLPassword);



    }
    public  void ClosingAlert(){
        if(CommonUtil.isLoggedIn){
            Intent mass = new Intent(this,MainActivity.class);
            mass.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mass);
        }
        else {
            Intent mass = new Intent(this,LoginActivity.class);
            mass.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mass);
        }
    }
    class CheckDBConnection extends AsyncTask<String,Void,String> {

        //  ArrayList hubList = new ArrayList();

        @Override
        public void  onPreExecute(){
            super.onPreExecute();
            progressBar.show();
        }

        @Override
        public void onPostExecute(String result) {
            progressBar.cancel();
            if(!result.startsWith("ERROR")){
                showCustomDialog("Message",result,false);
                SaveSettings();
            }
            else {
                showCustomDialog("Error",result,false);
            }
        }


        @Override
        protected String doInBackground(String... strings) {
            String status = "";
            Connection conn = null;
            Statement st = null;
            try {
                String hostname = strings[0];
                String Databasename = strings[1];
                String SQLUser = strings[2];
                String SQLPassword = strings[3];
                String DB_URL = "jdbc:postgresql://"+hostname+":5432/"+Databasename;
                String USER = SQLUser;
                String PASS = SQLPassword;
                String JDBC_DRIVER = "org.postgresql.Driver";
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
               // String statecode = strings[0];
                st = conn.createStatement();
                String sql;
                sql = "SELECT * FROM OPN_HUBS";
                ResultSet rs = st.executeQuery(sql);

                //STEP 5: Extract data from result set
                while (rs.next()) {
                   status = "Sucessfully Connected to PostgreSQL";
                }
                //STEP 6: Clean-up environment
                rs.close();
                st.close();
                conn.close();
            }  catch (Exception e) {
                //Handle errors for Class.forName
                //showCustomDialog("Exception",e.getMessage());
                e.printStackTrace();
                status = "ERROR: "+e.getMessage();
            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                    //showCustomDialog("Exception",se2.getMessage());
                    status = "ERROR: "+se2.getMessage();
                }// nothing we can do

                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                    //showCustomDialog("Exception",se.getMessage());
                    status = "ERROR: "+se.getMessage();
                }//end finally try
                return status;
            }
        }
    }
    public void showCustomDialog(String title, String Message, final boolean wait) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        // final EditText edt = (EditText) dialogView.findViewById(R.id.dialog_info);

        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(wait){
                    ClosingAlert();//do something with edt.getText().toString();
                }

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
}
