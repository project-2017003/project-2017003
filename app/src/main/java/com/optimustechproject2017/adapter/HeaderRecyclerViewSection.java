package com.optimustechproject2017.adapter;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.optimustechproject2017.Databases.SqliteDB;
import com.optimustechproject2017.Dialogs.LoginDialogFragment;
import com.optimustechproject2017.Interfaces.LoginDialogInterface;
import com.optimustechproject2017.R;
import com.optimustechproject2017.RestarentActivity;
import com.optimustechproject2017.User;

import java.util.HashMap;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;


/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class HeaderRecyclerViewSection extends StatelessSection {
    private static final String TAG = HeaderRecyclerViewSection.class.getSimpleName();
    SqliteDB sql;
    Context context;
    private String title;
    private FirebaseAuth auth;
    private List<ItemObject> list;

    public HeaderRecyclerViewSection(String title, List<ItemObject> list, Context context) {

        super(R.layout.header_layout, R.layout.item_list_row);

        this.title = title;
        this.list = list;
        this.context = context;
        sql = new SqliteDB(context);


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
        final ItemViewHolder iHolder = (ItemViewHolder)holder;
        iHolder.itemContent.setText(list.get(position).getContents());
        iHolder.Smalldetails.setText(list.get(position).getSmalldetails());
        iHolder.Price.setText("₹" + list.get(position).getPrice());

//        iHolder.quant.setText(list.get(position).getQuant());


        iHolder.incr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity= Integer.valueOf( iHolder.quant.getText().toString());
                if(quantity<=10)
                    quantity+=1;


                RelativeLayout relativeLayout = (RelativeLayout) ((RestarentActivity) context).findViewById(R.id.gotocart);
                if (relativeLayout.getVisibility() == View.INVISIBLE)
                    relativeLayout.setVisibility(View.VISIBLE);


                TextView total = (TextView) ((RestarentActivity) context).findViewById(R.id.total_price);

                TextView count = (TextView) ((RestarentActivity) context).findViewById(R.id.cart_count);


                auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null)
                    updatecart(quantity, auth.getCurrentUser().getEmail(), list.get(position).getID(), list.get(position).getPrice());

                else {
                    launchUserSpecificFragment(new LoginDialogInterface() {
                        @Override
                        public void successfulLoginOrRegistration(User user) {
                            // If signuppref was successful launch CartActivity.

                        }
                    }, context);
                }

                iHolder.quant.setText(quantity+"");


                count.setText("" + sql.quantcount());
                total.setText("₹" + sql.totalprice());

            }
        });
        iHolder.decr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity= Integer.valueOf( iHolder.quant.getText().toString());
                if(quantity>0)
                    quantity-=1;


                RelativeLayout relativeLayout = (RelativeLayout) ((RestarentActivity) context).findViewById(R.id.gotocart);


                TextView total = (TextView) ((RestarentActivity) context).findViewById(R.id.total_price);

                TextView count = (TextView) ((RestarentActivity) context).findViewById(R.id.cart_count);


                auth = FirebaseAuth.getInstance();
                if (auth.getCurrentUser() != null)
                    updatecart(quantity, auth.getCurrentUser().getEmail(), list.get(position).getID(), list.get(position).getPrice());

                else {
                    launchUserSpecificFragment(new LoginDialogInterface() {
                        @Override
                        public void successfulLoginOrRegistration(User user) {
                            // If signuppref was successful launch CartActivity.

                        }
                    }, context);
                }

                iHolder.quant.setText(quantity + "");

                count.setText("" + sql.quantcount());
                total.setText("₹" + sql.totalprice());

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

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }
    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder hHolder = (HeaderViewHolder)holder;
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
}