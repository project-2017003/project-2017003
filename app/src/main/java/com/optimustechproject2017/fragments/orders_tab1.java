package com.optimustechproject2017.fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optimustechproject2017.R;

/**
 * Created by HemanthKandula on 7/12/2017.
 */

//Our class extending fragment
public class orders_tab1 extends Fragment {
    public orders_tab1(){

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
        return inflater.inflate(R.layout.ordertabe_1, container, false);
    }
}