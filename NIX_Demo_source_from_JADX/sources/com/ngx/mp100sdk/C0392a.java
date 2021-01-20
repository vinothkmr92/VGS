package com.ngx.mp100sdk;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.ngx.mp100sdk.Enums.NGXBarcodeCommands;

/* renamed from: com.ngx.mp100sdk.a */
class C0392a {

    /* renamed from: a */
    final /* synthetic */ NGXPrinter f82a;

    /* renamed from: b */
    private Context f83b;

    /* renamed from: c */
    private ServiceConnection f84c;

    private C0392a(NGXPrinter nGXPrinter) {
        this.f82a = nGXPrinter;
        this.f84c = new C0395d(this);
    }

    /* synthetic */ C0392a(NGXPrinter nGXPrinter, byte b) {
        this(nGXPrinter);
    }

    /* renamed from: a */
    private static Bitmap m95a(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        int[] iArr = new int[(width * height)];
        for (int i = 0; i < height; i++) {
            for (int i2 = 0; i2 < width; i2++) {
                if (bitMatrix.get(i2, i)) {
                    iArr[(i * width) + i2] = -16777216;
                } else {
                    iArr[(i * width) + i2] = -1;
                }
            }
        }
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        createBitmap.setPixels(iArr, 0, width, 0, 0, width, height);
        return createBitmap;
    }

    /* renamed from: a */
    static Bitmap m96a(String str, NGXBarcodeCommands nGXBarcodeCommands, int i, int i2, boolean z) {
        BitMatrix encode;
        switch (C0393b.f85a[nGXBarcodeCommands.ordinal()]) {
            case 1:
                encode = new MultiFormatWriter().encode(str, BarcodeFormat.CODE_39, i, i2);
                break;
            case 2:
                if (str.length() == 11 || str.length() == 12) {
                    encode = new MultiFormatWriter().encode(str, BarcodeFormat.UPC_A, i, i2);
                    break;
                } else {
                    throw new Exception("Requested contents should be 11 or 12 digits long, but got " + str.length());
                }
                break;
            case 3:
                if (str.length() == 13) {
                    encode = new MultiFormatWriter().encode(str, BarcodeFormat.EAN_13, i, i2);
                    break;
                } else {
                    throw new Exception("Requested contents should be 13 digits long, but got " + str.length());
                }
            case 4:
                if (str.length() == 8) {
                    encode = new MultiFormatWriter().encode(str, BarcodeFormat.EAN_8, i, i2);
                    break;
                } else {
                    throw new Exception("Requested contents should be 8 digits long, but got " + str.length());
                }
            case 5:
                encode = new MultiFormatWriter().encode(str, BarcodeFormat.ITF, i, i2);
                break;
            case 6:
                encode = new MultiFormatWriter().encode(str, BarcodeFormat.CODABAR, i, i2);
                break;
            case 7:
                encode = new MultiFormatWriter().encode(str, BarcodeFormat.CODE_128, i, i2);
                break;
            case 8:
                encode = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, i, i2);
                break;
            case 9:
                encode = new MultiFormatWriter().encode(str, BarcodeFormat.PDF_417, i, i2);
                break;
            case 10:
                encode = new MultiFormatWriter().encode(str, BarcodeFormat.AZTEC, i, i2);
                break;
            case 11:
                encode = new MultiFormatWriter().encode(str, BarcodeFormat.DATA_MATRIX, i, i2);
                break;
            default:
                encode = null;
                break;
        }
        if (encode == null) {
            return null;
        }
        Bitmap a = m95a(encode);
        TextPaint textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.SANS_SERIF);
        textPaint.setTextSize(18.0f);
        Alignment alignment = Alignment.ALIGN_CENTER;
        int width = a.getWidth();
        StaticLayout staticLayout = new StaticLayout(str, textPaint, width, alignment, 1.0f, 1.0f, true);
        Bitmap createBitmap = Bitmap.createBitmap(width, staticLayout.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(-1);
        staticLayout.draw(canvas);
        Bitmap createBitmap2 = Bitmap.createBitmap(Math.max(a.getWidth(), createBitmap.getWidth()), a.getHeight() + createBitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas2 = new Canvas(createBitmap2);
        canvas2.drawColor(-1);
        canvas2.drawBitmap(a, 0.0f, 0.0f, null);
        canvas2.drawBitmap(createBitmap, 0.0f, (float) a.getHeight(), null);
        a.recycle();
        createBitmap.recycle();
        return createBitmap2;
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: a */
    public void mo8685a() {
        this.f82a.m90a();
        Intent intent = new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        this.f83b.startService(intent);
        this.f83b.bindService(intent, this.f84c, 1);
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: a */
    public void mo8686a(Context context) {
        this.f83b = context;
        mo8685a();
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: b */
    public Context mo8687b() {
        return this.f83b;
    }

    /* access modifiers changed from: 0000 */
    /* renamed from: c */
    public void mo8688c() {
        try {
            this.f83b.unbindService(this.f84c);
            this.f82a.f81m.clear();
        } catch (Exception e) {
            this.f82a.f81m.clear();
            throw e;
        }
    }
}
