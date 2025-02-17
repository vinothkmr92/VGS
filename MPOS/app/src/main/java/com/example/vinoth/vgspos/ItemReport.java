package com.example.vinoth.vgspos;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ItemReport extends AppCompatActivity implements  View.OnClickListener{

    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private static String EXCEL_SHEET_NAME = "Sheet1";
    private static Workbook workbook =null;
    private static Cell cell=null;
    private static CellStyle headerCellStyle=null;
    private static Sheet sheet = null;
    private static ItemReport instance;
    private final ActivityResultLauncher<Intent> storeageActivitytResultLanucher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){

                    }
                    else{

                    }
                }
            });
    DatabaseHelper dbHelper;
    TextView frmDateTextView;
    TextView toDateTextView;
    LinearLayout itemRptContainer;
    ScrollView itemsrptScrollview;
    DatePickerDialog datePickerDialog;
    DatePickerDialog todatePickerDialog;
    TextView searchTxtView;
    Dialog dialog;
    CheckBox stockReport;
    TextView txtViewTotalAmt;
    ImageButton btnShare;
    private Dialog progressBar;
    private ImageButton btnPrint;
    private Calendar calendar;
    private int year, month, day;
    private DatePickerDialog.OnDateSetListener mytoDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    String monthstr = String.valueOf(arg2+1);
                    monthstr = StringUtils.leftPad(monthstr,2,'0');
                    String dt = StringUtils.leftPad(String.valueOf(arg3),2,'0');
                    StringBuilder sb = new StringBuilder().append(arg1).append("-")
                            .append(monthstr).append("-").append(dt);
                    toDateTextView.setText(sb.toString());
                    LoadReportViews();
                }
            };
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    //setDate(arg1, arg2+1, arg3);
                    String monthstr = String.valueOf(arg2+1);
                    monthstr = StringUtils.leftPad(monthstr,2,'0');
                    String dt = StringUtils.leftPad(String.valueOf(arg3),2,'0');
                    StringBuilder sb = new StringBuilder().append(arg1).append("-")
                            .append(monthstr).append("-").append(dt);
                    frmDateTextView.setText(sb.toString());
                    LoadReportViews();
                }
            };

    public static boolean exportDataIntoWorkbook(Context context, String fileName,
                                                 ArrayList<ItemsRpt> dataList) {
        boolean isWorkbookWrittenIntoStorage;

        // Check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            Log.e("error", "Storage not available or read only");
            return false;
        }

        // Creating a New HSSF Workbook (.xls format)
        workbook = new HSSFWorkbook();

        setHeaderCellStyle();

        // Creating a New Sheet and Setting width for each column
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);
        sheet.setColumnWidth(0, (15 * 100));
        sheet.setColumnWidth(1, (15 * 300));
        sheet.setColumnWidth(2, (15 * 200));
        sheet.setColumnWidth(2, (15 * 200));

        setHeaderRow();
        fillDataIntoExcel(dataList);
        isWorkbookWrittenIntoStorage = storeExcelInStorage(context, fileName);

        return isWorkbookWrittenIntoStorage;
    }

    /**
     * Checks if Storage is READ-ONLY
     *
     * @return boolean
     */
    private static boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);
    }

    /**
     * Checks if Storage is Available
     *
     * @return boolean
     */
    private static boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(externalStorageState);
    }

    public static ItemReport getInstance() {
        return instance;
    }

    /**
     * Setup header cell style
     */
    private static void setHeaderCellStyle() {
        headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.AQUA.getIndex());
        headerCellStyle.setAlignment(HorizontalAlignment.CENTER);
    }

    /**
     * Setup Header Row
     */
    private static void setHeaderRow() {
        Row row = sheet.createRow(0);

        cell = row.createCell(0);
        cell.setCellValue("Item ID");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Item Name");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Quantity");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Amount");
        cell.setCellStyle(headerCellStyle);
    }

    /**
     * Fills Data into Excel Sheet
     * <p>
     * NOTE: Set row index as i+1 since 0th index belongs to header row
     *
     * @param dataList - List containing data to be filled into excel
     */
    private static void fillDataIntoExcel(ArrayList<ItemsRpt> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            // Create a New Row for every new entry in list
            Row rowData = sheet.createRow(i + 1);

            // Create Cells for each row
            cell = rowData.createCell(0);
            cell.setCellValue(dataList.get(i).getItemID());

            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getItemName());

            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getQuantity());

            cell = rowData.createCell(3);
            cell.setCellValue(dataList.get(i).getAmount());
        }
    }

    /**
     * Store Excel Workbook in external storage
     *
     * @param context  - application context
     * @param fileName - name of workbook which will be stored in device
     * @return boolean - returns state whether workbook is written into storage or not
     */
    private static boolean storeExcelInStorage(Context context, String fileName) {
        boolean isSuccess;
        File file = new File(context.getExternalFilesDir(DOWNLOAD_SERVICE), fileName);
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Log.e("LOG", "Writing file" + file);
            isSuccess = true;
        } catch (IOException e) {
            Log.e("ERROR", "Error writing Exception: ", e);
            isSuccess = false;
        } catch (Exception e) {
            Log.e("ERROR", "Failed to save file due to Exception: ", e);
            isSuccess = false;
        } finally {
            try {
                if (null != fileOutputStream) {
                    fileOutputStream.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return isSuccess;
    }

    private  void GetDefaultDate(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    public void showCustomDialog(String title, String Message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage("\n"+Message);
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCanceledOnTouchOutside(false);
        b.setCancelable(false);
        b.show();
    }

    private void PrintReport(ArrayList<ItemsRpt> items){
        try {
            String frmdt = frmDateTextView.getText().toString();
            String todt = toDateTextView.getText().toString();
            Common.saleReportFrmDate = frmdt;
            Common.saleReportToDate = todt;
            Common.isItemWiseRptBill = true;
            PrinterUtil printerUtil = new PrinterUtil(ItemReport.this,this,false);
            printerUtil.itemsRpts = items;
            printerUtil.Print();

        } catch (Exception e) {
            showCustomDialog("Print Error",e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_report);
        progressBar = new Dialog(ItemReport.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading");
        dbHelper = new DatabaseHelper(this);
        frmDateTextView = (TextView) findViewById(R.id.itemrptFrmDate);
        toDateTextView = (TextView) findViewById(R.id.itemrptToDate);
        txtViewTotalAmt = (TextView)findViewById(R.id.ttAmtPrdReport);
        btnShare = (ImageButton)findViewById(R.id.btnshareExcel);
        itemRptContainer = (LinearLayout)findViewById(R.id.itemsrptContainer);
        itemsrptScrollview = (ScrollView)findViewById(R.id.itemsrptScrollView);
        btnPrint = (ImageButton)findViewById(R.id.btnrptprint);
        btnPrint.setOnClickListener(this);
        frmDateTextView.setOnClickListener(this);
        toDateTextView.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        searchTxtView = (TextView)findViewById(R.id.customerinfoitemwise);
        stockReport = (CheckBox)findViewById(R.id.stocksRpt);
        stockReport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LoadReportViews();
            }
        });
        searchTxtView.setText("ALL");
        ArrayList<String> wts = new ArrayList<String>();
        wts.add("ALL");
        ArrayList<Customer> customers = dbHelper.GetCustomers();
        for(int i=0;i<customers.size();i++){
            wts.add(customers.get(i).getCustomerName());
        }

        searchTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize dialog
                dialog=new Dialog(ItemReport.this);

                // set custom dialog
                dialog.setContentView(R.layout.dialog_searchable_spinner);

                // set custom height and width
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT);

                // set transparent background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // show dialog
                dialog.show();

                // Initialize and assign variable
                EditText editText=dialog.findViewById(R.id.edit_text);
                ListView listView=dialog.findViewById(R.id.list_view);

                // Initialize array adapter
                ArrayAdapter<String> adapter=new ArrayAdapter<>(ItemReport.this, android.R.layout.simple_list_item_1,wts);

                // set adapter
                listView.setAdapter(adapter);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // when item selected from list
                        // set selected item on textView
                        searchTxtView.setText(adapter.getItem(position));

                        // Dismiss dialog
                        dialog.dismiss();
                        LoadReportViews();
                    }
                });
            }
        });
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        frmDateTextView.setText(format.format(date));
        toDateTextView.setText(format.format(date));
        LoadReportViews();
        GetDefaultDate();
        datePickerDialog = new DatePickerDialog(ItemReport.this,myDateListener,year,month,day);
        todatePickerDialog = new DatePickerDialog(ItemReport.this,mytoDateListener,year,month,day);
        instance = this;
        if(!checkFilePermission()){
            requestStoragePermission();
        }
    }

    @Override
    public void onBackPressed(){
        Common.waiter = "";
        super.onBackPressed();
    }
    public void requestStoragePermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            try {
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                storeageActivitytResultLanucher.launch(intent);
            } catch (Exception ex){
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storeageActivitytResultLanucher.launch(intent);
            }
        }
        else{
            ActivityCompat.requestPermissions(ItemReport.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}
                    ,1);
        }
    }

    public boolean checkFilePermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
            return  Environment.isExternalStorageManager();
        }
        else{
            int write = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void addCard(ItemsRpt items){
        View view = getLayoutInflater().inflate(R.layout.cart_item,null);
        TextView prname = view.findViewById(R.id.itemcard_name);
        TextView amt = view.findViewById(R.id.itemcard_amt);
        TextView qty = view.findViewById(R.id.itemcard_qty);
        if(stockReport.isChecked()){
            amt.setVisibility(View.INVISIBLE);
        }
        else{
            amt.setVisibility(View.VISIBLE);
        }
        prname.setText(items.getItemName());
        DecimalFormat formater = new DecimalFormat("#.###");
        //String qtyStr = String.format("%.3f",items.getQuantity());
        String qtyStr = formater.format(items.getQuantity());
        qtyStr = "QTY: "+qtyStr;
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        formatter.setMaximumFractionDigits(0);
        String symbol = formatter.getCurrency().getSymbol();
        String amtStr = formatter.format(items.getAmount()).replace(symbol,symbol+" ");
        amt.setText(amtStr);
        qty.setText(qtyStr);
        itemRptContainer.addView(view);
    }
    public void LoadReportViews(){
        try{
            itemRptContainer.removeAllViews();
            String frmdt = frmDateTextView.getText().toString();
            String todt = toDateTextView.getText().toString();
            String waiter  = searchTxtView.getText().toString();
            if(stockReport.isChecked()){
                txtViewTotalAmt.setVisibility(View.INVISIBLE);
            }
            else{
                txtViewTotalAmt.setVisibility(View.VISIBLE);
            }
            ArrayList<ItemsRpt> itemsRpts = dbHelper.GetReports(frmdt,todt,waiter,stockReport.isChecked());
            double totalAmt = 0;
            for(int i=0;i<itemsRpts.size();i++){
                ItemsRpt item = itemsRpts.get(i);
                addCard(item);
                totalAmt+=item.getAmount();
            }
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            formatter.setMaximumFractionDigits(0);
            String symbol = formatter.getCurrency().getSymbol();
            String ttSaleAmtstring = formatter.format(totalAmt).replace(symbol,symbol+" ");
            txtViewTotalAmt.setText(ttSaleAmtstring);
            itemsrptScrollview.fullScroll(ScrollView.FOCUS_UP);
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }

    }

    private void ExportExcel(){
        String frmdt = frmDateTextView.getText().toString();
        String todt = toDateTextView.getText().toString();
        String waiter  = searchTxtView.getText().toString();
        ArrayList<ItemsRpt> sal = dbHelper.GetReports(frmdt,todt,waiter,stockReport.isChecked());
        String fileName = "ItemiseSaleReport.xls";
        if(exportDataIntoWorkbook(getApplicationContext(),fileName,sal)){
            try{
                File file = new File(getApplicationContext().getExternalFilesDir(DOWNLOAD_SERVICE), fileName);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(ItemReport.this, BuildConfig.APPLICATION_ID + ".provider",file);
                intent.setDataAndType(uri,"application/vnd.ms-excel");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }
            catch (Exception ex){
                showCustomDialog("Exception",ex.getMessage());
            }
        }
        else{
            showCustomDialog("Warning","Could not export to Excel.");
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.itemrptFrmDate:
                datePickerDialog.show();
                break;
            case R.id.itemrptToDate:
                todatePickerDialog.show();
                break;
            case R.id.btnshareExcel:
                ExportExcel();
                break;
            case R.id.btnrptprint:
                String frmdt = frmDateTextView.getText().toString();
                String todt = toDateTextView.getText().toString();
                String waiter  = searchTxtView.getText().toString();
                ArrayList<ItemsRpt> items = dbHelper.GetReports(frmdt,todt,waiter,stockReport.isChecked());
                if(items.size()>0){
                    PrintReport(items);
                }
                else {
                    showCustomDialog("Warning","No Details to Print.");
                }
        }

    }
}