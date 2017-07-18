package com.optimustechproject2017.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.optimustechproject2017.R;

/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder{
    public TextView itemContent,Smalldetails,Price;
    public ItemViewHolder(View itemView) {
        super(itemView);
        itemContent = (TextView)itemView.findViewById(R.id.item_name);
        Smalldetails = (TextView)itemView.findViewById(R.id.small_details);
        Price = (TextView)itemView.findViewById(R.id.price);




    }
}