package android.support.p006v7.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.RequiresApi;
import android.support.p006v7.appcompat.C0289R;
import android.util.AttributeSet;
import android.widget.TextView;

@TargetApi(17)
@RequiresApi(17)
/* renamed from: android.support.v7.widget.AppCompatTextHelperV17 */
class AppCompatTextHelperV17 extends AppCompatTextHelper {
    private TintInfo mDrawableEndTint;
    private TintInfo mDrawableStartTint;

    AppCompatTextHelperV17(TextView view) {
        super(view);
    }

    /* access modifiers changed from: 0000 */
    public void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        super.loadFromAttributes(attrs, defStyleAttr);
        Context context = this.mView.getContext();
        AppCompatDrawableManager drawableManager = AppCompatDrawableManager.get();
        TypedArray a = context.obtainStyledAttributes(attrs, C0289R.styleable.AppCompatTextHelper, defStyleAttr, 0);
        if (a.hasValue(C0289R.styleable.AppCompatTextHelper_android_drawableStart)) {
            this.mDrawableStartTint = createTintInfo(context, drawableManager, a.getResourceId(C0289R.styleable.AppCompatTextHelper_android_drawableStart, 0));
        }
        if (a.hasValue(C0289R.styleable.AppCompatTextHelper_android_drawableEnd)) {
            this.mDrawableEndTint = createTintInfo(context, drawableManager, a.getResourceId(C0289R.styleable.AppCompatTextHelper_android_drawableEnd, 0));
        }
        a.recycle();
    }

    /* access modifiers changed from: 0000 */
    public void applyCompoundDrawablesTints() {
        super.applyCompoundDrawablesTints();
        if (this.mDrawableStartTint != null || this.mDrawableEndTint != null) {
            Drawable[] compoundDrawables = this.mView.getCompoundDrawablesRelative();
            applyCompoundDrawableTint(compoundDrawables[0], this.mDrawableStartTint);
            applyCompoundDrawableTint(compoundDrawables[2], this.mDrawableEndTint);
        }
    }
}
