package com.imin.printer;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FileUtil {
    Context mContext;
    public FileUtil getInstens(Context context){
        this.mContext = context;
        return this;
    }

    public File writeString2File(String Data, String filePath) {

        BufferedReader bufferedReader = null;

        BufferedWriter bufferedWriter = null;

        File distFile = null;
        try {
            distFile = new File(filePath);
            if (!distFile.getParentFile().exists()) distFile.getParentFile().mkdirs();
            bufferedReader = new BufferedReader(new StringReader(Data));
            bufferedWriter = new BufferedWriter(new FileWriter(distFile));
            char buf[] = new char[1024]; //字符缓冲区
            int len;
            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (Exception e) {
            Log.e("aaaaa","信息写入临时文件出错" + e.toString());
            e.printStackTrace();
        }

        return distFile;

    }

    //一级目录，以APP名称来建立
    public static String FirstFolder = "iminPrinter";
    //打印机固件文件夹名称
    public static final String PrinterFirmWare = "PrinterFirmWare";
    //APP一级文件夹路径
    public final static String APP_FOLDER_PATH = PrinterApplication.context.getExternalFilesDir(null).getAbsolutePath() + File.separator + FirstFolder + File.separator;
//    APP二级文件夹，打印机固件路径
    public final static String PrinterFirmWare_PATH = APP_FOLDER_PATH + PrinterFirmWare + File.separator;
    public final static String APP_FOLDER_PATH1 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + FirstFolder + File.separator;


    /**
     * 创建文件夹
     *
     * @param path
     * @return
     */
    public static synchronized boolean createDir(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        File file = new File(path, "a");//随意新建一个文件
        if (!file.exists()) {
            // 获取父文件
            File parent = file.getParentFile();
            if (!parent.exists()) {
                return parent.mkdirs();  //创建所有父文件夹
            }
        }
        return false;
    }

    /**
     * 创建文件
     *
     * @param path 路径
     * @param name 文件名称
     * @return
     */
    public static synchronized File createFile(String path, String name) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(name)) {
            return null;
        }
        File file = new File(path, name);
        if (file.exists()) {
            file.delete();
        }
        if (!file.exists()) {
            try {
                // 获取父文件
                File parent = file.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();  //创建所有父文件夹
                }
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 获取文件夹下全部文件
     *
     * @param path
     * @return
     */
    public static synchronized List<String> getDirAllFile(String path) {
        List<String> mlist = new ArrayList<>();
        File dirSecondFile = new File(path);
        if (!dirSecondFile.getParentFile().exists()) {
            dirSecondFile.mkdirs();
        }
        File[] files = dirSecondFile.listFiles();// 读取文件夹下文件
        for (int i = 0; i < files.length; i++) {
            if (files.length > 0) {
                mlist.add(files[i].getName());
            }
        }
        return mlist;
    }

    /**
     * 保存文件内容
     *
     * @param filePath
     * @param fileName
     * @param content
     * @return
     */
    public static boolean saveFile(String filePath, String fileName, String content) {
        File file = createFile(filePath, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content.getBytes("UTF-8"));
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    public static boolean saveFile(String filePath, String fileName, byte [] content) {
        File file = createFile(filePath, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(content);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    /**
     * 读取文件
     *
     * @param filePath
     * @param charsetName
     * @return
     */
    public static String readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            reader.close();
            return fileContent.toString();
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
    }

    /**
     * 获取文件名
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        File file=new File(path);
        if (file.exists()){
            return file.getName();
        }
        return "";
    }

    public byte [] stringToByte(String str) throws UnsupportedEncodingException {
        return str.getBytes("GB18030");
    }
    /**
     * 文件转字节流
     *
     * @param file
     * @return
     */
    public static byte[] fileToByte(File file) {
        if (file == null) {
            return null;
        }
        ByteArrayOutputStream outStream = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024*1024];
            byte[] data = null;
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            data = outStream.toByteArray();
            return data;
        } catch (IOException e) {
            return null;
        } finally {
            try {
                if (outStream != null) {
                    outStream.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                return null;
            }
        }

    }

    /**
     * 获得指定文件的byte数组
     */
    private byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 根据byte数组，生成文件
     */
    public static boolean createFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath, fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 获取Assets文件
     * @param fileName
     * @return
     */
    public static String getFromAssets(Context context,String fileName) {
        String result = "";
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null)
                result += line;
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 获取Assets文件
     * @param fileName
     * @return
     */
    public static byte [] getAssetsFile(Context context,String fileName) {
        InputStream in = null;
        ByteArrayOutputStream outStream = null;
        byte [] data=null;
        try {
            in = context.getResources().getAssets().open(fileName);
            outStream = new ByteArrayOutputStream();
            //创建byte数组
            byte[]  buffer = new byte[1024*1024];
            //将文件中的数据读到byte数组中
            int len = 0;
            while ((len = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            data = outStream.toByteArray();
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (in!=null){
                    in.close();
                }
                if (outStream!=null){
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

    /**
     * 保存图片
     * @param bmp
     * @param fileName
     * @return
     */
    public static boolean saveImageToGallery(Bitmap bmp, String fileName) {
        //生成路径
        File file =createFile(APP_FOLDER_PATH,fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 读取文件的修改时间
     * @param f
     * @return
     */
    public static String getModifiedTime(File f) {
        Calendar cal = Calendar.getInstance();
        long time = f.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTimeInMillis(time);
        // System.out.println("修改时间[2] " + formatter.format(cal.getTime()));
        // 输出：修改时间[2] 2009-08-17 10:32:38
        return formatter.format(cal.getTime());
    }
    /**
     * 判断文件是否存在
     * @param path 文件的路径
     * @return
     */
    public static boolean isExists(String path) {
        File file = new File(path);
        return file.exists();
    }
    /****
     *计算文件大小
     * @param length
     * @return
     */
    public static String showLongFileSize(Long length)
    {
        if(length>=1048576)
        {
            return (length/1048576)+"MB";
        }
        else if(length>=1024)
        {
            return (length/1024)+"KB";
        }
        else if(length<1024) {
            return length + "B";
        }else{
            return "0KB";
        }
    }
}
