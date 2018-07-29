package hub.com.stc.stc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnHubUser;
    Button tracking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnHubUser = (Button)findViewById(R.id.hubUserButton);
        tracking = (Button)findViewById(R.id.trackingButton);
btnHubUser.setOnClickListener(this);
tracking.setOnClickListener(this);
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
                Intent loginPage = new Intent(this,LoginActivity.class);
                loginPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginPage);
                return true;
            case R.id.Settings:
                Intent settingsPage = new Intent(this,Settings.class);
                settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
                return true;
            case R.id.homemenu:
                if(CommonUtil.isLoggedIn){
                    Intent page = new Intent(this,MainActivity.class);
                    page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(page);
                }
                else {
                    Intent loginage = new Intent(this,LoginActivity.class);
                    loginage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginage);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hubUserButton:
                Intent mass = new Intent(this,HubUserActivity.class);
                mass.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mass);
                break;
            case R.id.trackingButton:
                Intent trackPage = new Intent(this,TrackingActivity.class);
                trackPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(trackPage);
        }
    }
}
