package com.example.vinoth.vgsposbt;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.icu.text.NumberFormat;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
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
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class SaleReportActivity extends AppCompatActivity implements View.OnClickListener {

    public static String billNoToDelete;
    private static Sheet sheet = null;
    private static String EXCEL_SHEET_NAME = "Sheet1";
    private static Workbook workbook =null;
    private static Cell cell=null;
    private static CellStyle headerCellStyle=null;
    private static SaleReportActivity instance;
    private static String billDateToDelete;
    TextView frmDateTextView;
    TextView toDateTextView;
    DatePickerDialog datePickerDialog;
    public  static ArrayList<String> itemNames;
    DatePickerDialog todatePickerDialog;
    String selectedDate;
    DatabaseHelper dbHelper;
    GridLayout gridLayout;
    DynamicViewSaleReport dynamicView;
    TextView txtViewTotalSaleAmt;
    TextView txtViewTotalQty;
    TextView searchTxtView;
    Dialog dialog;
    public static Integer headCount;
    ImageButton btnDeletAllSale;
    LinearLayout saleRptContainer;
    Button btnPrintReport;
    Button btnExportExcel;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;
    private Dialog progressBar;
    private Executor executor;
    private UsbManager usbManager;
    private UsbDevice usbDevice;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
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
                    LoadSaleReport();
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
                    LoadSaleReport();
                }
            };

    public static SaleReportActivity getInstance() {
        return instance;
    }

    public static boolean exportDataIntoWorkbook(Context context, String fileName,
                                                 ArrayList<SaleReport> dataList,String frmDt,String toDt) {
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
        sheet.setColumnWidth(0, (15 * 200));
        sheet.setColumnWidth(1, (15 * 200));
        sheet.setColumnWidth(2, (15 * 200));
        sheet.setColumnWidth(3, (15 * 200));
        int prlist = itemNames.size();
        int k=4;
        for(int i=0;i<prlist;i++){
            sheet.setColumnWidth(k,(15*200));
            k++;
        }
        sheet.setColumnWidth(k,(15*300));
        setHeaderRow();
        fillDataIntoExcel(dataList,frmDt,toDt);
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
        cell.setCellValue("Zone");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("Ward");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("Supplier");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(3);
        cell.setCellValue("Supplier ID");
        cell.setCellStyle(headerCellStyle);
        int k=4;
        for(int i=0;i<itemNames.size();i++){
            cell = row.createCell(k);
            cell.setCellValue(itemNames.get(i));
            cell.setCellStyle(headerCellStyle);
            k++;
        }
        cell = row.createCell(k);
        cell.setCellValue("TOTAL QTY: KGS/NOS: ");
        cell.setCellStyle(headerCellStyle);
    }

    /**
     * Fills Data into Excel Sheet
     * <p>
     * NOTE: Set row index as i+1 since 0th index belongs to header row
     *
     * @param dataList - List containing data to be filled into excel
     */
    private static void fillDataIntoExcel(ArrayList<SaleReport> dataList,String frmDt,String toDt) {
        DecimalFormat amtFormat = new DecimalFormat("#.###");
        for (int i = 0; i < dataList.size(); i++) {
            // Create a New Row for every new entry in list
            Row rowData = sheet.createRow(i + 1);

            cell = rowData.createCell(0);
            cell.setCellValue(dataList.get(i).zone);
            // Create Cells for each row
            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).ward);

            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).CustomerName);

            cell = rowData.createCell(3);
            cell.setCellValue(dataList.get(i).CustomerID);

            Map<String,Double> itemwiseqty = dataList.get(i).itemwiseqty;

            int k=4;
            for (String name:
                 itemwiseqty.keySet()) {
                cell = rowData.createCell(k);
                String q = amtFormat.format(itemwiseqty.get(name));
                cell.setCellValue(q);
                k++;
            }
            cell = rowData.createCell(k);
            String ttwt = amtFormat.format(dataList.get(i).totalWt);
            cell.setCellValue(ttwt+"/0");
        }
        Collection<Map<String,Double>> myCol = dataList.stream().map(d->d.itemwiseqty).collect(Collectors.toList());
        Map<String,Double> itemconsolidated = myCol.stream().flatMap(m->m.entrySet().stream()).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,Double::sum));
        TreeMap<String,Double> sortedmap = new TreeMap<>(itemconsolidated);
        int newrowindex = dataList.size()+2;
        Row rowData = sheet.createRow(newrowindex);
        cell = rowData.createCell(0);
        cell.setCellValue("TOTAL QTY: ");

        cell = rowData.createCell(1);
        cell.setCellValue(" ");

        cell = rowData.createCell(2);
        cell.setCellValue(" ");
        int k=3;
        for (String name:
                sortedmap.keySet()) {
            cell = rowData.createCell(k);
            String q = amtFormat.format(sortedmap.get(name));
            cell.setCellValue(q);
            k++;
        }
        newrowindex++;
        Row newRow = sheet.createRow(newrowindex);
        cell = newRow.createCell(0);
        cell.setCellValue("-----------------------");
        newrowindex++;
        newRow = sheet.createRow(newrowindex);
        cell = newRow.createCell(0);
        Double totalSum = dataList.stream().mapToDouble(m->m.totalWt).sum();
        String ttwt = amtFormat.format(totalSum);
        cell.setCellValue("TOTAL QTY: KGS/NOS: "+ttwt+"/0");
        newrowindex++;
        newRow = sheet.createRow(newrowindex);
        cell = newRow.createCell(0);
        String headcount = amtFormat.format(headCount);
        cell.setCellValue("HEAD COUNT: "+headcount);
        newrowindex++;
        newRow = sheet.createRow(newrowindex);
        cell = newRow.createCell(0);
        cell.setCellValue("-----------------------");
        newrowindex++;
        newRow = sheet.createRow(newrowindex);
        cell = newRow.createCell(0);
        cell.setCellValue("From: "+frmDt+" To: "+toDt);
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
    public void onBackPressed(){
        Common.waiter = "";
        super.onBackPressed();
    }
    private  void GetDefaultDate(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
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
        btnPrintReport =  findViewById(R.id.btnsalerptprint);
        btnExportExcel = findViewById(R.id.btnshareExcel);
        btnDeletAllSale = findViewById(R.id.btnDeleteAllSale);
        saleRptContainer = (LinearLayout) findViewById(R.id.saleprtContainer);
        txtViewTotalSaleAmt = (TextView)findViewById(R.id.totalSaleAmt);
        txtViewTotalQty = findViewById(R.id.saleRptTQty);
        frmDateTextView.setOnClickListener(this);
        toDateTextView.setOnClickListener(this);
        btnPrintReport.setOnClickListener(this);
        btnExportExcel.setOnClickListener(this);
        btnDeletAllSale.setOnClickListener(this);
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
        itemNames = dbHelper.GetItemNames();
        LoadSaleReport();
        searchTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize dialog
                dialog=new Dialog(SaleReportActivity.this);

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
                        LoadSaleReport();

                    }
                });
            }
        });
        GetDefaultDate();
        datePickerDialog = new DatePickerDialog(SaleReportActivity.this,myDateListener,year,month,day);
        todatePickerDialog = new DatePickerDialog(SaleReportActivity.this,mytoDateListener,year,month,day);
        instance = this;
        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(SaleReportActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                if(!billDateToDelete.isEmpty() && !billNoToDelete.isEmpty()){
                    DeleteBill();
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication MPOS")
                .setSubtitle("Please Authenticate to Delete Bill")
                .setNegativeButtonText("Use account password")
                .build();

    }

    private void ExportExcel(){
        ArrayList<SaleReport> sal = GetSaleReport();
        SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aa", Locale.getDefault());
        Date date = new Date();
        String fileName = "SaleReport_"+format.format(date)+".xls";
        String frmDt = frmDateTextView.getText().toString();
        String toDt = toDateTextView.getText().toString();
        if(exportDataIntoWorkbook(getApplicationContext(),fileName,sal,frmDt,toDt)){
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
    private void addCard(SaleReport sr){
        View view = getLayoutInflater().inflate(R.layout.cart,null);
        TextView id = view.findViewById(R.id.salecart_customerid);
        TextView name = view.findViewById(R.id.salecart_customerName);
        TextView ttqt = view.findViewById(R.id.salecart_totalQt);
        ImageButton printbtn = view.findViewById(R.id.salecart_print);
        ImageButton deletebtn = view.findViewById(R.id.salecart_delete);
        id.setText(sr.CustomerID);
        name.setText(sr.CustomerName);
        DecimalFormat decimalFormat = new DecimalFormat("0.000");
        String totalqtystring = decimalFormat.format(sr.totalWt);
        ttqt.setText(totalqtystring);
        String btnTag =  sr.getBillNo()+"~"+sr.getBillDate();
        printbtn.setTag(btnTag);
        deletebtn.setTag(btnTag);
        printbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String billNo = (String)v.getTag();
                PrintBill(billNo);
            }
        });
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String billNo = (String)v.getTag();
                DeleteBill(billNo);
            }
        });
        saleRptContainer.addView(view);
    }
    private ArrayList<SaleReport> GetSaleReport(){
        String frmdt = frmDateTextView.getText().toString();
        String todt = toDateTextView.getText().toString();
        String waiter  = searchTxtView.getText().toString();
        ArrayList<SaleReport> sal = dbHelper.GetSalesReport(frmdt,todt,waiter);
        sal.sort(Comparator.comparing(SaleReport::getWard));
        headCount = dbHelper.GetHeadCount(frmdt,todt);
        return sal;
    }
    public static Float convertToFloat(double doubleValue) {
        return (float) doubleValue;
    }

    private void LoadSaleReport(){
        try{
            saleRptContainer.removeAllViews();
            ArrayList<SaleReport> items = GetSaleReport();
            double totalSaleamt=0;
            double ttQty = 0d;
            for(int i=0;i<items.size();i++){
                SaleReport sr = items.get(i);
                addCard(sr);
                totalSaleamt+=sr.getBillAmount();
                ttQty+=sr.totalWt;
            }
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            formatter.setMaximumFractionDigits(0);
            String symbol = formatter.getCurrency().getSymbol();
            String ttSaleAmtstring = formatter.format(totalSaleamt).replace(symbol,symbol+" ");
            txtViewTotalSaleAmt.setText(ttSaleAmtstring);
            DecimalFormat decimalFormat = new DecimalFormat("0.000");
            txtViewTotalQty.setText(decimalFormat.format(ttQty));

            //setupPieChart(cashAmt,cardAmt,upiAmt);
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
            case R.id.salerptFrmDate:
                datePickerDialog.show();
                break;
            case R.id.salerptToDate:
                todatePickerDialog.show();
                break;
            case R.id.btnDeleteAllSale:
                dbHelper.DeleteBill();
                Toast.makeText(this,"Deleted All Sale Entries..",Toast.LENGTH_SHORT).show();
                LoadSaleReport();
                HomeActivity.getInstance().RefreshBillNo();
                break;
            case R.id.btnshareExcel:
                ExportExcel();
                break;
            case R.id.btnsalerptprint:
                try{
                    ArrayList<SaleReport> items = GetSaleReport();
                    if(items.size()>0){
                        Common.saleReportFrmDate = frmDateTextView.getText().toString();
                        Common.saleReportToDate = toDateTextView.getText().toString();
                        PrinterUtil printerUtil = new PrinterUtil(SaleReportActivity.this,this,false);
                        printerUtil.saleReports = items;
                        printerUtil.headCount = headCount;
                        printerUtil.Print();
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
    public  void DeleteBill(){
        try{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
            Date billds = format.parse(billDateToDelete);
            dbHelper.DeleteBill(format.format(billds),billNoToDelete);
            Toast.makeText(getApplicationContext(),"Bill no: "+billNoToDelete+" has been deleted.",Toast.LENGTH_LONG).show();
            LoadSaleReport();
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }
    public void DeleteBill(String billdetail){
        try{
            String[] bd = billdetail.split("~");
            if(bd.length>1){
                billDateToDelete = bd[1];
                billNoToDelete = bd[0];
                PasscodeActivity.srpInstance= this;
                PasscodeActivity.isUserPasscode = true;
                Intent intendt = new Intent(this,PasscodeActivity.class);
                startActivityForResult(intendt,123);
            }
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 123:
                if (Common.isAuthenticated) {
                    DeleteBill();
                }
                break;
        }
    }
    public void PrintBill(String billdetail){
        try{
            String[] bd = billdetail.split("~");
            if(bd.length>1){
                ReceiptData receiptData = new ReceiptData();
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
                    ic.setQty(bi.getQty());
                    ic.BinWeight = bi.BinWeight;
                    ic.setDeduction(bi.getDeduction());
                    Item it = dbHelper.GetItem(bi.getItem_Name());
                    user = bi.getWaiter();
                    billno = bi.getBill_No();
                    billdt = bi.getBill_Date();
                    itemsCarts.add(ic);
                }
                receiptData.billDate = billdt;
                receiptData.billno = billno;
                receiptData.itemsCarts = itemsCarts;
                Customer cu = dbHelper.GetCustomer(user);
                if(cu!=null){
                    receiptData.selectedCustomer = cu;
                }
                receiptData.waiter = user;
                PrinterUtil printerUtil = new PrinterUtil(SaleReportActivity.this,this,true);
                printerUtil.receiptData = receiptData;
                printerUtil.onlyBill = true;
                printerUtil.Print();
            }
        }
        catch (Exception ex){
            showCustomDialog("Error",ex.getMessage());
        }
    }

}