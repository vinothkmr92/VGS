package com.imin.printer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {

    /**
     * 获取当前系统的语言环境
     *
     * @param context
     * @return boolean
     */
    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    /**
     * 获取Assets子文件夹下的文件数据流数组InputStream[]
     *
     * @param context
     * @return InputStream[]
     */
    @SuppressWarnings("unused")
    private static InputStream[] getAssetsImgaes(String imgPath, Context context) {
        String[] list = null;
        InputStream[] arryStream = null;
        try {
            list = context.getResources().getAssets().list(imgPath);
            arryStream = new InputStream[3];
            for (int i = 0; i < list.length; i++) {
                InputStream is = context.getResources().getAssets()
                        .open(imgPath + File.separator + list[i]);
                arryStream[i] = is;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arryStream;
    }

    /*
     * 未转换为十六进制字节的字符串
     *
     * @param paramString
     *
     * @return byte[]
     */
    public static byte[] hexStr2Bytesnoenter(String paramString) {
        String[] paramStr = paramString.split(" ");
        byte[] arrayOfByte = new byte[paramStr.length];

        for (int j = 0; j < paramStr.length; j++) {
            arrayOfByte[j] = Integer.decode("0x" + paramStr[j]).byteValue();
        }
        return arrayOfByte;
    }

    public static byte[] getHexCmd(String paramString) {
        String[] paramStr = paramString.split(" ");
        byte[] arrayOfByte = new byte[paramStr.length];

        for (int j = 0; j < paramStr.length; j++) {
            if (String.valueOf(arrayOfByte[j]).length() == 4) {
                byte d1 = Byte.valueOf(String.valueOf(arrayOfByte[j]).substring(0, 2));
                byte d2 = Byte.valueOf(String.valueOf(arrayOfByte[j]).substring(2, 4));
                arrayOfByte[j] = Integer.decode("0x" + d1).byteValue();
                arrayOfByte[j + 1] = Integer.decode("0x" + d2).byteValue();
            }
            if (!isHexNumberRex(String.valueOf(arrayOfByte[j]))) {
                break;
            }
            arrayOfByte[j] = Integer.decode("0x" + paramStr[j]).byteValue();
        }
        return arrayOfByte;
    }

    private static boolean isOctNumberRex(String str) {
        String validate = "\\d+";
        return str.matches(validate);
    }

    private static boolean isHexNumberRex(String str) {
        String validate = "(?i)[0-9a-f]+";
        return str.matches(validate);
    }

    /**
     * 统计指定字符串中某个符号出现的次数
     *
     * @param str
     * @return int
     */
    public static int Count(String strData, String str) {
        int iBmpNum = 0;
        for (int i = 0; i < strData.length(); i++) {
            String getS = strData.substring(i, i + 1);
            if (getS.equals(str)) {
                iBmpNum++;
            }
        }
        //System.out.println(str + "出现了:" + iBmpNum + "次");
        return iBmpNum;
    }

    /**
     * 字符串转换为16进制
     *
     * @param strPart
     * @return
     */
    @SuppressLint({"UseValueOf", "DefaultLocale"})
    public static String stringTo16Hex(String strPart) {
        if (strPart == "")
            return "";
        try {
            byte[] b = strPart.getBytes("gbk"); // 数组指定编码格式，解决中英文乱码
            String str = "";
            for (int i = 0; i < b.length; i++) {
                Integer I = new Integer(b[i]);
                @SuppressWarnings("static-access")
                String strTmp = I.toHexString(b[i]);
                if (strTmp.length() > 2)
                    strTmp = strTmp.substring(strTmp.length() - 2) + " ";
                else
                    strTmp = strTmp.substring(0, strTmp.length()) + " ";
                str = str + strTmp;
            }
            return str.toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * @param a   转化数据
     * @param len 占用字节数
     * @return String
     * @Title:intToHexString
     * @Description:10进制数字转成16进制
     */
    public static String intToHexString(int a, int len) {
        len <<= 1;
        String hexString = Integer.toHexString(a);
        int b = len - hexString.length();
        if (b > 0) {
            for (int i = 0; i < b; i++) {
                hexString = "0" + hexString;
            }
        }
        return hexString;
    }

    @SuppressLint("DefaultLocale")
    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    /**
     * 通过选择文件获取路径
     *
     * @param context
     * @param uri
     * @return String
     */
    public static String getPath(Context context, Uri uri) {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection,
                        null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * uri 转path
     * @param selectedVideoUri
     * @param contentResolver
     * @return
     */
    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                                   ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }



    /**
     * Android 10 以上适配
     * @param context
     * @param uri
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static String uriToFileApiQ(Context context, Uri uri) {
        File file = null;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
            Log.i("xgh****", "uriToFileApiQ uri==> "+file.getPath()+"   "+file.getAbsolutePath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //把文件复制到沙盒目录
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                try {
                    InputStream is = contentResolver.openInputStream(uri);
                    File cache = new File(context.getExternalCacheDir().getAbsolutePath(), Math.round((Math.random() + 1) * 1000) + displayName);
                    FileOutputStream fos = new FileOutputStream(cache);
                    FileUtils.copy(is, fos);
                    file = cache;
                    fos.close();
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Log.i("xgh****", "uriToFileApiQ 111 uri==> "+file.getPath()+"   "+file.getAbsolutePath());
        }
        return file.getAbsolutePath();
    }


    /**
     * 获取单色位图
     *
     * @param inputBMP
     * @return Bitmap
     */
    public static Bitmap getSinglePic(Bitmap inputBMP) {
        int[] pix = new int[inputBMP.getWidth() * inputBMP.getHeight()];
        int[] colorTemp = new int[inputBMP.getWidth() * inputBMP.getHeight()];
        inputBMP.getPixels(pix, 0, inputBMP.getWidth(), 0, 0,
                inputBMP.getWidth(), inputBMP.getHeight());
        Bitmap returnBMP = Bitmap.createBitmap(inputBMP.getWidth(),
                inputBMP.getHeight(), Bitmap.Config.RGB_565);
        int lightNumber = 127;// 曝光度，這個顔色是中間值，如果大於中間值，那就是黑色，否則白色，数值越小，曝光度越高
//		for (int j = 0; j < colorTemp.length; j++) {
//			// 将颜色数组中的RGB值取反，255减去当前颜色值就获得当前颜色的反色
//			// 網上的，但是我要進行曝光處理，使他變成單色圖
//			colorTemp[j] = Color.rgb(Color.red(pix[j]) > lightNumber ? 255 : 0,
//					Color.green(pix[j]) > lightNumber ? 255 : 0,
//					Color.blue(pix[j]) > lightNumber ? 255 : 0);
//		}
        for (int j = 0; j < colorTemp.length; j++) {
            colorTemp[j] = Color.rgb(Color.red(pix[j]), Color.green(pix[j]),
                    Color.blue(pix[j]));
        }
        for (int i = 0; i < colorTemp.length; i++) {
            // 這裏需要思考一下，上一步有可能得到：純紅，純黃，純藍，黑色，白色這樣5種顔色，前三種是應該變成白色還是黑色呢？
            // 發現這是一個很複雜的問題，涉及到不同區域閒顔色的對比，如果是黑色包圍紅色，那紅色就應該是白色，反之變成黑色。。。
            // 似乎衹能具體問題具體分析，這裏就先把黃色設成白色，藍色=白色，紅色=黑色
            int r = Color.red(pix[i]);
            int g = Color.green(pix[i]);
            int b = Color.blue(pix[i]);
            // 有兩種顔色以上的混合，那就是變成黑色但目前这种方法，对于黑白的曝光效果更出色，
            // 原理是设置一个曝光值，然后三种颜色相加大于3倍的曝光值，才是黑色，否则白色
            if (r + g + b > 3 * lightNumber) {
                colorTemp[i] = Color.rgb(255, 255, 255);
            } else {
                colorTemp[i] = Color.rgb(0, 0, 0);
            }
        }
        returnBMP.setPixels(colorTemp, 0, inputBMP.getWidth(), 0, 0,
                inputBMP.getWidth(), inputBMP.getHeight());
        return returnBMP;
    }

    /**
     * jpg png bmp 彩色图片转换Bitmap数据为int[]数组
     *
     * @param bm
     * @return int[]
     */
    public static int[] getPixelsByBitmap(Bitmap bm) {
        int width, heigh;
        width = bm.getWidth();
        heigh = bm.getHeight();
        int iDataLen = width * heigh;
        int[] pixels = new int[iDataLen];
        bm.getPixels(pixels, 0, width, 0, 0, width, heigh);
        return pixels;
    }

    public static byte[] intToByte(int... cmd) {
        byte[] bytes = new byte[cmd.length];
        for (int i = 0; i < cmd.length; i++) {
            bytes[i] = (byte) cmd[i];
        }
        return bytes;
    }

    /**
     * BitmapOption 位图选项
     *
     * @param inSampleSize
     * @return
     */
    private static Options getBitmapOption(int inSampleSize) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = inSampleSize;
        options.inPreferredConfig = Config.ARGB_4444; // T4 二维码图片效果最佳
        return options;
    }

    /**
     * 获取Bitmap数据
     *
     * @param imgPath
     * @return
     */
    public static Bitmap getBitmapData(String imgPath) {
        Bitmap bm = BitmapFactory.decodeFile(imgPath, getBitmapOption(1)); // 将图片的长和宽缩小为原来的1/2
        return bm;
    }

    /**
     * ---保存Bitmap到本地路径-------------------------------------------------------
     */
    private static final String SD_PATH = "/sdcard/masung/pic/";
    private static final String IN_PATH = "/masung/pic/";

    /**
     * 随机生产文件名
     *
     * @return
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        try {
            filePic = new File(savePath + generateFileName() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block 自动生成的catch块
            e.printStackTrace();
            return null;
        }

        return filePic.getAbsolutePath();
    }

    /**
     * 将bitmap保存为JPG格式的图片
     *
     * @param bitmap
     */
    public static String imagePath = "";

    public static String saveMypic(Bitmap bitmap, Context context) {
        //非空判断
        if (bitmap == null) {
            return "";
        }
        // 保存图片
        try {
            String filePath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/masung";
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            imagePath = filePath + "/_MS_001.jpg";
            File image = new File(imagePath);
            if (image.exists()) {
                file.delete();
            }
            FileOutputStream stream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.flush();
            stream.close();
            Toast.makeText(context, "保存成功，已经保存到:" + imagePath, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "保存失败!", Toast.LENGTH_SHORT).show();
        }
        return imagePath;
    }
    /** ------------------------------END---------------------------------------*/

    /**
     * SharedPreferences存储数据方式工具类
     *
     * @author zuolongsnail
     */
    public final static String SETTING = "imin";

    // 移除数据
    public static void removeValue(Context context, String key) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        sp.clear();
        sp.commit();
    }

    public static void putValue(Context context, String key, int value) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        sp.putInt(key, value);
        sp.commit();
    }

    public static void putValue(Context context, String key, boolean value) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        sp.putBoolean(key, value);
        sp.commit();
    }

    public static void putValue(Context context, String key, String value) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        sp.putString(key, value);
        sp.commit();
    }

    public static int getValue(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        int value = sp.getInt(key, defValue);
        return value;
    }

    public static boolean getValue(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        boolean value = sp.getBoolean(key, defValue);
        return value;
    }

    public static String getValue(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        String value = sp.getString(key, defValue);
        return value;
    }


    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int dp2px(Context context, int value) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5);
    }

    public static int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    //获取底部返回键等按键的高度
    public static int getBackButtonHight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    //检查是否存在虚拟按键
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    //检查虚拟按键是否被重写
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    public static String getNumberString(String s){
        if (s == null||s.length()==0 || s.equals("")){
            return "0";
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(s);
        if( !isNum.matches() ){
            return "0";
        }
        return s;
    }

    public static boolean isEmpty(String s){
        if (s == null||s.length()==0 || s.equals("")){
            return true;
        }
        return false;
    }

    @SuppressLint("NewApi")
    public static int getWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
            int widthPixels = outMetrics.widthPixels;
            int heightPixels = outMetrics.heightPixels;
            int densityDpi = outMetrics.densityDpi;
            float density = outMetrics.density;
            float scaledDensity = outMetrics.scaledDensity;
            //可用显示大小的绝对宽度（以像素为单位）。
            //可用显示大小的绝对高度（以像素为单位）。
            //屏幕密度表示为每英寸点数。
            //显示器的逻辑密度。
            //显示屏上显示的字体缩放系数。
            Log.d("display", "widthPixels = " + widthPixels + ",heightPixels = " + heightPixels + "\n" +
                    ",densityDpi = " + densityDpi + "\n" +
                    ",density = " + density + ",scaledDensity = " + scaledDensity);
            return widthPixels;
        }
        return 0;
    }

    @SuppressLint("NewApi")
    public static int getHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getRealMetrics(outMetrics);
            int widthPixels = outMetrics.widthPixels;
            int heightPixels = outMetrics.heightPixels;
            int densityDpi = outMetrics.densityDpi;
            float density = outMetrics.density;
            float scaledDensity = outMetrics.scaledDensity;
            //可用显示大小的绝对宽度（以像素为单位）。
            //可用显示大小的绝对高度（以像素为单位）。
            //屏幕密度表示为每英寸点数。
            //显示器的逻辑密度。
            //显示屏上显示的字体缩放系数。
            Log.d("display", "widthPixels = " + widthPixels + ",heightPixels = " + heightPixels + "\n" +
                    ",densityDpi = " + densityDpi + "\n" +
                    ",density = " + density + ",scaledDensity = " + scaledDensity);
            return heightPixels;
        }
        return 0;
    }


    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++)
        {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 十六进制转换字符串
     * @return String 对应的字符串
     */
    public static String hexStr2Str(String hexStr)
    {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++)
        {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * bytes转换成十六进制字符串
     * @param  b byte数组
     * @return String 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] b)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * bytes字符串转换为Byte值
     * @param  src Byte字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src)
    {
        int m=0,n=0;
        int l=src.length()/2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++)
        {
            m=i*2+1;
            n=m+1;
            ret[i] = Byte.decode("0x" + src.substring(i*2, m) + src.substring(m,n));
        }
        return ret;
    }

    /**
     * String的字符串转换成unicode的String
     * @param  strText 全角字符串
     * @return String 每个unicode之间无分隔符
     * @throws Exception
     */
    public static String strToUnicode(String strText)
            throws Exception
    {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++)
        {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128)
                str.append("\\u" + strHex);
            else // 低位在前面补00
                str.append("\\u00" + strHex);
        }
        return str.toString();
    }

    /**
     * unicode的String转换成String的字符串
     * @param  hex 16进制值字符串 （一个unicode为2byte）
     * @return String 全角字符串
     */
    public static String unicodeToString(String hex)
    {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++)
        {
            String s = hex.substring(i * 6, (i + 1) * 6);
            // 高位需要补上00再转
            String s1 = s.substring(2, 4) + "00";
            // 低位直接转
            String s2 = s.substring(4);
            // 将16进制的string转为int
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
            // 将int转换为字符
            char[] chars = Character.toChars(n);
            str.append(new String(chars));
        }
        return str.toString();
    }
    public static String intToHex(int n) {
        StringBuffer s = new StringBuffer();
        String a;
        char []b = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        while(n != 0){
            s = s.append(b[n%16]);
            n = n/16;
        }
        a = s.reverse().toString();
        return a;
    }

}
