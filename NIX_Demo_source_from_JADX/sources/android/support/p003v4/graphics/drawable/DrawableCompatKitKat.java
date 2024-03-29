package android.support.p003v4.graphics.drawable;

import android.annotation.TargetApi;
import android.graphics.drawable.Drawable;
import android.support.annotation.RequiresApi;

@TargetApi(19)
@RequiresApi(19)
/* renamed from: android.support.v4.graphics.drawable.DrawableCompatKitKat */
class DrawableCompatKitKat {
    DrawableCompatKitKat() {
    }

    public static void setAutoMirrored(Drawable drawable, boolean mirrored) {
        drawable.setAutoMirrored(mirrored);
    }

    public static boolean isAutoMirrored(Drawable drawable) {
        return drawable.isAutoMirrored();
    }

    public static Drawable wrapForTinting(Drawable drawable) {
        if (!(drawable instanceof TintAwareDrawable)) {
            return new DrawableWrapperKitKat(drawable);
        }
        return drawable;
    }

    public static int getAlpha(Drawable drawable) {
        return drawable.getAlpha();
    }
}
