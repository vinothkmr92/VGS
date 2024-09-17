package com.imin.printer;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    protected List<T> data;
    protected OnClickListener onClickListener;


    public BaseRecyclerAdapter(List<T> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(getLayoutResId(i), viewGroup, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int i) {
        bindContent(holder, i);
    }

    protected abstract int getLayoutResId(int position);

    protected abstract void bindContent(BaseViewHolder holder, int i);

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


}
