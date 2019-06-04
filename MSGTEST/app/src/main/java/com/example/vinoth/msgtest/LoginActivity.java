package com.example.vinoth.msgtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseUser currentuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void onStart(){
        super.onStart();
        if(currentuser != null){
            SendUserToMainActivity();
        }
    }

    public void SendUserToMainActivity(){
        Intent intend = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intend);
    }
}
