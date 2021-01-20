package com.ngx.mp100sdk;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.support.p003v4.view.ViewCompat;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;

/* renamed from: com.ngx.mp100sdk.e */
public final class C0396e {

    /* renamed from: a */
    private static int f88a = 384;

    /* renamed from: b */
    private static int f89b = 0;

    /* renamed from: c */
    private static int f90c = 0;

    /* renamed from: a */
    protected static int m104a() {
        return f88a;
    }

    /* renamed from: a */
    static Bitmap m105a(Bitmap bitmap) {
        if (bitmap.getWidth() > f88a) {
            Point a = m108a(bitmap.getWidth(), bitmap.getHeight());
            return Bitmap.createScaledBitmap(bitmap, a.x, a.y, false);
        }
        Bitmap createBitmap = Bitmap.createBitmap(f88a, bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(-1);
        canvas.drawBitmap(bitmap, (float) ((f88a - bitmap.getWidth()) / 2), 0.0f, null);
        return createBitmap;
    }

    /* renamed from: a */
    protected static Bitmap m106a(String str) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        f89b = options.outHeight;
        f90c = options.outWidth;
        if (!(f89b >= 8 && f90c >= 8)) {
            return null;
        }
        Options options2 = new Options();
        options2.inSampleSize = (f90c <= f88a || f90c <= f89b) ? 1 : Math.round(((float) f90c) / ((float) f88a));
        if (VERSION.SDK_INT >= 11) {
            options2.inMutable = true;
        }
        Bitmap decodeFile = BitmapFactory.decodeFile(str, options2);
        if (decodeFile.getWidth() > f88a) {
            Point a = m108a(decodeFile.getWidth(), decodeFile.getHeight());
            return Bitmap.createScaledBitmap(decodeFile, a.x, a.y, false);
        }
        Bitmap createBitmap = Bitmap.createBitmap(f88a, decodeFile.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(-1);
        canvas.drawBitmap(decodeFile, (float) ((f88a - decodeFile.getWidth()) / 2), 0.0f, null);
        return createBitmap;
    }

    /* renamed from: a */
    protected static Bitmap m107a(String str, Alignment alignment, TextPaint textPaint) {
        TextPaint textPaint2 = new TextPaint();
        if (textPaint != null) {
            textPaint2.set(textPaint);
        } else {
            textPaint2.setColor(ViewCompat.MEASURED_STATE_MASK);
            textPaint2.setTextSize(16.0f);
        }
        StaticLayout staticLayout = new StaticLayout(str, textPaint2, f88a, alignment, 1.0f, 1.0f, true);
        Bitmap createBitmap = Bitmap.createBitmap(f88a, staticLayout.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.drawColor(-1);
        staticLayout.draw(canvas);
        return createBitmap;
    }

    /* renamed from: a */
    private static Point m108a(int i, int i2) {
        Point point = new Point();
        float f = ((float) i) / ((float) i2);
        if (i > f88a) {
            i2 = (int) (((float) i) / f);
        }
        point.x = f88a;
        point.y = i2;
        return point;
    }
}
