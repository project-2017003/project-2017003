package com.optimustechproject2017;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.optimustechproject2017.Adapters.ClickListener;
import com.optimustechproject2017.Adapters.RecyclerTouchListener;
import com.optimustechproject2017.adapter.HeaderRecyclerViewSectioncart;
import com.optimustechproject2017.adapter.ItemObject;
import com.optimustechproject2017.payment.UIActivity;
import com.optimustechproject2017.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import timber.log.Timber;

/**
 * Created by HemanthKandula on 7/21/2017.
 */

public class CartActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private View emptyCart;
    private View cartFooter;


    private RecyclerView sectionHeader;
    private SectionedRecyclerViewAdapter sectionAdapter;


    private RecyclerView cartRecycler;
//    private CartRecyclerAdapter cartRecyclerAdapter;

    // Footer views and variables
    private TextView cartItemCountTv;
    private TextView cartTotalPriceTv;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransitionExit();
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    protected void overridePendingTransitionExit() {
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cart);
        Timber.d("%s - onCreateView", this.getClass().getSimpleName());

        progressDialog = Utils.generateProgressDialog(getApplicationContext(), false);

        Intent intent = getIntent();
        String response = intent.getStringExtra("response");


        Log.d("cartresp2", response);
        sectionHeader = (RecyclerView) findViewById(R.id.cart_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        sectionHeader.setLayoutManager(linearLayoutManager);
        sectionHeader.setHasFixedSize(true);
        HeaderRecyclerViewSectioncart firstSection = new HeaderRecyclerViewSectioncart("Cart", getcat(response), CartActivity.this);

        sectionAdapter = new SectionedRecyclerViewAdapter();
        sectionAdapter.addSection(firstSection);

        sectionHeader.setAdapter(sectionAdapter);


        sectionHeader.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), sectionHeader, new ClickListener() {
            @Override
            public void onClick(View view, int position) {


                // TestRecyclerViewAdapter album = mContentItems.get(position);


//                Intent Cl = new Intent(getContext(), ProductDetail.class);
//                Cl.putExtra("name","name");
//                Cl.putExtra("no",position);
//
//                startActivity(Cl);

            }


            @Override
            public void onLongClick(View view, int position) {
                //Album album = albumList.get(position);

                // Toast.makeText(getApplicationContext(), album.getName() + " is selected!", Toast.LENGTH_SHORT).show();

            }
        }));



        //        prepareCartRecycler(view);

        emptyCart = findViewById(R.id.cart_empty);
        View emptyCartAction =findViewById(R.id.cart_empty_action);
        emptyCartAction.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                // Just open drawer menu.

            }
        });

        cartFooter =findViewById(R.id.cart_footer);
        cartItemCountTv = (TextView) findViewById(R.id.cart_footer_quantity);
        cartTotalPriceTv = (TextView) findViewById(R.id.cart_footer_price);


//        view.findViewById(R.id.cart_footer_action).setOnClickListener(new OnSingleClickListener() {
//            @Override
//            public void onSingleClick(View view) {
//                DiscountDialogFragment discountDialog = DiscountDialogFragment.newInstance(new RequestListener() {
//                    @Override
//                    public void requestSuccess(long newId) {
//                        getCartContent();
//                    }
//
//                    @Override
//                    public void requestFailed(VolleyError error) {
//                        MsgUtils.logAndShowErrorMessage(getActivity(), error);
//                    }
//                });
//
//                if (discountDialog != null) {
//                    discountDialog.show(getFragmentManager(), DiscountDialogFragment.class.getSimpleName());
//                }
//            }
//        });

        Button order = (Button) findViewById(R.id.cart_order);
        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(new Intent(getApplicationContext(), UIActivity.class));
            }
        });

//        getCartContent();




    }




    private List<ItemObject> getcat(String response) {
        List<ItemObject> data = new ArrayList<ItemObject>();

        try {


            JSONArray arr = new JSONArray(response);


            System.out.println(arr.length());
            if (arr.length() != 0) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);


                    data.add(new ItemObject(obj.get("ItemID").toString(), obj.get("ItemName").toString(), obj.get("itemdetails").toString(), obj.get("Price").toString(), obj.get("Quant").toString()));


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







}
