package com.optimustechproject2017.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.optimustechproject2017.R;
import com.optimustechproject2017.adapter.Movie;
import com.optimustechproject2017.adapter.MoviesAdapter;
import com.optimustechproject2017.api.EndPoints;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Search_fragment extends android.support.v4.app.Fragment {
    ProgressDialog prgDialog;

    AVLoadingIndicatorView avi;

    private MoviesAdapter mAdapter;
    private List<Movie> mContentItems = new ArrayList<>();
    private RecyclerView mRecycler;


    public static Search_fragment newInstance() {
        Search_fragment fragment = new Search_fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }
    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        View fv = inflater.inflate(R.layout.activity_search_fragment, container, false);

//        Toolbar toolbar = (Toolbar) fv.findViewById(R.id.toolbar);

        mRecycler = (RecyclerView) fv.findViewById(R.id.card_recycler_view);

        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        mRecycler.setLayoutManager(layoutManager);

        mAdapter = new MoviesAdapter(mContentItems, getActivity());


        //mAdapter = new RecyclerViewMaterialAdapter();
        mRecycler.setAdapter(mAdapter);

        SearchView searchView = (SearchView) fv.findViewById(R.id.search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // mAdapter.getFilter().filter(query);
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // mAdapter.getFilter().filter(newText);

                filter(newText);
                return false;
            }
        });
//        EditText editText =(EditText )fv.findViewById(R.id.search_bar) ;

//        editText.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void afterTextChanged(Editable s) {}
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start,
//                                          int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start,
//                                      int before, int count) {
//
//                Log.d("hhh",s.toString());
//        mAdapter.getFilter().filter(s);
//            }
//        });


//        avi  = (AVLoadingIndicatorView) fv.findViewById(R.id.avi);


        if (mContentItems.size() == 0) {
            getcats();
        }


        return fv;

    }


//
//
//    void startAnim(){
//        avi.show();
//        // or avi.smoothToShow();
//    }
//
//    void stopAnim(){
//        avi.hide();
//        // or avi.smoothToHide();
//    }


    public void getcats() {


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        //progressDialog.show();

//startAnim();

        // Log.d("event",Clustername);
        client.post(EndPoints.Category, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //progressDialog.hide();
                System.out.println(response);

                setcat(response);
                //stopAnim();
                // System.out.println(response);

            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {

                // stopAnim();
                // TODO Auto-generated method stub
                //progressDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), " Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void setcat(String response) {

        try {
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            if (arr.length() != 0) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);


                    Movie a = new Movie(obj.get("CAT").toString(),
                            obj.get("URL").toString()

                    );
                    mContentItems.add(a);


                }

                mAdapter.notifyDataSetChanged();

//                updateMySQLSyncSts(gson.toJson(Eventsynclist));

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.search_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);


        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);


//        getMenuInflater().inflate(R.menu.menu_main, menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    void filter(String text) {
        List<Movie> temp = new ArrayList();
        for (Movie d : mContentItems) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getTitle().toLowerCase().contains(text)) {
                temp.add(d);
            }
        }
        //update recyclerview
        mAdapter.updateList(temp);
    }




}
