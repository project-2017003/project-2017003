package com.optimustechproject2017.payment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.PaymentDistribution;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.classes.Month;
import com.citrus.sdk.classes.Year;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.CitrusCash;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.sdk.payment.DebitCardOption;
import com.citrus.sdk.payment.MVCOption;
import com.citrus.sdk.payment.MerchantPaymentOption;
import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.widgets.CardNumberEditText;
import com.citrus.widgets.ExpiryDate;
import com.optimustechproject2017.R;
import com.orhanobut.logger.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WalletPaymentFragment} interface
 * to handle interaction events.
 * Use the {@link WalletPGPaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletPGPaymentFragment extends Fragment {

    final Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
        @Override
        public void success(TransactionResponse transactionResponse) {
            if (isVisible() && isAdded())
                ((UIActivity) getActivity()).showSnackBar(transactionResponse.getMessage());
            // reset();
        }

        @Override
        public void error(CitrusError error) {
            if (isVisible() && isAdded())
                ((UIActivity) getActivity()).showSnackBar(error.getMessage());
            // reset();
        }
    };
    private Amount amount = null;
    private CitrusClient citrusClient = null;
    private CheckBox citrusWalletCheckBox = null;
    private CheckBox mvcBox = null;
    private CheckBox citrusCashBox = null;
    private UserManagementFragment.UserManagementInteractionListener mListener;
    private ArrayList<NetbankingOption> mNetbankingOptionsList = null;
    private ArrayList<PaymentOption> walletList = new ArrayList<>();
    private SavedOptionsAdapter savedOptionsAdapter = null;
    private RadioGroup radioPaymentOptionGroup;
    private TextView paymentOptionText = null;
    private TextView payWalletPg = null;
    private TextView txtremainingamount = null;
    private RadioButton ccRadioBtn, dcRadioBtn, netBankRadioBtn, savedAccRadioBtn = null;
    private Amount citrusCashAmount = null;
    private double balanceAmount = -1;
    private double mvcAmount = -1;
    private List<PaymentOption> paymentOptionList = new ArrayList<>();
    private CitrusCash citrusCashOption = null;
    private MVCOption mvcOption = null;
    private CitrusCash paymentOption2 = null;
    private PaymentOption otherPaymentOption = null;
    private boolean paymentAttempted = false;
    private boolean doOnceForMVC = false;
    private boolean doOnceForCash = false;
    private PaymentDistribution mPaymentDistribution = null;
    private boolean citrusCashChecked;
    private boolean mvcChecked;

    public WalletPGPaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WalletPGPaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WalletPGPaymentFragment newInstance(Utils.PaymentType paymentType, Amount amount) {
        WalletPGPaymentFragment fragment = new WalletPGPaymentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("paymentType", paymentType);
        bundle.putParcelable("amount", amount);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            amount = bundle.getParcelable("amount");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        balanceAmount = amount.getValueAsDouble();
        citrusClient = CitrusClient.getInstance(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_wallet_pg_payment, container,
                false);

        mvcBox = (CheckBox) rootView.findViewById(R.id.mvcCheckBoxId);
        citrusCashBox = (CheckBox) rootView.findViewById(R.id.citrusCashCheckBoxId);


        paymentOptionText = (TextView) rootView.findViewById(R.id.paymentMsgTextViewId);
        txtremainingamount = (TextView) rootView.findViewById(R.id.txtremainingamount);
        payWalletPg = (TextView) rootView.findViewById(R.id.payWalletPgId);
        payWalletPg.setText("Pay " + amount.getValueAsDouble());


        payWalletPg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!citrusCashBox.isChecked() && !mvcBox.isChecked()) {
                    if (otherPaymentOption == null) { // we need payment option
                        ((UIActivity) getActivity()).showSnackBar("Please select a payment option.");
                    } else {
                        try {
                            citrusClient.simpliPay(new PaymentType.SplitPayment(amount, Constants.BILL_URL, otherPaymentOption, citrusCashChecked, mvcChecked), callback);
                        } catch (CitrusException e) {
                            e.printStackTrace();
                        }
                    }
                } else {  // MVC or Citrus Cash is sufficiente
                    if (!citrusCashBox.isChecked() || !mvcBox.isChecked()) {
                        try {
                            citrusClient.simpliPay(new PaymentType.SplitPayment(amount, Constants.BILL_URL, otherPaymentOption, citrusCashBox.isChecked(), mvcBox.isChecked()), callback);
                        } catch (CitrusException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (TextUtils.isEmpty(txtremainingamount.getText().toString())) {
                            try {
                                citrusClient.simpliPay(new PaymentType.SplitPayment(amount, Constants.BILL_URL, null, citrusCashBox.isChecked(), mvcBox.isChecked()), callback);
                            } catch (CitrusException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            try {
                                citrusClient.simpliPay(new PaymentType.SplitPayment(amount, Constants.BILL_URL, otherPaymentOption, citrusCashBox.isChecked(), mvcBox.isChecked()), callback);
                            } catch (CitrusException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });

        ccRadioBtn = (RadioButton) rootView.findViewById(R.id.radioCreditCard);
        dcRadioBtn = (RadioButton) rootView.findViewById(R.id.radioDebitCard);
        netBankRadioBtn = (RadioButton) rootView.findViewById(R.id.radioNetBank);
        savedAccRadioBtn = (RadioButton) rootView.findViewById(R.id.radioSavedAccounts);

        ccRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreditDebitDialog(CardOption.CardType.CREDIT);
            }
        });

        dcRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreditDebitDialog(CardOption.CardType.DEBIT);
            }
        });

        netBankRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNetBankingDialog();
            }
        });

        savedAccRadioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSavedAccountsDialog();
            }
        });

        radioPaymentOptionGroup = (RadioGroup) rootView.findViewById(R.id.radioPaymentOption);

        handleUIViewClicks(false);
        fetchWallet();

        return rootView;
    }

    private void handleUIViewClicks(boolean toggleVal) {
        mvcBox.setEnabled(toggleVal);
        citrusCashBox.setEnabled(toggleVal);
        ccRadioBtn.setEnabled(toggleVal);
        dcRadioBtn.setEnabled(toggleVal);
        netBankRadioBtn.setEnabled(toggleVal);
        savedAccRadioBtn.setEnabled(toggleVal);
        payWalletPg.setEnabled(toggleVal);
        payWalletPg.setClickable(toggleVal);
    }

    private void fetchWallet() {
        savedOptionsAdapter = new SavedOptionsAdapter(getActivity(), walletList);
        citrusClient.getWallet(new Callback<List<PaymentOption>>() {
            @Override
            public void success(List<PaymentOption> paymentOptionList) {
                for (PaymentOption paymentOption : paymentOptionList) {
                    if (!(paymentOption instanceof CitrusCash || paymentOption instanceof MVCOption)) {
                        walletList.add(paymentOption);
                    }
                    if (paymentOption instanceof MVCOption) {
                        mvcOption = (MVCOption) paymentOption;
                        mvcAmount = ((MVCOption) paymentOption).getMaxBalance().getValueAsDouble();

                        //   mvcBox.setText(paymentOption.getName());
                    }
                    if (paymentOption instanceof CitrusCash) {
                        citrusCashOption = (CitrusCash) paymentOption;
                        citrusCashAmount = ((CitrusCash) paymentOption).getMaxBalance();
                        //  citrusCashBox.setText(paymentOption.getName());

                    }


                    savedOptionsAdapter.setWalletList(walletList);
                    savedOptionsAdapter.notifyDataSetChanged();
                }

                citrusClient.getPaymentDistribution(amount, new Callback<PaymentDistribution>() {
                    @Override
                    public void success(PaymentDistribution paymentDistribution) {
                        Logger.d("Payment Distribution ****" + paymentDistribution.toString());
                        mPaymentDistribution = paymentDistribution;
                        updateUI();
                        //updateUI();
                    }

                    @Override
                    public void error(CitrusError error) {

                    }
                });
                mvcBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (mPaymentDistribution != null) {
                            mvcChecked = isChecked;

                            if (mPaymentDistribution.isMVCAvailable()) {
                                double amount = mPaymentDistribution.getMvcAmount().getValueAsDouble();
                                if (!doOnceForMVC) {
                                    updateOtherPaymentsAmount(amount, isChecked);
                                } else {
                                    doOnceForMVC = true;
                                }

                            } else {
                                mvcBox.setChecked(false);
                            }
                        }
                    }
                });

                citrusCashBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (mPaymentDistribution != null) {
                            citrusCashChecked = isChecked;

                            if (mPaymentDistribution.isCitrusCashAvailable()) {
                                double amount = mPaymentDistribution.getCitrusCashAmount().getValueAsDouble();
                                if (!doOnceForCash) {
                                    updateOtherPaymentsAmount(amount, isChecked);
                                } else {
                                    doOnceForCash = true;
                                }

                            } else {
                                if (citrusCashAmount.getValueAsDouble() >= amount.getValueAsDouble() && !mvcChecked) {
                                    if (isChecked)
                                        updateOtherPaymentsAmount(amount.getValueAsDouble(), isChecked);
                                    else {
                                        String text = "CASH BALANCE ₹ :" + citrusCashAmount.getValue() + "  USED ₹:" + "0";
                                        citrusCashBox.setText(text);
                                        txtremainingamount.setText(String.valueOf(amount.getValueAsDouble()));
                                    }

                                } else {
                                    citrusCashBox.setChecked(false);
                                }
                            }
                        }
                    }
                });


                // When citrus wallet amount is not enough for transaction.
                double citrusWalletAmount = mvcAmount + citrusCashAmount.getValueAsDouble();
                if (citrusWalletAmount < amount.getValueAsDouble()) {
                    citrusCashBox.setEnabled(false);
                }

                handleUIViewClicks(true);
            }

            @Override
            public void error(CitrusError error) {
                if (isVisible() && isAdded())
                    ((UIActivity) getActivity()).showSnackBar(error.getMessage());
            }
        });
    }

    private void updateUI() {

        if (mPaymentDistribution.isMVCAvailable()) {
            mvcBox.setChecked(true);

        }
        if (mPaymentDistribution.isCitrusCashAvailable()) {
            citrusCashBox.setChecked(true);

        }
        updateMVCText();
        updateCashText();
        txtremainingamount.setText(mPaymentDistribution.getOtherPaymentOptionAmount().getValue());
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
                    if (citrusClient.isOneTapPaymentEnabledForCard((CardOption) paymentOption)) {
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

    private void showNetBankingDialog() {

        final Dialog dialog = new Dialog(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_net_banking, null);
        final NetbankingAdapter netbankingAdapter = new NetbankingAdapter(getActivity(), mNetbankingOptionsList);
        RecyclerView recylerViewNetbanking = (RecyclerView) view.findViewById(R.id.recycler_view_netbanking);
        recylerViewNetbanking.setAdapter(netbankingAdapter);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recylerViewNetbanking.setHasFixedSize(true);
        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recylerViewNetbanking.setLayoutManager(mLayoutManager);

        CitrusClient.getInstance(getActivity()).getMerchantPaymentOptions(new Callback<MerchantPaymentOption>() {
            @Override
            public void success(MerchantPaymentOption merchantPaymentOption) {

                netbankingAdapter.setNetbankingOptionList(merchantPaymentOption.getNetbankingOptionList());
                netbankingAdapter.notifyDataSetChanged();
                mNetbankingOptionsList = merchantPaymentOption.getNetbankingOptionList();
            }

            @Override
            public void error(CitrusError error) {
                if (isVisible() && isAdded())
                    ((UIActivity) getActivity()).showSnackBar(error.getMessage());
            }
        });

        recylerViewNetbanking.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childView, int position) {
                otherPaymentOption = getNetBankItem(position);
                // otherPaymentOption.setTransactionAmount(new Amount(amount));
                dialog.dismiss();
            }

            @Override
            public void onItemLongPress(View childView, int position) {

            }
        }));

        dialog.setContentView(view);
        dialog.show();
    }

    private void showCreditDebitDialog(final CardOption.CardType cType) {
        final Dialog dialog = new Dialog(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_credit_debit_card, null);

        CheckBox checkboxSaveCard = (CheckBox) view.findViewById(R.id.checkboxSaveCard);
        checkboxSaveCard.setVisibility(View.GONE);

        final CardNumberEditText editCardNumber = (CardNumberEditText) view.findViewById(R.id.cardHolderNumber);
        final ExpiryDate editExpiryDate = (ExpiryDate) view.findViewById(R.id.cardExpiry);
        final EditText editCardHolderName = (EditText) view.findViewById(R.id.cardHolderName);
        final EditText cardHolderNickName = (EditText) view.findViewById(R.id.cardHolderNickName);
        final EditText editCVV = (EditText) view.findViewById(R.id.cardCvv);

        TextView submitButton = (TextView) view.findViewById(R.id.load);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editCardNumber.getWindowToken(), 0);

                String cardHolderName = editCardHolderName.getText().toString();
                String cardNumber = editCardNumber.getText().toString();
                String cardCVV = editCVV.getText().toString();
                Month month = editExpiryDate.getMonth();
                Year year = editExpiryDate.getYear();

                CardOption cardOption;
                if (cType == CardOption.CardType.DEBIT) {
                    cardOption = new DebitCardOption(cardHolderName, cardNumber, cardCVV, month, year);
                } else {
                    cardOption = new CreditCardOption(cardHolderName, cardNumber, cardCVV, month, year);
                }
                otherPaymentOption = cardOption;
                //  otherPaymentOption.setTransactionAmount(new Amount(amount));
                dialog.dismiss();

            }
        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void reset() {
        if (paymentOptionList != null) {
            paymentOptionList.clear();
        }
        fetchWallet();

        if (radioPaymentOptionGroup != null)
            radioPaymentOptionGroup.clearCheck();

        if (mvcBox != null)
            mvcBox.setChecked(false);
        if (citrusCashBox != null)
            citrusCashBox.setChecked(false);

        paymentAttempted = false;
    }

    private PaymentOption getItem(int position) {
        PaymentOption paymentOption = null;

        if (walletList != null && walletList.size() > position && position >= -1) {
            paymentOption = walletList.get(position);
        }

        return paymentOption;
    }

    private NetbankingOption getNetBankItem(int position) {
        NetbankingOption netbankingOption = null;

        if (mNetbankingOptionsList != null && mNetbankingOptionsList.size() > position && position >= -1) {
            netbankingOption = mNetbankingOptionsList.get(position);
        }

        return netbankingOption;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (UserManagementFragment.UserManagementInteractionListener) activity;
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


    private void showCreditDebitPrompt(String message, final CardOption.CardType cType) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        String positiveButtonText = "Confirm";
        alert.setMessage(message);
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        alert.setView(input);
        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                String amt = input.getText().toString();
                if (TextUtils.isEmpty(amt) || Double.parseDouble(amt) == 0) {
                    ((UIActivity) getActivity()).showSnackBar("Invalid amount entered.");
                    if (cType == CardOption.CardType.CREDIT) {
                        ccRadioBtn.setChecked(false);
                    } else if (cType == CardOption.CardType.DEBIT) {
                        dcRadioBtn.setChecked(false);
                    }
                } else {
                    showCreditDebitDialog(cType);
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Hide the keyboard.
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                dialog.cancel();
                if (cType == CardOption.CardType.CREDIT) {
                    ccRadioBtn.setChecked(false);
                } else if (cType == CardOption.CardType.DEBIT) {
                    dcRadioBtn.setChecked(false);
                }
            }
        });

        input.requestFocus();
        alert.show();
    }
