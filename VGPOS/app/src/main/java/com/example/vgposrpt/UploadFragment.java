package com.example.vgposrpt;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.DOWNLOAD_SERVICE;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.icu.text.NumberFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;


public class UploadFragment extends Fragment implements View.OnClickListener {


    public static final int requestcode = 1;
    private static String EXCEL_SHEET_NAME = "Sheet1";
    private static Workbook workbook =null;
    private static Cell cell=null;
    private static CellStyle headerCellStyle=null;
    private static Sheet sheet = null;

    static {
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLInputFactory",
                "com.fasterxml.aalto.stax.InputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLOutputFactory",
                "com.fasterxml.aalto.stax.OutputFactoryImpl"
        );
        System.setProperty(
                "org.apache.poi.javax.xml.stream.XMLEventFactory",
                "com.fasterxml.aalto.stax.EventFactoryImpl"
        );
    }


    Button btnUpload;
    Button btnDownload;
    LinearLayout prMasterView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Context cnt = getContext();
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        btnUpload = view.findViewById(R.id.btnUpload);
        btnDownload = view.findViewById(R.id.btnDownload);
        prMasterView = view.findViewById(R.id.prMaster);
        btnUpload.setOnClickListener(this);
        btnDownload.setOnClickListener(this);

        LoadPRView(CommonUtil.productsFull);
        return view;
    }


    private void LoadPRView(ArrayList<Product> products){
        prMasterView.removeAllViews();
        for (Product pr : products) {
            View view = getLayoutInflater().inflate(R.layout.product,null);
            TextView prName = view.findViewById(R.id.prMasterItemName);
            prName.setText(pr.getProductName());
            TextView prPrice = view.findViewById(R.id.prMasterItemPrice);
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
            formatter.setMaximumFractionDigits(0);
            String symbol = formatter.getCurrency().getSymbol();
            prPrice.setText(formatter.format(pr.getPrice()).replace(symbol,symbol+" "));
            Button editBtn = view.findViewById(R.id.prMasterBtnEdit);
            editBtn.setTag(pr.getProductID());
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //
                }
            });
            prMasterView.addView(view);
        }

    }

    public void showCustomDialog(String title,String Message) {
        AlertDialog.Builder dialog =  new AlertDialog.Builder(getContext());
        dialog.setTitle(title);
        dialog.setMessage("\n"+Message);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

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
        cell.setCellValue("PRODUCT_ID");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(1);
        cell.setCellValue("PRODUCT_NAME");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(2);
        cell.setCellValue("CATEGORY");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(3);
        cell.setCellValue("PURCHASE_PRICE");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(4);
        cell.setCellValue("MRP");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(5);
        cell.setCellValue("SELLING_PRICE");
        cell.setCellStyle(headerCellStyle);

        cell = row.createCell(6);
        cell.setCellValue("STOCKS");
        cell.setCellStyle(headerCellStyle);
    }

    /**
     * Fills Data into Excel Sheet
     * <p>
     * NOTE: Set row index as i+1 since 0th index belongs to header row
     *
     * @param dataList - List containing data to be filled into excel
     */
    private static void fillDataIntoExcel(ArrayList<Product> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            // Create a New Row for every new entry in list
            Row rowData = sheet.createRow(i + 1);

            // Create Cells for each row
            cell = rowData.createCell(0);
            cell.setCellValue(dataList.get(i).getProductID());

            cell = rowData.createCell(1);
            cell.setCellValue(dataList.get(i).getProductName());

            cell = rowData.createCell(2);
            cell.setCellValue(dataList.get(i).getCategory());

            cell = rowData.createCell(3);
            cell.setCellValue(dataList.get(i).getPurchasedPrice());

            cell = rowData.createCell(4);
            cell.setCellValue(dataList.get(i).getMRP());
            cell = rowData.createCell(5);
            cell.setCellValue(dataList.get(i).getPrice());
            cell = rowData.createCell(6);
            cell.setCellValue(dataList.get(i).getStocks());
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

    public static boolean exportDataIntoWorkbook(Context context, String fileName,
                                                 ArrayList<Product> dataList) {
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
        sheet.setColumnWidth(1, (15 * 300));
        sheet.setColumnWidth(2, (15 * 200));
        sheet.setColumnWidth(3, (15 * 200));
        sheet.setColumnWidth(4, (15 * 200));
        sheet.setColumnWidth(5, (15 * 200));
        sheet.setColumnWidth(6, (15 * 200));

        setHeaderRow();
        fillDataIntoExcel(dataList);
        isWorkbookWrittenIntoStorage = storeExcelInStorage(context, fileName);

        return isWorkbookWrittenIntoStorage;
    }

    public static String getPath(Context context, Uri uri) {
        try {

            /*File filen = new File(uri.getPath());//create path from uri
            final String[] splitn = filen.getPath().split(":");//split the path.
            return  splitn[1];//assign it to a string(your choice).*/
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    String fileName = getFilePath(context, uri);
                    if (fileName != null) {
                        return Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Download/" + fileName;
                    }

                    String id = DocumentsContract.getDocumentId(uri);
                    if (id.startsWith("raw:")) {
                        id = id.replaceFirst("raw:", "");
                        File file = new File(id);
                        if (file.exists())
                            return id;
                    }

                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    else if("document".equals(type)){
                        String fileName = getFilePath(context, uri);
                        if (fileName != null) {
                            String rootpath = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
                            return rootpath + "/Documents/" + fileName;
                        }

                        String id = DocumentsContract.getDocumentId(uri);
                        if (id.startsWith("raw:")) {
                            id = id.replaceFirst("raw:", "");
                            File file = new File(id);
                            if (file.exists())
                                return id;
                        }

                        final Uri contentUris = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                        return getDataColumn(context, contentUris, null, null);
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                    // Return the remote address
                    if (isGooglePhotosUri(uri))
                        return uri.getLastPathSegment();
                    return uri.getPath();
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();
                return getDataColumn(context, uri, null, null);
            }

            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }

            return null;
        }
        catch (Exception ex){
            Toast.makeText(context.getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            return  null;
        }
    }

    public static String getFilePath(Context context, Uri uri) {

        Cursor cursor = null;
        final String[] projection = {
                MediaStore.MediaColumns.DISPLAY_NAME
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnUpload:
                UploadExcel();
                break;
            case R.id.btnDownload:
                new ExportProducts().execute("");
                break;
        }
    }
    private void UploadExcel(){
        try {
            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
            fileintent.setType("*/*");
            startActivityForResult(fileintent, requestcode);
        } catch (Exception ex) {
            showCustomDialog("Error",ex.getMessage());
        }
    }
    private boolean isDouble(String val){
        try{
            Double d = Double.parseDouble(val);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }
    private boolean isNumeric(String val){
        try{
            Integer integer = Integer.parseInt(val);
            return true;
        }
        catch (Exception ex){
            return false;
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case requestcode:
                String FilePath = getPath(getContext(),data.getData());
                Log.e("File Path",FilePath);
                if(FilePath.contains("/root_path")){
                    FilePath = FilePath.replace("/root_path","");
                }
                Log.e("File Path",FilePath);
                try {
                    if (resultCode == RESULT_OK) {
                        AssetManager am = getActivity().getAssets();
                        InputStream inStream;
                        Workbook wb = null;
                        try {
                            inStream = new FileInputStream(FilePath);
                            Log.e("Extension",FilePath.substring(FilePath.lastIndexOf(".")));
                            if(FilePath.substring(FilePath.lastIndexOf(".")).equals(".xls")){
                                Log.e("File Type","Selected file is XLS");
                                wb = new HSSFWorkbook(inStream);
                            }
                            else if(FilePath.substring(FilePath.lastIndexOf(".")).equals(".xlsx")){
                                Log.e("File Type","Selected file is XLSX");
                                wb = new XSSFWorkbook(inStream);
                            }
                            else{
                                wb = null;
                                showCustomDialog("Status","Please select valid Excel file.");
                                return;
                            }
                            inStream.close();
                            Sheet sheet = wb.getSheetAt(0);
                            ArrayList<Product> products = new ArrayList<>();
                            for(Iterator<Row> rit = sheet.rowIterator(); rit.hasNext();){
                                Product pr = new Product();
                                Row row = rit.next();
                                row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(2,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(3,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(4,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(5,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                row.getCell(6,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellType(CellType.STRING);
                                String prid = row.getCell(0,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                if(prid.equalsIgnoreCase("PRODUCT_ID")){
                                    continue;
                                }
                                pr.setProductID(prid);
                                pr.setProductName(row.getCell(1,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                                pr.setCategory(row.getCell(2,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
                                String ppstr = row.getCell(3,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                pr.setPurchasedPrice(isDouble(ppstr) ? Double.parseDouble(ppstr):0);

                                String mrpstr = row.getCell(4,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                pr.setMRP(isDouble(mrpstr) ? Double.parseDouble(mrpstr):0);
                                String sellingstr = row.getCell(5,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                pr.setPrice(isDouble(sellingstr) ? Double.parseDouble(sellingstr):0);
                                String stockstr = row.getCell(6,Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
                                pr.setStocks(isDouble(stockstr) ? Double.parseDouble(stockstr):0);
                                pr.setBranchCode(1);
                                products.add(pr);
                            }
                            new ImportProducts().execute(products);
                        } catch (IOException e) {
                            showCustomDialog("Status",e.getMessage());
                            e.printStackTrace();
                        }

                    }
                } catch (Exception ex) {
                    showCustomDialog("Status",ex.getMessage());
                    ex.printStackTrace();
                    //lbl.setText(ex.getMessage().toString());
                }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }


    public class ImportProducts extends AsyncTask<ArrayList<Product>,String, ArrayList<Product>>
    {
        int branchCode = CommonUtil.defBranch;
        Boolean isSuccess = false;
        String error = "";
        ConnectionClass connectionClass = null;
        Connection con = null;
        private final ProgressDialog dialog = new ProgressDialog(getContext(),R.style.CustomProgressStyle);

        @Override
        protected void onPreExecute() {
            connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
            con = connectionClass.CONN();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setIcon(R.mipmap.salesreport);
            dialog.setMessage("Uploading Products...");
            dialog.show();
            super.onPreExecute();
        }

        private ArrayList<Product> GetAllProducts(){
            ArrayList<Product> prlist = new ArrayList<>();
            try{
                Statement stmt = con.createStatement();
                String productquery = "SELECT P.PPWT,P.MRP,P.BRANCH_CODE,P.PRODUCT_ID,P.PRODUCT_DESCRIPTION,P.SELLING_PRICE,C.Category_Name,s.Supplier_Name,st.available_quantity as av FROM PRODUCTS p,\n" +
                        "(SELECT S.AVAILABLE_QUANTITY,A.* FROM Stocks S,(SELECT PRODUCT_ID,BRANCH_CODE,MAX(UPDATED_DATE) AS DT FROM \n" +
                        "STOCKS GROUP BY PRODUCT_ID,BRANCH_CODE) A WHERE S.PRODUCT_ID=A.PRODUCT_ID AND A.BRANCH_CODE=S.BRANCH_CODE AND \n" +
                        "S.UPDATED_DATE=A.DT) st,Category c,Suppliers s where c.Category_ID=p.CATEGORY_ID and \n" +
                        "s.Supplier_ID=p.SUPPLIER_ID and st.product_id=p.product_id and \n" +
                        "st.branch_code=p.branch_code AND P.BRANCH_CODE="+branchCode;
                ResultSet productResults = stmt.executeQuery(productquery);
                while (productResults.next())
                {
                    Product pr = new Product();
                    pr.setBranchCode(productResults.getInt("BRANCH_CODE"));
                    pr.setProductID(productResults.getString("PRODUCT_ID"));
                    pr.setProductName(productResults.getString("PRODUCT_DESCRIPTION"));
                    pr.setPrice(productResults.getDouble("SELLING_PRICE"));
                    pr.setMRP(productResults.getDouble("MRP"));
                    pr.setPurchasedPrice(productResults.getDouble("PPWT"));
                    pr.setCategory(productResults.getString("Category_Name"));
                    pr.setSupplierName(productResults.getString("Supplier_Name"));
                    pr.setStocks(productResults.getDouble("AV"));
                    if(pr.getCategory().isEmpty()){
                        pr.setCategory("GENERAL");
                    }
                    if(pr.getSupplierName().isEmpty()){
                        pr.setSupplierName("OWN");
                    }
                    prlist.add(pr);
                }
                productResults.close();
                prlist.forEach(p->{
                    try{
                        String query = "SELECT BATCH_NO FROM PRODUCT_BATCH_DETAILS WHERE BRANCH_CODE="+p.getBranchCode()+" AND PRODUCT_ID='"+p.getProductID()+"' AND AVAILABLE_QUANTITY>0";
                        ResultSet r = stmt.executeQuery(query);
                        if(r.next()){
                            p.setBatchNo(r.getString("BATCH_NO"));
                        }
                        r.close();
                        if(p.getBatchNo().isEmpty()){
                            p.setBatchNo(String.valueOf(p.getMRP()));
                        }
                    }
                    catch (Exception e){

                    }

                });
            }
            catch (Exception ex){

            }
            return prlist;
        }

        @Override
        protected void onPostExecute(ArrayList<Product> prlist) {
            if(!isSuccess){
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Error",error);
            }
            else {
                showCustomDialog("Status",String.format("Successfully Uploaded %d Products.",prlist.size()));
                ArrayList<Product> prs = GetAllProducts();
                CommonUtil.productsFull = prs;
                LoadPRView(prs);
            }
            if(dialog.isShowing()){
                dialog.hide();
            }
        }
        private Product GetProduct(String productid){
            Product pr = null;
            try{
                Statement stmt = con.createStatement();
                String productquery = "SELECT P.PPWT,P.MRP,P.BRANCH_CODE,P.PRODUCT_ID,P.PRODUCT_DESCRIPTION,P.SELLING_PRICE,C.Category_Name,s.Supplier_Name,st.available_quantity as av FROM PRODUCTS p,\n" +
                        "(SELECT S.AVAILABLE_QUANTITY,A.* FROM Stocks S,(SELECT PRODUCT_ID,BRANCH_CODE,MAX(UPDATED_DATE) AS DT FROM \n" +
                        "STOCKS GROUP BY PRODUCT_ID,BRANCH_CODE) A WHERE S.PRODUCT_ID=A.PRODUCT_ID AND A.BRANCH_CODE=S.BRANCH_CODE AND \n" +
                        "S.UPDATED_DATE=A.DT) st,Category c,Suppliers s where c.Category_ID=p.CATEGORY_ID and \n" +
                        "s.Supplier_ID=p.SUPPLIER_ID and st.product_id=p.product_id and \n" +
                        "st.branch_code=p.branch_code AND P.BRANCH_CODE="+branchCode+ " AND P.PRODUCT_ID='"+productid+"'";
                ResultSet productResults = stmt.executeQuery(productquery);
                while (productResults.next())
                {
                    pr = new Product();
                    pr.setBranchCode(productResults.getInt("BRANCH_CODE"));
                    pr.setProductID(productResults.getString("PRODUCT_ID"));
                    pr.setProductName(productResults.getString("PRODUCT_DESCRIPTION"));
                    pr.setPrice(productResults.getDouble("SELLING_PRICE"));
                    pr.setMRP(productResults.getDouble("MRP"));
                    pr.setPurchasedPrice(productResults.getDouble("PPWT"));
                    pr.setCategory(productResults.getString("Category_Name"));
                    pr.setSupplierName(productResults.getString("Supplier_Name"));
                    pr.setStocks(productResults.getDouble("AV"));
                    if(pr.getCategory().isEmpty()){
                        pr.setCategory("GENERAL");
                    }
                    if(pr.getSupplierName().isEmpty()){
                        pr.setSupplierName("OWN");
                    }

                }
                productResults.close();
            }
            catch (Exception ex){

            }
            return pr;
        }
        private  Double GetAvailableQty(String productID,Integer branchCode) throws SQLException {
            Double qty = 0d;
            String query = "SELECT S.AVAILABLE_QUANTITY FROM Stocks S,(SELECT PRODUCT_ID,BRANCH_CODE,MAX(UPDATED_DATE) AS DT FROM STOCKS GROUP BY PRODUCT_ID,BRANCH_CODE) A WHERE S.PRODUCT_ID=A.PRODUCT_ID AND A.BRANCH_CODE=S.BRANCH_CODE AND S.UPDATED_DATE=A.DT and A.Product_ID='"+productID+"' AND A.BRANCH_CODE="+branchCode;
            Statement stmt = con.createStatement();
            ResultSet s = stmt.executeQuery(query);
            if(s.next()){
                qty = s.getDouble("AVAILABLE_QUANTITY");
            }
            return qty;
        }
        private boolean UpdateStocks(Product pr) throws SQLException {
            boolean isDone = false;
            Double avQty = GetAvailableQty(pr.getProductID(),pr.getBranchCode());
            Double avNow = avQty+pr.getStocks();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            Date date = new Date();
            String upddt = format.format(date);
            String query = "INSERT INTO STOCKS VALUES ('"+pr.getProductID()+"',"+avNow+","+pr.getStocks()+",0,'"+upddt+"',"+pr.getBranchCode()+")";
            Statement stmt = con.createStatement();
            Integer rowAff = stmt.executeUpdate(query);
            isDone = rowAff>0;
            return  isDone;
        }
        private int GetCategoryID(String category){
            int catid = 0;
            try{
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM CATEGORY WHERE UPPER(Category_Name)='%1s'",category.toUpperCase()));
                while (rs.next())
                {
                    catid = rs.getInt("CATEGORY_ID");
                }
                rs.close();
                if(catid==0){
                    ResultSet rss = stmt.executeQuery("SELECT MAX(CATEGORY_ID) AS CATEGORY_ID FROM CATEGORY");
                    while (rss.next())
                    {
                        catid = rss.getInt("CATEGORY_ID");
                    }
                    rss.close();
                    catid++;
                    stmt.executeUpdate(String.format("INSERT INTO CATEGORY VALUES (%1d,'%2s',1)",catid,category));
                }
            }
            catch (Exception ex)
            {
                Log.e("ERROR",ex.getMessage());
            }
            return catid;
        }

        @Override
        protected ArrayList<Product> doInBackground(ArrayList<Product>... params) {
            ArrayList<Product> prds = params[0];
            ArrayList<Product> products = new ArrayList<>();
            try {
                if (con == null) {
                    error = "Database Connection Failed";
                } else {
                    for (Product p : prds) {
                        try {
                            int rowAffected = 0;
                            Product product = GetProduct(p.getProductID());
                            int categoryid = GetCategoryID(p.getCategory());
                            Statement stmt = con.createStatement();
                            String query = String.format("INSERT INTO PRODUCTS VALUES ('%s','%s',%f,%f,%f,'PCS',1,0,1,%f,0,0,%f,GETDATE(),'%s',1,0,0,10,1,%d,0,0,N'%s',N'%s',' ',0,%d,' ')",
                                    p.getProductID(), p.getProductName(), p.getMRP(), p.getPrice(), p.getPurchasedPrice(), p.getPurchasedPrice(), p.getPrice(), CommonUtil.loggedinUser, categoryid, p.getProductName(), p.getProductName(), branchCode);
                            if (product != null) {
                                query = String.format("UPDATE PRODUCTS SET PRODUCT_DESCRIPTION='%s',MRP=%f,SELLING_PRICE=%f,PURCHASED_PRICE=%f,PPWT=%f,CATEGORY_ID=%d WHERE PRODUCT_ID='%s'",
                                        p.getProductName(), p.getMRP(), p.getPrice(), p.getPurchasedPrice(), p.getPurchasedPrice(), categoryid, p.getProductID());
                                stmt.executeUpdate(query);
                                query = String.format("UPDATE P SET P.PURCHASED_PRICE=%f,P.PPWT=(%f*(T.TAX_VALUE/100))+%f FROM PRODUCTS P,TAX_INFORMATION T WHERE P.TAX_ID=T.TAX_ID AND P.PRODUCT_ID='%s'", p.getPurchasedPrice(), p.getPurchasedPrice(), p.getPurchasedPrice(), p.getProductID());
                            }
                            rowAffected = stmt.executeUpdate(query);
                            isSuccess = rowAffected > 0;
                            if (isSuccess) {
                                UpdateStocks(p);
                                products.add(p);
                            }
                        } catch (Exception ex) {
                            error = ex.getMessage();
                            break;
                        }
                    }
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions:"+ex.getMessage();
            }
            return prds;
        }
    }

    public class ExportProducts extends AsyncTask<String,String, ArrayList<Product>>
    {
        int branchCode = CommonUtil.defBranch;
        Boolean isSuccess = false;
        String error = "";
        ConnectionClass connectionClass = null;
        Connection con = null;
        private final ProgressDialog dialog = new ProgressDialog(getContext(),R.style.CustomProgressStyle);

        @Override
        protected void onPreExecute() {
            connectionClass = new ConnectionClass(CommonUtil.SQL_SERVER,CommonUtil.DB,CommonUtil.USERNAME,CommonUtil.PASSWORD);
            con = connectionClass.CONN();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setIcon(R.mipmap.salesreport);
            dialog.setMessage("Downloading Products...");
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<Product> prlist) {
            if(!isSuccess){
                if(dialog.isShowing()){
                    dialog.hide();
                }
                showCustomDialog("Error",error);
            }
            else {
                String fileName = "Products.xls";
                if(exportDataIntoWorkbook(getContext(),fileName,prlist)){
                    try{
                        File file = new File(getContext().getExternalFilesDir(DOWNLOAD_SERVICE), fileName);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = FileProvider.getUriForFile(getContext(), "com.example.vgposrpt.provider",file);
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
            if(dialog.isShowing()){
                dialog.hide();
            }
        }


        @Override
        protected ArrayList<Product> doInBackground(String... params) {
            ArrayList<Product> prds = new ArrayList<>();
            try {
                if (con == null) {
                    error = "Database Connection Failed";
                } else {
                    Statement stmt = con.createStatement();
                    String productquery = "SELECT P.PPWT,P.MRP,P.BRANCH_CODE,P.PRODUCT_ID,P.PRODUCT_DESCRIPTION,P.SELLING_PRICE,C.Category_Name,s.Supplier_Name,st.available_quantity as av FROM PRODUCTS p,\n" +
                            "(SELECT S.AVAILABLE_QUANTITY,A.* FROM Stocks S,(SELECT PRODUCT_ID,BRANCH_CODE,MAX(UPDATED_DATE) AS DT FROM \n" +
                            "STOCKS GROUP BY PRODUCT_ID,BRANCH_CODE) A WHERE S.PRODUCT_ID=A.PRODUCT_ID AND A.BRANCH_CODE=S.BRANCH_CODE AND \n" +
                            "S.UPDATED_DATE=A.DT) st,Category c,Suppliers s where c.Category_ID=p.CATEGORY_ID and \n" +
                            "s.Supplier_ID=p.SUPPLIER_ID and st.product_id=p.product_id and \n" +
                            "st.branch_code=p.branch_code AND P.BRANCH_CODE="+branchCode;
                    ResultSet productResults = stmt.executeQuery(productquery);
                    while (productResults.next())
                    {
                        Product pr = new Product();
                        pr.setBranchCode(productResults.getInt("BRANCH_CODE"));
                        pr.setProductID(productResults.getString("PRODUCT_ID"));
                        pr.setProductName(productResults.getString("PRODUCT_DESCRIPTION"));
                        pr.setPrice(productResults.getDouble("SELLING_PRICE"));
                        pr.setMRP(productResults.getDouble("MRP"));
                        pr.setPurchasedPrice(productResults.getDouble("PPWT"));
                        pr.setCategory(productResults.getString("Category_Name"));
                        pr.setSupplierName(productResults.getString("Supplier_Name"));
                        pr.setStocks(productResults.getDouble("AV"));
                        if(pr.getCategory().isEmpty()){
                            pr.setCategory("GENERAL");
                        }
                        if(pr.getSupplierName().isEmpty()){
                            pr.setSupplierName("OWN");
                        }
                        prds.add(pr);
                    }
                    productResults.close();
                    prds.forEach(p->{
                        try{
                            String query = "SELECT BATCH_NO FROM PRODUCT_BATCH_DETAILS WHERE BRANCH_CODE="+p.getBranchCode()+" AND PRODUCT_ID='"+p.getProductID()+"' AND AVAILABLE_QUANTITY>0";
                            ResultSet r = stmt.executeQuery(query);
                            if(r.next()){
                                p.setBatchNo(r.getString("BATCH_NO"));
                            }
                            r.close();
                            if(p.getBatchNo().isEmpty()){
                                p.setBatchNo(String.valueOf(p.getMRP()));
                            }
                        }
                        catch (Exception e){

                        }

                    });
                    isSuccess = true;
                }
            }
            catch (Exception ex)
            {
                isSuccess = false;
                error = "Exceptions"+ex.getMessage();
            }
            return prds;
        }
    }
}