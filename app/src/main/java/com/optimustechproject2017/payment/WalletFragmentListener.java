package com.optimustechproject2017.payment;

import android.support.v4.app.Fragment;

import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CashoutInfo;

/**
 * Created by salil on 3/6/15.
 */
public interface WalletFragmentListener {
    void onPaymentComplete(Utils.PaymentType paymentType, TransactionResponse transactionResponse);

    void onPaymentTypeSelected(Utils.PaymentType paymentType, Amount amount);

    void onPaymentTypeSelected(Utils.DPRequestType dpRequestType, Amount originalAmount, String couponCode, Amount alteredAmount);

    void onCashoutSelected(CashoutInfo cashoutInfo);

    void onUpdateProfileSelected();

    void onAutoLoadSelected(Utils.PaymentType paymentType, Amount amount, String updatedLoadAmount, String updatedThresholdAmount, boolean isUpdateAutoLoad);

    void showSnackBar(String message);

    void onWalletPgSplitOptionSelected(Fragment fragment);
}
