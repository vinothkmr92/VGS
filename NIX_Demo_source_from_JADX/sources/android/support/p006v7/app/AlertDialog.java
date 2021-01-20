package android.support.p006v7.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.ArrayRes;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.RestrictTo.Scope;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.p006v7.app.AlertController.AlertParams;
import android.support.p006v7.appcompat.C0289R;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

/* renamed from: android.support.v7.app.AlertDialog */
public class AlertDialog extends AppCompatDialog implements DialogInterface {
    static final int LAYOUT_HINT_NONE = 0;
    static final int LAYOUT_HINT_SIDE = 1;
    final AlertController mAlert;

    /* renamed from: android.support.v7.app.AlertDialog$Builder */
    public static class Builder {

        /* renamed from: P */
        private final AlertParams f19P;
        private final int mTheme;

        public Builder(@NonNull Context context) {
            this(context, AlertDialog.resolveDialogTheme(context, 0));
        }

        public Builder(@NonNull Context context, @StyleRes int themeResId) {
            this.f19P = new AlertParams(new ContextThemeWrapper(context, AlertDialog.resolveDialogTheme(context, themeResId)));
            this.mTheme = themeResId;
        }

        @NonNull
        public Context getContext() {
            return this.f19P.mContext;
        }

        public Builder setTitle(@StringRes int titleId) {
            this.f19P.mTitle = this.f19P.mContext.getText(titleId);
            return this;
        }

        public Builder setTitle(@Nullable CharSequence title) {
            this.f19P.mTitle = title;
            return this;
        }

        public Builder setCustomTitle(@Nullable View customTitleView) {
            this.f19P.mCustomTitleView = customTitleView;
            return this;
        }

        public Builder setMessage(@StringRes int messageId) {
            this.f19P.mMessage = this.f19P.mContext.getText(messageId);
            return this;
        }

        public Builder setMessage(@Nullable CharSequence message) {
            this.f19P.mMessage = message;
            return this;
        }

        public Builder setIcon(@DrawableRes int iconId) {
            this.f19P.mIconId = iconId;
            return this;
        }

        public Builder setIcon(@Nullable Drawable icon) {
            this.f19P.mIcon = icon;
            return this;
        }

        public Builder setIconAttribute(@AttrRes int attrId) {
            TypedValue out = new TypedValue();
            this.f19P.mContext.getTheme().resolveAttribute(attrId, out, true);
            this.f19P.mIconId = out.resourceId;
            return this;
        }

        public Builder setPositiveButton(@StringRes int textId, OnClickListener listener) {
            this.f19P.mPositiveButtonText = this.f19P.mContext.getText(textId);
            this.f19P.mPositiveButtonListener = listener;
            return this;
        }

        public Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            this.f19P.mPositiveButtonText = text;
            this.f19P.mPositiveButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(@StringRes int textId, OnClickListener listener) {
            this.f19P.mNegativeButtonText = this.f19P.mContext.getText(textId);
            this.f19P.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            this.f19P.mNegativeButtonText = text;
            this.f19P.mNegativeButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(@StringRes int textId, OnClickListener listener) {
            this.f19P.mNeutralButtonText = this.f19P.mContext.getText(textId);
            this.f19P.mNeutralButtonListener = listener;
            return this;
        }

        public Builder setNeutralButton(CharSequence text, OnClickListener listener) {
            this.f19P.mNeutralButtonText = text;
            this.f19P.mNeutralButtonListener = listener;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.f19P.mCancelable = cancelable;
            return this;
        }

        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            this.f19P.mOnCancelListener = onCancelListener;
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            this.f19P.mOnDismissListener = onDismissListener;
            return this;
        }

        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            this.f19P.mOnKeyListener = onKeyListener;
            return this;
        }

        public Builder setItems(@ArrayRes int itemsId, OnClickListener listener) {
            this.f19P.mItems = this.f19P.mContext.getResources().getTextArray(itemsId);
            this.f19P.mOnClickListener = listener;
            return this;
        }

        public Builder setItems(CharSequence[] items, OnClickListener listener) {
            this.f19P.mItems = items;
            this.f19P.mOnClickListener = listener;
            return this;
        }

        public Builder setAdapter(ListAdapter adapter, OnClickListener listener) {
            this.f19P.mAdapter = adapter;
            this.f19P.mOnClickListener = listener;
            return this;
        }

        public Builder setCursor(Cursor cursor, OnClickListener listener, String labelColumn) {
            this.f19P.mCursor = cursor;
            this.f19P.mLabelColumn = labelColumn;
            this.f19P.mOnClickListener = listener;
            return this;
        }

        public Builder setMultiChoiceItems(@ArrayRes int itemsId, boolean[] checkedItems, OnMultiChoiceClickListener listener) {
            this.f19P.mItems = this.f19P.mContext.getResources().getTextArray(itemsId);
            this.f19P.mOnCheckboxClickListener = listener;
            this.f19P.mCheckedItems = checkedItems;
            this.f19P.mIsMultiChoice = true;
            return this;
        }

