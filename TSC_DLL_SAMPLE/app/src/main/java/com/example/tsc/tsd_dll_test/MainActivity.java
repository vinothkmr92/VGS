package com.example.tsc.tsd_dll_test;

import android.content.res.AssetManager;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tscdll.TSCActivity;
import com.example.tscdll.TscWifiActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    TSCActivity TscDll = new TSCActivity();//BT
    //TscWifiActivity TscDll = new TscWifiActivity();//NET

    private Button send;
    private EditText etText1;
    private EditText etText2;

    private static String PDFName = "test_2inch.pdf";
    private static File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/" + PDFName);


    private static int paper_width = 50;
    private static int paper_height = 100;
    private static int speed = 4;
    private static int density = 15;
    private static int sensor = 0;
    private static int sensor_distance = 0;
    private static int sensor_offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = (Button) findViewById(R.id.button1);
        etText1 = (EditText) findViewById(R.id.etText);

        send.setOnClickListener(new OnClickListener() {
            public void onClick(View v)
            {

                try {
                    TscDll.openport(etText1.getText().toString()); //BT
                    //TscDll.openport(etText1.getText().toString(), 9100); //NET

                    //TscDll.setup(paper_width, paper_height, speed, density, sensor, sensor_distance, sensor_offset);
                    TscDll.sendcommand("SIZE 75 mm, 50 mm\r\n");
                    //TscDll.sendcommand("GAP 2 mm, 0 mm\r\n");//Gap media
                    //TscDll.sendcommand("BLINE 2 mm, 0 mm\r\n");//blackmark media

                    TscDll.sendcommand("SPEED 4\r\n");
                    TscDll.sendcommand("DENSITY 12\r\n");
                    TscDll.sendcommand("CODEPAGE UTF-8\r\n");
                    TscDll.sendcommand("SET TEAR ON\r\n");
                    TscDll.sendcommand("SET COUNTER @1 1\r\n");
                    TscDll.sendcommand("@1 = \"0001\"\r\n");

                    TscDll.clearbuffer();
                    TscDll.sendcommand("TEXT 100,300,\"ROMAN.TTF\",0,12,12,@1\r\n");
                    TscDll.sendcommand("TEXT 100,400,\"ROMAN.TTF\",0,12,12,\"TEST FONT\"\r\n");
                    TscDll.barcode(100, 100, "128", 100, 1, 0, 3, 3, "123456789");
                    TscDll.printerfont(100, 250, "3", 0, 1, 1, "987654321");
                    TscDll.printlabel(2, 1);

                    TscDll.closeport(5000);

                }
                catch (Exception ex)
                {
                }

            }

        });
    }

}
