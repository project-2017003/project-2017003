package com.optimustechproject2017.payment;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.CitrusUser;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.payment.MerchantPaymentOption;
import com.citrus.sdk.response.CitrusError;
import com.citruspay.lazypay.common.LazyPay;
import com.citruspay.lazypay.common.LazyPayConfig;
import com.citruspay.lazypay.common.LazyPayException;
import com.citruspay.lazypay.data.ProductAttributes;
import com.citruspay.lazypay.data.ProductSkuDetail;
import com.citruspay.lazypay.data.Sku;
import com.optimustechproject2017.R;

public class CitrusLpExampleActivity extends AppCompatActivity {

    private static final String PREF_EMAIL = "email";
    private static final String PREF_MOBILE = "mobile";
    private static final String PREF_AMOUNT = "amt";
    private CitrusClient citrusClient;
    private EditText editEmailId, editMobileNo, editAmount;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lp_example);

        editEmailId = (EditText) findViewById(R.id.edit_email_id);
        editMobileNo = (EditText) findViewById(R.id.edit_mobile_no);
        editAmount = (EditText) findViewById(R.id.edit_amount);

        citrusClient = CitrusClient.getInstance(getApplicationContext());

        populateFields();

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void populateFields() {
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        final String emailId = preferences.getString(PREF_EMAIL, "");
        final String mobile = preferences.getString(PREF_MOBILE, "");
        final String amount = preferences.getString(PREF_AMOUNT, "");

        if (TextUtils.isEmpty(emailId) && TextUtils.isEmpty(mobile)) {
            citrusClient.getProfileInfo(new Callback<CitrusUser>() {
                @Override
                public void success(CitrusUser citrusUser) {
                    editEmailId.setText(citrusUser.getEmailId());
                    editMobileNo.setText(citrusUser.getMobileNo());
                }

                @Override
                public void error(CitrusError error) {

                }
            });
        } else {
            editEmailId.setText(emailId);
            editMobileNo.setText(mobile);
        }

        editAmount.setText(amount);
    }

    private void saveFields() {

        final String emailId = editEmailId.getText().toString();
        final String mobileNo = editMobileNo.getText().toString();
        final String amount = editAmount.getText().toString();


        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        editor.putString(PREF_EMAIL, emailId);
        editor.putString(PREF_MOBILE, mobileNo);
        editor.putString(PREF_AMOUNT, amount);

        editor.commit();
    }

    private boolean validateFields() {
        final String emailId = editEmailId.getText().toString();
        final String mobileNo = editMobileNo.getText().toString();
        final String amount = editAmount.getText().toString();

        if (TextUtils.isEmpty(emailId) || TextUtils.isEmpty(mobileNo) || TextUtils.isEmpty(amount)) {
            return false;
        } else {
            saveFields();
            return true;
        }
    }

    public void onCheckEligibilityClicked(View view) {
        if (validateFields()) {
            progressBar.setVisibility(View.VISIBLE);
            checkMerchantEligibility(new Callback<Boolean>() {
                @Override
                public void success(final Boolean eligible) {
                    progressBar.setVisibility(View.GONE);
                    if (eligible) {
                        showResult("Result", "LazyPay is enabled for this merchant");
                    } else {
                        showResult("Result", "LazyPay is not enabled for this merchant");
                    }
                }

                @Override
                public void error(CitrusError error) {
                    progressBar.setVisibility(View.GONE);
                    showResult("Error", error.getMessage());
                }
            });
        } else {
            showToast("Invalid data entered");
        }
    }

    private void checkMerchantEligibility(final Callback<Boolean> callback) {
        citrusClient.getMerchantPaymentOptions(new Callback<MerchantPaymentOption>() {
            @Override
            public void success(final MerchantPaymentOption merchantPaymentOption) {
                if (merchantPaymentOption != null && merchantPaymentOption.isLazyPayEnabled()) {
                    callback.success(true);
                } else {
                    callback.success(false);
                }
            }

            @Override
            public void error(final CitrusError error) {
                callback.error(error);
            }
        });
    }


    public void onLazyPayNowClicked(View view) {
        checkMerchantEligibility(new Callback<Boolean>() {
            @Override
            public void success(final Boolean eligible) {
                if (eligible) {
                    if (validateFields()) {
                        final String emailId = editEmailId.getText().toString();
                        final String mobileNo = editMobileNo.getText().toString();
                        final String amount = editAmount.getText().toString();

                        final CitrusUser lpCitrusUser = new CitrusUser(emailId, mobileNo);
                        CitrusUser.Address lpUserAddress = new CitrusUser.Address("Street1", "Street2", "Pune", "Maharashtra", "India", "411044");
                        lpCitrusUser.setAddress(lpUserAddress);
                        startLazyPay(new Amount(amount), lpCitrusUser);
                    } else {
                        showToast("Invalid data entered");
                    }
                } else {
                    showResult("Cannot proceed", "LazyPay is not enabled for this merchant");
                }
            }

            @Override
            public void error(CitrusError error) {
                showResult("Error while checking Merchant LazyPay Eligibility", error.getMessage());
            }
        });
    }

    private void startLazyPay(final Amount amount, final CitrusUser citrusUser) {
        final Callback<TransactionResponse> lpTransactionCallback = new Callback<TransactionResponse>() {
            @Override
            public void success(final TransactionResponse transactionResponse) {
                if (transactionResponse == null) {
                    showResult("Failed", "LazyPay transaction failed");
                } else {
                    if (transactionResponse.getTransactionStatus() == TransactionResponse.TransactionStatus.SUCCESSFUL) {
                        showResult(transactionResponse.getTransactionStatus().name(),
                                String.format("Details:\nTransaction id = %s\nIssuerRefNo = %s\nPgTxnNo = %s\nAmount = %s",
                                        transactionResponse.getTransactionId(),
                                        transactionResponse.getTransactionDetails().getIssuerRefNo(),
                                        transactionResponse.getTransactionDetails().getPgTxnNo(),
                                        transactionResponse.getTransactionAmount()));
                    }
                }
            }

            @Override
            public void error(CitrusError error) {
                showResult(error.getStatus().name(), "LazyPay transaction failed: " + error.getMessage());
            }
        };

        try {
            final LazyPayConfig lpConfig = new LazyPayConfig();

            lpConfig.setLpPaySuccessfulCallback(lpTransactionCallback);
            lpConfig.setAmount(amount);

            lpConfig.setCitrusUser(citrusUser);

            final ProductSkuDetail productSkuDetail = new ProductSkuDetail();
            productSkuDetail.setProductId("Prod_1");
            productSkuDetail.setDescription("Prod_description");
            productSkuDetail.setImageUrl("www.prod4image.com");
            productSkuDetail.setShippable(true);

            final ProductAttributes attributes = new ProductAttributes();
            attributes.setSize("32");
            attributes.setColor("blue");
            productSkuDetail.setAttributes(attributes);

            final Sku[] skus = new Sku[1];
            skus[0] = new Sku();
            skus[0].setSkuId("Sku1");
            skus[0].setPrice(amount.getValue());
            skus[0].setAttributes(attributes);
            productSkuDetail.setSkus(skus);

            final ProductSkuDetail[] productSkuDetails = new ProductSkuDetail[1];
            productSkuDetails[0] = productSkuDetail;

            lpConfig.setBillUrl(Constants.BILL_URL);

            lpConfig.setProductDetails(productSkuDetails);

            LazyPay.initiatePayment(this, lpConfig);
        } catch (LazyPayException e) {
            showResult("Error starting LazyPay", e.getMessage());
            e.printStackTrace();
        }
    }

    private void showResult(final String title, final String message) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(title);
        alert.setMessage(message);

        alert.setPositiveButton("OK", null);

        alert.show();
    }

    public void onLpSignOutClicked(final View view) {
        final boolean signedOut = LazyPay.signOutFromLazyPay(this);
        if (signedOut) {
            showResult("User signed out from LazyPay", "LP token cleared. Auto Debit will not work next time");
        } else {
            showResult("User signed out from LazyPay", "No LP token found");
        }
    }

    @Override
    protected void onDestroy() {
        LazyPay.destroy();
        super.onDestroy();
    }

    private void showToast(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
