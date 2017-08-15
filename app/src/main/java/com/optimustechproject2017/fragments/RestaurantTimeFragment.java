package com.optimustechproject2017.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.optimustechproject2017.Fab;
import com.optimustechproject2017.R;
import com.optimustechproject2017.SettingsMy;
import com.optimustechproject2017.adapter.HeaderRecyclerViewSection;
import com.optimustechproject2017.adapter.ItemObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * Created by HemanthKandula on 7/12/2017.
 */

//Our class extending fragment
public class RestaurantTimeFragment extends Fragment {
    MaterialSheetFab<Fab> materialSheetFab;
    LinearLayoutManager linearLayoutManager;
    HashSet numberofcats;
    private RecyclerView sectionHeader;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private int statusBarColor;
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


        final String response = getArguments().getString("response", "None");


        setrestitems(response);
        SettingsMy.setRestarentdet(response);


        sectionHeader = (RecyclerView)fv.findViewById(R.id.recycler_view_items);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        sectionHeader.setLayoutManager(linearLayoutManager);
        sectionHeader.setHasFixedSize(false);

        sectionAdapter = new SectionedRecyclerViewAdapter();

        final ListView listview = (ListView) fv.findViewById(R.id.listviews);
        final ArrayList<String> list = new ArrayList<String>();


        for (Object s : numberofcats) {
            System.out.println("Value: " + s.toString());
            System.out.println(getcat(s.toString(), response).size());
            list.add(s.toString());

            sectionAdapter.addSection(new HeaderRecyclerViewSection(s.toString(), getcat(s.toString(), response), getActivity()));
//            sectionAdapter.addSection(ss);

        }


        ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(), R.layout.simplerow, list);
        listview.setAdapter(listAdapter);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Toast.makeText(getApplicationContext(),
//                        " " + position, Toast.LENGTH_LONG)
//                        .show();

                linearLayoutManager.scrollToPosition(getcat(list.get(position), response).size() * position);

            }
        });

        sectionHeader.setOnClickListener(null);


//        HeaderRecyclerViewSection ss =  new HeaderRecyclerViewSection("VEG",getDataSource());
//        sectionAdapter.addSection(ss);

//        HeaderRecyclerViewSection firstSection = new HeaderRecyclerViewSection("Most Popular", getDataSource());
//        HeaderRecyclerViewSection secondSection = new HeaderRecyclerViewSection("VEG", getDataSource());
//        HeaderRecyclerViewSection thirdSection = new HeaderRecyclerViewSection("Non-Veg", getDataSource());
//        HeaderRecyclerViewSection fourth = new HeaderRecyclerViewSection("Snacks", getDataSource());
//        sectionAdapter.addSection(firstSection);
//        sectionAdapter.addSection(secondSection);
//        sectionAdapter.addSection(thirdSection);
//        sectionAdapter.addSection(fourth);
//


        sectionHeader.setAdapter(sectionAdapter);


        setupFab(fv);


//  sectionHeader.addOnItemTouchListener(new RecyclerTouchListener(getContext(), sectionHeader, new ClickListener() {


//            @Override
//            public void onClick(View view, int position) {
//
//
//
//                // TestRecyclerViewAdapter album = mContentItems.get(position);
//
//
////                Intent Cl = new Intent(getContext(), ProductDetail.class);
////                Cl.putExtra("name","name");
////                Cl.putExtra("no",position);
////
////                startActivity(Cl);
//
//       }
//
//
//
//            @Override
//            public void onLongClick(View view, int position) {
//                //Album album = albumList.get(position);
//
//                // Toast.makeText(getApplicationContext(), album.getName() + " is selected!", Toast.LENGTH_SHORT).show();
//
//            }
//        }));





