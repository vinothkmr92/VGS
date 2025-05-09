package android.support.design.widget;

import android.content.Context;
import android.support.p006v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class TextInputEditText extends AppCompatEditText {
    public TextInputEditText(Context context) {
        super(context);
    }

    public TextInputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextInputEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection ic = super.onCreateInputConnection(outAttrs);
        if (ic != null && outAttrs.hintText == null) {
            ViewParent parent = getParent();
            while (true) {
                if (!(parent instanceof View)) {
                    break;
                } else if (parent instanceof TextInputLayout) {
                    outAttrs.hintText = ((TextInputLayout) parent).getHint();
                    break;
                } else {
                    parent = parent.getParent();
                }
            }
        }
        return ic;
    }
}
