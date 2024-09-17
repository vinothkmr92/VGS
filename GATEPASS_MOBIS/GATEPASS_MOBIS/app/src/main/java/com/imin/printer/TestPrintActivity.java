
package com.imin.printer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.imin.library.SystemPropManager;
import com.imin.printer.js.JsActivity;
import com.imin.printerlib.IminPrintUtils;
import com.imin.printerlib.interfaces.PrintResultCallback;
import com.imin.printerlib.interfaces.PrinterResultCallback;
import com.imin.printerlib.util.BmpUtils;
import com.imin.printerlib.util.BytesUtil;
import com.imin.printerlib.util.CodeFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.imin.printerlib.util.Utils.isDoubleQRDev;
import static java.lang.Thread.sleep;


public class TestPrintActivity extends AppCompatActivity {

    private RecyclerView rvView;
    private List<TestBean> data;
    private EditText edit_bar_width, edit_bar_height, edit_bar_position, edit_qr_size, edit_qr_left, edit_qr_error_lev;
    private int barWidth, barHeight, barTextPos, qrCodeSize, qrCodeErrorLev, barAndQrLeftSize;
    private static final String TAG = "TestPrintActivity";
    public static IminPrintUtils mIminPrintUtils;
    private int orientation;
    private GridLayoutManager settingLayoutManager;
    private BluetoothStateReceiver mBluetoothStateReceiver;
    private Spinner spin_one;
    private MyListView mLvPairedDevices;
    private int bluetoothPosition = -1;
    private DeviceListAdapter mAdapter;
    private int connectType;
    private Button jsPrintBtn;
    private List<String> connectTypeList;

    private int spinnerPosition;
    private TextView tv_tips;
    public static final int TOAST = 100;
    private EditText edit_tqr1_error_lev;
    private EditText edit_tqr1_left;
    private EditText edit_tqr2_error_lev;
    private EditText edit_tqr2_left;
    private EditText edit_tqr1_version;
    private EditText edit_tqr2_version;
    private EditText edit_tqr_size;
    private EditText edit_tqr_str1;
    private EditText edit_tqr_str2;
    private byte[] content;
    private ImageView ivImage;
    private Button changePrint;
    private TextView cutters;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_print);
        checkStorageManagerPermission();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 0);
        }
        edit_bar_width = findViewById(R.id.edit_bar_width);
        edit_bar_height = findViewById(R.id.edit_bar_height);
        edit_bar_position = findViewById(R.id.edit_bar_position);
        edit_qr_size = findViewById(R.id.edit_qr_size);
        edit_qr_left = findViewById(R.id.edit_qr_left);
        edit_qr_error_lev = findViewById(R.id.edit_qr_error_lev);
        jsPrintBtn = findViewById(R.id.js_print);
        tv_tips = findViewById(R.id.tv_tips);
        rvView = findViewById(R.id.rv_list);
        spin_one = findViewById(R.id.spin_one);
        edit_tqr_size = findViewById(R.id.edit_tqr_size);
        edit_tqr1_error_lev = findViewById(R.id.edit_tqr1_error_lev);
        edit_tqr1_left = findViewById(R.id.edit_tqr1_left);
        edit_tqr2_error_lev = findViewById(R.id.edit_tqr2_error_lev);
        edit_tqr2_left = findViewById(R.id.edit_tqr2_left);
        edit_tqr1_version = findViewById(R.id.edit_tqr1_version);
        edit_tqr2_version = findViewById(R.id.edit_tqr2_version);
        edit_tqr_str1 = findViewById(R.id.edit_tqr_str1);
        edit_tqr_str2 = findViewById(R.id.edit_tqr_str2);
        ivImage = findViewById(R.id.ivImage);
        changePrint = findViewById(R.id.changePrint);
        cutters = findViewById(R.id.cutters);
        initView();
        initReceiver();
        if (mIminPrintUtils == null) {
            mIminPrintUtils = IminPrintUtils.getInstance(TestPrintActivity.this);

        }
