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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CashoutInfo;
import com.citrus.sdk.classes.CitrusConfig;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.classes.Month;
import com.citrus.sdk.classes.Year;
import com.citrus.sdk.logger.CitrusLogger;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.sdk.payment.PaymentBill;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citrus.sdk.response.CitrusResponse;
import com.citrus.sdk.response.PaymentResponse;
import com.citruspay.lazypay.common.LazyPay;
import com.optimustechproject2017.R;

public class UIActivity extends AppCompatActivity implements UserManagementFragment.UserManagementInteractionListener, WalletFragmentListener {

    private static final int SETTINGS_ACTION = 11;
    public static Activity activity = null;
    private FragmentManager fragmentManager = null;
    private Context mContext = this;
    private CitrusClient citrusClient = null;
    private CitrusConfig citrusConfig = null;
    private FrameLayout frameLayout = null;
    private View snackBarParent = null;
    private String app_name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui);

        activity = this;
//        app_name = getResources().getString(R.string.app_name);

        frameLayout = (FrameLayout) findViewById(R.id.container);

        fragmentManager = getSupportFragmentManager();

        citrusClient = CitrusClient.getInstance(mContext);
        citrusClient.setLogLevel(CitrusLogger.LogLevel.DEBUG);

        initCitrusClient();

        citrusClient.enableAutoOtpReading(true);
        citrusConfig = CitrusConfig.getInstance();
        citrusConfig.setColorPrimary(Constants.colorPrimary);
        citrusConfig.setColorPrimaryDark(Constants.colorPrimaryDark);
        citrusConfig.setTextColorPrimary(Constants.textColor);
        //showUI();

        onShowWalletScreen(true);
        snackBarParent = new View(this);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 200);
        layoutParams.gravity = Gravity.BOTTOM;
        snackBarParent.setLayoutParams(layoutParams);
        frameLayout.addView(snackBarParent);

        CreditCardOption creditCardOption = new CreditCardOption("Mangesh", "4386280018570121", "123", Month.AUG, Year._2020);


    }

    @Override
    protected void onResume() {
        super.onResume();
        String preferredEnv = Utils.getPreferredEnvironment(this);
        setTitle("PAYMENT");
    }

    private void initCitrusClient() {
        Utils.updateMerchantSignatures(this);

        citrusClient.init(Constants.SIGNUP_ID, Constants.SIGNUP_SECRET, Constants.SIGNIN_ID, Constants.SIGNIN_SECRET, Constants.VANITY, Constants.environment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_ui, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.action_settings) {
//            Intent intent = new Intent(this, SettingsActivity.class);
//            startActivityForResult(intent, SETTINGS_ACTION);
//        }
//        if (item.getItemId() == R.id.action_clearcache) {
//            AssetDownloadManager.getInstance().clearCache();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private void showUI() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .add(R.id.container, UIActivityFragment.newInstance());
        fragmentTransaction.commit();
    }

    public void onUserManagementClicked(View view) {
        UserManagementFragment fragment = UserManagementFragment.newInstance(this);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onPaymentComplete(Utils.PaymentType paymentType, TransactionResponse transactionResponse) {
        if (transactionResponse != null) {
//            Utils.showToast(mContext, transactionResponse.getMessage());
            showSnackBar(transactionResponse.getMessage());
        }

        if (paymentType == Utils.PaymentType.LOAD_MONEY) {
            citrusClient.isAutoLoadAvailable(new Callback<Boolean>() { //first check if auto load is available
                @Override
                public void success(Boolean aBoolean) {
                    if (aBoolean) {
                        promptAutoLoadSubscription();
                    }
                }

                @Override
                public void error(CitrusError error) {

                }
            });
        }
    }

    @Override
    public void onPaymentTypeSelected(Utils.PaymentType paymentType, Amount amount) {
        if (paymentType == Utils.PaymentType.NEW_CITRUS_CASH) {

            citrusClient.getBill(Constants.BILL_URL, amount, Constants.AMOUNT_DOUBLE_PRECISION_FORMAT, new Callback<PaymentBill>() {
                @Override
                public void success(PaymentBill paymentBill) {
                    //try {
                    if (paymentBill != null) {
                        PaymentType.CitrusCash citrusCash = new PaymentType.CitrusCash(paymentBill);
                        citrusClient.simpliPay(citrusCash, new Callback<TransactionResponse>() {
                            @Override
                            public void success(TransactionResponse transactionResponse) {
                                if (getApplicationContext() != null) {
                                    showSnackBar(transactionResponse.getMessage());
                                }
                            }

                            @Override
                            public void error(CitrusError error) {
                                if (getApplicationContext() != null) {
                                    showSnackBar(error.getMessage());
                                }
                            }
                        });
                    } else {
                        showSnackBar("Incorrect Bill Received from Server.");
                    }
                   /* } catch (CitrusException e) {
                        e.printStackTrace();
                        showSnackBar(e.getMessage());
                    }*/
                }

                @Override
                public void error(CitrusError error) {
                    showSnackBar(error.getMessage());
                }
            });


        } else if (paymentType == Utils.PaymentType.CITRUS_CASH) {

            try {
                citrusClient.simpliPay(new PaymentType.CitrusCash(amount, Constants.BILL_URL), new Callback<TransactionResponse>() {
                    @Override
                    public void success(TransactionResponse transactionResponse) {
                        if (getApplicationContext() != null) {
                            showSnackBar(transactionResponse.getMessage());
                        }
                    }

                    @Override
                    public void error(CitrusError error) {
                        if (getApplicationContext() != null) {
                            showSnackBar(error.getMessage());
                        }
                    }
                });
            } catch (CitrusException e) {
                e.printStackTrace();
                if (getApplicationContext() != null) {
                    showSnackBar(e.getMessage());
                }
            }
        } else if (paymentType == Utils.PaymentType.WALLET_PG_PAYMENT) {
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
//                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                    .replace(R.id.container, WalletPGPaymentFragment.newInstance(paymentType, amount));


            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.container, WalletPGPaymentFragment.newInstance(paymentType, amount));

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else if (paymentType == Utils.PaymentType.AUTO_LOAD_MONEY) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.container, AutoLoadFragment.newInstance(amount));

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.container, CardPaymentFragment.newInstance(paymentType, amount));

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onPaymentTypeSelected(Utils.DPRequestType dpRequestType, Amount
            originalAmount, String couponCode, Amount alteredAmount) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, CardPaymentFragment.newInstance(dpRequestType, originalAmount, couponCode, alteredAmount));

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onCashoutSelected(CashoutInfo cashoutInfo) {
        citrusClient.saveCashoutInfo(cashoutInfo, new Callback<CitrusResponse>() {
            @Override
            public void success(CitrusResponse citrusResponse) {
                showSnackBar(citrusResponse.getMessage());
            }

            @Override
            public void error(CitrusError error) {
                showSnackBar(error.getMessage());
            }
        });

        citrusClient.cashout(cashoutInfo, new Callback<PaymentResponse>() {
            @Override
            public void success(PaymentResponse paymentResponse) {
                String message = "";
                if (paymentResponse.getStatus() == CitrusResponse.Status.SUCCESSFUL) {
                    message = "Cashout Was Successful";
                }
                showSnackBar(message);
            }

            @Override
            public void error(CitrusError error) {
                showSnackBar(error.getMessage());
            }
        });
    }

    @Override
    public void onUpdateProfileSelected() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, UpdateProfileFragment.newInstance());

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onAutoLoadSelected(Utils.PaymentType paymentType, Amount amount, String updatedLoadAmount, String updatedThresholdAmount, boolean isUpdate) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.container, AutoLoadFragment.newInstance(amount, updatedLoadAmount, updatedThresholdAmount));

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public void onWalletPaymentClicked(View view) {
        onShowWalletScreen(true);
    }

    @Override
    public void showSnackBar(String message) {
        if (snackBarParent != null & !isFinishing())
            Snackbar.make(snackBarParent, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onWalletPgSplitOptionSelected(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .add(R.id.container, fragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onShowWalletScreen(boolean isAddToBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.container, WalletPaymentFragment.newInstance());
        if (isAddToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_ACTION) {
            if (data != null) {
                boolean settingsChanged = data.getBooleanExtra(SettingsActivity.KEY_SETTINGS_CHANGED, false);
                if (settingsChanged) {
                    Utils.updateMerchantSignatures(this);
                    citrusClient.destroy();
                    LazyPay.destroy();
                    citrusClient.init(Constants.SIGNUP_ID, Constants.SIGNUP_SECRET, Constants.SIGNIN_ID, Constants.SIGNIN_SECRET, Constants.VANITY, Constants.environment);

                    String preferredEnv = Utils.getPreferredEnvironment(this);
                    setTitle("Payment");

                    citrusClient.signOut(new Callback<CitrusResponse>() {
                        @Override
                        public void success(CitrusResponse citrusResponse) {
                            Utils.showToast(UIActivity.this, citrusResponse.getMessage());
                        }

                        @Override
                        public void error(CitrusError error) {
                            Utils.showToast(UIActivity.this, error.getMessage());
                        }
                    });

                }

            }

        }
    }


    private void promptAutoLoadSubscription() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(UIActivity.this);
        String message = null;
        String positiveButtonText = "Yes";


        LinearLayout linearLayout = new LinearLayout(UIActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        alert.setTitle("Enable Auto-Load?");
        alert.setMessage(getString(R.string.auto_load_message));

        alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.container, AutoLoadFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }


    @Override
    public void onBackPressed() {

        finish();

    }
}
