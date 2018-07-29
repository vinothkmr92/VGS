package hub.com.stc.stc;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

public class TrackingActivity extends AppCompatActivity implements View.OnClickListener {

    EditText awbText ;
    WebView webConent;
    private Dialog progressBar;
    public static final String TRACKING_URL ="http://180.151.69.100/cgi_bin/show_status1.php?awb=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        awbText = (EditText)findViewById(R.id.awbTrack);
        webConent = (WebView)findViewById(R.id.webConent);
        progressBar = new Dialog(TrackingActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        webConent.getSettings().setLoadWithOverviewMode(true);
        webConent.getSettings().setUseWideViewPort(true);
        progressBar.setTitle("Loading");
        awbText.setOnKeyListener(new View.OnKeyListener() {


            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String awb = awbText.getText().toString();
                    String url = TRACKING_URL+awb;
                    progressBar.show();
                    webConent.loadUrl(url);
                    awbText.setFocusableInTouchMode(true);
                    awbText.setFocusable(true);
                    awbText.requestFocus();
                    return false;
                }
                return false;
            }
        });
        webConent.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view,String url){
                super.onPageFinished(view,url);
                progressBar.cancel();
                awbText.setText("");
            }
        });
    }

    @Override
    public void onClick(View v) {


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
}
