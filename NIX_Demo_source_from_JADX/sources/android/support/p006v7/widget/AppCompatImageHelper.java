package android.support.p006v7.widget;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.p006v7.appcompat.C0289R;
import android.support.p006v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.widget.ImageView;

@RestrictTo({Scope.LIBRARY_GROUP})
/* renamed from: android.support.v7.widget.AppCompatImageHelper */
public class AppCompatImageHelper {
    private final ImageView mView;

    public AppCompatImageHelper(ImageView view) {
        this.mView = view;
    }

    /* JADX INFO: finally extract failed */
    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        TintTypedArray a = null;
        try {
            Drawable drawable = this.mView.getDrawable();
            if (drawable == null) {
                a = TintTypedArray.obtainStyledAttributes(this.mView.getContext(), attrs, C0289R.styleable.AppCompatImageView, defStyleAttr, 0);
                int id = a.getResourceId(C0289R.styleable.AppCompatImageView_srcCompat, -1);
                if (id != -1) {
                    drawable = AppCompatResources.getDrawable(this.mView.getContext(), id);
                    if (drawable != null) {
                        this.mView.setImageDrawable(drawable);
                    }
                }
            }
            if (drawable != null) {
                DrawableUtils.fixDrawable(drawable);
            }
            if (a != null) {
                a.recycle();
            }
        } catch (Throwable th) {
            if (a != null) {
                a.recycle();
            }
            throw th;
        }
    }

    public void setImageResource(int resId) {
        if (resId != 0) {
            Drawable d = AppCompatResources.getDrawable(this.mView.getContext(), resId);
            if (d != null) {
                DrawableUtils.fixDrawable(d);
            }
            this.mView.setImageDrawable(d);
            return;
        }
        this.mView.setImageDrawable(null);
    }

    /* access modifiers changed from: 0000 */
    public boolean hasOverlappingRendering() {
        Drawable background = this.mView.getBackground();
        if (VERSION.SDK_INT < 21 || !(background instanceof RippleDrawable)) {
            return true;
        }
        return false;
    }
}