//        mIminPrintUtils.resetDevice();
    }

    private void checkStorageManagerPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillAdapter();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        if (Utils.getWidth(this) <= 1460) {
            dm.density = 1.8f;
            dm.scaledDensity = 1.8f;
            dm.densityDpi = 200;
        } else {
            dm.density = 2.0f;
            dm.scaledDensity = 2.0f;
            dm.densityDpi = 220;
        }

    }


    @Override
    protected void onDestroy() {
        if (mBluetoothStateReceiver != null) {
            unregisterReceiver(mBluetoothStateReceiver);
            mBluetoothStateReceiver = null;
        }
        IminPrintUtils.getInstance(this).disConnectDevices();
        super.onDestroy();

    }

    Thread thread;


    // boolean isInit = false;
    boolean isIminPrint = true;//切换USB打印机 true imin  false 默认列表第一位

    private void initView() {
        final String deviceModel = SystemPropManager.getModel();
        Log.i("xgh", "deviceModel:" + deviceModel);
        connectTypeList = new ArrayList<>();
        if (TextUtils.equals("M2-202", deviceModel)
                || TextUtils.equals("M2-203", deviceModel)
                || TextUtils.equals("M2-Pro", deviceModel)
                || TextUtils.equals("Z2Pro", deviceModel)
        ) {
            connectTypeList.add("SPI");
            connectTypeList.add("Bluetooth");
            changePrint.setVisibility(View.GONE);
        } else if (TextUtils.equals("S1-701", deviceModel) || TextUtils.equals("S1-702", deviceModel)) {
            connectTypeList.add("USB");
            connectTypeList.add("Bluetooth");
        } else if (TextUtils.equals("D1p-601", deviceModel) || TextUtils.equals("D1p-602", deviceModel)
                || TextUtils.equals("D1p-603", deviceModel) || TextUtils.equals("D1p-604", deviceModel)
                || TextUtils.equals("D1w-701", deviceModel) || TextUtils.equals("D1w-702", deviceModel)
                || TextUtils.equals("D1w-703", deviceModel) || TextUtils.equals("D1w-704", deviceModel)
                || TextUtils.equals("D4-501", deviceModel) || TextUtils.equals("D4-502", deviceModel)
                || TextUtils.equals("D4-503", deviceModel) || TextUtils.equals("D4-504", deviceModel)
                || deviceModel.startsWith("D4-505") || TextUtils.equals("M2-Max", deviceModel)
                || TextUtils.equals("D1", deviceModel) || TextUtils.equals("D1-Pro", deviceModel)
                || deviceModel.startsWith("Swift") || TextUtils.equals("I22T01", deviceModel)
                || TextUtils.equals("TF1-11", deviceModel) || TextUtils.equals("D3-510", deviceModel) || Build.MODEL.equals("W27_Pro")) {
            connectTypeList.add("USB");
            connectTypeList.add("Bluetooth");
            if (TextUtils.equals("Swift 1", deviceModel)) {
                changePrint.setVisibility(View.GONE);
            }
        } else {
            tv_tips.setVisibility(View.VISIBLE);
            rvView.setVisibility(View.GONE);
            tv_tips.setText("暂不支持当前设备");
            changePrint.setVisibility(View.GONE);
            tv_tips.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: ");

                }
            });
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, connectTypeList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_one.setAdapter(spinnerAdapter);

        mLvPairedDevices = findViewById(R.id.lv_paired_devices);
        mAdapter = new DeviceListAdapter(this);
        mLvPairedDevices.setAdapter(mAdapter);
        mLvPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothPosition = position;
                mAdapter.notifyDataSetChanged();
            }
        });
        jsPrintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), JsActivity.class);
                startActivity(intent);
            }
        });

        changePrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isIminPrint == true) {
                    isIminPrint = false;
                    changePrint.setText("Switch printers: other");
                    mIminPrintUtils.setInitIminPrinter(false);
                } else {
                    isIminPrint = true;
                    changePrint.setText("Switch printers: imin");
                    mIminPrintUtils.setInitIminPrinter(true);
                }
            }
        });

        spin_one.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (R.id.spin_one == parent.getId()) {
                    spinnerPosition = position;

                    if (TextUtils.equals("USB", connectTypeList.get(position))) {
                        connectType = 1;
                        mLvPairedDevices.setVisibility(View.GONE);
                    } else if (TextUtils.equals("Bluetooth", connectTypeList.get(position))) {
                        connectType = 2;
                        if (BluetoothUtil.isBluetoothOn()) {
                            fillAdapter();
                        } else {
                            BluetoothUtil.openBluetooth(TestPrintActivity.this);
                        }
                    } else if (TextUtils.equals("SPI", connectTypeList.get(position))) {
                        connectType = 3;
                        Log.e("testspi", "当前打印机是否是SPI=====>" + com.imin.printerlib.util.Utils.getSystemPropertiesBoolean("persist.sys.isSPI"));
                        mLvPairedDevices.setVisibility(View.GONE);

                    }

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        if (data == null) {
            data = new ArrayList<>();
            data.add(new TestBean(0, "init printer"));
            data.add(new TestBean(-1, "reset device"));
            data.add(new TestBean(1, "get printer status"));
            data.add(new TestBean(2, "feed paper"));
            data.add(new TestBean(3, "cut paper"));
            data.add(new TestBean(4, "print text"));

            data.add(new TestBean(5, "print a column of the table"));
            data.add(new TestBean(6, "print single image"));
            data.add(new TestBean(7, "print multiple images"));

            data.add(new TestBean(8, "print barcode"));
            data.add(new TestBean(9, "set barcode width"));
            data.add(new TestBean(10, "set barcode height"));
            data.add(new TestBean(11, "set barcode text position"));


            data.add(new TestBean(12, "print QR code"));
            data.add(new TestBean(13, "set QR code size"));
            data.add(new TestBean(14, "set QrCode error correction Lev"));
            data.add(new TestBean(15, "set barcode and QR coed left margin"));
            data.add(new TestBean(16, "start print service"));
            data.add(new TestBean(17, "print 128A"));
            data.add(new TestBean(18, "print 128B"));
            data.add(new TestBean(19, "print 128C"));
            data.add(new TestBean(20, "print BarCode 19"));//21

            data.add(new TestBean(21, "print AntiWhite Text"));//25   22
            data.add(new TestBean(22, "set Text Typeface"));//26   23
            data.add(new TestBean(23, getResources().getString(R.string.setting_fonts)));//24设置字库
            //剩下D4,S1,Swift1 不支持双二维码
            if (!deviceModel.startsWith("S1") && !deviceModel.startsWith("Swift1")
                    && !deviceModel.startsWith("D4") && !deviceModel.startsWith("D1w")){
            data.add(new TestBean(24, "print Double QR"));//27 打印双二维码   25
            }

            if (TextUtils.equals("I22T01", deviceModel) || TextUtils.equals("TF1-11", deviceModel)) {
                data.add(new TestBean(25, "Number of cutters"));//27 获取切刀次数  26
                data.add(new TestBean(26, "Get paper distance"));//28  获取走纸距离  27
                data.add(new TestBean(27, "Serial Number"));//27  获取打印机序列号  27

                cutters.setVisibility(View.VISIBLE);
                cutters.setText("");
            }
            data.add(new TestBean(28, "print Example"));//28  获取打印机序列号  28
            data.add(new TestBean(29, "enter Trans Printer"));//  开始打印事务  29
            data.add(new TestBean(30, "commit Trans Printer"));//  提交打印事务  30
            data.add(new TestBean(31, "exit Trans Printer"));//  退出打印事务  31
            data.add(new TestBean(32, "Open Log"));//32  获取打印机序列号  32
            data.add(new TestBean(33, "print Trans"));//33  事务打印小票例子

        }


        orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            settingLayoutManager = new GridLayoutManager(this, 6, GridLayoutManager.VERTICAL, false);
        } else {
            settingLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);

        }
        rvView.setHasFixedSize(true);
        rvView.setNestedScrollingEnabled(false);
        rvView.setLayoutManager(settingLayoutManager);
        ButtonAdapter adapter = new ButtonAdapter(data, this);

        adapter.setOnClickListener(new OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view, final int pos, Object o) {

                try {
                    Log.i("XGH", "position:111   " + pos);
                    switch (pos) {
                        case -1:
                            mIminPrintUtils.resetDevice();
                            break;
                        case 0:
                            //   isInit = true;
                            Log.i("xgh", " :" + spinnerPosition + " :" + connectTypeList.get(spinnerPosition));
                            if (TextUtils.equals("USB", connectTypeList.get(spinnerPosition))) {
                                mIminPrintUtils.initPrinter(IminPrintUtils.PrintConnectType.USB);

                            } else if (TextUtils.equals("Bluetooth", connectTypeList.get(spinnerPosition))) {
                                Log.i("XGH", "bluetoothPosition:" + bluetoothPosition);
                                if (bluetoothPosition >= 0) {
                                    BluetoothDevice device = mAdapter.getItem(bluetoothPosition);
                                    Log.i("XGH", "device:" + device);


                                    mIminPrintUtils.initPrinter(IminPrintUtils.PrintConnectType.BLUETOOTH, device);

                                }
                            } else if (TextUtils.equals("SPI", connectTypeList.get(spinnerPosition))) {
                                mIminPrintUtils.initPrinter(IminPrintUtils.PrintConnectType.SPI);
                            }
                            break;
                        case 1:
                            if (TextUtils.equals("USB", connectTypeList.get(spinnerPosition))) {
                                int status = mIminPrintUtils.getPrinterStatus(IminPrintUtils.PrintConnectType.USB);
                                //针对S1， //0：打印机正常 1：打印机未连接或未上电 3：打印头打开 7：纸尽  8：纸将尽  99：其它错误
                                Log.d("XGH", " print USB status:" + status);
                                showToast(status + "");
                            } else if (TextUtils.equals("Bluetooth", connectTypeList.get(spinnerPosition))) {
                                showToast("Not support");
                            } else if (TextUtils.equals("SPI", connectTypeList.get(spinnerPosition))) {
                                int status = mIminPrintUtils.getPrinterStatus(IminPrintUtils.PrintConnectType.SPI);
                                showToast(status + "");
                            }
                            break;
                        case 2:
                            mIminPrintUtils.printAndLineFeed();
                            mIminPrintUtils.printAndFeedPaper(100);
                            break;
                        case 3:
                            mIminPrintUtils.partialCut();
                            break;
                        case 4:
                            String text = "iMin致力于使用先进的技术来帮助合作伙伴实现业务数字化。我们致力于成为东盟国家领先的智能商务设备提供商，帮助合作伙伴有效地连接\n";

                            mIminPrintUtils.printText("iMin committed to use advanced technologies to help our business partners digitize their business.We are dedicated in becoming a leading provider of smart business equipment " +
                                    "in ASEAN countries,assisting our partners to connect, create and utilize data effectively.\n");
                            mIminPrintUtils.printAndFeedPaper(100);

                            break;
                        case 5:
                            String[] strings3 = new String[]{"Test", "Description Description Description@48", "192.00"};
                            int[] colsWidthArr3 = new int[]{2, 6, 2};
                            int[] colsAlign3 = new int[]{0, 0, 2};
                            int[] colsSize3 = new int[]{26, 26, 26};
                            mIminPrintUtils.printColumnsText(strings3, colsWidthArr3,
                                    colsAlign3, colsSize3);
                            mIminPrintUtils.printAndFeedPaper(100);

                            break;
                        case 6:

                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icona);
                            bitmap = getBlackWhiteBitmap(bitmap);
                            mIminPrintUtils.printSingleBitmap(bitmap);
                            mIminPrintUtils.printAndFeedPaper(100);
                            break;
                        case 7:
                            List<Bitmap> mBitmapList1 = new ArrayList<>();
                            Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_rabit);
                            mBitmapList1.add(bitmap2);
                            mIminPrintUtils.printMultiBitmap(mBitmapList1, 1);
                            mIminPrintUtils.printAndFeedPaper(100);
                            break;
                        case 8:
                            try {
                                if (Utils.isEmpty(edit_bar_height.getText().toString())) {
                                    barHeight = 100;
                                } else {
                                    barHeight = Integer.valueOf(Utils.getNumberString(edit_bar_height.getText().toString()));
                                }
                                mIminPrintUtils.setBarCodeHeight(barHeight);

                                if (Utils.isEmpty(edit_bar_width.getText().toString())) {
                                    barWidth = 1;
                                } else {
                                    barWidth = Integer.valueOf(Utils.getNumberString(edit_bar_width.getText().toString()));
                                }
                                mIminPrintUtils.setBarCodeWidth(barWidth);
//                                mIminPrintUtils.setBarCodeWidth(1);
//                                mIminPrintUtils.printBarCode(4, "ABC123ABCAB12", 1);//ABC123ABCAB12
                                mIminPrintUtils.printBarCode(4, "123456", 1);
                                mIminPrintUtils.printText("Test1\n");
                                mIminPrintUtils.printBarCode(5, "123456789", 1);
                                mIminPrintUtils.printText("Test2\n");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mIminPrintUtils.printAndFeedPaper(100);
                            break;
                        case 9:
                            //2-6
                            if (Utils.isEmpty(edit_bar_width.getText().toString())) {
                                barWidth = 4;
                            } else {
                                barWidth = Integer.valueOf(Utils.getNumberString(edit_bar_width.getText().toString()));
                            }
                            mIminPrintUtils.setBarCodeWidth(barWidth);
                            break;
                        case 10://0-255
                            if (Utils.isEmpty(edit_bar_height.getText().toString())) {
                                barHeight = 100;
                            } else {
                                barHeight = Integer.valueOf(Utils.getNumberString(edit_bar_height.getText().toString()));
                            }
                            mIminPrintUtils.setBarCodeHeight(barHeight);
                            break;
                        case 11:
                            //0 none  1 up  2 down  3 up and down
                            if (Utils.isEmpty(edit_bar_position.getText().toString())) {
                                barTextPos = 1;
                            } else {
                                barTextPos = Integer.valueOf(Utils.getNumberString(edit_bar_position.getText().toString()));
                            }
                            mIminPrintUtils.setBarCodeContentPrintPos(barTextPos);
                            break;
                        case 12:
                            //左
                            mIminPrintUtils.printQrCode("123456", 0);
//                            mIminPrintUtils.printQrCode("00020101021226620017ID.CO.BANKBJB.WWW01189360011030017878590208017878590303URE51470017ID.CO.BANKBJB.WWW0215ID10232534051390303URE52049399530336054064515005802ID5925SAMSATOUTLET LEUWIPANJANG6007Bandung61054023562510124QRIS2023082811353438927102120852941057610703C0263046560", 0);
                            //中
                            mIminPrintUtils.printQrCode("123456", 1);
                            //右
                            mIminPrintUtils.printQrCode("123456", 2);
                            mIminPrintUtils.printAndFeedPaper(100);
                            break;
                        case 13:
                            //1-13
                            if (Utils.isEmpty(edit_qr_size.getText().toString())) {
                                qrCodeSize = 6;
                            } else {
                                qrCodeSize = Integer.valueOf(Utils.getNumberString(edit_qr_size.getText().toString()));
                            }
                            mIminPrintUtils.setQrCodeSize(qrCodeSize);

                            break;
                        case 14:
                            //48-51
                            if (Utils.isEmpty(edit_qr_error_lev.getText().toString())) {
                                qrCodeErrorLev = 48;
                            } else {
                                qrCodeErrorLev = Integer.valueOf(Utils.getNumberString(edit_qr_error_lev.getText().toString()));
                            }
                            mIminPrintUtils.setQrCodeErrorCorrectionLev(qrCodeErrorLev);
                            break;
                        case 15:
                            //0-576
                            if (Utils.isEmpty(edit_qr_left.getText().toString())) {
                                barAndQrLeftSize = 0;
                            } else {
                                barAndQrLeftSize = Integer.valueOf(Utils.getNumberString(edit_qr_left.getText().toString()));
                            }
                            mIminPrintUtils.setLeftMargin(barAndQrLeftSize);
                            break;

                        case 16:
                            // isInit = false;
                            Intent intent = new Intent(TestPrintActivity.this, TestService.class);
                            intent.putExtra("connectType", connectType);
                            startService(intent);
                            break;
                        case 17:
                            try {
                                Log.i("XGH", "position=:  18 ");
//                                mIminPrintUtils.setBarCodeHeight(70);
//                                mIminPrintUtils.setBarCodeWidth(3);
                                mIminPrintUtils.printBarCode(73, "{A0123456", 1);
                                mIminPrintUtils.printText("QR128A\n");
                                mIminPrintUtils.printAndFeedPaper(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 18:
                            try {
                                Log.i("XGH", "position=:  19 ");
//                                mIminPrintUtils.setBarCodeHeight(70);
//                                mIminPrintUtils.setBarCodeWidth(3);
                                mIminPrintUtils.printBarCode(73, "{B12CAa--", 1);
                                mIminPrintUtils.printText("QR128B\n");
                                mIminPrintUtils.printAndFeedPaper(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 19:
                            try {
                                Log.i("XGH", "position=:  20 ");
//                                mIminPrintUtils.setBarCodeHeight(100);
//                                mIminPrintUtils.setBarCodeWidth(2);
                                mIminPrintUtils.printBarCode(73, "{C0099997", 1);
                                mIminPrintUtils.printText("QR128C\n");
                                mIminPrintUtils.printAndFeedPaper(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case 20:
                            mIminPrintUtils.printBarCode39ToBitmap("11110AQ899015859344", 1300, 320);//打印code39  19位
                            mIminPrintUtils.printText("code39 :11110AQ899015859344\n");
                            //根据不同格式的打印
                            mIminPrintUtils.printBarCodeToBitmapFormat("11110AQ899015859344", 1300, 120, CodeFormat.CODE_128);
                            mIminPrintUtils.printText("CODE_128 :11110AQ899015859344\n");
                            mIminPrintUtils.printAndFeedPaper(100);
                            break;
                        case 21:
                            String antiWhiteText = "            Dine in       " +
                                    "\n        220819-00001E-1     \n" +
                                    "         1st floor/table 7    \n";
                            String t = "iMin committed to use advanced technologies to help our business partners digitize their business.We are dedicated in becoming a leading provider of smart business equipment \" +\n" +
                                    "                                            \"in ASEAN countries,assisting our partners to connect, create and utilize data effectively.\n";
                            String tests1 = "ORDER NOTE:no                              6.25 ";
                            String tests = "ORDER NOTE:no\n";
                            mIminPrintUtils.setTextStyle(Typeface.BOLD);
                            mIminPrintUtils.setTextSize(35);
                            //目前打印机固件版本打印反白图片打印完之后需要走纸才不会出现 打印图片白线的情况
                            mIminPrintUtils.printAntiWhiteText(/*t + */antiWhiteText);
                            mIminPrintUtils.setTextSize(28);
                            mIminPrintUtils.setTextStyle(Typeface.NORMAL);

                            String conte = "iMin committed to use advanced technologies .   ";

                            mIminPrintUtils.printAndFeedPaper(100);
                            break;
                        case 22:
                            mIminPrintUtils.setTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/liulixingshu.ttf"));
                            mIminPrintUtils.printText("1. iMin committed to use advanced technologies to help our business ...\n");
                            mIminPrintUtils.printAndLineFeed();
                            mIminPrintUtils.setTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/quanzhenguyin.ttf"));
                            mIminPrintUtils.printText("2. iMin committed to use advanced technologies to help our business ...\n");
                            mIminPrintUtils.printAndLineFeed();
                            mIminPrintUtils.setTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/yushixingshu.TTF"));
                            mIminPrintUtils.printText("3. iMin committed to use advanced technologies to help our business ...\n");
                            mIminPrintUtils.printAndLineFeed();
                            mIminPrintUtils.setTextTypeface(Typeface.createFromAsset(getAssets(), "fonts/zhangcao.ttf"));
                            mIminPrintUtils.printText("4. iMin committed to use advanced technologies to help our business ...\n");
                            mIminPrintUtils.printAndFeedPaper(100);
                            mIminPrintUtils.setTextTypeface(null);
                            break;
                        case 23:
                            final String printText = "ÇüéâäůćçłëŐőîŹÄĆÇüéâäůćçłëŐőîŹÄĆÇüéâäůćçłëŐőîŹÄĆÇüéâäůćçłëŐőîŹÄĆ";
                            final List<String> selectList = new ArrayList<>();
                            selectList.add("CP-852(Central and Eastern Europe)");
                            if (deviceModel.startsWith("D4") || deviceModel.startsWith("D1")
                                    || deviceModel.startsWith("TF1") || deviceModel.startsWith("M2-Max")
                                    || TextUtils.equals("I22T01", deviceModel) || deviceModel.equals("TF1-11")) {
                                selectList.add("Code Page 1250 Latin-2");
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    final ChooseFontsView chooseFontsView = new ChooseFontsView(TestPrintActivity.this);
                                    chooseFontsView.setData(selectList, printText, mIminPrintUtils);

                                    AlertDialog.Builder chooseBuilder = new AlertDialog.Builder(TestPrintActivity.this);
                                    chooseBuilder.setView(chooseFontsView);
                                    final AlertDialog chooseAlertDialog = chooseBuilder.create();
                                    chooseAlertDialog.setCancelable(false);
                                    chooseAlertDialog.show();

                                    chooseFontsView.cancelBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            chooseAlertDialog.dismiss();
                                        }
                                    });
                                }
                            });
                            break;
                        case 24:
                            if (!isDoubleQRDev()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(TestPrintActivity.this, "This model does not support", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                return;
                            }

                            mIminPrintUtils.setDoubleQRSize(Integer.parseInt(edit_tqr_size.getText().toString().trim()));
                            mIminPrintUtils.setDoubleQR1Level(Integer.parseInt(edit_tqr1_error_lev.getText().toString().trim()));
                            mIminPrintUtils.setDoubleQR2Level(Integer.parseInt(edit_tqr2_error_lev.getText().toString().trim()));
                            mIminPrintUtils.setDoubleQR1MarginLeft(Integer.parseInt(edit_tqr1_left.getText().toString().trim()));
                            mIminPrintUtils.setDoubleQR2MarginLeft(Integer.parseInt(edit_tqr2_left.getText().toString().trim()));
                            mIminPrintUtils.setDoubleQR1Version(Integer.parseInt(edit_tqr1_version.getText().toString().trim()));
                            mIminPrintUtils.setDoubleQR2Version(Integer.parseInt(edit_tqr2_version.getText().toString().trim()));
                            mIminPrintUtils.printDoubleQR(edit_tqr_str1.getText().toString().trim(), edit_tqr_str2.getText().toString().trim());

                            mIminPrintUtils.printAndFeedPaper(100);
                            break;
                        case 25://获取切刀次数
                            if (TextUtils.equals("Bluetooth", connectTypeList.get(spinnerPosition))) {
                                showToast("Not support");
                                return;
                            }
                            mIminPrintUtils.getPrintCutterNumber(new PrinterResultCallback() {
                                @Override
                                public void printResult(boolean success, int result, String string) {
                                    cutters.setText("getPrintCutterNumber==》" + string);
                                }
                            });
                            break;
                        case 26://获取走纸距离
                            if (TextUtils.equals("Bluetooth", connectTypeList.get(spinnerPosition))) {
                                showToast("Not support");
                                return;
                            }
                            mIminPrintUtils.getPrinterPaperDistance(new PrinterResultCallback() {
                                @Override
                                public void printResult(boolean success, int result, String string) {
                                    cutters.setText("getPrinterPaperDistance==》" + string);
                                }
                            });
                            break;
                        case 27://获取打印机序列号
                            if (TextUtils.equals("Bluetooth", connectTypeList.get(spinnerPosition))) {
                                showToast("Not support");
                                return;
                            }
                            mIminPrintUtils.getPrinterSerialNumber(new PrinterResultCallback() {
                                @Override
                                public void printResult(boolean success, int result, String string) {
                                    cutters.setText("getPrinterSerialNumber==》" + string);
                                }
                            });
                            break;
                        case 28:
                            printExample(1);
                            break;
                        case 29:
                            mIminPrintUtils.enterPrinterBuffer(false);
                            break;
                        case 30:
                            if (mIminPrintUtils != null) {
                                mIminPrintUtils.commitPrinterBuffer(new PrintResultCallback() {
                                    @Override
                                    public void printResult(int result) {
                                        Log.d(TAG, "commitPrinterBuffer printResult: " + result);
                                        showToast(result + "");

                                    }
                                });
                            }
                            break;
                        case 31:
                            if (mIminPrintUtils != null) {
                                mIminPrintUtils.exitPrinterBuffer(true, new PrintResultCallback() {
                                    @Override
                                    public void printResult(int result) {
                                        Log.d(TAG, "exitPrinterBuffer printResult: " + result);
                                        showToast(result + "");
                                    }
                                });
                            }
                            break;
                        case 32:
                            mIminPrintUtils.setIsOpenLog(IminPrintUtils.isOpenLog == 1 ? 0 : 1);
                            break;
                        case 33:
                            startActivity(new Intent(TestPrintActivity.this, BufferActivity.class));
                            break;
                        default:
                            Log.d(TAG, "click ->" + pos);
                    }
                } catch (Exception e) {
                }
            }
        });
        rvView.setAdapter(adapter);

    }

    //文字转图片
    private Bitmap getTextBitmap(String text, int textSize, Typeface typeface, int textLineSpacing) {
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        textPaint.setTypeface(typeface);
        textPaint.setUnderlineText(false);

        textPaint.setColor(Color.BLACK);
        // int with 576 80mm   58mm->384
        int with = 576;
        StaticLayout staticLayout = new StaticLayout(text, textPaint, with,
                Layout.Alignment.ALIGN_NORMAL, textLineSpacing, 1.0f, false);
        Bitmap newBitmap = Bitmap.createBitmap(with, (int) (staticLayout.getHeight()), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(newBitmap);
        staticLayout.draw(canvas);
        return newBitmap;
    }

    List<Layout.Alignment> mAlignments = new ArrayList<>();

    //colNom->colTextArr.length
//表格转图片
    private Bitmap getTableBitMap(String[] colTextArr, int[] colWidthArr, int[] colAlign,
                                  int[] size, int colNum, int textLineSpacing) {
        // int with 576 80mm   58mm->384
        int with = 576;
        mAlignments.clear();
        for (int i = 0; i < colAlign.length; i++) {
            switch (colAlign[i]) {
                case 0:
                    mAlignments.add(Layout.Alignment.ALIGN_NORMAL);
                    break;
                case 1:
                    mAlignments.add(Layout.Alignment.ALIGN_CENTER);
                    break;
                case 2:
                    mAlignments.add(Layout.Alignment.ALIGN_OPPOSITE);
                    break;
            }
        }
        TextPaint textPaint = new TextPaint();
        int[] allTextHeight = new int[colNum];
        int totalWitchRadio = getTotal(colWidthArr);
        StaticLayout staticLayout = null;
        for (int i = 0; i < colWidthArr.length; i++) {
            if (size != null && size.length == colWidthArr.length) {
                textPaint.setTextSize(size[i]);
            } else {
                textPaint.setTextSize(28);
            }
//            textPaint.setTextSize(size[i]);
            staticLayout = new StaticLayout(colTextArr[i], textPaint,
                    with * colWidthArr[i] / totalWitchRadio, mAlignments.get(i), textLineSpacing, 0.0f, false);
            allTextHeight[i] = staticLayout.getHeight();
        }
        Bitmap newBitmap = Bitmap.createBitmap(with, getMax(allTextHeight), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(newBitmap);
        for (int j = 0; j < colNum; j++) {
            if (size != null && size.length == colNum) {
                textPaint.setTextSize(size[j]);
            } else {
                textPaint.setTextSize(28);
            }
            staticLayout = new StaticLayout(colTextArr[j], textPaint, with * colWidthArr[j] / totalWitchRadio,
                    mAlignments.get(j), textLineSpacing, 0.0f, false);
            staticLayout.draw(canvas);
            canvas.translate(with * colWidthArr[j] / totalWitchRadio, 0); //align top
        }
        return newBitmap;
    }

    private int getMax(int[] arr) {
        int max = 0;
        for (int i = 0; i < arr.length; i++) {
            max = max > arr[i] ? max : arr[i];
        }
        return max;
    }

    private int getTotal(int[] arr) {
        int total = 0;
        for (int i = 0; i < arr.length; i++) {
            total += arr[i];
        }
        return total;
    }


    @SuppressLint("NewApi")
    private void printExample(int type) {
        mIminPrintUtils.setUnderline(false);
        //mIminPrintUtils.setPageFormat(type);
        mIminPrintUtils.setAlignment(2);
        mIminPrintUtils.printText("订单编号：220411A0015\n");

        mIminPrintUtils.setTextSize(48);
        mIminPrintUtils.setAlignment(1);
        mIminPrintUtils.setTextStyle(Typeface.BOLD);
        mIminPrintUtils.printText("外带：15\n");
        mIminPrintUtils.setTextSize(26);
        mIminPrintUtils.setAlignment(0);
        mIminPrintUtils.setTextStyle(Typeface.NORMAL);
        mIminPrintUtils.printAndFeedPaper(20);
        mIminPrintUtils.sendRAWData(getLineByte());
        mIminPrintUtils.printAndFeedPaper(5);
        mIminPrintUtils.printText("订单编号：220411A0015\n");
        mIminPrintUtils.printText("订单日期：2022-04-11 15:58:30\n");
        mIminPrintUtils.printAndFeedPaper(10);
        mIminPrintUtils.sendRAWData(getLineByte());
        mIminPrintUtils.printText("品名/数量/单价\n");
        mIminPrintUtils.printAndFeedPaper(5);
        mIminPrintUtils.sendRAWData(getLineByte());
        mIminPrintUtils.printAndFeedPaper(5);
        mIminPrintUtils.setTextStyle(Typeface.BOLD);
        String[] strings3 = new String[]{"#脆肠-1*NTD 150 [面线，加高丽菜*40，加鸭肉丸_6颗+35]", "NTD 150"};
        int[] colsWidthArr3 = new int[]{6, 2};
        int[] colsAlign3 = new int[]{0, 2};
        int[] colsSize3 = new int[]{26, 26};
        mIminPrintUtils.printColumnsText(strings3, colsWidthArr3,
                colsAlign3, colsSize3);
//        mIminPrintUtils.printAndFeedPaper(5);
        String[] strings31 = new String[]{"#加鸭肉丸_6颗*NTD 75 [面线]", "NTD 75"};
        int[] colsWidthArr31 = new int[]{6, 2};
        int[] colsAlign31 = new int[]{0, 2};
        int[] colsSize31 = new int[]{26, 26};
        mIminPrintUtils.printColumnsText(strings31, colsWidthArr31,
                colsAlign31, colsSize31);
        mIminPrintUtils.printAndFeedPaper(5);
        mIminPrintUtils.printText("共：2项\n");
        mIminPrintUtils.printAndFeedPaper(5);
//        mIminPrintUtils.printSingleBitmap(getLine());
        mIminPrintUtils.sendRAWData(getLineByte());
//        mIminPrintUtils.sendRAWData(getLineByte());
        mIminPrintUtils.printAndFeedPaper(5);

        mIminPrintUtils.setTextStyle(Typeface.NORMAL);
        String[] strings32 = new String[]{"小计：", "NTD 225"};
        int[] colsWidthArr32 = new int[]{6, 2};
        int[] colsAlign32 = new int[]{0, 2};
        int[] colsSize32 = new int[]{26, 26};
        mIminPrintUtils.printColumnsText(strings32, colsWidthArr32,
                colsAlign32, colsSize32);
        mIminPrintUtils.printAndFeedPaper(5);
        mIminPrintUtils.sendRAWData(new byte[]{27, 50});
        mIminPrintUtils.sendRAWData(new byte[]{27, 51, 10});//101
//        mIminPrintUtils.printSingleBitmap(getLine());
        mIminPrintUtils.sendRAWData(getLineByte());
        mIminPrintUtils.printAndFeedPaper(5);
        mIminPrintUtils.sendRAWData(new byte[]{27, 50});
        mIminPrintUtils.sendRAWData(new byte[]{27, 51, 10});//101
        String[] strings33 = new String[]{"总计：", "NTD 225"};
        int[] colsWidthArr33 = new int[]{6, 2};
        int[] colsAlign33 = new int[]{0, 2};
        int[] colsSize33 = new int[]{32, 32};
        mIminPrintUtils.printColumnsText(strings33, colsWidthArr33,
                colsAlign33, colsSize33);
        mIminPrintUtils.printAndFeedPaper(5);
        mIminPrintUtils.sendRAWData(new byte[]{27, 50});
        mIminPrintUtils.sendRAWData(new byte[]{27, 51, 10});//101
        //mIminPrintUtils.printSingleBitmap(getLine());
        mIminPrintUtils.sendRAWData(getLineByte());
        mIminPrintUtils.printAndFeedPaper(5);
        mIminPrintUtils.sendRAWData(new byte[]{27, 50});
        mIminPrintUtils.sendRAWData(new byte[]{27, 51, 10});//101
        String[] strings34 = new String[]{"现金：", "NTD 225"};
        int[] colsWidthArr34 = new int[]{6, 2};
        int[] colsAlign34 = new int[]{0, 2};
        int[] colsSize34 = new int[]{32, 32};
        mIminPrintUtils.printColumnsText(strings34, colsWidthArr34,
                colsAlign34, colsSize34);
        mIminPrintUtils.printAndFeedPaper(100);
    }

    private byte[] getLineByte() {
        Drawable drawable = getResources().getDrawable(R.drawable.line);
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap inputBmp = Bitmap.createBitmap(640, 4, config);
        Canvas canvas = new Canvas(inputBmp);// 建立对应bitmap的画布
        drawable.setBounds(0, 0, 640, 0);
        drawable.draw(canvas);// 把drawable内容画到画布中
        byte[] bytes = PrintDiskImagefile(inputBmp);
        return bytes;
    }

    private String toChineseHex(String s) {
        String ss = s;
        byte[] bt = new byte[0];

        try {
            bt = ss.getBytes("GBK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String s1 = "";
        for (int i = 0; i < bt.length; i++) {
            String tempStr = Integer.toHexString(bt[i]);
            if (tempStr.length() > 2)
                tempStr = tempStr.substring(tempStr.length() - 2);
            s1 = s1 + tempStr + "";
        }
        return s1.toUpperCase();
    }


    int printSum = 0;
    int i = 0;

    private boolean checkNumber(String input) {
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TOAST:
                    toast(msg.obj.toString());
                    break;
                case 300:
                    toast(msg.arg1 + "");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            int status = IminPrintUtils.getInstance(TestPrintActivity.this).getPrinterStatus(IminPrintUtils.PrintConnectType.USB);
                            Message message = Message.obtain();
                            message.what = 300;
                            // message.arg1 = status;
                            handler.sendMessage(message);
                        }
                    }).start();
                    break;
                case 400:
                    cutters.setText((String) msg.obj);
                    break;
                default:
            }
        }
    };


    private void showToast(String mMessage) {
        Message msg = new Message();
        msg.what = TOAST;
        msg.obj = mMessage;
        handler.sendMessage(msg);
    }

    // 两次点击按钮接口之间的点击间隔不能少于1000毫秒
    private final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime;

    public boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    private void initReceiver() {
        if (mBluetoothStateReceiver == null) {
            mBluetoothStateReceiver = new BluetoothStateReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBluetoothStateReceiver, filter);
        }
    }


    class BluetoothStateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            switch (state) {
                case BluetoothAdapter.STATE_TURNING_ON:
                    toast("Bluetooth ON");
                    break;

                case BluetoothAdapter.STATE_TURNING_OFF:
                    toast("Bluetooth OFF");
                    break;
            }
        }
    }

    protected void toast(String message) {
        Toast.makeText(TestPrintActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

        public DeviceListAdapter(Context context) {
            super(context, 0);
        }

        @TargetApi(Build.VERSION_CODES.ECLAIR)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            BluetoothDevice device = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_bluetooth_device, parent, false);
            }

            TextView tvDeviceName = (TextView) convertView.findViewById(R.id.tv_device_name);
            CheckBox cbDevice = (CheckBox) convertView.findViewById(R.id.cb_device);

            tvDeviceName.setText(device.getName());

            cbDevice.setChecked(position == bluetoothPosition);

            return convertView;
        }

    }


    private void fillAdapter() {
        List<BluetoothDevice> printerDevices = BluetoothUtil.getPairedDevices();
        mAdapter.clear();
        mAdapter.addAll(printerDevices);
        refreshButtonText(printerDevices);
    }

    private void refreshButtonText(List<BluetoothDevice> printerDevices) {
        if (printerDevices.size() > 0 && connectType == 2) {
            mLvPairedDevices.setVisibility(View.VISIBLE);
        }
    }


    public static Bitmap getBlackWhiteBitmap(Bitmap bitmap) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        int color = 0;
        int a, r, g, b, r1, g1, b1;
        int[] oldPx = new int[w * h];
        int[] newPx = new int[w * h];

        bitmap.getPixels(oldPx, 0, w, 0, 0, w, h);
        for (int i = 0; i < w * h; i++) {
            color = oldPx[i];
            r = Color.red(color);
            g = Color.green(color);
            b = Color.blue(color);
            a = Color.alpha(color);
            //黑白矩阵
            r1 = (int) (0.33 * r + 0.59 * g + 0.11 * b);
            g1 = (int) (0.33 * r + 0.59 * g + 0.11 * b);
            b1 = (int) (0.33 * r + 0.59 * g + 0.11 * b);
            //检查各像素值是否超出范围
            if (r1 > 255) {
                r1 = 255;
            }

            if (g1 > 255) {
                g1 = 255;
            }

            if (b1 > 255) {
                b1 = 255;
            }

            newPx[i] = Color.argb(a, r1, g1, b1);
        }
        resultBitmap.setPixels(newPx, 0, w, 0, 0, w, h);
        return /*getGreyBitmap(resultBitmap)*/resultBitmap;
    }

    public static Bitmap getGreyBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        } else {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            int[] gray = new int[height * width];

            int e;
            int i;
            int j;
            int g;
            for (e = 0; e < height; ++e) {
                for (i = 0; i < width; ++i) {
                    j = pixels[width * e + i];
                    g = (j & 16711680) >> 16;
                    gray[width * e + i] = g;
                }
            }

            for (i = 0; i < height; ++i) {
                for (j = 0; j < width; ++j) {
                    g = gray[width * i + j];
                    if (g >= 128) {
                        pixels[width * i + j] = -1;
                        e = g - 255;
                    } else {
                        pixels[width * i + j] = -16777216;
                        e = g - 0;
                    }

                    if (j < width - 1 && i < height - 1) {
                        gray[width * i + j + 1] += 3 * e / 8;
                        gray[width * (i + 1) + j] += 3 * e / 8;
                        gray[width * (i + 1) + j + 1] += e / 4;
                    } else if (j == width - 1 && i < height - 1) {
                        gray[width * (i + 1) + j] += 3 * e / 8;
                    } else if (j < width - 1 && i == height - 1) {
                        gray[width * i + j + 1] += e / 4;
                    }
                }
            }

            Bitmap mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            mBitmap = ThumbnailUtils.extractThumbnail(mBitmap, width, height);
            return mBitmap;
        }
    }

    private static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int height = bmpOriginal.getHeight();
        int width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0F);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0.0F, 0.0F, paint);
        return bmpGrayscale;
    }

    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        System.out.println(bmp.getConfig());
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        if (width > 640) {
            width = 640;
        }
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] gray = new int[height * width];
        try {
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int grey = pixels[width * i + j];
                    int red = ((grey & 0x00FF0000) >> 16);
                    gray[width * i + j] = red;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "PrintBmp:" + e.getMessage());
        }

        int e = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int g = gray[width * i + j];
                if (g >= 128) {
                    pixels[width * i + j] = 0xffffffff;
                    e = g - 255;

                } else {
                    pixels[width * i + j] = 0xff000000;
                    e = g - 0;
                }
                if (j < width - 1 && i < height - 1) {
                    gray[width * i + j + 1] += 3 * e / 8;
                    gray[width * (i + 1) + j] += 3 * e / 8;
                    gray[width * (i + 1) + j + 1] += e / 4;
                } else if (j == width - 1 && i < height - 1) {
                    gray[width * (i + 1) + j] += 3 * e / 8;
                } else if (j < width - 1 && i == height - 1) {
                    gray[width * (i) + j + 1] += e / 4;
                }
            }

        }

        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, width, height);
        return resizeBmp;
    }

    /**
     * 将bitmap图转换为头四位有宽高的光栅位图
     */
    public static byte[] getBytesFromBitMap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bw = (width - 1) / 8 + 1;

        byte[] rv = new byte[height * bw + 4];
        rv[0] = (byte) bw;//xL
        rv[1] = (byte) (bw >> 8);//xH
        rv[2] = (byte) height;
        rv[3] = (byte) (height >> 8);

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int clr = pixels[width * i + j];
                int red = (clr & 0x00ff0000) >> 16;
                int green = (clr & 0x0000ff00) >> 8;
                int blue = clr & 0x000000ff;
                byte gray = (RGB2Gray(red, green, blue));
                rv[bw * i + j / 8 + 4] = (byte) (rv[bw * i + j / 8 + 4] | (gray << (7 - j % 8)));
            }
        }

        return rv;
    }

    private static byte RGB2Gray(int r, int g, int b) {
        return (false ? ((int) (0.29900 * r + 0.58700 * g + 0.11400 * b) > 200)
                : ((int) (0.29900 * r + 0.58700 * g + 0.11400 * b) < 200)) ? (byte) 1 : (byte) 0;
    }

    //光栅位图打印
    public static byte[] printBitmap(Bitmap bitmap) {
        byte[] bytes1 = new byte[4];
        bytes1[0] = 0x1D;
        bytes1[1] = 0x76;
        bytes1[2] = 0x30;
        bytes1[3] = 0x00;

        byte[] bytes2 = BytesUtil.getBytesFromBitMap(bitmap);
        return BytesUtil.byteMerger(bytes1, bytes2);
    }

    public static byte[] PrintDiskImagefile(Bitmap bitmap) {
        byte[] bytes;


//        if (!strPath.substring(strPath.toLowerCase().indexOf(".") + 1).equals("bmp")) {
//            bitmap = convertToBlackWhite(bitmap);
//            int width = bitmap.getWidth();
//            int  heigh = bitmap.getHeight();
//            int iDataLen = width * heigh;
//            int[] pixels = new int[iDataLen];
//            bitmap.getPixels(pixels, 0, width, 0, 0, width, heigh);
//            bytes = PrintDiskImagefile(pixels,width,heigh);
//        }else
//        {
        int width = bitmap.getWidth();
        int heigh = bitmap.getHeight();
        int iDataLen = width * heigh;
        int[] pixels = new int[iDataLen];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, heigh);
        bytes = PrintDiskImagefile(pixels, width, heigh);

