package com.imin.printer;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.widget.Button;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;


public class ButtonAdapter extends BaseRecyclerAdapter<TestBean> {
    private Button bt_item;
    private OnClickListener onClickListener;
    private int itemHeight, itemWidth, intervalPx;


    public ButtonAdapter(List<TestBean> data, Context context) {
        super(data);
    }

    @Override
    protected int getLayoutResId(int position) {
        return R.layout.item_print_button;
    }

    @Override
    protected void bindContent(BaseViewHolder holder, final int i) {
        View itemView = holder.itemView;
        bt_item = itemView.findViewById(R.id.bt_item);
        bt_item.setText(data.get(i).getTitle());
        bt_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(v,data.get(i).getId(), data.get(i).getTitle());
                }
            }
        });

    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
