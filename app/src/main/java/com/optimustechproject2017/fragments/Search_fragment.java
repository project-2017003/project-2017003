package com.optimustechproject2017.fragments;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optimustechproject2017.R;

public class Search_fragment extends android.support.v4.app.Fragment {

    public static Search_fragment newInstance() {
        Search_fragment fragment = new Search_fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        View fv = inflater.inflate(R.layout.activity_search_fragment, container, false);

        Toolbar toolbar = (android.support.v7.widget.Toolbar) fv.findViewById(R.id.toolbar);

        return fv;

    }
}
