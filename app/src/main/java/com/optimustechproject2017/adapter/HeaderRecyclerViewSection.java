package com.optimustechproject2017.adapter;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.optimustechproject2017.R;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import me.himanshusoni.quantityview.QuantityView;


/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class HeaderRecyclerViewSection extends StatelessSection {
    private static final String TAG = HeaderRecyclerViewSection.class.getSimpleName();
    private String title;

    private List<ItemObject> list;
    public HeaderRecyclerViewSection(String title, List<ItemObject> list) {
        super(R.layout.header_layout, R.layout.item_list_row);

        this.title = title;
        this.list = list;

    }
    @Override
    public int getContentItemsTotal() {
        return list.size();
    }
    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }
    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder iHolder = (ItemViewHolder)holder;
        iHolder.itemContent.setText(list.get(position).getContents());
        iHolder.Smalldetails.setText(list.get(position).getSmalldetails());
        iHolder.Price.setText(list.get(position).getPrice());


        iHolder.incr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity= Integer.valueOf( iHolder.quant.getText().toString());
                if(quantity<=10)
                    quantity+=1;

                iHolder.quant.setText(quantity+"");
            }
        });
        iHolder.decr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity= Integer.valueOf( iHolder.quant.getText().toString());
                if(quantity>0)
                    quantity-=1;
                iHolder.quant.setText(quantity+"");            }
        });

    }
    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder hHolder = (HeaderViewHolder)holder;
        hHolder.headerTitle.setText(title);
    }
}