//        }

        return bytes;
    }

    public static byte[] PrintDiskImagefile(int[] pixels, int iWidth, int iHeight) {
        int iBw = iWidth / 8;
        int iMod = iWidth % 8;
        if (iMod > 0)
            iBw = iBw + 1;
        int iDataLen = iBw * iHeight;
        byte[] bCmd = new byte[iDataLen + 8];
        int iIndex = 0;
        bCmd[iIndex++] = 0x1D;
        bCmd[iIndex++] = 0x76;
        bCmd[iIndex++] = 0x30;
        bCmd[iIndex++] = 0x0;
        bCmd[iIndex++] = (byte) iBw;
        bCmd[iIndex++] = (byte) (iBw >> 8);
        bCmd[iIndex++] = (byte) iHeight;
        bCmd[iIndex++] = (byte) (iHeight >> 8);

        int iValue1 = 0;
        int iValue2 = 0;
        int iRow = 0;
        int iCol = 0;
        int iW = 0;
        int iValue3 = 0;
        int iValue4 = 0;
        for (iRow = 0; iRow < iHeight; iRow++) {
            for (iCol = 0; iCol < iBw - 1; iCol++) {
                iValue2 = 0;

                iValue1 = BmpUtils.getPixelBlackWhiteValue(pixels[iW++]);
                //Log.d("dzm","=== iValue1 = " + iValue1);
                if (iValue1 == 1)
                    iValue2 = iValue2 + 0x80;
                iValue1 = BmpUtils.getPixelBlackWhiteValue(pixels[iW++]);
                if (iValue1 == 1)
                    iValue2 = iValue2 + 0x40;
                iValue1 = BmpUtils.getPixelBlackWhiteValue(pixels[iW++]);
                if (iValue1 == 1)
                    iValue2 = iValue2 + 0x20;
                iValue1 = BmpUtils.getPixelBlackWhiteValue(pixels[iW++]);
                if (iValue1 == 1)
                    iValue2 = iValue2 + 0x10;
                iValue1 = BmpUtils.getPixelBlackWhiteValue(pixels[iW++]);
                if (iValue1 == 1)
                    iValue2 = iValue2 + 0x8;
                iValue1 = BmpUtils.getPixelBlackWhiteValue(pixels[iW++]);
                if (iValue1 == 1)
                    iValue2 = iValue2 + 0x4;
                iValue1 = BmpUtils.getPixelBlackWhiteValue(pixels[iW++]);
                if (iValue1 == 1)
                    iValue2 = iValue2 + 0x2;
                iValue1 = BmpUtils.getPixelBlackWhiteValue(pixels[iW++]);
                if (iValue1 == 1)
                    iValue2 = iValue2 + 0x1;
                if (iValue3 < -1) // w1
                    iValue4 = iValue4 + 0x10;
                bCmd[iIndex++] = (byte) (iValue2);
            }
            iValue2 = 0;
            if (iValue4 > 0)      // w2
                iValue3 = 1;
            if (iMod == 0) {
                for (iCol = 8; iCol > iMod; iCol--) {
                    iValue1 = BmpUtils.getPixelBlackWhiteValue(pixels[iW++]);
                    if (iValue1 == 1)
                        iValue2 = iValue2 + (1 << iCol);
                }
            } else {
                for (iCol = 0; iCol < iMod; iCol++) {
                    iValue1 = BmpUtils.getPixelBlackWhiteValue(pixels[iW++]);
                    if (iValue1 == 1)
                        iValue2 = iValue2 + (1 << (8 - iCol));
                }
            }
            bCmd[iIndex++] = (byte) (iValue2);
        }
        return bCmd;
    }

    public static byte[] PrintDiskAlphaImagefile(int[] pixels, int iWidth, int iHeight) {
        int iBw = iWidth / 8;
        int iMod = iWidth % 8;
        if (iMod > 0)
            iBw = iBw + 1;
        int iDataLen = iBw * iHeight;
        byte[] bCmd = new byte[iDataLen + 8];
        int iIndex = 0;
        bCmd[iIndex++] = 0x1D;
        bCmd[iIndex++] = 0x76;
        bCmd[iIndex++] = 0x30;
        bCmd[iIndex++] = 0x0;
        bCmd[iIndex++] = (byte) iBw;
        bCmd[iIndex++] = (byte) (iBw >> 8);
        bCmd[iIndex++] = (byte) iHeight;
        bCmd[iIndex++] = (byte) (iHeight >> 8);

        int iValue1 = 0;
        int iValue2 = 0;
        int iRow = 0;
        int iCol = 0;
        int iW = 0;
        int iValue3 = 0;
        int iValue4 = 0;
        for (iRow = 0; iRow < iHeight; iRow++) {
            for (iCol = 0; iCol < iBw - 1; iCol++) {
                iValue2 = 0;

                iValue1 = pixels[iW++];
                if (iValue1 < -1)
                    iValue2 = iValue2 + 0x80;
                iValue1 = pixels[iW++];
                if (iValue1 < -1)
                    iValue2 = iValue2 + 0x40;
                iValue1 = pixels[iW++];
                if (iValue1 < -1)
                    iValue2 = iValue2 + 0x20;
                iValue1 = pixels[iW++];
                if (iValue1 < -1)
                    iValue2 = iValue2 + 0x10;
                iValue1 = pixels[iW++];
                if (iValue1 < -1)
                    iValue2 = iValue2 + 0x8;
                iValue1 = pixels[iW++];
                if (iValue1 < -1)
                    iValue2 = iValue2 + 0x4;
                iValue1 = pixels[iW++];
                if (iValue1 < -1)
                    iValue2 = iValue2 + 0x2;
                iValue1 = pixels[iW++];
                if (iValue1 < -1)
                    iValue2 = iValue2 + 0x1;
                if (iValue3 < -1) // w1
                    iValue4 = iValue4 + 0x10;
                bCmd[iIndex++] = (byte) (iValue2);
            }
            iValue2 = 0;
            if (iValue4 > 0)      // w2
                iValue3 = 1;
            if (iMod == 0) {
                for (iCol = 8; iCol > iMod; iCol--) {
                    iValue1 = pixels[iW++];
                    if (iValue1 < -1)
                        iValue2 = iValue2 + (1 << iCol);
                }
            } else {
                for (iCol = 0; iCol < iMod; iCol++) {
                    iValue1 = pixels[iW++];
                    if (iValue1 < -1)
                        iValue2 = iValue2 + (1 << (8 - iCol));
                }
            }
            bCmd[iIndex++] = (byte) (iValue2);
        }
        return bCmd;
    }

    public static String imageChangeBase64(String imagePath) {
        ByteArrayOutputStream outPut = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        try {
            // 创建URL
            URL url = new URL(imagePath);
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10 * 1000);

            if (conn.getResponseCode() != 200) {
                return "fail";//连接失败/链接失效/图片不存在
            }
            InputStream inStream = conn.getInputStream();
            int len = -1;
            while ((len = inStream.read(data)) != -1) {
                outPut.write(data, 0, len);
            }
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        byte[] encode = Base64.encode(outPut.toByteArray(), Base64.DEFAULT);
        String res = new String(encode);
        return res;
    }

}
