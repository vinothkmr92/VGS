package com.ngx.mp100sdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout.Alignment;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Toast;
import com.ngx.mp100sdk.Enums.Alignments;
import com.ngx.mp100sdk.Enums.C0391a;
import com.ngx.mp100sdk.Enums.NGXBarcodeCommands;
import com.ngx.mp100sdk.Intefaces.INGXCallback;
import java.util.ArrayList;
import java.util.List;
import p000a.p001a.p002a.C0000a;
import p000a.p001a.p002a.C0003d;

public class NGXPrinter {

    /* renamed from: a */
    private static final NGXPrinter f69a = new NGXPrinter();

    /* renamed from: b */
    private static final byte[] f70b = {27, 33, 16};

    /* renamed from: c */
    private static final byte[] f71c = {27, 33, 32};

    /* renamed from: d */
    private static final byte[] f72d = {27, 33, 0};

    /* renamed from: e */
    private static final byte[] f73e = {27, 33, 8};

    /* renamed from: f */
    private static final byte[] f74f = {27, 64};

    /* renamed from: g */
    private static boolean f75g;

    /* renamed from: h */
    private final byte[] f76h = {10};
    /* access modifiers changed from: private */

    /* renamed from: i */
    public C0003d f77i = null;

    /* renamed from: j */
    private C0000a f78j = null;
    /* access modifiers changed from: private */

    /* renamed from: k */
    public INGXCallback f79k = null;

    /* renamed from: l */
    private C0392a f80l = new C0392a(this, 0);
    /* access modifiers changed from: private */

    /* renamed from: m */
    public List f81m = new ArrayList();

    static {
        if (!Build.MODEL.equals(C0391a.NIX.toString())) {
            f75g = false;
            Log.i("NGX", "NOT NIX");
            return;
        }
        f75g = true;
        Log.i("NGX", "NIX");
    }

    private NGXPrinter() {
    }

    /* access modifiers changed from: private */
    /* renamed from: a */
    public void m90a() {
        if (this.f79k != null) {
            this.f78j = new C0394c(this);
        }
    }

