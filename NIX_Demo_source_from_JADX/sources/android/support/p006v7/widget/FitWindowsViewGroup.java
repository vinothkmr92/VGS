package android.support.p006v7.widget;

import android.graphics.Rect;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;

@RestrictTo({Scope.LIBRARY_GROUP})
/* renamed from: android.support.v7.widget.FitWindowsViewGroup */
public interface FitWindowsViewGroup {

    /* renamed from: android.support.v7.widget.FitWindowsViewGroup$OnFitSystemWindowsListener */
    public interface OnFitSystemWindowsListener {
        void onFitSystemWindows(Rect rect);
    }

    void setOnFitSystemWindowsListener(OnFitSystemWindowsListener onFitSystemWindowsListener);
}
