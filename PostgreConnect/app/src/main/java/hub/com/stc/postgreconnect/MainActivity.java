package hub.com.stc.postgreconnect;

import android.database.SQLException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnConnect;
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String DB_URL = "jdbc:postgresql://localhost:5432/HUB";

    //  Database credentials
    static final String USER = "postgres";
    static final String PASS = "1@Vinothkmr";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnConnect = (Button)findViewById(R.id.connect);
        btnConnect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
       // DBConnection connection = new DBConnection();
        try{
           new DBConnection().execute("");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    class DBConnection extends AsyncTask<String,Void,ArrayList> {

        ArrayList hubList = new ArrayList();

        @Override
        public void onPostExecute(ArrayList result) {
            for(int i=0;i<result.size();i++){
                System.out.println(result.get(i));
            }

        }


        @Override
        protected ArrayList doInBackground(String... strings) {
            Connection conn = null;
            Statement st = null;
            try {
                //STEP 2: Register JDBC driver
                Class.forName("org.postgresql.Driver");

                //STEP 3: Open a connection
                System.out.println("Connecting to database...");
                conn = DriverManager.getConnection("jdbc:postgresql://192.168.43.107:5432/HUB", "postgres", "1@Vinothkmr");

                //STEP 4: Execute a query
                System.out.println("Creating statement...");
                st = conn.createStatement();
                String sql;
                sql = "SELECT  hub_code FROM OPN_HUBS";
                ResultSet rs = st.executeQuery(sql);

                //STEP 5: Extract data from result set
                while (rs.next()) {
                    //Retrieve by column name
                    String hub = rs.getString("hub_code");
                    hubList.add(hub);
                }
                //STEP 6: Clean-up environment
                rs.close();
                st.close();
                conn.close();
            } catch (SQLException se) {
                //Handle errors for JDBC
                se.printStackTrace();
            } catch (Exception e) {
                //Handle errors for Class.forName
                e.printStackTrace();
            } finally {
                //finally block used to close resources
                try {
                    if (st != null)
                        st.close();
                } catch (Exception se2) {
                }// nothing we can do
                try {
                    if (conn != null)
                        conn.close();
                } catch (Exception se) {
                    se.printStackTrace();
                }//end finally try
                return hubList;
            }
        }
    }
}
