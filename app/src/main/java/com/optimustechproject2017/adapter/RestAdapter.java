package com.optimustechproject2017.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.optimustechproject2017.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class RestAdapter extends RecyclerView.Adapter<RestAdapter.MyViewHolder> implements Filterable {

    private List<Movie> moviesList;
    private Context mContext;


    private List<Movie> mFilteredList;

//    private List<Movie> SearchList;


    public RestAdapter(List<Movie> moviesList, Context mContext) {
        this.moviesList = moviesList;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_rest, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        holder.title.setText(movie.getTitle());

//        Glide.with(mContext).load(movie.getURL()).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                System.out.print(charSequence);
                String charString = charSequence.toString();

                if (charString.isEmpty()) {

                    mFilteredList = moviesList;
                } else {
                    ArrayList<Movie> filteredList = new ArrayList<>();

                    for (Movie movie : moviesList) {

                        if (movie.getTitle().toLowerCase().contains(charString)) {

                            filteredList.add(movie);


                        }
                    }

                    mFilteredList = filteredList;


                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mFilteredList = (List<Movie>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public void updateList(List<Movie> list) {
        moviesList = list;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;


        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.item_name);
            thumbnail = (ImageView) view.findViewById(R.id.catImage);

        }
    }
}