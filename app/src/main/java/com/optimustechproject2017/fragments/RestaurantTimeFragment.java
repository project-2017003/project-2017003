package com.optimustechproject2017.fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.optimustechproject2017.Fab;
import com.optimustechproject2017.R;
import com.optimustechproject2017.adapter.HeaderRecyclerViewSection;
import com.optimustechproject2017.adapter.ItemObject;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by HemanthKandula on 7/12/2017.
 */

//Our class extending fragment
public class RestaurantTimeFragment extends Fragment {
    MaterialSheetFab<Fab> materialSheetFab;
    private RecyclerView sectionHeader;
    private SectionedRecyclerViewAdapter sectionAdapter;

    public RestaurantTimeFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating


        View fv= inflater.inflate(R.layout.fragment_restaurent_time, container, false);



        sectionHeader = (RecyclerView)fv.findViewById(R.id.recycler_view_items);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        sectionHeader.setLayoutManager(linearLayoutManager);
        sectionHeader.setHasFixedSize(true);
        HeaderRecyclerViewSection firstSection = new HeaderRecyclerViewSection("Most Popular", getDataSource());
        HeaderRecyclerViewSection secondSection = new HeaderRecyclerViewSection("Cat1", getDataSource());
        HeaderRecyclerViewSection thirdSection = new HeaderRecyclerViewSection("CAT2", getDataSource());
        sectionAdapter = new SectionedRecyclerViewAdapter();
        sectionAdapter.addSection(firstSection);
        sectionAdapter.addSection(secondSection);
        sectionAdapter.addSection(thirdSection);
        sectionHeader.setAdapter(sectionAdapter);


        Fab fab = (Fab) fv.findViewById(R.id.fab);
        View sheetView = fv.findViewById(R.id.fab_sheet);
        View overlay = fv.findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.white);
        int fabColor = getResources().getColor(R.color.black_transparent_70percent);

        // Initialize material sheet FAB
//        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
//                sheetColor, fabColor);


        return fv;

    }



    private List<ItemObject> getDataSource(){
        List<ItemObject> data = new ArrayList<ItemObject>();

        data.add(new ItemObject("RestaurantName","details aboit it ","Rs 100"));
        data.add(new ItemObject("RestaurantName","details aboit it ","Rs 150"));

//        data.add(new ItemObject("This is the item content in the first position"));
//        data.add(new ItemObject("This is the item content in the second position"));
        return data;
    }




}

