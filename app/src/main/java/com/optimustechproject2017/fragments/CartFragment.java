package com.optimustechproject2017.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.optimustechproject2017.MainActivity;
import com.optimustechproject2017.R;
import com.optimustechproject2017.utils.MsgUtils;
import com.optimustechproject2017.utils.Utils;

import timber.log.Timber;

/**
 * Created by HemanthKandula on 7/21/2017.
 */

public class CartFragment extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private View emptyCart;
    private View cartFooter;

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
                if (getApplicationContext() instanceof MainActivity) {
//                    ((MainActivity) getActivity()).onOrderCreateSelected();
                }
            }
        });

//        getCartContent();




    }




}
