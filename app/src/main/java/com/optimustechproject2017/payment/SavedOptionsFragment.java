package com.optimustechproject2017.payment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.dynamicPricing.DynamicPricingRequestType;
import com.citrus.sdk.dynamicPricing.DynamicPricingResponse;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.CitrusCash;
import com.citrus.sdk.payment.MVCOption;
import com.citrus.sdk.payment.PaymentOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.optimustechproject2017.R;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class SavedOptionsFragment extends Fragment {

    private Utils.PaymentType paymentType = null;
    private Utils.DPRequestType dpRequestType = null;
    private Amount amount = null;
    private String couponCode = null;
    private Amount alteredAmount = null;
    private ArrayList<PaymentOption> walletList = null;
    private CitrusClient citrusClient = null;
    private SavedOptionsAdapter savedOptionsAdapter = null;

    public SavedOptionsFragment() {

    }

    public static SavedOptionsFragment newInstance(Utils.PaymentType paymentType, Amount amount) {
        SavedOptionsFragment fragment = new SavedOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("paymentType", paymentType);
        bundle.putParcelable("amount", amount);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static SavedOptionsFragment newInstance(Utils.DPRequestType dpRequestType, Amount originalAmount, String couponCode, Amount alteredAmount) {
        SavedOptionsFragment fragment = new SavedOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("paymentType", Utils.PaymentType.DYNAMIC_PRICING);
        bundle.putSerializable("dpRequestType", dpRequestType);
        bundle.putParcelable("amount", originalAmount);
        bundle.putParcelable("alteredAmount", alteredAmount);
        bundle.putString("couponCode", couponCode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            paymentType = (Utils.PaymentType) bundle.getSerializable("paymentType");
            dpRequestType = (Utils.DPRequestType) bundle.getSerializable("dpRequestType");
            amount = bundle.getParcelable("amount");
            alteredAmount = bundle.getParcelable("alteredAmount");
            couponCode = bundle.getString("couponCode");

            walletList = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        citrusClient = CitrusClient.getInstance(getActivity());

        View returnView = inflater.inflate(R.layout.fragment_saved_cards, container, false);

        savedOptionsAdapter = new SavedOptionsAdapter(getActivity(), walletList);

        RecyclerView recylerViewNetbanking = (RecyclerView) returnView.findViewById(R.id.recycler_view_saved_options);
        recylerViewNetbanking.setAdapter(savedOptionsAdapter);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recylerViewNetbanking.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recylerViewNetbanking.setLayoutManager(mLayoutManager);

        recylerViewNetbanking.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new OnItemClickListener()));

        // In Case of DP show Citrus Cash As a Payment Option
      /*  if (paymentType == Utils.PaymentType.DYNAMIC_PRICING) {
            citrusClient.getBalance(new Callback<Amount>() {
                @Override
                public void success(Amount amount) {
                    CitrusCash citrusCash = new CitrusCash(amount);
                    walletList.add(0, citrusCash);
                }

                @Override
                public void error(CitrusError error) {
                    //NOOP
                }
            });
        }*/

        fetchWallet();

        return returnView;
    }

    private void fetchWallet() {
        citrusClient.getWallet(new Callback<List<PaymentOption>>() {
            @Override
            public void success(List<PaymentOption> paymentOptionList) {
                walletList.clear();
                // In case of not a wallet pg payment remove citrus cash and MVC from the payment options list.
                if (paymentType == Utils.PaymentType.PG_PAYMENT
                        || paymentType == Utils.PaymentType.NEW_PG_PAYMENT
                        || paymentType == Utils.PaymentType.LOAD_MONEY
                        || paymentType == Utils.PaymentType.CITRUS_CASH
                        || paymentType == Utils.PaymentType.DYNAMIC_PRICING
                        || paymentType == Utils.PaymentType.NEW_CITRUS_CASH) {
                    for (PaymentOption paymentOption : paymentOptionList) {
                        // Do not put the Citrus Cash and MVC as a payment instrument in case not a wallet pg payment.
                        if (!(paymentOption instanceof CitrusCash || paymentOption instanceof MVCOption)) {
                            walletList.add(paymentOption);
                        }
                    }
                } else if (paymentType == Utils.PaymentType.DYNAMIC_PRICING && walletList.size() == 1) {
                    // In Case of DP show Citrus Cash As a Payment Option
                    walletList.addAll(1, paymentOptionList);

                    savedOptionsAdapter.setWalletList(walletList);
                } else {
                    walletList.addAll(0, paymentOptionList);
                    savedOptionsAdapter.setWalletList(walletList);
                }

                savedOptionsAdapter.notifyDataSetChanged();
            }

            @Override
            public void error(CitrusError error) {
//                Utils.showToast(getActivity(), error.getMessage());
                if (getActivity() != null) {
                    ((UIActivity) getActivity()).showSnackBar(error.getMessage());
                } else {
                    Logger.d("ERROR", error.getMessage());
                }
            }
        });
    }

    private PaymentOption getItem(int position) {
        PaymentOption paymentOption = null;

        if (walletList != null && walletList.size() > position && position >= -1) {
            paymentOption = walletList.get(position);
        }

        return paymentOption;
    }

    private void proceedToPayment(final Utils.PaymentType paymentType, PaymentOption paymentOption) {

        if (paymentType == Utils.PaymentType.DYNAMIC_PRICING) {
            DynamicPricingRequestType dynamicPricingRequestType = null;

            if (dpRequestType == Utils.DPRequestType.SEARCH_AND_APPLY) {
                dynamicPricingRequestType = new DynamicPricingRequestType.SearchAndApplyRule(amount, paymentOption, null);
            } else if (dpRequestType == Utils.DPRequestType.CALCULATE_PRICING) {
                dynamicPricingRequestType = new DynamicPricingRequestType.CalculatePrice(amount, paymentOption, couponCode, null);
            } else if (dpRequestType == Utils.DPRequestType.VALIDATE_RULE) {
                dynamicPricingRequestType = new DynamicPricingRequestType.ValidateRule(amount, paymentOption, couponCode, alteredAmount, null);
            }

            citrusClient.performDynamicPricing(dynamicPricingRequestType, Constants.BILL_URL, new Callback<DynamicPricingResponse>() {
                @Override
                public void success(DynamicPricingResponse dynamicPricingResponse) {
                    showPrompt(dynamicPricingResponse);
                }

                @Override
                public void error(CitrusError error) {
                    ((UIActivity) getActivity()).showSnackBar(error.getMessage());
                }
            });
        } else {

            PaymentType paymentType1;

            Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse) {
                    if (getActivity() != null) {
                        ((UIActivity) getActivity()).onPaymentComplete(paymentType, transactionResponse);
                    }
                }

                @Override
                public void error(CitrusError error) {
                    if (getActivity() != null) {
                        ((UIActivity) getActivity()).showSnackBar(error.getMessage());
                    }
                }
            };

            try {
                if (paymentType == Utils.PaymentType.LOAD_MONEY) {
                    paymentType1 = new PaymentType.LoadMoney(amount, paymentOption);
                    citrusClient.simpliPay(paymentType1, callback);
                } else if (paymentType == Utils.PaymentType.PG_PAYMENT) {
                    paymentType1 = new PaymentType.PGPayment(amount, Constants.BILL_URL, paymentOption, null);
                    citrusClient.simpliPay(paymentType1, callback);
                } else if (this.paymentType == Utils.PaymentType.NEW_PG_PAYMENT) {
                    paymentType1 = new PaymentType.PGPayment(amount, Constants.BILL_URL, paymentOption, null);
                    ((PaymentType.PGPayment) paymentType1).useSingleHop(true);
                    citrusClient.simpliPay(paymentType1, callback);
                }
            } catch (CitrusException e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    ((UIActivity) getActivity()).showSnackBar(e.getMessage());
                }
            }
        }
    }

    private void proceedToFastPayment(final Utils.PaymentType paymentType, PaymentOption paymentOption) {

        if (paymentType == Utils.PaymentType.DYNAMIC_PRICING) {
            DynamicPricingRequestType dynamicPricingRequestType = null;

            if (dpRequestType == Utils.DPRequestType.SEARCH_AND_APPLY) {
                dynamicPricingRequestType = new DynamicPricingRequestType.SearchAndApplyRule(amount, paymentOption, null);
            } else if (dpRequestType == Utils.DPRequestType.CALCULATE_PRICING) {
                dynamicPricingRequestType = new DynamicPricingRequestType.CalculatePrice(amount, paymentOption, couponCode, null);
            } else if (dpRequestType == Utils.DPRequestType.VALIDATE_RULE) {
                dynamicPricingRequestType = new DynamicPricingRequestType.ValidateRule(amount, paymentOption, couponCode, alteredAmount, null);
            }

            citrusClient.performDynamicPricing(dynamicPricingRequestType, Constants.BILL_URL, new Callback<DynamicPricingResponse>() {
                @Override
                public void success(DynamicPricingResponse dynamicPricingResponse) {
                    showPrompt(dynamicPricingResponse);
                }

                @Override
                public void error(CitrusError error) {
                    if (getActivity() != null) {
                        ((UIActivity) getActivity()).showSnackBar(error.getMessage());
                    }
                }
            });

        } else {

            PaymentType paymentType1;

            Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse) {
                    ((UIActivity) getActivity()).onPaymentComplete(paymentType, transactionResponse);
                }

                @Override
                public void error(CitrusError error) {
                    if (getActivity() != null) {
                        ((UIActivity) getActivity()).showSnackBar(error.getMessage());
                    }
                }
            };

            try {
                if (paymentType == Utils.PaymentType.LOAD_MONEY) {
                    paymentType1 = new PaymentType.LoadMoney(amount, paymentOption);
                    citrusClient.simpliPay(paymentType1, callback);
                } else if (paymentType == Utils.PaymentType.PG_PAYMENT) {
                    paymentType1 = new PaymentType.PGPayment(amount, Constants.BILL_URL, paymentOption, null);
                    citrusClient.simpliPay(paymentType1, callback);
                }
            } catch (CitrusException e) {
                e.printStackTrace();

                if (getActivity() != null) {
                    ((UIActivity) getActivity()).showSnackBar(e.getMessage());
                }
            }
        }
    }

    private void proceedToFastMakePayment(final Utils.PaymentType paymentType, PaymentOption paymentOption) {

        if (paymentType == Utils.PaymentType.DYNAMIC_PRICING) {
            DynamicPricingRequestType dynamicPricingRequestType = null;

            if (dpRequestType == Utils.DPRequestType.SEARCH_AND_APPLY) {
                dynamicPricingRequestType = new DynamicPricingRequestType.SearchAndApplyRule(amount, paymentOption, null);
            } else if (dpRequestType == Utils.DPRequestType.CALCULATE_PRICING) {
                dynamicPricingRequestType = new DynamicPricingRequestType.CalculatePrice(amount, paymentOption, couponCode, null);
            } else if (dpRequestType == Utils.DPRequestType.VALIDATE_RULE) {
                dynamicPricingRequestType = new DynamicPricingRequestType.ValidateRule(amount, paymentOption, couponCode, alteredAmount, null);
            }

            citrusClient.performDynamicPricing(dynamicPricingRequestType, Constants.BILL_URL, new Callback<DynamicPricingResponse>() {
                @Override
                public void success(DynamicPricingResponse dynamicPricingResponse) {
                    showPrompt(dynamicPricingResponse);
                }

                @Override
                public void error(CitrusError error) {
                    ((UIActivity) getActivity()).showSnackBar(error.getMessage());
                }
            });

        } else {

            PaymentType paymentType1;

            Callback<TransactionResponse> callback = new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse) {
                    ((UIActivity) getActivity()).onPaymentComplete(paymentType, transactionResponse);
                }

                @Override
                public void error(CitrusError error) {
                    if (getActivity() != null) {
                        ((UIActivity) getActivity()).showSnackBar(error.getMessage());
                    }
                }
            };

            try {
                if (paymentType == Utils.PaymentType.LOAD_MONEY) {
                    paymentType1 = new PaymentType.LoadMoney(amount, paymentOption);
                    citrusClient.simpliPay((PaymentType.LoadMoney) paymentType1, callback);
                } else if (paymentType == Utils.PaymentType.NEW_PG_PAYMENT) {
                    paymentType1 = new PaymentType.PGPayment(amount, Constants.BILL_URL, paymentOption, null);
                    ((PaymentType.PGPayment) paymentType1).useSingleHop(true);
                    citrusClient.simpliPay(paymentType1, callback);
                }
            } catch (CitrusException e) {
                e.printStackTrace();

                if (getActivity() != null) {
                    ((UIActivity) getActivity()).showSnackBar(e.getMessage());
                }
            }
        }
    }

    private void showPrompt(final Utils.PaymentType paymentType, final PaymentOption paymentOption) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        String message = "Please enter CVV?";
        String positiveButtonText = "OK";
        alert.setTitle("CVV");
        alert.setMessage(message);
        // Set an EditText view to get user input
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
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

                // Set the OTP
                ((CardOption) paymentOption).setCardCVV(cvv);
                proceedToPayment(paymentType, paymentOption);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        input.requestFocus();
        alert.show();
    }

    private void showPrompt(final DynamicPricingResponse dynamicPricingResponse) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        String message = dynamicPricingResponse.getMessage();
        String positiveButtonText = "Pay";

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final TextView originalAmount = new TextView(getActivity());
        final TextView alteredAmount = new TextView(getActivity());
        final TextView txtMessage = new TextView(getActivity());
        final TextView txtConsumerMessage = new TextView(getActivity());

        linearLayout.addView(originalAmount);
        linearLayout.addView(alteredAmount);
        linearLayout.addView(txtMessage);
        linearLayout.addView(txtConsumerMessage);

        originalAmount.setText("Original Amount : " + (dynamicPricingResponse.getOriginalAmount() != null ? dynamicPricingResponse.getOriginalAmount().getValue() : ""));
        alteredAmount.setText("Altered Amount : " + (dynamicPricingResponse.getAlteredAmount() != null ? dynamicPricingResponse.getAlteredAmount().getValue() : ""));
        txtMessage.setText("Message : " + dynamicPricingResponse.getMessage());
        txtMessage.setText("Consumer Message : " + dynamicPricingResponse.getConsumerMessage());

        alert.setTitle("Dynamic Pricing Response");
        alert.setMessage(message);
        alert.setView(linearLayout);
        if (dynamicPricingResponse.getStatus() == DynamicPricingResponse.Status.SUCCESS) {
            alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int whichButton) {

                    try {
                        PaymentType.PGPayment pgPayment = new PaymentType.PGPayment(dynamicPricingResponse);

                        CitrusClient.getInstance(getActivity()).simpliPay(pgPayment, new Callback<TransactionResponse>() {
                            @Override
                            public void success(TransactionResponse transactionResponse) {
                                ((UIActivity) getActivity()).showSnackBar(transactionResponse.getMessage());
                            }

                            @Override
                            public void error(CitrusError error) {
                                ((UIActivity) getActivity()).showSnackBar(error.getMessage());
                            }
                        });
                    } catch (CitrusException e) {
                        e.printStackTrace();
                    }

                    dialog.dismiss();
                }
            });
        }

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    private class OnItemClickListener extends RecyclerItemClickListener.SimpleOnItemClickListener {

        @Override
        public void onItemClick(View childView, int position) {
            PaymentOption paymentOption = getItem(position);

            if (paymentOption instanceof CardOption) {
                if (citrusClient.isOneTapPaymentEnabledForCard((CardOption) paymentOption)) {
                    if (paymentType == Utils.PaymentType.NEW_PG_PAYMENT) {
                        proceedToFastMakePayment(paymentType, paymentOption);

                    } else {
                        proceedToFastPayment(paymentType, paymentOption);
                    }
                } else {
                    showPrompt(paymentType, paymentOption);
                }

            } else {
                proceedToPayment(paymentType, paymentOption);
            }
        }

        @Override
        public void onItemLongPress(View childView, int position) {
            PaymentOption paymentOption = getItem(position);

            citrusClient.deletePaymentOption(paymentOption, new Callback<CitrusResponse>() {
                @Override
                public void success(CitrusResponse citrusResponse) {
//                    Utils.showToast(getActivity(), citrusResponse.getMessage());
                    ((UIActivity) getActivity()).showSnackBar(citrusResponse.getMessage());

                    walletList.clear();
                    savedOptionsAdapter.notifyDataSetChanged();
                    fetchWallet();
                }

                @Override
                public void error(CitrusError error) {
//                    Utils.showToast(getActivity(), error.getMessage());
                    ((UIActivity) getActivity()).showSnackBar(error.getMessage());
                }
            });
        }
    }
}