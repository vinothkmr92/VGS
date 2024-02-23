package com.example.vinoth.vgspos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SaleReportActivity extends AppCompatActivity implements View.OnClickListener {

    TextView frmDateTextView;
    TextView toDateTextView;
    ImageButton btnFrmDatePicker;
    ImageButton btnToDatePicker;
    Button btnGetReport;
    ImageButton btnPrintReport;

    private static Sheet sheet = null;

    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    DatePickerDialog datePickerDialog;
    DatePickerDialog todatePickerDialog;
    String selectedDate;
    DatabaseHelper dbHelper;
    GridLayout gridLayout;
    DynamicViewSaleReport dynamicView;
    TextView txtViewTotalSaleAmt;
    private Dialog progressBar;
    TextView searchTxtView;
    Dialog dialog;
    private static String EXCEL_SHEET_NAME = "Sheet1";
    private static Workbook workbook =null;
    private static Cell cell=null;
    private static CellStyle headerCellStyle=null;
    ImageButton btnExportExcel;
    private static SaleReportActivity instance;

    public static SaleReportActivity getInstance() {
        return instance;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }

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
                }
            };
    @Override
    public void onBackPressed(){
        Common.billDate = new Date();
        Common.billNo = 0;
        Common.itemsCarts = null;
        Common.waiter = "";
        super.onBackPressed();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.exit:
                finish();
                System.exit(0);
                return true;
            case R.id.uploadExcel:
                Intent dcpage = new Intent(this,UploadActivity.class);
                dcpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(dcpage);
                return  true;
            case R.id.settings:
                Intent settingsPage = new Intent(this,Settings.class);
                settingsPage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(settingsPage);
                return  true;
            case R.id.homemenu:
                Common.billDate = new Date();
                Common.billNo = 0;
                Common.itemsCarts = null;
                Common.waiter = "";
                Intent page = new Intent(this,HomeActivity.class);
                page.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(page);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
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
                }
            };

    public static boolean exportDataIntoWorkbook(Context context, String fileName,
                                                 ArrayList<SaleReport> dataList) {
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
        sheet.setColumnWidth(0, (15 * 300));
        sheet.setColumnWidth(1, (15 * 200));
        sheet.setColumnWidth(2, (15 * 200));

        setHeaderRow();
        fillDataIntoExcel(dataList);
        isWorkbookWrittenIntoStorage = storeExcelInStorage(context, fileName);

        return isWorkbookWrittenIntoStorage;
    }

    private  void GetDefaultDate(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
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
        cell.setCellValue("Bill Date");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Bill No");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Bill Amount");
        cell.setCellStyle(headerCellStyle);
    }

    /**
     * Fills Data into Excel Sheet
     * <p>
     * NOTE: Set row index as i+1 since 0th index belongs to header row
     *
     * @param dataList - List containing data to be filled into excel
     */
    private static void fillDataIntoExcel(ArrayList<SaleReport> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            // Create a New Row for every new entry in list
            Row rowData = sheet.createRow(i + 1);

            // Create Cells for each row
            cell = rowData.createCell(0);
            Date bt = dataList.get(i).getBillDt();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm aa", Locale.getDefault());
            String d = format.format(bt);
            cell.setCellValue(d);

            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getBillNo());

            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getBillAmount());
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_report);
        dbHelper = new DatabaseHelper(this);
        progressBar = new Dialog(SaleReportActivity.this);
        progressBar.setContentView(R.layout.custom_progress_dialog);
        progressBar.setTitle("Loading...");
        frmDateTextView = (TextView) findViewById(R.id.salerptFrmDate);
        toDateTextView = (TextView) findViewById(R.id.salerptToDate);
        btnFrmDatePicker = (ImageButton) findViewById(R.id.btndpFrmDate);
        btnToDatePicker = (ImageButton) findViewById(R.id.btndpToDate);
        btnGetReport = (Button) findViewById(R.id.btngetsalereport);
        btnPrintReport = (ImageButton) findViewById(R.id.btnsalerptprint);
        btnExportExcel = (ImageButton)findViewById(R.id.btnshareExcel);
        gridLayout = (GridLayout)findViewById(R.id.gridDataSaleRpt);
        txtViewTotalSaleAmt = (TextView)findViewById(R.id.totalSaleAmt);
        btnGetReport.setOnClickListener(this);
        btnPrintReport.setOnClickListener(this);
        btnFrmDatePicker.setOnClickListener(this);
        btnToDatePicker.setOnClickListener(this);
        btnExportExcel.setOnClickListener(this);
        searchTxtView = (TextView)findViewById(R.id.customerinfosaleRpt);
        searchTxtView.setText("ALL");
        ArrayList<String> wts = new ArrayList<String>();
        wts.add("ALL");
        ArrayList<Customer> customers = dbHelper.GetCustomers();
        for(int i=0;i<customers.size();i++){
            wts.add(customers.get(i).getCustomerName());
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        frmDateTextView.setText(format.format(date));
        toDateTextView.setText(format.format(date));
        searchTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize dialog
                dialog=new Dialog(SaleReportActivity.this);

                // set custom dialog
                dialog.setContentView(R.layout.dialog_searchable_spinner);

                // set custom height and width
                dialog.getWindow().setLayout(500,500);

                // set transparent background
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                // show dialog
                dialog.show();

                // Initialize and assign variable
                EditText editText=dialog.findViewById(R.id.edit_text);
                ListView listView=dialog.findViewById(R.id.list_view);

                // Initialize array adapter
                ArrayAdapter<String> adapter=new ArrayAdapter<>(SaleReportActivity.this, android.R.layout.simple_list_item_1,wts);

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

                    }
                });
            }
        });
        GetDefaultDate();
        datePickerDialog = new DatePickerDialog(SaleReportActivity.this,myDateListener,year,month,day);
        todatePickerDialog = new DatePickerDialog(SaleReportActivity.this,mytoDateListener,year,month,day);
        instance = this;
    }

    private void ExportExcel(){
        ArrayList<SaleReport> sal = GetSaleReport();
        String fileName = "SaleReport.xls";
        if(exportDataIntoWorkbook(getApplicationContext(),fileName,sal)){
            try{
                File file = new File(getApplicationContext().getExternalFilesDir(DOWNLOAD_SERVICE), fileName);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(SaleReportActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);
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
    private ArrayList<SaleReport> GetSaleReport(){
        String frmdt = frmDateTextView.getText().toString();
        String todt = toDateTextView.getText().toString();
        String waiter  = searchTxtView.getText().toString();
        ArrayList<SaleReport> sal = dbHelper.GetSalesReport(frmdt,todt,waiter);
        return sal;
    }

    private void LoadSaleReport(){
        try{
            ArrayList<SaleReport> items = GetSaleReport();
            int rc = gridLayout.getRowCount();
            if(rc>1){
                int count = (rc-1)*5;
                gridLayout.removeViews(5,count);
            }
            double totalSaleamt=0;
            for(int i=0;i<items.size();i++){
                SaleReport sr = items.get(i);
                dynamicView = new DynamicViewSaleReport(this);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm aaa",Locale.getDefault());
                gridLayout.addView(dynamicView.billNoTextView(this,sr.getBillNo()));
                gridLayout.addView(dynamicView.billDateTextView(this,format.format(sr.getBillDt())));
                String saleAmtStr = String.format("%.0f",sr.getBillAmount());
                gridLayout.addView(dynamicView.billAmountTextView(this,saleAmtStr));
                String billdetails = sr.getBillNo()+"~"+sr.getBillDate();
                gridLayout.addView(dynamicView.printButton(this,billdetails));
                gridLayout.addView(dynamicView.deleteButton(this,billdetails));
                totalSaleamt+=sr.getBillAmount();
            }
            String ttSaleAmtstring = String.format("%.0f",totalSaleamt);
            txtViewTotalSaleAmt.setText("₹ "+ttSaleAmtstring);
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }

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
        b.setCancelable(false);
        b.setCanceledOnTouchOutside(false);
        b.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btndpFrmDate:
                datePickerDialog.show();
                break;
            case R.id.btndpToDate:
                todatePickerDialog.show();
                break;
            case R.id.btngetsalereport:
                progressBar.show();
                LoadSaleReport();
                if(progressBar.isShowing())
                    progressBar.cancel();
                break;
            case R.id.btnshareExcel:
                ExportExcel();
                break;
            case R.id.btnsalerptprint:
                try{
                    ArrayList<SaleReport> items = GetSaleReport();
                    if(items.size()>0){
                        Common.saleReports = items;
                        Common.saleReportFrmDate = frmDateTextView.getText().toString();
                        Common.saleReportToDate = toDateTextView.getText().toString();
                        if(Common.isWifiPrint){
                            PrintWifi printWifi = new PrintWifi(SaleReportActivity.this,false);
                            printWifi.Print();
                        }
                        else{
                            PrintBluetooth printBluetooth = new PrintBluetooth(SaleReportActivity.this);
                            printBluetooth.PrintSaleReport();
                        }
                    }
                    else {
                        showCustomDialog("Info","No record found.");
                    }
                }
                catch (Exception ex){
                    showCustomDialog("Error",ex.getMessage().toString());
                }
        }
    }
    public void DeleteBill(String billdetail){
        try{
            String[] bd = billdetail.split("~");
            if(bd.length>1){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
                SimpleDateFormat formatwithtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
                Date billds = format.parse(bd[1]);
                dbHelper.DeleteBill(format.format(billds),bd[0]);
                showCustomDialog("MSG","Deleted bill sucessfully");
                LoadSaleReport();
            }
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }
    public void PrintBill(String billdetail){
        try{
            String[] bd = billdetail.split("~");
            if(bd.length>1){
                int billno = 0;
                String user = "";
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
                SimpleDateFormat formatwithtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
                Date billds = format.parse(bd[1]);
                Date billdt = formatwithtime.parse(bd[1]);
                ArrayList<Bills_Item> bills = dbHelper.GetBills_Item(format.format(billds),bd[0]);
                ArrayList<ItemsCart> itemsCarts = new ArrayList<>();
                for (Bills_Item bi:
                     bills) {
                    ItemsCart ic = new ItemsCart();
                    ic.setItem_No(bi.getItem_No());
                    ic.setItem_Name(bi.getItem_Name());
                    ic.setPrice(bi.getPrice());
                    ic.setQty((int) bi.getQty());
                    Item it = dbHelper.GetItem(bi.getItem_No());
                    if(it!=null){
                        ic.setMRP(it.getAcPrice());
                    }
                    user = bi.getWaiter();
                    billno = bi.getBill_No();
                    billdt = bi.getBill_Date();
                    itemsCarts.add(ic);
                }
                Common.billDate = billdt;
                Common.billNo = billno;
                Common.itemsCarts = itemsCarts;
                Common.waiter = user;
                if(Common.isWifiPrint){
                    PrintWifi printWifi = new PrintWifi(SaleReportActivity.this,true);
                    printWifi.onlyBill = true;
                    printWifi.Print();
                }
                else{
                    PrintBluetooth printBluetooth = new PrintBluetooth(SaleReportActivity.this);
                    printBluetooth.isReprint = true;
                    printBluetooth.Print();
                    printBluetooth.CloseBT();
                }
            }
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }
}