        public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems, OnMultiChoiceClickListener listener) {
            this.f19P.mItems = items;
            this.f19P.mOnCheckboxClickListener = listener;
            this.f19P.mCheckedItems = checkedItems;
            this.f19P.mIsMultiChoice = true;
            return this;
        }

        public Builder setMultiChoiceItems(Cursor cursor, String isCheckedColumn, String labelColumn, OnMultiChoiceClickListener listener) {
            this.f19P.mCursor = cursor;
            this.f19P.mOnCheckboxClickListener = listener;
            this.f19P.mIsCheckedColumn = isCheckedColumn;
            this.f19P.mLabelColumn = labelColumn;
            this.f19P.mIsMultiChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(@ArrayRes int itemsId, int checkedItem, OnClickListener listener) {
            this.f19P.mItems = this.f19P.mContext.getResources().getTextArray(itemsId);
            this.f19P.mOnClickListener = listener;
            this.f19P.mCheckedItem = checkedItem;
            this.f19P.mIsSingleChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn, OnClickListener listener) {
            this.f19P.mCursor = cursor;
            this.f19P.mOnClickListener = listener;
            this.f19P.mCheckedItem = checkedItem;
            this.f19P.mLabelColumn = labelColumn;
            this.f19P.mIsSingleChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, OnClickListener listener) {
            this.f19P.mItems = items;
            this.f19P.mOnClickListener = listener;
            this.f19P.mCheckedItem = checkedItem;
            this.f19P.mIsSingleChoice = true;
            return this;
        }

        public Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem, OnClickListener listener) {
            this.f19P.mAdapter = adapter;
            this.f19P.mOnClickListener = listener;
            this.f19P.mCheckedItem = checkedItem;
            this.f19P.mIsSingleChoice = true;
            return this;
        }

        public Builder setOnItemSelectedListener(OnItemSelectedListener listener) {
            this.f19P.mOnItemSelectedListener = listener;
            return this;
        }

        public Builder setView(int layoutResId) {
            this.f19P.mView = null;
            this.f19P.mViewLayoutResId = layoutResId;
            this.f19P.mViewSpacingSpecified = false;
            return this;
        }

        public Builder setView(View view) {
            this.f19P.mView = view;
            this.f19P.mViewLayoutResId = 0;
            this.f19P.mViewSpacingSpecified = false;
            return this;
        }

        @RestrictTo({Scope.LIBRARY_GROUP})
        @Deprecated
        public Builder setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
            this.f19P.mView = view;
            this.f19P.mViewLayoutResId = 0;
            this.f19P.mViewSpacingSpecified = true;
            this.f19P.mViewSpacingLeft = viewSpacingLeft;
            this.f19P.mViewSpacingTop = viewSpacingTop;
            this.f19P.mViewSpacingRight = viewSpacingRight;
            this.f19P.mViewSpacingBottom = viewSpacingBottom;
            return this;
        }

        @Deprecated
        public Builder setInverseBackgroundForced(boolean useInverseBackground) {
            this.f19P.mForceInverseBackground = useInverseBackground;
            return this;
        }

        @RestrictTo({Scope.LIBRARY_GROUP})
        public Builder setRecycleOnMeasureEnabled(boolean enabled) {
            this.f19P.mRecycleOnMeasure = enabled;
            return this;
        }

        public AlertDialog create() {
            AlertDialog dialog = new AlertDialog(this.f19P.mContext, this.mTheme);
            this.f19P.apply(dialog.mAlert);
            dialog.setCancelable(this.f19P.mCancelable);
            if (this.f19P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(this.f19P.mOnCancelListener);
            dialog.setOnDismissListener(this.f19P.mOnDismissListener);
            if (this.f19P.mOnKeyListener != null) {
                dialog.setOnKeyListener(this.f19P.mOnKeyListener);
            }
            return dialog;
        }

        public AlertDialog show() {
            AlertDialog dialog = create();
            dialog.show();
            return dialog;
        }
    }

    protected AlertDialog(@NonNull Context context) {
        this(context, 0);
    }

    protected AlertDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, resolveDialogTheme(context, themeResId));
        this.mAlert = new AlertController(getContext(), this, getWindow());
    }

    protected AlertDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        this(context, 0);
        setCancelable(cancelable);
        setOnCancelListener(cancelListener);
    }

    static int resolveDialogTheme(@NonNull Context context, @StyleRes int resid) {
        if (resid >= 16777216) {
            return resid;
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(C0289R.attr.alertDialogTheme, outValue, true);
        return outValue.resourceId;
    }

    public Button getButton(int whichButton) {
        return this.mAlert.getButton(whichButton);
    }

    public ListView getListView() {
        return this.mAlert.getListView();
    }

    public void setTitle(CharSequence title) {
        super.setTitle(title);
        this.mAlert.setTitle(title);
    }

    public void setCustomTitle(View customTitleView) {
        this.mAlert.setCustomTitle(customTitleView);
    }

    public void setMessage(CharSequence message) {
        this.mAlert.setMessage(message);
    }

    public void setView(View view) {
        this.mAlert.setView(view);
    }

    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight, int viewSpacingBottom) {
        this.mAlert.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom);
    }

    /* access modifiers changed from: 0000 */
    public void setButtonPanelLayoutHint(int layoutHint) {
        this.mAlert.setButtonPanelLayoutHint(layoutHint);
    }

    public void setButton(int whichButton, CharSequence text, Message msg) {
        this.mAlert.setButton(whichButton, text, null, msg);
    }

    public void setButton(int whichButton, CharSequence text, OnClickListener listener) {
        this.mAlert.setButton(whichButton, text, listener, null);
    }

    public void setIcon(int resId) {
        this.mAlert.setIcon(resId);
    }

    public void setIcon(Drawable icon) {
        this.mAlert.setIcon(icon);
    }

    public void setIconAttribute(int attrId) {
        TypedValue out = new TypedValue();
        getContext().getTheme().resolveAttribute(attrId, out, true);
        this.mAlert.setIcon(out.resourceId);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAlert.installContent();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (this.mAlert.onKeyDown(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (this.mAlert.onKeyUp(keyCode, event)) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
