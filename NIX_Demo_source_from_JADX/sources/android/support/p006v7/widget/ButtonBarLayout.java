package android.support.p006v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build.VERSION;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.p003v4.content.res.ConfigurationHelper;
import android.support.p003v4.view.ViewCompat;
import android.support.p006v7.appcompat.C0289R;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

@RestrictTo({Scope.LIBRARY_GROUP})
/* renamed from: android.support.v7.widget.ButtonBarLayout */
public class ButtonBarLayout extends LinearLayout {
    private static final int ALLOW_STACKING_MIN_HEIGHT_DP = 320;
    private static final int PEEK_BUTTON_DP = 16;
    private boolean mAllowStacking;
    private int mLastWidthSize = -1;

    public ButtonBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        boolean allowStackingDefault = ConfigurationHelper.getScreenHeightDp(getResources()) >= ALLOW_STACKING_MIN_HEIGHT_DP;
        TypedArray ta = context.obtainStyledAttributes(attrs, C0289R.styleable.ButtonBarLayout);
        this.mAllowStacking = ta.getBoolean(C0289R.styleable.ButtonBarLayout_allowStacking, allowStackingDefault);
        ta.recycle();
    }

    public void setAllowStacking(boolean allowStacking) {
        if (this.mAllowStacking != allowStacking) {
            this.mAllowStacking = allowStacking;
            if (!this.mAllowStacking && getOrientation() == 1) {
                setStacked(false);
            }
            requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int initialWidthMeasureSpec;
        boolean stack;
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (this.mAllowStacking) {
            if (widthSize > this.mLastWidthSize && isStacked()) {
                setStacked(false);
            }
            this.mLastWidthSize = widthSize;
        }
        boolean needsRemeasure = false;
        if (isStacked() || MeasureSpec.getMode(widthMeasureSpec) != 1073741824) {
            initialWidthMeasureSpec = widthMeasureSpec;
        } else {
            initialWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, Integer.MIN_VALUE);
            needsRemeasure = true;
        }
        super.onMeasure(initialWidthMeasureSpec, heightMeasureSpec);
        if (this.mAllowStacking && !isStacked()) {
            if (VERSION.SDK_INT >= 11) {
                stack = (ViewCompat.getMeasuredWidthAndState(this) & ViewCompat.MEASURED_STATE_MASK) == 16777216;
            } else {
                int childWidthTotal = 0;
                for (int i = 0; i < getChildCount(); i++) {
                    childWidthTotal += getChildAt(i).getMeasuredWidth();
                }
                stack = (getPaddingLeft() + childWidthTotal) + getPaddingRight() > widthSize;
            }
            if (stack) {
                setStacked(true);
                needsRemeasure = true;
            }
        }
        if (needsRemeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        int minHeight = 0;
        int firstVisible = getNextVisibleChildIndex(0);
        if (firstVisible >= 0) {
            View firstButton = getChildAt(firstVisible);
            LayoutParams firstParams = (LayoutParams) firstButton.getLayoutParams();
            minHeight = 0 + getPaddingTop() + firstButton.getMeasuredHeight() + firstParams.topMargin + firstParams.bottomMargin;
            if (isStacked()) {
                int secondVisible = getNextVisibleChildIndex(firstVisible + 1);
                if (secondVisible >= 0) {
                    minHeight = (int) (((float) minHeight) + ((float) getChildAt(secondVisible).getPaddingTop()) + (16.0f * getResources().getDisplayMetrics().density));
                }
            } else {
                minHeight += getPaddingBottom();
            }
        }
        if (ViewCompat.getMinimumHeight(this) != minHeight) {
            setMinimumHeight(minHeight);
        }
    }

    private int getNextVisibleChildIndex(int index) {
        int count = getChildCount();
        for (int i = index; i < count; i++) {
            if (getChildAt(i).getVisibility() == 0) {
                return i;
            }
        }
        return -1;
    }

    private void setStacked(boolean stacked) {
        setOrientation(stacked ? 1 : 0);
        setGravity(stacked ? 5 : 80);
        View spacer = findViewById(C0289R.C0291id.spacer);
        if (spacer != null) {
            spacer.setVisibility(stacked ? 8 : 4);
        }
        for (int i = getChildCount() - 2; i >= 0; i--) {
            bringChildToFront(getChildAt(i));
        }
    }

    private boolean isStacked() {
        return getOrientation() == 1;
    }
}
