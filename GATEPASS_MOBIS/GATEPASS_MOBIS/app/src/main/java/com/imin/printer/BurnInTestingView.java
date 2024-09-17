package com.imin.printer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BurnInTestingView extends LinearLayout {
    public BurnInTestingView(Context context) {
        super(context);
        initView(context);
    }

    public Button confirmBtn;
    public Button cancelBtn;
    public Button pauseBtn;
    public EditText intervalEt;
    public EditText timesEt;
    public ProgressBar progressBar;
    public TextView progressTv;
    public TextView sumTv;

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.burn_in_testing, this, true);
        confirmBtn = findViewById(R.id.confirm_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        pauseBtn = findViewById(R.id.pause_btn);
        intervalEt = findViewById(R.id.interval_et);
        timesEt = findViewById(R.id.times_et);
        progressBar = findViewById(R.id.progress);
        progressTv = findViewById(R.id.progress_tv);
        sumTv = findViewById(R.id.sum_tv);
    }
}
