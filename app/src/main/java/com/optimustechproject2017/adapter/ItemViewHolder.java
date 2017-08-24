package com.optimustechproject2017.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimustechproject2017.R;


/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder{
    public TextView itemContent,Smalldetails,Price,quant;
    public ImageView ItemImage;
    public Button incr,decr;
    public ItemViewHolder(View itemView) {
        super(itemView);
        itemContent = (TextView)itemView.findViewById(R.id.item_name);
        Smalldetails = (TextView)itemView.findViewById(R.id.small_details);
        ItemImage = (ImageView) itemView.findViewById(R.id.item_image);
        Price = (TextView)itemView.findViewById(R.id.price);
        quant = (TextView)itemView.findViewById(R.id.quantity_text_view);
        quant.setText("0");

        incr=(Button)itemView.findViewById(R.id.incre);
        decr=(Button)itemView.findViewById(R.id.decre);






    }
}