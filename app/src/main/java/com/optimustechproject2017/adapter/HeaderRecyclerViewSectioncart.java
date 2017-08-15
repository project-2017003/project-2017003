package com.optimustechproject2017.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.optimustechproject2017.Databases.SqliteDB;
import com.optimustechproject2017.Dialogs.LoginDialogFragment;
import com.optimustechproject2017.Interfaces.LoginDialogInterface;
import com.optimustechproject2017.R;
import com.optimustechproject2017.RestarentActivity;
import com.optimustechproject2017.User;
import com.optimustechproject2017.api.EndPoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class HeaderRecyclerViewSectioncart extends StatelessSection {
    private static final String TAG = HeaderRecyclerViewSectioncart.class.getSimpleName();
    SqliteDB sql;
    ProgressDialog prgDialog;
    Context context;
    private String title;
    private FirebaseAuth auth;
    private List<ItemObject> list;

    public HeaderRecyclerViewSectioncart(String title, List<ItemObject> list, Context context) {

        super(R.layout.header_layout, R.layout.item_list_row);

        this.title = title;
        this.list = list;
        this.context = context;
        sql = new SqliteDB(context);

        prgDialog = new ProgressDialog(context);
        prgDialog.setMessage(" Please wait...");
        prgDialog.setCancelable(false);


    }

    @Override
    public int getContentItemsTotal() {
        return list.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemViewHolder iHolder = (ItemViewHolder) holder;
        iHolder.itemContent.setText(list.get(position).getContents());
        iHolder.Smalldetails.setText(list.get(position).getSmalldetails());
        String cost = "₹" + list.get(position).getPrice();
        iHolder.Price.setText(cost);

        iHolder.quant.setText(list.get(position).getQuant());


        iHolder.incr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.valueOf(iHolder.quant.getText().toString());
                if (quantity <= 10)
                    quantity += 1;
                auth = FirebaseAuth.getInstance();

                Log.d("EMail", auth.getCurrentUser().getEmail());
                if (auth.getCurrentUser() != null) {
                    updatecart(quantity, auth.getCurrentUser().getEmail(), list.get(position).getID(), list.get(position).getPrice());
                    updatecart();
                } else {
                    launchUserSpecificFragment(new LoginDialogInterface() {
                        @Override
                        public void successfulLoginOrRegistration(User user) {
                            // If signuppref was successful launch CartActivity.

                        }
                    }, context);
                }

                iHolder.quant.setText(quantity + "");
            }
        });
        iHolder.decr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.valueOf(iHolder.quant.getText().toString());
                if (quantity > 0)
                    quantity -= 1;

                auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null) {
                    updatecart(quantity, auth.getCurrentUser().getEmail(), list.get(position).getID(), list.get(position).getPrice());
                    updatecart();
                } else {
                    launchUserSpecificFragment(new LoginDialogInterface() {
                        @Override
                        public void successfulLoginOrRegistration(User user) {
                            // If signuppref was successful launch CartActivity.

                        }
                    }, context);
                }

                iHolder.quant.setText(quantity + "");
            }
        });

    }

    private void updatecart(int quantity, String email, String ItemID, String price) {


        List<String> items = sql.getitems();
        HashMap<String, String> queryValues = new HashMap<String, String>();
        queryValues.put("ID", ItemID);
        queryValues.put("price", price);
        queryValues.put("Quant", quantity + "");
        queryValues.put("EmailPhone", email);


        if (!items.contains(ItemID)) {

            sql.insertcart(queryValues);
        } else {

            sql.updateUser(queryValues);
        }


    }


    private void updatecart() {

        final AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        Gson gson = new GsonBuilder().create();


        ArrayList<HashMap<String, String>> userList = sql.getAllcartItems();
        if (userList.size() != 0) {
            prgDialog.show();
            params.put("Cart", gson.toJson(sql.getAllcartItems()));
            System.out.println(gson.toJson(sql.getAllcartItems()));
            client.post(EndPoints.UpdateCart, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    System.out.println(response);
                    prgDialog.hide();


                }

                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    // TODO Auto-generated method stub
                    prgDialog.hide();
                    if (statusCode == 404) {
                        Toast.makeText(context, "Requested resource not found", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(context, "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Please connect  to Internet", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Toast.makeText(context, "No Food in cart ", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder hHolder = (HeaderViewHolder) holder;
        hHolder.headerTitle.setText(title);
    }


    private void launchUserSpecificFragment(LoginDialogInterface loginListener, Context context) {
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
//            context.startActivity(new Intent(context, CartActivity.class));

        } else {
            DialogFragment loginDialogFragment = LoginDialogFragment.newInstance(loginListener);
            loginDialogFragment.show(((RestarentActivity) context).getSupportFragmentManager(), LoginDialogFragment.class.getSimpleName());
        }
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