//6220180051800078431mag

    public void updateOtherPaymentsAmount(double amount, boolean isChecked) {

        if (mvcChecked && citrusCashChecked) {
            if (mPaymentDistribution.getOtherPaymentOptionAmount().equals(new Amount("0.00"))) {
                updateMVCText();
                updateCashText();
                txtremainingamount.setText(mPaymentDistribution.getOtherPaymentOptionAmount().getValue());
            } else {
                txtremainingamount.setText(mPaymentDistribution.getOtherPaymentOptionAmount().getValue());
            }
        } else if (mvcChecked && !citrusCashChecked) {
            // remaining amount = total - mvcamount OR otherPaymentAmount + citrusCashAmount
            BigDecimal amount1 = BigDecimal.valueOf(mPaymentDistribution.getOtherPaymentOptionAmount().getValueAsDouble());
            BigDecimal amount2 = BigDecimal.valueOf(mPaymentDistribution.getCitrusCashAmount().getValueAsDouble());
            BigDecimal amount3 = amount1.add(amount2);
            txtremainingamount.setText(amount3.toString());
        } else if (!mvcChecked && citrusCashChecked) {
            // remaining amount = total - citruscashAmount OR otherPaymentAmount + mvcAmount
            if (mPaymentDistribution.isCitrusCashAvailable()) {
                BigDecimal amount1 = BigDecimal.valueOf(mPaymentDistribution.getOtherPaymentOptionAmount().getValueAsDouble());
                BigDecimal amount2 = BigDecimal.valueOf(mPaymentDistribution.getMvcAmount().getValueAsDouble());
                BigDecimal amount3 = amount1.add(amount2);
                if (mPaymentDistribution.getOtherPaymentOptionAmount().equals(new Amount("0.00"))) {
                    txtremainingamount.setText("0");
                    String text = "CASH BALANCE ₹ :" + citrusCashAmount.getValue() + "  USED ₹:" + this.amount.getValue();
                    citrusCashBox.setText(text);
                } else {
                    txtremainingamount.setText(amount3.toString());
                }
            } else {
                txtremainingamount.setText("0");
                String text = "CASH BALANCE ₹ :" + citrusCashAmount.getValue() + "  USED ₹:" + amount;
                citrusCashBox.setText(text);
            }
        } else {
            // Both are not checked.
            // remaining amount = otherPaymentAmount + mvcAmount + citrusCashAmount
            BigDecimal amount1 = BigDecimal.valueOf(mPaymentDistribution.getOtherPaymentOptionAmount().getValueAsDouble());
            BigDecimal amount2 = BigDecimal.valueOf(mPaymentDistribution.getMvcAmount().getValueAsDouble());
            BigDecimal amount3 = BigDecimal.valueOf(mPaymentDistribution.getCitrusCashAmount().getValueAsDouble());
            BigDecimal amount4 = amount1.add(amount2);
            amount4 = amount4.add(amount3);
            txtremainingamount.setText(amount4.toString());
        }
    }


    public void updateMVCText() {
        String text = "MVC BALANCE ₹ :" + mvcAmount + "  USED ₹:" + mPaymentDistribution.getMvcAmount().getValue();
        mvcBox.setText(text);
    }

    public void updateCashText() {
        String text = "CASH BALANCE ₹ :" + citrusCashAmount.getValue() + "  USED ₹:" + mPaymentDistribution.getCitrusCashAmount().getValue();
        citrusCashBox.setText(text);
    }

}

