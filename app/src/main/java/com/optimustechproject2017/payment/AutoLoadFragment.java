package com.optimustechproject2017.payment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.SubscriptionResponse;
import com.citrus.sdk.views.CitrusProgressBar;
import com.citrus.widgets.CardNumberEditText;
import com.citrus.widgets.ExpiryDate;
import com.optimustechproject2017.R;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AutoLoadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AutoLoadFragment extends Fragment {

    final static String AUTO_LOAD = "AUTO_LOAD_TYPE";
    @BindView(R.id.paymentdetails)
    RelativeLayout paymentdetails;
    @BindView(R.id.cardHolderNumber)
    CardNumberEditText cardHolderNumber;
    @BindView(R.id.cardNumberLayout)
    LinearLayout cardNumberLayout;
    @BindView(R.id.cardNickNameLayout)
    LinearLayout cardNickNameLayout;
    @BindView(R.id.cardExpiry)
    ExpiryDate cardExpiry;
    @BindView(R.id.cardCvv)
    EditText cardCvv;
    @BindView(R.id.expiryLayout)
    LinearLayout expiryLayout;
    @BindView(R.id.switch_compat)
    SwitchCompat switchCompat;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.threshhold_amount)
    EditText threshholdAmount;
    @BindView(R.id.textView5)
    TextView textView5;
    @BindView(R.id.textView6)
    TextView textView6;
    @BindView(R.id.rs500)
    Button rs500;
    @BindView(R.id.rs1000)
    Button rs1000;
    @BindView(R.id.rs2000)
    Button rs2000;
    @BindView(R.id.load_info)
    RelativeLayout loadInfo;
    @BindView(R.id.load)
    TextView load;
    @BindView(R.id.loadamount)
    EditText loadamount;
    Callback<SubscriptionResponse> callback;
    AUTO_LOAD_TYPE mAUTO_load_type = null;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private CitrusClient citrusClient = null;
    private Context context = null;
    private EditText edtFirstName = null;
    private EditText edtLatName = null;
    private Button btnUpdateProfile = null;
    private boolean isAutoLoadEnabled = true;
    private Amount amout = null;
    private String updatedThresholdAmount = null;
    private String updatedLoadAmount = null;
    private CitrusProgressBar mProgressBar = null;

    ;

    public AutoLoadFragment() {
        // Required empty public constructor
    }

    /**
     * this constructor will be used for quick auto load
     *
     * @param amount
     * @return
     */
    public static AutoLoadFragment newInstance(Amount amount) {
        AutoLoadFragment fragment = new AutoLoadFragment();
        Bundle args = new Bundle();
     /*   args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/

        args.putParcelable("amount", amount);
        args.putSerializable(AUTO_LOAD, AUTO_LOAD_TYPE.QUICK_AUTO_LOAD);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * this constructor will be used to update auto load subscription
     *
     * @param amount
     * @param updatedLoadAmount
     * @param updatedThresholdAmount
     * @return
     */
    public static AutoLoadFragment newInstance(Amount amount, String updatedLoadAmount, String updatedThresholdAmount) {
        AutoLoadFragment fragment = new AutoLoadFragment();
        Bundle args = new Bundle();
     /*   args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);*/

        args.putParcelable("amount", amount);
        args.putSerializable(AUTO_LOAD, AUTO_LOAD_TYPE.UPDATE_AUTO_LOAD);
        args.putString("UPDATED_LOAD_AMOUNT", updatedLoadAmount);
        args.putString("UPDATED_THRESHOLD_AMOUNT", updatedThresholdAmount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * this constructor will be used for lazy load
     *
     * @return
     */
    public static AutoLoadFragment newInstance() {
        AutoLoadFragment fragment = new AutoLoadFragment();
        Bundle args = new Bundle();
        args.putSerializable("AUTO_LOAD_TYPE", AUTO_LOAD_TYPE.LAZY_AUTO_LOAD);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            amout = getArguments().getParcelable("amount");
            updatedThresholdAmount = getArguments().getString("UPDATED_THRESHOLD_AMOUNT");
            updatedLoadAmount = getArguments().getString("UPDATED_LOAD_AMOUNT");
            mAUTO_load_type = (AUTO_LOAD_TYPE) getArguments().getSerializable(AUTO_LOAD);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_autoload, container, false);
        ActionBar actionBar = ((UIActivity) getActivity()).getSupportActionBar();

        context = getActivity();

        citrusClient = CitrusClient.getInstance(context.getApplicationContext());

        ButterKnife.bind(this, rootView);
        threshholdAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (threshholdAmount != null) {
                        if (TextUtils.isEmpty(threshholdAmount.getText().toString())) {
                            Toast.makeText(getActivity(), "amount cant be less than 500", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int amount = Integer.parseInt(threshholdAmount.getText().toString());
                        if (amount < 500)
                            threshholdAmount.setError("Amount cant be less than 500");
                    }
                }
            }
        });
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAutoLoadEnabled = isChecked;
            }
        });

        callback = new Callback<SubscriptionResponse>() {
            @Override
            public void success(SubscriptionResponse subscriptionResponse) {
                dismissDialog();
                load.setEnabled(false);
                Logger.d("SUBSCRIPTION RESPONSE ***" + subscriptionResponse.getSubscriptionResponseMessage());
                Toast.makeText(getActivity(), subscriptionResponse.getSubscriptionResponseMessage(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void error(CitrusError error) {
                dismissDialog();
                Logger.d("ERROR ****" + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        };

        if (updatedLoadAmount != null) {
            loadamount.setText(updatedLoadAmount);
        }
        if (updatedThresholdAmount != null) {
            threshholdAmount.setText(updatedThresholdAmount);
        }
        switchCompat.setChecked(true);
        if (mAUTO_load_type == AUTO_LOAD_TYPE.LAZY_AUTO_LOAD) {
            paymentdetails.setVisibility(View.INVISIBLE);
        }
        mProgressBar = new CitrusProgressBar(getActivity());
        return rootView;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick({R.id.switch_compat, R.id.rs500, R.id.rs1000, R.id.rs2000, R.id.load})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.switch_compat:
                break;
            case R.id.rs500:
                loadamount.setText("500");
                break;
            case R.id.rs1000:
                loadamount.setText("1000");
                break;
            case R.id.rs2000:
                loadamount.setText("2000");
                break;
            case R.id.load:
                validateAndLoad();
                break;
        }
    }

    private void validateAndLoad() {

        if (mAUTO_load_type != AUTO_LOAD_TYPE.LAZY_AUTO_LOAD) {
            if (TextUtils.isEmpty(cardHolderNumber.getText())) {
                Toast.makeText(getActivity(), "Card number cant be blank", Toast.LENGTH_SHORT).show();
                return;
            }


            if (TextUtils.isEmpty(cardExpiry.getText())) {
                Toast.makeText(getActivity(), "Card Expiry cant be blank", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(cardCvv.getText())) {
                Toast.makeText(getActivity(), "Card CVV cant be blank", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        CreditCardOption cardOption = new CreditCardOption("", cardHolderNumber.getText().toString(), cardCvv.getText().toString(), cardExpiry.getMonth(), cardExpiry.getYear());
        final PaymentType paymentType;//= null;
        try {

            if (isAutoLoadEnabled) {
                if (mAUTO_load_type == AUTO_LOAD_TYPE.UPDATE_AUTO_LOAD) {
                    showDialog();
                    paymentType = new PaymentType.LoadMoney(amout, cardOption);
                    citrusClient.updateSubscriptiontoHigherValue((PaymentType.LoadMoney) paymentType, new Amount(threshholdAmount.getText().toString()), new Amount(loadamount.getText().toString()), callback);
                } else if (mAUTO_load_type == AUTO_LOAD_TYPE.LAZY_AUTO_LOAD) {
                    showDialog();
                    citrusClient.autoLoadMoney(new Amount(threshholdAmount.getText().toString()), new Amount(loadamount.getText().toString()), callback);
                } else {
                    paymentType = new PaymentType.LoadMoney(amout, cardOption);
                    citrusClient.isActiveSubscriptionPresent(new Callback<Boolean>() { //check if active subscription exists and then auto Load
                        @Override
                        public void success(Boolean aBoolean) {
                            if (!aBoolean) {
                                try {
                                    citrusClient.autoLoadMoney((PaymentType.LoadMoney) paymentType, new Amount(threshholdAmount.getText().toString()), new Amount(loadamount.getText().toString()), callback);
                                } catch (CitrusException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Active Subscription already exists", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void error(CitrusError error) {
                            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            dismissDialog();
                        }
                    });
                }

            } else {
                paymentType = new PaymentType.LoadMoney(amout, cardOption);
                citrusClient.simpliPay((PaymentType.LoadMoney) paymentType, new Callback<TransactionResponse>() {
                    @Override
                    public void success(TransactionResponse transactionResponse) {
                        Logger.d("RESPONSE ***" + transactionResponse.getMessage());
                        Toast.makeText(getActivity(), transactionResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void error(CitrusError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        dismissDialog();
                    }
                });
            }
        } catch (CitrusException e) {
            e.printStackTrace();
            dismissDialog();
        }
    }

    private void showDialog() {
        if (mProgressBar != null) {
            mProgressBar.show();
            mProgressBar.setCanceledOnTouchOutside(false);
            mProgressBar.setCancelable(false);
        }
    }

    private void dismissDialog() {
        if (mProgressBar != null) {
            mProgressBar.dismiss();
        }
    }

    public enum AUTO_LOAD_TYPE {
        QUICK_AUTO_LOAD, UPDATE_AUTO_LOAD, LAZY_AUTO_LOAD

    }
}
