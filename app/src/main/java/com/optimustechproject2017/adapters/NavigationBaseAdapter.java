package com.optimustechproject2017.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimustechproject2017.R;

import java.util.ArrayList;




public class NavigationBaseAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<NavigationItems> listPojo;

    public NavigationBaseAdapter(Context context, ArrayList<NavigationItems> listPojo) {
        super();
        this.context = context;
        this.listPojo = listPojo;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listPojo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listPojo.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        MyViewHolder holder = null;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(
                    R.layout.row, null);

            holder = new MyViewHolder();

            holder.name = (TextView) convertView
                    .findViewById(R.id.title);



            // At here set animation by their position.. Means if position = 0.. then animation start on textview with i*50 = 0 startOffset


            convertView.setTag(holder);

        } else {

            holder = (MyViewHolder) convertView.getTag();
        }

        NavigationItems rowItem = (NavigationItems) getItem(position);


        holder.name.setText(rowItem.getName());


        return convertView;
    }

    class MyViewHolder {


        private TextView name;


    }


}
