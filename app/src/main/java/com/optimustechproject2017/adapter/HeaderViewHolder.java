package com.optimustechproject2017.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.optimustechproject2017.R;

/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class HeaderViewHolder extends RecyclerView.ViewHolder{
    public TextView headerTitle;
    public HeaderViewHolder(View itemView) {
        super(itemView);
        headerTitle = (TextView)itemView.findViewById(R.id.header_id);
    }
}