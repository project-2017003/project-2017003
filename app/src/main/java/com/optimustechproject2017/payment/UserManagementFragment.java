/*
 *
 *    Copyright 2014 Citrus Payment Solutions Pvt. Ltd.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 * /
 */

package com.optimustechproject2017.payment;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.login.AccessType;
import com.citrus.sdk.login.AvailableLoginType;
import com.citrus.sdk.login.CitrusLoginApi;
import com.citrus.sdk.login.FindUserResponse;
import com.citrus.sdk.login.PasswordType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.optimustechproject2017.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserManagementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserManagementFragment extends Fragment implements View.OnClickListener {


    private static final long RESEND_TIMER = 15000;
    private UserManagementInteractionListener mListener = null;
    private Button btnLinkUser = null;
    private Button btnSignIn = null;
    private Button btnResend = null;
    private Button btnResetPassword = null;
    private EditText editOtp = null;
    private EditText editEmailId = null;
    private EditText editMobileNo = null;
    private EditText editPassword = null;
    private TextView textMessage = null;
    private CheckBox fullScopeCheckBox = null;
    private CheckBox limitedScopeCheckBox = null;
    private CitrusClient citrusClient = null;
    private Context context = null;
    private View rootView = null;
    private SharedPreferences prefs = null;
    private CitrusLoginApi citrusLoginApi;
    private FindUserResponse findUserResponse;

    public UserManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserManagementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserManagementFragment newInstance(Context context) {
        return new UserManagementFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_user_management, container, false);

        editOtp = (EditText) rootView.findViewById(R.id.edit_otp_id);
        editEmailId = (EditText) rootView.findViewById(R.id.edit_email_id);
        editMobileNo = (EditText) rootView.findViewById(R.id.edit_mobile_no);
        editPassword = (EditText) rootView.findViewById(R.id.edit_password);
        textMessage = (TextView) rootView.findViewById(R.id.txt_user_mgmt_message);

        btnLinkUser = (Button) rootView.findViewById(R.id.btn_login);
        btnSignIn = (Button) rootView.findViewById(R.id.btn_signin);
        btnResend = (Button) rootView.findViewById(R.id.btn_resend);

        btnResetPassword = (Button) rootView.findViewById(R.id.btn_reset_password);


        fullScopeCheckBox = (CheckBox) rootView.findViewById(R.id.fullScopeCheckBoxId);
        limitedScopeCheckBox = (CheckBox) rootView.findViewById(R.id.limitedCheckBox);

        fullScopeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                limitedScopeCheckBox.setChecked(!isChecked);
            }
        });

        limitedScopeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fullScopeCheckBox.setChecked(!isChecked);
            }
        });

        btnLinkUser.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
        btnResend.setOnClickListener(this);

        context = getActivity();

        citrusClient = CitrusClient.getInstance(context.getApplicationContext());

        prefs = PreferenceManager.getDefaultSharedPreferences(context);


        String prefferedEmailID = prefs.getString(Utils.getResourceString(context, R.string.pref_email_id_key), Utils.getResourceString(context, R.string.dummy_email_hint));
        String prefferedMobileNum = prefs.getString(Utils.getResourceString(context, R.string.pref_mobile_num_key), Utils.getResourceString(context, R.string.dummy_mobile_hint));

        if (!TextUtils.isEmpty(prefferedEmailID))
            editEmailId.setText(prefferedEmailID);
        if (!TextUtils.isEmpty(prefferedMobileNum))
            editMobileNo.setText(prefferedMobileNum);


        return rootView;
    }

    /**
     * Call Link User Extended API
     */
    private void doLogin() {
        String emailId = editEmailId.getText().toString();
        String mobileNo = editMobileNo.getText().toString();

        AccessType accessType = fullScopeCheckBox.isChecked() ? AccessType.FULL : AccessType.LIMITED;

        citrusLoginApi = new CitrusLoginApi.Builder(getActivity())
                .mobile(mobileNo)
                .email(emailId)
                .accessType(accessType)
                .environment(citrusClient.getEnvironment())
                .build();

        boolean useCitrusLoginScreen = prefs.getBoolean(getString(R.string.pref_show_citrus_login_screen), true);

        if (!useCitrusLoginScreen) {
            citrusLoginApi.setLoginProcessor(new CitrusLoginApi.LoginProcessor(getActivity()) {
                @Override
                public void onShowLoginScreen(FindUserResponse findUserResponse, CitrusLoginApi citrusLoginApi) {
                    UserManagementFragment.this.findUserResponse = findUserResponse;
                    modifyUIForPassword(findUserResponse);
                }

                @Override
                public PasswordType getPasswordType() {
                    String mOtpString = editOtp.getText().toString();
                    String passwordString = UserManagementFragment.this.editPassword.getText().toString();

                    PasswordType passwordType = null;

                    if (mOtpString.length() > 0) {
                        passwordType = PasswordType.MOTP;
                    } else if (passwordString.length() > 0) {
                        passwordType = PasswordType.PASSWORD;
                    }

                    return passwordType;
                }

                @Override
                public String getPasswordOrOTP() {
                    String mOtpString = editOtp.getText().toString();
                    String passwordString = editPassword.getText().toString();

                    String password = null;

                    if (mOtpString.length() > 0) {
                        password = mOtpString;
                    } else if (passwordString.length() > 0) {
                        password = passwordString;
                    }

                    return password;
                }
            });
        }

        citrusLoginApi.setListener(new CitrusLoginApi.CitrusLoginApiListener() {
            @Override
            public void onLoginSuccess() {
                mListener.onShowWalletScreen(false);
            }

            @Override
            public void onError(CitrusError error) {
                ((UIActivity) getActivity()).showSnackBar(error.getMessage());
                textMessage.setText(error.getMessage());
                clearPasswordFields();
            }

            @Override
            public void onLoginCancelled() {

            }
        });

        citrusClient.doLogin(citrusLoginApi);
    }

    /**
     * Perform Link User Extended Sign In.
     */
    private void signIn() {
        citrusLoginApi.proceed(findUserResponse);
    }


    private void resetPassword() {
        String emailId = editEmailId.getText().toString();

        citrusClient.resetPassword(emailId, new Callback<CitrusResponse>() {
            @Override
            public void success(CitrusResponse citrusResponse) {
                ((UIActivity) getActivity()).showSnackBar(citrusResponse.getMessage());
                textMessage.setText(citrusResponse.getMessage());
            }

            @Override
            public void error(CitrusError error) {
                ((UIActivity) getActivity()).showSnackBar(error.getMessage());
                textMessage.setText(error.getMessage());
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (UserManagementInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement UserManagementInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                doLogin();
                break;
            case R.id.btn_signin:
                signIn();
                break;
            case R.id.btn_reset_password:
                resetPassword();
                break;
            case R.id.btn_resend:
                doLogin();
                btnResend.setClickable(false);
                btnResend.setEnabled(false);
                break;
        }
    }

    private void clearPasswordFields() {
        editOtp.getText().clear();
        editPassword.getText().clear();
    }

    private void modifyUIForPassword(FindUserResponse findUserResponse) {

        AvailableLoginType availableLoginType = findUserResponse.getAvailableLoginType();
        if (availableLoginType != AvailableLoginType.LOGIN_NONE) {
            String linkUserMessage = findUserResponse.getLoginMessage();

            textMessage.setText(linkUserMessage);
            btnSignIn.setVisibility(View.VISIBLE);
            btnLinkUser.setVisibility(View.GONE);
            editOtp.setVisibility(View.VISIBLE);
            editPassword.setVisibility(View.VISIBLE);
            btnResend.setVisibility(View.VISIBLE);
            editEmailId.setEnabled(false);
            editEmailId.setHint("");
            editMobileNo.setEnabled(false);

            if (TextUtils.isEmpty(editEmailId.getText().toString())) {
                btnResetPassword.setVisibility(View.INVISIBLE);
            }

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    btnResend.setClickable(true);
                    btnResend.setEnabled(true);
                }
            }, RESEND_TIMER);


            switch (availableLoginType) {

                case LOGIN_WITH_MOTP_OR_PASSWORD:
                    // Show Mobile otp and password sign in screen
                    editOtp.setHint("Mobile OTP");
                    rootView.findViewById(R.id.oRTextViewId).setVisibility(View.VISIBLE);
                    break;
                case LOGIN_WITH_MOTP:
                    // Show Mobile otp sign in screen
                    editOtp.setHint("Mobile OTP");
                    editPassword.setVisibility(View.GONE);
                    break;
                case LOGIN_WITH_EOTP_OR_PASSWORD:
                    // Show Email otp and password sign in screen
                    editOtp.setHint("Email OTP");
                    rootView.findViewById(R.id.oRTextViewId).setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

    }

    public interface UserManagementInteractionListener {
        void onShowWalletScreen(boolean isAddToBackStack);
    }
}
