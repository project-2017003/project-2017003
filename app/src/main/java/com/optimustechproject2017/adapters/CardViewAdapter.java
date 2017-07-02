package com.optimustechproject2017.adapters;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.optimustechproject2017.R;

import java.util.ArrayList;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {

    private static ArrayList<FeedProperties> dataSet;

    public CardViewAdapter(ArrayList<FeedProperties> os_versions) {

        dataSet = os_versions;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
// create a new view
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.card_view, null);

        // create ViewHolder

        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        FeedProperties fp = dataSet.get(i);

        viewHolder.tvVersionName.setText(fp.getTitle());
        viewHolder.iconView.setImageResource(fp.getThumbnail());
       // viewHolder.iconView.setBackgroundResource(fp.getThumbnail());

        viewHolder.feed = fp;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvVersionName;
        public ImageView iconView;

        public FeedProperties feed;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);

            tvVersionName = (TextView) itemLayoutView
                    .findViewById(R.id.tvVersionName);
            iconView = (ImageView) itemLayoutView
                    .findViewById(R.id.iconId);

            itemLayoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                 /*   Intent intent = new Intent(v.getContext(), SecondPage.class);
                    v.getContext().startActivity(intent);
                    Toast.makeText(v.getContext(), "os version is: " + feed.getTitle(), Toast.LENGTH_SHORT).show();*/
                }
            });


        }

    }
}