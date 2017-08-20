package com.optimustechproject2017.fragments;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optimustechproject2017.R;

import java.util.ArrayList;

public class AccountsFragment extends android.support.v4.app.Fragment {

    public static AccountsFragment newInstance() {
        AccountsFragment fragment = new AccountsFragment();
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
        View fv = inflater.inflate(R.layout.activity_accounts_fragment, container, false);

        Toolbar toolbar = (Toolbar) fv.findViewById(R.id.toolbar);

        final ArrayList<String> list = new ArrayList<String>();

        list.add("liked restaurants");

        list.add("Past orders ");


        list.add("Account Settings ");


        return fv;

    }
}
