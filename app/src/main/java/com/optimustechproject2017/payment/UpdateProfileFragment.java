package com.optimustechproject2017.payment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.classes.CitrusUMResponse;
import com.citrus.sdk.response.CitrusError;
import com.optimustechproject2017.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CitrusClient citrusClient = null;
    private Context context = null;

    private EditText edtFirstName = null;
    private EditText edtLatName = null;

    private Button btnUpdateProfile = null;


    public UpdateProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UpdateProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateProfileFragment newInstance() {
        UpdateProfileFragment fragment = new UpdateProfileFragment();
        Bundle args = new Bundle();
     /*   args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_updateprofile, container, false);
        ActionBar actionBar = ((UIActivity) getActivity()).getSupportActionBar();

        context = getActivity();

        citrusClient = CitrusClient.getInstance(context.getApplicationContext());
       /* actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Update Profile");*/
        edtFirstName = (EditText) rootView.findViewById(R.id.edtfirstname);
        edtLatName = (EditText) rootView.findViewById(R.id.edtLastName);
        btnUpdateProfile = (Button) rootView.findViewById(R.id.btnUpdateProfile);
        citrusClient.getProfileInfo(new Callback<CitrusUser>() {
            @Override
            public void success(CitrusUser citrusUser) {
                edtFirstName.setText(citrusUser.getFirstName());
                edtLatName.setText(citrusUser.getLastName());
            }

            @Override
            public void error(CitrusError error) {

            }
        });
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                citrusClient.updateProfileInfo(edtFirstName.getText().toString(), edtLatName.getText().toString(), new Callback<CitrusUMResponse>() {
                    @Override
                    public void success(CitrusUMResponse citrusUMResponse) {
                        Toast.makeText(context, citrusUMResponse.getJSON().toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void error(CitrusError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        return rootView;

    }

}