//        Fab fab = (Fab) fv.findViewById(R.id.fab);
//        View sheetView = fv.findViewById(R.id.fab_sheet);
//        View overlay = fv.findViewById(R.id.overlay);
//        int sheetColor = getResources().getColor(R.color.white);
//        int fabColor = getResources().getColor(R.color.black_transparent_70percent);
//
//        // Initialize material sheet FAB
////        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
////                sheetColor, fabColor);
//
//setupFab(fv);
        return fv;

    }

    private void setrestitems(String response) {


        try {
            JSONArray arr = new JSONArray(response);


            numberofcats = new HashSet();
//            numberofcats1= new ArrayList<>();
//
            System.out.println(arr.length());
            if (arr.length() != 0) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);

                    numberofcats.add(obj.get("Cat").toString());


                }


//                updateMySQLSyncSts(gson.toJson(Eventsynclist));

            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void setupFab(View fablaout) {

        Fab fab = (Fab) fablaout.findViewById(R.id.fab);
        View sheetView = fablaout.findViewById(R.id.fab_sheet);
        View overlay = fablaout.findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.white);
        int fabColor = getResources().getColor(R.color.black_transparent_70percent);

        // Create material sheet FAB
        MaterialSheetFab<Fab> materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        // Set material sheet event listener
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Save current status bar color
                statusBarColor = getStatusBarColor();
                // Set darker status bar color to match the dim overlay
                setStatusBarColor(getResources().getColor(R.color.cardview_dark_background));
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
                setStatusBarColor(statusBarColor);
            }
        });


    }


//    public void Scroll(int pos) {
// //        linearLayoutManager = new LinearLayoutManager(getActivity());
////        sectionHeader.setLayoutManager(linearLayoutManager);
////        sectionHeader.setHasFixedSize(true);
////        HeaderRecyclerViewSection firstSection = new HeaderRecyclerViewSection("Most Popular", getDataSource());
////        HeaderRecyclerViewSection secondSection = new HeaderRecyclerViewSection("VEG", getDataSource());
////        HeaderRecyclerViewSection thirdSection = new HeaderRecyclerViewSection("Non-Veg", getDataSource());
////        HeaderRecyclerViewSection fourth = new HeaderRecyclerViewSection("Snacks", getDataSource());
////        sectionAdapter = new SectionedRecyclerViewAdapter();
////        sectionAdapter.addSection(firstSection);
////        sectionAdapter.addSection(secondSection);
////        sectionAdapter.addSection(thirdSection);
////        sectionAdapter.addSection(fourth);
////        sectionHeader.setAdapter(sectionAdapter);
//
//
//        linearLayoutManager.scrollToPosition(getDataSource().size() * pos);
//
//
//    }
//
//
//    private List<ItemObject> getDataSource(){
//        List<ItemObject> data = new ArrayList<ItemObject>();
//
//        data.add(new ItemObject("RestaurantName","details aboit it ","Rs 100"));
//        data.add(new ItemObject("RestaurantName","details aboit it ","Rs 150"));
//
////        data.add(new ItemObject("This is the item content in the first position"));
////        data.add(new ItemObject("This is the item content in the second position"));
//        return data;
//    }


    private List<ItemObject> getcat(String catname, String response) {
        List<ItemObject> data = new ArrayList<ItemObject>();

        try {


            JSONArray arr = new JSONArray(response);


            System.out.println(arr.length());
            if (arr.length() != 0) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);

                    if (obj.get("Cat").toString().equals(catname)) {
                        data.add(new ItemObject(obj.get("ID").toString(), obj.get("ItemName").toString(), obj.get("itemdetails").toString(), obj.get("Price").toString()));


                    }
                }


//                updateMySQLSyncSts(gson.toJson(Eventsynclist));

            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


//
//
//        data.add(new ItemObject("RestaurantName","details aboit it ","Rs 100"));
//        data.add(new ItemObject("RestaurantName","details aboit it ","Rs 150"));

//        data.add(new ItemObject("This is the item content in the first position"));
//        data.add(new ItemObject("This is the item content in the second position"));
        return data;
    }

    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getActivity().getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(color);
        }
    }


}