    /* renamed from: b */
    static /* synthetic */ void m91b(NGXPrinter nGXPrinter) {
        if (nGXPrinter.f77i == null) {
            Log.i("NGX", " PrinterService NULL Value present");
        } else if (nGXPrinter.f78j == null) {
            Log.i("NGX", "PrinterCallback NULL Value present");
        } else {
            try {
                nGXPrinter.f77i.mo10a(nGXPrinter.f78j);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static NGXPrinter getNgxPrinterInstance() {
        if (f75g) {
            return f69a;
        }
        return null;
    }

    public void addImage(Bitmap bitmap) {
        if (bitmap == null) {
            Toast.makeText(this.f80l.mo8687b(), "Given bitmap is null", 1).show();
        } else {
            this.f81m.add(C0396e.m105a(bitmap));
        }
    }

    public void addImage(String str) {
        if (str == null || str.length() == 0) {
            Toast.makeText(this.f80l.mo8687b(), "Image Path is not provided.", 1).show();
            return;
        }
        Bitmap a = C0396e.m106a(str);
        if (a == null) {
            Toast.makeText(this.f80l.mo8687b(), "Image is not present in given path", 1).show();
        } else {
            this.f81m.add(a);
        }
    }

    public void addText(String str) {
        if (str == null || str.length() == 0) {
            Toast.makeText(this.f80l.mo8687b(), "Not text to add", 1).show();
            return;
        }
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(20.0f);
        addText(str, Alignment.ALIGN_NORMAL, textPaint);
    }

    public void addText(String str, Alignment alignment, TextPaint textPaint) {
        if (str == null || str.length() == 0) {
            Toast.makeText(this.f80l.mo8687b(), "Not text to add", 1).show();
            return;
        }
        this.f81m.add(C0396e.m107a(str, alignment, textPaint));
    }

    public int getMaxImgPrintWidth() {
        return C0396e.m104a();
    }

    public int getServiceStatus() {
        return this.f77i.mo7a();
    }

    public void initService(Context context) {
        this.f80l.mo8686a(context);
    }

    public void initService(Context context, INGXCallback iNGXCallback) {
        this.f79k = iNGXCallback;
        this.f80l.mo8686a(context);
    }

    public void lineFeed() {
        lineFeed(1);
    }

    public void lineFeed(int i) {
        for (int i2 = 0; i2 < i; i2++) {
            this.f77i.mo17a(this.f76h, this.f78j);
        }
    }

    public void onActivityDestroy() {
        this.f80l.mo8688c();
    }

    public void print() {
        Bitmap bitmap = null;
        int i = 0;
        try {
            if (this.f81m.size() <= 0) {
                Toast.makeText(this.f80l.mo8687b(), "Not text to print", 1).show();
                this.f81m.clear();
                return;
            }
            int i2 = 0;
            for (Bitmap height : this.f81m) {
                i2 = height.getHeight() + i2;
            }
            Bitmap createBitmap = Bitmap.createBitmap(C0396e.m104a(), i2, Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.drawColor(-1);
            for (Bitmap bitmap2 : this.f81m) {
                canvas.drawBitmap(bitmap2, 0.0f, (float) i, null);
                i = bitmap2.getHeight() + i;
            }
            this.f77i.mo11a(createBitmap, this.f78j);
            this.f77i.mo17a(this.f76h, this.f78j);
            this.f81m.clear();
            if (createBitmap != null) {
                createBitmap.recycle();
            }
        } catch (Exception e) {
            throw e;
        } catch (Throwable th) {
            this.f81m.clear();
            if (bitmap != null) {
                bitmap.recycle();
            }
            throw th;
        }
    }

    public void printBarcode(String str) {
        printBarcode(str, Alignment.ALIGN_NORMAL, NGXBarcodeCommands.CODE39, 162, 2, 3);
    }

    public void printBarcode(String str, Alignment alignment, NGXBarcodeCommands nGXBarcodeCommands, int i, int i2) {
        printBarcode(str, alignment, nGXBarcodeCommands, i, i2, 3);
    }

    public void printBarcode(String str, Alignment alignment, NGXBarcodeCommands nGXBarcodeCommands, int i, int i2, int i3) {
        if (i2 > getMaxImgPrintWidth()) {
            throw new Exception("Barcode Width should be less than 385, but got " + i2);
        } else if (i <= 0 || i > 255) {
            throw new Exception("Barcode Height should be between 1 and 255, but got " + i);
        } else {
            Bitmap a = C0392a.m96a(str, nGXBarcodeCommands, i2, i, true);
            if (a != null) {
                this.f77i.mo11a(a, this.f78j);
                this.f77i.mo17a(this.f76h, this.f78j);
                a.recycle();
                return;
            }
            throw new Exception("Exception during Barcode generation.");
        }
    }

    public void printBarcode(String str, NGXBarcodeCommands nGXBarcodeCommands, int i, int i2) {
        printBarcode(str, Alignment.ALIGN_NORMAL, nGXBarcodeCommands, i, i2, 3);
    }

    public void printImage(Bitmap bitmap) {
        printImage(bitmap, 0);
    }

    public void printImage(Bitmap bitmap, int i) {
        if (i <= 0) {
            i = bitmap.getHeight();
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), i);
        Bitmap a = C0396e.m105a(createBitmap);
        this.f77i.mo11a(a, this.f78j);
        this.f77i.mo17a(this.f76h, this.f78j);
        a.recycle();
        createBitmap.recycle();
    }

    public void printLogo(String str) {
        printImage(C0396e.m106a(str));
    }

    public void printText(String str) {
        printText(str, Alignments.LEFT, 25);
    }

    public void printText(String str, Alignments alignments) {
        printText(str, alignments, 25);
    }

    public void printText(String str, Alignments alignments, int i) {
        this.f77i.mo8a((float) i, this.f78j);
        this.f77i.mo20b(alignments.ordinal(), this.f78j);
        this.f77i.mo22b(str, this.f78j);
        this.f77i.mo17a(this.f76h, this.f78j);
    }

    public void printUnicodeText(String str, Alignment alignment, TextPaint textPaint) {
        Bitmap a = C0396e.m107a(str, alignment, textPaint);
        this.f77i.mo11a(a, this.f78j);
        this.f77i.mo17a(this.f76h, this.f78j);
        a.recycle();
    }

    public void printUnicodeText(String str, boolean z) {
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(20.0f);
        if (z) {
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, 1));
        }
        printUnicodeText(str, Alignment.ALIGN_NORMAL, textPaint);
    }

    public void setAlignment(Alignments alignments) {
        this.f77i.mo20b(alignments.ordinal(), this.f78j);
    }

    public void setCallback(INGXCallback iNGXCallback) {
        this.f79k = iNGXCallback;
        m90a();
    }

    public void setDefault() {
        this.f77i.mo17a(f74f, this.f78j);
    }

    public void setStyleBold() {
        this.f77i.mo17a(f73e, this.f78j);
    }

    public void setStyleDoubleHeight() {
        this.f77i.mo17a(f70b, this.f78j);
    }

    public void setStyleDoubleWidth() {
        this.f77i.mo17a(f71c, this.f78j);
    }

    public void setStyleNormal() {
        this.f77i.mo17a(f72d, this.f78j);
    }
}
