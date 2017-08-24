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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CashoutInfo;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.logger.CitrusLogger;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.sdk.payment.MasterPassOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.response.PaymentResponse;
import com.citrus.sdk.response.SubscriptionResponse;
import com.optimustechproject2017.Databases.SqliteDB;
import com.optimustechproject2017.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.optimustechproject2017.payment.Utils.PaymentType.AUTO_LOAD_MONEY;
import static com.optimustechproject2017.payment.Utils.PaymentType.LOAD_MONEY;
import static com.optimustechproject2017.payment.Utils.PaymentType.NEW_CITRUS_CASH;
import static com.optimustechproject2017.payment.Utils.PaymentType.NEW_PG_PAYMENT;
import static com.optimustechproject2017.payment.Utils.PaymentType.PG_PAYMENT;
import static com.optimustechproject2017.payment.Utils.PaymentType.WALLET_PG_PAYMENT;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WalletFragmentListener} interface
 * to handle interaction events.
 * Use the {@link WalletPaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletPaymentFragment extends Fragment implements View.OnClickListener {

    /*@Bind(R.id.btnsubscriptionlist)
    Button btnsubscriptionlist;*/
    @BindView(R.id.btnactivesubscription)
    Button btnactivesubscription;
    /*@Bind(R.id.btninactivesubscription)
    Button btninactivesubscription;*/
    @BindView(R.id.btndeactivatesubscription)
    Button btndeactivatesubscription;
    @BindView(R.id.btnsavedcardsubscription)
    Button btnsavedcardsubscription;
    @BindView(R.id.btnisActiveSubscription)
    Button btnisActiveSubscription;
    @BindView(R.id.btUpdateSubscription)
    Button btUpdateSubscription;
    SubscriptionResponse activeSubscription;
    MasterPassOption masterPassOption = null;
    String[] AUTO_LOAD_CARD_SCHEMS = {"MASTER_CARD", "VISA"};
    /*@Bind(R.id.btUpdateSubscriptiontohighervalue)
    Button btUpdateSubscriptiontohighervalue;*/
    private WalletFragmentListener mListener;
    private CitrusClient mCitrusClient = null;
    private Context mContext = null;
    private Button btnGetBalance = null;
    private Button btnLoadMoney = null;
    private Button btnPGPayment = null;
    private Button btnGetWithdrawInfo = null;
    private Button btnWithdraw = null;
    private Button btnSendMoney = null;
    private Button btnPerformDP = null;
    private Button btnUpdateProfile = null;
    private Button btnGetProfile = null;
    private Button btnNewPayUsingCash = null;
    private Button btnNewPGPayment = null;
    private Button btnautoload;
    private ImageButton btnMasterPass = null;
    private Button btnWalletPGPayment = null;
    private Button btnLazyPay;
    private SavedOptionsAdapter savedOptionsAdapter = null;
    private PaymentOption otherPaymentOption = null;
    private ArrayList<PaymentOption> walletList = new ArrayList<>();


    public WalletPaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WalletPaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WalletPaymentFragment newInstance() {
        WalletPaymentFragment fragment = new WalletPaymentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
        mCitrusClient = CitrusClient.getInstance(mContext.getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wallet_payment, container, false);


        btnGetBalance = (Button) rootView.findViewById(R.id.btn_get_balance);
        btnLoadMoney = (Button) rootView.findViewById(R.id.btn_load_money);
        btnNewPayUsingCash = (Button) rootView.findViewById(R.id.btn_new_pay_using_cash);
        btnNewPGPayment = (Button) rootView.findViewById(R.id.btn_new_pg_payment);
        btnPGPayment = (Button) rootView.findViewById(R.id.btn_pg_payment);
        btnWalletPGPayment = (Button) rootView.findViewById(R.id.btn_wallet_pg_payment);
        btnWithdraw = (Button) rootView.findViewById(R.id.btn_cashout);
        btnGetWithdrawInfo = (Button) rootView.findViewById(R.id.btn_get_cashout_info);
        btnSendMoney = (Button) rootView.findViewById(R.id.btn_send_money);
        btnPerformDP = (Button) rootView.findViewById(R.id.btn_perform_dp);

        btnUpdateProfile = (Button) rootView.findViewById(R.id.btn_update_profile);
        btnGetProfile = (Button) rootView.findViewById(R.id.btn_get_profile);
        btnLazyPay = (Button) rootView.findViewById(R.id.btn_lazypay_payment);

        btnautoload = (Button) rootView.findViewById(R.id.btn_autoLoad);

        SqliteDB sql = new SqliteDB(getActivity());


        int TotalPrice = sql.totalprice();

        mListener.onPaymentTypeSelected(NEW_PG_PAYMENT, new Amount(String.valueOf(TotalPrice)));


        // btnMasterPass = (ImageButton) rootView.findViewById(R.id.btnMasterpass);
        btnGetBalance.setOnClickListener(this);
        btnLoadMoney.setOnClickListener(this);
        btnNewPayUsingCash.setOnClickListener(this);
        btnNewPGPayment.setOnClickListener(this);
        btnPGPayment.setOnClickListener(this);
        btnWalletPGPayment.setOnClickListener(this);
        btnGetWithdrawInfo.setOnClickListener(this);
        btnWithdraw.setOnClickListener(this);
        btnSendMoney.setOnClickListener(this);
        btnPerformDP.setOnClickListener(this);
        btnUpdateProfile.setOnClickListener(this);
        btnGetProfile.setOnClickListener(this);
        btnLazyPay.setOnClickListener(this);
        btnautoload.setOnClickListener(this);

        btnPerformDP.setVisibility(View.VISIBLE);

        ButterKnife.bind(this, rootView);
        updateView();
        return rootView;
    }

    private void updateView() {

        mCitrusClient.getActiveSubscriptions(new Callback<SubscriptionResponse>() {
            @Override
            public void success(SubscriptionResponse subscriptionResponse) {
                if (WalletPaymentFragment.this.isVisible() && isAdded()) {
                    if (subscriptionResponse != null) {

                        if (btUpdateSubscription != null) {
                            btUpdateSubscription.setVisibility(View.VISIBLE);
                            btndeactivatesubscription.setVisibility(View.VISIBLE);
                            activeSubscription = subscriptionResponse;
                        }
                    } else {
                        if (btUpdateSubscription != null) {
                            btUpdateSubscription.setVisibility(View.GONE);
                            btndeactivatesubscription.setVisibility(View.GONE);
                            btnactivesubscription.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void error(CitrusError error) {
                //    Toast.makeText(getActivity(), "SignIn with Full Scope TOken for auto load feature.", Toast.LENGTH_SHORT).show();
                if (WalletPaymentFragment.this.isVisible() && isAdded()) {
                    hideAutoLoadButton();
                }
            }
        });
    }

    void hideAutoLoadButton() {
        btnautoload.setVisibility(View.GONE);
        btnactivesubscription.setVisibility(View.GONE);
        btndeactivatesubscription.setVisibility(View.GONE);
        btnisActiveSubscription.setVisibility(View.GONE);
        btnsavedcardsubscription.setVisibility(View.GONE);
        btUpdateSubscription.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (WalletFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement WalletFragmentListener");
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
            case R.id.btn_get_balance:
                getBalance();
                break;
            case R.id.btn_load_money:
                loadMoney();
                break;
            case R.id.btn_new_pay_using_cash:
                payUsingNewCash();
                break;
            case R.id.btn_new_pg_payment:
                newPgPayment();
                break;
            case R.id.btn_pg_payment:
                pgPayment();
                break;
            case R.id.btn_wallet_pg_payment:
                walletPGPayment();
                break;
            case R.id.btn_cashout:
                cashout();
                break;
            case R.id.btn_get_cashout_info:
                getCashoutInfo();
                break;
            case R.id.btn_send_money:
                sendMoney();
                break;
            case R.id.btn_perform_dp:
                performDP();
                break;
            case R.id.btn_update_profile:
                updateProfile();
                break;
            case R.id.btn_get_profile:
                getProfile();
                break;
            case R.id.btn_lazypay_payment:
                lpPayment();
                break;
            case R.id.btn_autoLoad:
                autoLoad();
        }
    }

    private void autoLoad() {
        showPrompt(AUTO_LOAD_MONEY);
    }

    private void updateProfile() {
        mListener.onUpdateProfileSelected();
    }

    private void getBalance() {

        mCitrusClient.getBalance(new Callback<Amount>() {
            @Override
            public void success(Amount amount) {
//                Utils.showToast(mContext, "Balance : " + amount.getValue());
                ((UIActivity) getActivity()).showSnackBar("Balance : " + amount.getValueAsDouble());
            }

            @Override
            public void error(CitrusError error) {
//                Utils.showToast(mContext, error.getMessage());
                ((UIActivity) getActivity()).showSnackBar(error.getMessage());
            }
        });
    }

    private void loadMoney() {
        showPrompt(LOAD_MONEY);
    }

    private void payUsingNewCash() {
        showPrompt(NEW_CITRUS_CASH);
    }

    private void newPgPayment() {
        showPrompt(NEW_PG_PAYMENT);
    }

    private void pgPayment() {
        showPrompt(PG_PAYMENT);
    }

    private void lpPayment() {
        final Intent lpIntent = new Intent(getContext(), CitrusLpExampleActivity.class);
        startActivity(lpIntent);
    }

    private void walletPGPayment() {
        showPrompt(WALLET_PG_PAYMENT);
    }

    private void cashout() {
        showCashoutPrompt();
    }

    private void getCashoutInfo() {
        mCitrusClient.getCashoutInfo(new Callback<CashoutInfo>() {
            @Override
            public void success(CashoutInfo cashoutInfo) {
//                Utils.showToast(getActivity(), cashoutInfo.toString());
                ((UIActivity) getActivity()).showSnackBar(cashoutInfo.toString());
            }

            @Override
            public void error(CitrusError error) {
//                Utils.showToast(getActivity(), error.getMessage());
                ((UIActivity) getActivity()).showSnackBar(error.getMessage());
            }
        });
    }

    private void sendMoney() {
        showSendMoneyPrompt();
    }

    private void performDP() {
        showDynamicPricingPrompt();
    }

    private void showPrompt(final Utils.PaymentType paymentType) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        String message = null;
        String positiveButtonText = null;

        switch (paymentType) {
            case LOAD_MONEY:
            case AUTO_LOAD_MONEY:
                message = "Please enter the amount to load.";
                positiveButtonText = "Load Money";
                break;
            case CITRUS_CASH:
            case NEW_CITRUS_CASH:
                message = "Please enter the transaction amount.";
                positiveButtonText = "Pay";
                break;
            case PG_PAYMENT:
            case NEW_PG_PAYMENT:
            case WALLET_PG_PAYMENT:
                message = "Please enter the transaction amount.";
                positiveButtonText = "Make Payment";
                break;
        }

        SqliteDB sql = new SqliteDB(getActivity());


        int TotalPrice = sql.totalprice();


        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        alert.setTitle("Transaction Amount?");
        alert.setMessage(message);
        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alert.setView(input);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();

                mListener.onPaymentTypeSelected(paymentType, new Amount(value));

                input.clearFocus();
                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        input.requestFocus();
        // alert.show();


        mListener.onPaymentTypeSelected(paymentType, new Amount(String.valueOf(TotalPrice)));
    }


    private void showCashoutPrompt() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        String message = "Please enter account details.";
        String positiveButtonText = "Withdraw";

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final TextView labelAmount = new TextView(getActivity());
        final EditText editAmount = new EditText(getActivity());
        final TextView labelAccountNo = new TextView(getActivity());
        final EditText editAccountNo = new EditText(getActivity());
        editAccountNo.setSingleLine(true);
        final TextView labelAccountHolderName = new TextView(getActivity());
        final EditText editAccountHolderName = new EditText(getActivity());
        editAccountHolderName.setSingleLine(true);
        final TextView labelIfscCode = new TextView(getActivity());
        final EditText editIfscCode = new EditText(getActivity());
        editIfscCode.setSingleLine(true);

        labelAmount.setText("Withdrawal Amount");
        labelAccountNo.setText("Account Number");
        labelAccountHolderName.setText("Account Holder Name");
        labelIfscCode.setText("IFSC Code");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        labelAmount.setLayoutParams(layoutParams);
        labelAccountNo.setLayoutParams(layoutParams);
        labelAccountHolderName.setLayoutParams(layoutParams);
        labelIfscCode.setLayoutParams(layoutParams);
        editAmount.setLayoutParams(layoutParams);
        editAccountNo.setLayoutParams(layoutParams);
        editAccountHolderName.setLayoutParams(layoutParams);
        editIfscCode.setLayoutParams(layoutParams);

        linearLayout.addView(labelAmount);
        linearLayout.addView(editAmount);
        linearLayout.addView(labelAccountNo);
        linearLayout.addView(editAccountNo);
        linearLayout.addView(labelAccountHolderName);
        linearLayout.addView(editAccountHolderName);
        linearLayout.addView(labelIfscCode);
        linearLayout.addView(editIfscCode);

        int paddingPx = Utils.getSizeInPx(getActivity(), 32);
        linearLayout.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

        editAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        alert.setTitle("Withdraw Money To Your Account");
        alert.setMessage(message);

        alert.setView(linearLayout);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                String amount = editAmount.getText().toString();
                String accontNo = editAccountNo.getText().toString();
                String accountHolderName = editAccountHolderName.getText().toString();
                String ifsc = editIfscCode.getText().toString();

                CashoutInfo cashoutInfo = new CashoutInfo(new Amount(amount), accontNo, accountHolderName, ifsc);
                mListener.onCashoutSelected(cashoutInfo);

                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editAmount.getWindowToken(), 0);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        editAmount.requestFocus();
        alert.show();
    }

    private void showSendMoneyPrompt() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final String message = "Send Money to Friend In A Flash";
        String positiveButtonText = "Send";

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final TextView labelAmount = new TextView(getActivity());
        final EditText editAmount = new EditText(getActivity());
        final TextView labelMobileNo = new TextView(getActivity());
        final EditText editMobileNo = new EditText(getActivity());
        final TextView labelMessage = new TextView(getActivity());
        final EditText editMessage = new EditText(getActivity());
        editAmount.setSingleLine(true);
        editMobileNo.setSingleLine(true);
        editMessage.setSingleLine(true);

        labelAmount.setText("Amount");
        labelMobileNo.setText("Enter Mobile No of Friend");
        labelMessage.setText("Enter Message (Optional)");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        labelAmount.setLayoutParams(layoutParams);
        labelMobileNo.setLayoutParams(layoutParams);
        labelMessage.setLayoutParams(layoutParams);
        editAmount.setLayoutParams(layoutParams);
        editMobileNo.setLayoutParams(layoutParams);
        editMessage.setLayoutParams(layoutParams);

        linearLayout.addView(labelAmount);
        linearLayout.addView(editAmount);
        linearLayout.addView(labelMobileNo);
        linearLayout.addView(editMobileNo);
        linearLayout.addView(labelMessage);
        linearLayout.addView(editMessage);

        int paddingPx = Utils.getSizeInPx(getActivity(), 32);
        linearLayout.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

        editAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editMobileNo.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setTitle("Send Money In A Flash");
        alert.setMessage(message);

        alert.setView(linearLayout);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                String amount = editAmount.getText().toString();
                String mobileNo = editMobileNo.getText().toString();
                String message = editMessage.getText().toString();

                mCitrusClient.sendMoneyToMoblieNo(new Amount(amount), mobileNo, message, new Callback<PaymentResponse>() {
                    @Override
                    public void success(PaymentResponse paymentResponse) {
//                        Utils.showToast(getActivity(), paymentResponse.getStatus() == CitrusResponse.Status.SUCCESSFUL ? "Sent Money Successfully." : "Failed To Send the Money");
                        ((UIActivity) getActivity()).showSnackBar(paymentResponse.getStatus() == CitrusResponse.Status.SUCCESSFUL ? "Sent Money Successfully." : "Failed To Send the Money");
                    }

                    @Override
                    public void error(CitrusError error) {
//                        Utils.showToast(getActivity(), error.getMessage());
                        ((UIActivity) getActivity()).showSnackBar(error.getMessage());
                    }
                });
                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editAmount.getWindowToken(), 0);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        editAmount.requestFocus();
        alert.show();
    }

    private void showDynamicPricingPrompt() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        String message = "Apply Dynamic Pricing";
        String positiveButtonText = "Apply";
        final Utils.DPRequestType[] dpRequestType = new Utils.DPRequestType[1];
        ScrollView scrollView = new ScrollView(getActivity());
        LinearLayout linearLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.dynamic_pricing_input_layout, null);
        final EditText editTransactionAmount = (EditText) linearLayout.findViewById(R.id.edit_txn_amount);
        final EditText editCouponCode = (EditText) linearLayout.findViewById(R.id.edit_coupon_code);
        final EditText editAlteredAmount = (EditText) linearLayout.findViewById(R.id.edit_altered_amount);
        final LinearLayout layoutCouponCode = (LinearLayout) linearLayout.findViewById(R.id.layout_for_coupon_code);
        final LinearLayout layoutAlteredAmount = (LinearLayout) linearLayout.findViewById(R.id.layout_for_altered_amount);
        Spinner spinner = (Spinner) linearLayout.findViewById(R.id.spinner_dp_request_type);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        dpRequestType[0] = Utils.DPRequestType.SEARCH_AND_APPLY;
                        layoutCouponCode.setVisibility(View.GONE);
                        layoutAlteredAmount.setVisibility(View.GONE);
                        break;
                    case 1:
                        dpRequestType[0] = Utils.DPRequestType.CALCULATE_PRICING;
                        layoutCouponCode.setVisibility(View.VISIBLE);
                        layoutAlteredAmount.setVisibility(View.GONE);
                        break;
                    case 2:
                        dpRequestType[0] = Utils.DPRequestType.VALIDATE_RULE;
                        layoutCouponCode.setVisibility(View.VISIBLE);
                        layoutAlteredAmount.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        alert.setTitle("Perform Dynamic Pricing");
        alert.setMessage(message);

        scrollView.addView(linearLayout);
        alert.setView(scrollView);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                String amount = editTransactionAmount.getText().toString();
                String alteredAmount = editAlteredAmount.getText().toString();
                String couponCode = editCouponCode.getText().toString();

                mListener.onPaymentTypeSelected(dpRequestType[0], new Amount(amount), couponCode, new Amount(alteredAmount));

                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTransactionAmount.getWindowToken(), 0);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        editTransactionAmount.requestFocus();
        alert.show();
    }

    private void getProfile() {
        mCitrusClient.getProfileInfo(new Callback<CitrusUser>() {
            @Override
            public void success(CitrusUser citrusUser) {
                Toast.makeText(mContext, citrusUser.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void error(CitrusError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.btnactivesubscription)
//, R.id.btnactivesubscription, R.id.btninactivesubscription})
    public void getActiveSubscriptionList() {
        mCitrusClient.getActiveSubscriptions(new Callback<SubscriptionResponse>() {
            @Override
            public void success(SubscriptionResponse subscriptionResponses) {
                if (subscriptionResponses != null) {
                    CitrusLogger.i("SUBSCRIPTION LIST " + subscriptionResponses.toString());
                    Toast.makeText(getActivity(), subscriptionResponses.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "No active subscription exists.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error(CitrusError error) {
                CitrusLogger.e("SUBSCRIPTION LIST ERROR" + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @OnClick(R.id.btndeactivatesubscription)
    public void deactivate() {
        deaActivateSubscription();
    }

    public void deaActivateSubscription() {
        mCitrusClient.deActivateSubscription(new Callback<SubscriptionResponse>() {
            @Override
            public void success(SubscriptionResponse subscriptionResponse) {
                CitrusLogger.i("DEACTIVATED SUBSCRIPTION RESPONSE " + subscriptionResponse.toString());
                Toast.makeText(getActivity(), subscriptionResponse.toString(), Toast.LENGTH_SHORT).show();
                btndeactivatesubscription.setVisibility(View.GONE);
                btUpdateSubscription.setVisibility(View.GONE);
                activeSubscription = null;
            }

            @Override
            public void error(CitrusError error) {
                CitrusLogger.e("DEACTIVATED SUBSCRIPTION ERROR " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @OnClick(R.id.btnisActiveSubscription)
    public void checkActiveSubscription() {
        mCitrusClient.isActiveSubscriptionPresent(new Callback<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                if (aBoolean)
                    Toast.makeText(getActivity(), "Active Subscription Exists for the user.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getActivity(), "Active Subscription Does Not Exists for the user.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void error(CitrusError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.btnsavedcardsubscription)
    void showTokenizedSubscriptionPrompt() {

        mCitrusClient.isActiveSubscriptionPresent(new Callback<Boolean>() {
            @Override
            public void success(Boolean aBoolean) {
                if (!aBoolean) {
                    showTokenizedPrompt();
                } else {
                    Toast.makeText(getActivity(), "Active Subscription already exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void error(CitrusError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showSavedAccountsDialog() {
        final Dialog dialog = new Dialog(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_saved_cards, null);
        RecyclerView recyclerViewSavedAcc = (RecyclerView) view.findViewById(R.id.recycler_view_saved_options);
        if (walletList != null && walletList.size() > 0) {
            recyclerViewSavedAcc.setAdapter(savedOptionsAdapter);
        } else {
            // In case there are no saved accounts.
            recyclerViewSavedAcc.setVisibility(View.GONE);
            view.findViewById(R.id.noSavedAccTextViewId).setVisibility(View.VISIBLE);
        }
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewSavedAcc.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewSavedAcc.setLayoutManager(mLayoutManager);

        recyclerViewSavedAcc.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childView, int position) {
                PaymentOption paymentOption = getItem(position);
                dialog.dismiss();

                if (paymentOption instanceof CardOption) {
                    if (mCitrusClient.isOneTapPaymentEnabledForCard((CardOption) paymentOption)) {
                        otherPaymentOption = paymentOption;
                        //otherPaymentOption.setTransactionAmount(new Amount(amount));
                    } else {
                        showCvvPrompt(paymentOption);
                    }

                } else {
                    otherPaymentOption = paymentOption;
                    //otherPaymentOption.setTransactionAmount(new Amount(amount));
                }
            }

            @Override
            public void onItemLongPress(View childView, int position) {
            }
        }));

        dialog.setContentView(view);
        dialog.show();
    }


    private void showCvvPrompt(final PaymentOption paymentOption) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        String message = "Please enter CVV.";
        String positiveButtonText = "OK";
        alert.setTitle("CVV");
        alert.setMessage(message);
        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setTransformationMethod(PasswordTransformationMethod.getInstance());
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(4);
        input.setFilters(FilterArray);
        alert.setView(input);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                String cvv = input.getText().toString();
                input.clearFocus();
                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                otherPaymentOption = paymentOption;
                ((CardOption) otherPaymentOption).setCardCVV(cvv);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            }
        });

        input.requestFocus();
        alert.show();
    }

    private PaymentOption getItem(int position) {
        PaymentOption paymentOption = null;

        if (walletList != null && walletList.size() > position && position >= -1) {
            paymentOption = walletList.get(position);
        }

        return paymentOption;
    }

    void showTokenizedPrompt() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        final String message = "Auto Load Money with Saved Card";
        String positiveButtonText = "Auto Load";

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final TextView labelamt = new TextView(getActivity());
        final EditText editAmount = new EditText(getActivity());
        final TextView labelAmount = new TextView(getActivity());
        final EditText editLoadAmount = new EditText(getActivity());
        final TextView labelMobileNo = new TextView(getActivity());
        final EditText editThresholdAmount = new EditText(getActivity());
        final Button btnSelectSavedCards = new Button(getActivity());
        btnSelectSavedCards.setText("Select Saved Card");

        editLoadAmount.setSingleLine(true);
        editThresholdAmount.setSingleLine(true);

        editAmount.setSingleLine(true);
        labelamt.setText("Load Amount");
        labelAmount.setText("Auto Load Amount");
        labelMobileNo.setText("Threshold Amount");

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        labelamt.setLayoutParams(layoutParams);
        editAmount.setLayoutParams(layoutParams);
        labelAmount.setLayoutParams(layoutParams);
        labelMobileNo.setLayoutParams(layoutParams);
        editLoadAmount.setLayoutParams(layoutParams);
        editThresholdAmount.setLayoutParams(layoutParams);
        btnSelectSavedCards.setLayoutParams(layoutParams);

        btnSelectSavedCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCitrusClient.getWallet(new Callback<List<PaymentOption>>() {
                    @Override
                    public void success(List<PaymentOption> paymentOptions) {
                        walletList.clear();
                        for (PaymentOption paymentOption : paymentOptions) {
                            if (paymentOption instanceof CreditCardOption) {
                                if (Arrays.asList(AUTO_LOAD_CARD_SCHEMS).contains(((CardOption) paymentOption).getCardScheme().toString()))
                                    walletList.add(paymentOption); //only available for Master and Visa Credit Card....
                            }
                        }
                        savedOptionsAdapter = new SavedOptionsAdapter(getActivity(), walletList);
                        showSavedAccountsDialog();
                    }

                    @Override
                    public void error(CitrusError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        linearLayout.addView(labelamt);
        linearLayout.addView(editAmount);
        linearLayout.addView(labelAmount);
        linearLayout.addView(editLoadAmount);
        linearLayout.addView(labelMobileNo);
        linearLayout.addView(editThresholdAmount);
        linearLayout.addView(btnSelectSavedCards);

        int paddingPx = Utils.getSizeInPx(getActivity(), 32);
        linearLayout.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

        editLoadAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editThresholdAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setTitle("Auto Load Money with Saved Card");
        alert.setMessage(message);

        alert.setView(linearLayout);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                final String amount = editAmount.getText().toString();
                final String loadAmount = editLoadAmount.getText().toString();
                final String thresHoldAmount = editThresholdAmount.getText().toString();
                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editAmount.getWindowToken(), 0);

                if (TextUtils.isEmpty(amount)) {
                    Toast.makeText(getActivity(), "Amount cant be blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(loadAmount)) {
                    Toast.makeText(getActivity(), "Load Amount cant be blank", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(thresHoldAmount)) {
                    Toast.makeText(getActivity(), "thresHoldAmount cant be blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Double.valueOf(thresHoldAmount) < new Double("500")) {
                    Toast.makeText(getActivity(), "thresHoldAmount  should not be less than 500", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Double.valueOf(loadAmount) < new Double(thresHoldAmount)) {
                    Toast.makeText(getActivity(), "Load Amount should not be less than thresHoldAmount", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (otherPaymentOption == null) {
                    Toast.makeText(getActivity(), "Saved Card Option is null.", Toast.LENGTH_SHORT).show();
                }

                try {
                    PaymentType paymentType = new PaymentType.LoadMoney(new Amount(amount), otherPaymentOption);
                    mCitrusClient.autoLoadMoney((PaymentType.LoadMoney) paymentType, new Amount(thresHoldAmount), new Amount(loadAmount), new Callback<SubscriptionResponse>() {
                        @Override
                        public void success(SubscriptionResponse subscriptionResponse) {
                            CitrusLogger.i("AUTO LOAD RESPONSE " + subscriptionResponse.getSubscriptionResponseMessage());

                            Toast.makeText(getActivity(), subscriptionResponse.getSubscriptionResponseMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void error(CitrusError error) {
                            CitrusLogger.e("AUTO LOAD ERROR " + error.getMessage());
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (CitrusException e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        editLoadAmount.requestFocus();
        alert.show();
    }


    @OnClick(R.id.btUpdateSubscription)
    public void updateSubscription() {
        if (activeSubscription != null) {
            showUpdateSubscriptionPrompt(false);
        } else {
            mCitrusClient.updateSubScriptiontoLoweValue(new Amount("508"), new Amount("515"), new Callback<SubscriptionResponse>() {
                @Override
                public void success(SubscriptionResponse subscriptionResponse) {

                }

                @Override
                public void error(CitrusError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    //we will use the same method to update subscription to lower and higher value
    //i u want to update to higher value - again load Money is required
    private void showUpdateSubscriptionPrompt(final boolean isUpdateToHigherValue) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        //    final String message = "Update Subscription to Lowe Amount";
        String positiveButtonText = "Update Subscription ";

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final TextView labelsubscriptionID = new TextView(getActivity());
        final EditText edtAmount = new EditText(getActivity());
        final TextView labelAmount = new TextView(getActivity());
        final EditText editLoadAmount = new EditText(getActivity());
        final TextView labelMobileNo = new TextView(getActivity());
        final EditText editThresholdAmount = new EditText(getActivity());

        editLoadAmount.setSingleLine(true);
        editThresholdAmount.setSingleLine(true);
        edtAmount.setSingleLine(true);
        edtAmount.setInputType(InputType.TYPE_NULL);
        labelsubscriptionID.setText("Load Money Amount");
        labelAmount.setText("Current Auto Load Amount");
        editLoadAmount.setText(String.valueOf(activeSubscription.getLoadAmount()));
        labelMobileNo.setText("Current Threshold Amount");
        editThresholdAmount.setText(String.valueOf(activeSubscription.getThresholdAmount()));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        labelsubscriptionID.setLayoutParams(layoutParams);
        edtAmount.setLayoutParams(layoutParams);
        labelAmount.setLayoutParams(layoutParams);
        labelMobileNo.setLayoutParams(layoutParams);
        editLoadAmount.setLayoutParams(layoutParams);
        editThresholdAmount.setLayoutParams(layoutParams);


        linearLayout.addView(labelAmount);
        linearLayout.addView(editLoadAmount);
        linearLayout.addView(labelMobileNo);
        linearLayout.addView(editThresholdAmount);
        linearLayout.addView(labelsubscriptionID);
        linearLayout.addView(edtAmount);
        edtAmount.setText("1.00");

        int paddingPx = Utils.getSizeInPx(getActivity(), 32);
        linearLayout.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

        editLoadAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        edtAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editLoadAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
               /* if (!hasFocus) {
                    if (Double.valueOf(editLoadAmount.getText().toString()) > activeSubscription.getLoadAmount()) {
                        labelsubscriptionID.setVisibility(View.VISIBLE);
                        edtAmount.setVisibility(View.VISIBLE);
                        edtAmount.setText("1.00");

                    } else {
                        labelsubscriptionID.setVisibility(View.INVISIBLE);
                        edtAmount.setVisibility(View.INVISIBLE);
                    }
                }*/
            }
        });
        editThresholdAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setTitle("Update Subscription ");
        alert.setMessage("Updating Load amount to higher will require Load Money transactions.");

        alert.setView(linearLayout);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                final String trAmount = edtAmount.getText().toString();

                final String loadAmount = editLoadAmount.getText().toString();
                final String thresHoldAmount = editThresholdAmount.getText().toString();

                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtAmount.getWindowToken(), 0);

                if (TextUtils.isEmpty(trAmount) && edtAmount.getVisibility() == View.VISIBLE) {
                    Toast.makeText(getActivity(), " load amount cant be blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(loadAmount)) {
                    Toast.makeText(getActivity(), "Auto Load Amount cant be blank", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(thresHoldAmount)) {
                    Toast.makeText(getActivity(), "thresHoldAmount cant be blank", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (Double.valueOf(thresHoldAmount) < new Double("500")) {
                    Toast.makeText(getActivity(), "thresHoldAmount  should not be less than 500", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (Double.valueOf(loadAmount) < new Double(thresHoldAmount)) {
                    Toast.makeText(getActivity(), "Load Amount should not be less than thresHoldAmount", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Double.valueOf(editLoadAmount.getText().toString()) > activeSubscription.getLoadAmount()) { //update to higher value
                    mListener.onAutoLoadSelected(AUTO_LOAD_MONEY, new Amount(trAmount), editLoadAmount.getText().toString(),
                            editThresholdAmount.getText().toString(), true);
                } else { //update to lower value
                    mCitrusClient.updateSubScriptiontoLoweValue(new Amount(thresHoldAmount), new Amount(loadAmount), new Callback<SubscriptionResponse>() {
                        @Override
                        public void success(SubscriptionResponse subscriptionResponse) {
                            Toast.makeText(getActivity(), subscriptionResponse.toString(), Toast.LENGTH_SHORT).show();

                            CitrusLogger.i("updateSubscription response " + subscriptionResponse.toString());

                            activeSubscription = subscriptionResponse;//update the active subscription Object
                        }

                        @Override
                        public void error(CitrusError error) {
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            CitrusLogger.e("ERROR ***updateSubscription" + error.getMessage());
                        }
                    });
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        editLoadAmount.requestFocus();
        alert.show();
    }

}


