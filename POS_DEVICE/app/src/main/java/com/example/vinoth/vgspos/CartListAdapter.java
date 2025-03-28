package com.example.vinoth.vgspos;
import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;



public  class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.ViewHolder> {
    private ArrayList<ItemsCart> callListResponses = new ArrayList<ItemsCart>();
    final List templist=new ArrayList<>();
    private Activity context;
    int lastPosition=0;
    public CartListAdapter(Activity context, ArrayList<ItemsCart> callListResponses)
    {
        super();
        this.context = context;
        this.callListResponses=callListResponses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.singleitemlayout, parent, false);

        return new ViewHolder(itemView);
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ItemsCart call = (ItemsCart) callListResponses.get(position);

        holder.itemname.setText(call.getItem_Name());
        holder.tv_quantity.setText(String.valueOf(call.getQty()));

        holder.cart_minus_img.setOnClickListener(new QuantityListener(context, holder.tv_quantity,call,false));
        holder.cart_plus_img.setOnClickListener(new QuantityListener(context, holder.tv_quantity,call,true));
        holder.img_deleteitem.setOnClickListener(new DeleteItemListener(context,call,this));
        //this.notifyItemInserted(position);
    }

    @Override
    public long getItemId(int position) {
        return this.callListResponses == null ? 0 : this.callListResponses.get(position).getItem_No();
    }
    //Animating single element
    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_to_right);
            viewToAnimate.startAnimation(animation);
            lastPosition=position;
        }
        position++;
    }

    @Override
    public int getItemCount() {
        return QuantityListener.itemsCarts.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemname, tv_quantity;
        ImageView cart_minus_img, cart_plus_img,img_deleteitem;

        public ViewHolder(View itemView) {
            super(itemView);
            cart_minus_img=(ImageView) itemView.findViewById(R.id.cart_minus_img);
            cart_plus_img=(ImageView) itemView.findViewById(R.id.cart_plus_img);
            img_deleteitem=(ImageView) itemView.findViewById(R.id.img_deleteitem);
            itemname=(TextView) itemView.findViewById(R.id.tv_name);
            tv_quantity=(TextView) itemView.findViewById(R.id.tv_qty);

        }
    }



}