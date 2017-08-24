package com.optimustechproject2017.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.optimustechproject2017.Dialogs.LoginDialogFragment;
import com.optimustechproject2017.Interfaces.LoginDialogInterface;
import com.optimustechproject2017.R;
import com.optimustechproject2017.SettingsMy;
import com.optimustechproject2017.SplashActivity;
import com.optimustechproject2017.User;

public class AccountsFragment extends android.support.v4.app.Fragment {
    private FirebaseAuth auth;

    private ExpandableRelativeLayout mExpandLayout;

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
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {

            login();


        }
        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        View fv = inflater.inflate(R.layout.activity_accounts_fragment, container, false);

        Toolbar toolbar = (Toolbar) fv.findViewById(R.id.toolbar);

        TextView name = (TextView) fv.findViewById(R.id.Name);
        TextView Email = (TextView) fv.findViewById(R.id.Email);


        if (auth.getCurrentUser() != null) {
            name.setText(auth.getCurrentUser().getDisplayName());
            Email.setText(auth.getCurrentUser().getEmail());


        }


//        mExpandButton = (Button) findViewById(R.id.expandButton);
        mExpandLayout = (ExpandableRelativeLayout) fv.findViewById(R.id.expandableLayout);
        mExpandLayout.collapse();
        RelativeLayout rl = (RelativeLayout) fv.findViewById(R.id.account);
        RelativeLayout logout = (RelativeLayout) fv.findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SplashActivity.class));

                SettingsMy.removeprefs();
                auth.signOut();
                getActivity().finish();
            }
        });
        rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandLayout.toggle();

            }
        });






        return fv;

    }


    private void launchUserSpecificFragment(LoginDialogInterface loginListener, Context context) {
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
//            context.startActivity(new Intent(context, CartActivity.class));

        } else {
            DialogFragment loginDialogFragment = LoginDialogFragment.newInstance(loginListener);
            loginDialogFragment.show(getFragmentManager(), LoginDialogFragment.class.getSimpleName());
        }
    }

    public void login() {

        launchUserSpecificFragment(new LoginDialogInterface() {
            @Override
            public void successfulLoginOrRegistration(User user) {
                // If signuppref was successful launch CartActivity.

            }
        }, getActivity());


        if (auth.getCurrentUser() == null) {
//            context.startActivity(new Intent(context, CartActivity.class));

            getActivity().getSupportFragmentManager().popBackStack();


        }

    }

}
