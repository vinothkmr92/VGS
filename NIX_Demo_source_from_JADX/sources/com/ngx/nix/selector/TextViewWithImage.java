package com.ngx.nix.selector;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class TextViewWithImage extends LinearLayout {
    private ImageView mImage;
    private TextView mText;

    public TextViewWithImage(Context context) {
        super(context);
        setOrientation(0);
        this.mImage = new ImageView(context);
        this.mText = new TextView(context);
        LayoutParams lp = new LayoutParams(0, -2, 1.0f);
        lp.weight = 1.0f;
        addView(this.mImage, lp);
        addView(this.mText, new LayoutParams(0, -2, 3.0f));
    }

    public CharSequence getText() {
        return this.mText.getText();
    }

    public void setImageResource(int resId) {
        if (resId == -1) {
            this.mImage.setVisibility(8);
        } else {
            this.mImage.setImageResource(resId);
        }
    }

    public void setText(String aText) {
        this.mText.setText(aText);
    }
}
