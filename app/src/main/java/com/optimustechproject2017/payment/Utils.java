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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.Environment;
import com.citrus.sdk.logger.CitrusLogger;
import com.optimustechproject2017.R;

/**
 * Created by salil on 29/5/15.
 */
public class Utils {

    public static void showToast(Context context, String message) {
        Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    public static int getSizeInPx(Context context, int sizeInDP) {
        float density = context.getResources().getDisplayMetrics().density;
        float px = sizeInDP * density;

        return (int) px;
    }

    /**
     * @param context
     * @return preferredEnvironment
     */
    public static String getPreferredEnvironment(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(getResourceString(context, R.string.prefs_environment_key), Constants.environment.toString());
    }

    /**
     * @param context
     */
    public static void updateMerchantSignatures(Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String billUrl = prefs.getString(getResourceString(context, R.string.pref_bill_url_key), getResourceString(context, R.string.prefs_bill_url_default_value));
        boolean useDafaultSignautes = prefs.getBoolean(getResourceString(context, R.string.pref_key_use_default_signatures), true);
        boolean enableLog = prefs.getBoolean(getResourceString(context, R.string.pref_enable_logs_key), true);

        Constants.enableLogging = enableLog;
        CitrusClient.getInstance(context).setLogLevel(enableLog == true ? CitrusLogger.LogLevel.DEBUG : CitrusLogger.LogLevel.ERROR);

        String preferredEnv = Utils.getPreferredEnvironment(context);

        if (preferredEnv.equalsIgnoreCase(Utils.getResourceString(context, R.string.environment_preference_default_value))) {
            // Sandbox env

            if (useDafaultSignautes) {
                Constants.BILL_URL = MerchantConfigParameters.SANDBOX.getBillUrl();
                Constants.VANITY = MerchantConfigParameters.SANDBOX.getVanity();
                Constants.environment = Environment.SANDBOX;
                Constants.SIGNUP_ID = MerchantConfigParameters.SANDBOX.getSignUpId();
                Constants.SIGNUP_SECRET = MerchantConfigParameters.SANDBOX.getSignUpSecret();
                Constants.SIGNIN_ID = MerchantConfigParameters.SANDBOX.getSignInId();
                Constants.SIGNIN_SECRET = MerchantConfigParameters.SANDBOX.getSignInSecret();
            }
        } else if (preferredEnv.equalsIgnoreCase("Production")) {
            // Production Env
            if (useDafaultSignautes) {
                Constants.BILL_URL = MerchantConfigParameters.PRODUCTION.getBillUrl();
                Constants.VANITY = MerchantConfigParameters.PRODUCTION.getVanity();
                Constants.environment = Environment.PRODUCTION;
                Constants.SIGNUP_ID = MerchantConfigParameters.PRODUCTION.getSignUpId();
                Constants.SIGNUP_SECRET = MerchantConfigParameters.PRODUCTION.getSignUpSecret();
                Constants.SIGNIN_ID = MerchantConfigParameters.PRODUCTION.getSignInId();
                Constants.SIGNIN_SECRET = MerchantConfigParameters.PRODUCTION.getSignInSecret();
            }
        }

    }

    /**
     * @param context
     * @param resourceId
     * @return resourceString
     */
    public static String getResourceString(Context context, int resourceId) {

        return context.getString(resourceId);
    }

    public enum PaymentType {
        AUTO_LOAD_MONEY, LOAD_MONEY, NEW_CITRUS_CASH, CITRUS_CASH, NEW_PG_PAYMENT, PG_PAYMENT, DYNAMIC_PRICING, WALLET_PG_PAYMENT;
    }

    public enum DPRequestType {
        SEARCH_AND_APPLY, CALCULATE_PRICING, VALIDATE_RULE;
    }

    public enum MerchantConfigParameters {
        SANDBOX {
            @Override
            public String getBillUrl() {
                return "https://salty-plateau-1529.herokuapp.com/billGenerator.sandbox.php";
            }

            @Override
            public String getVanity() {
                return "nativeSDK";
            }

            @Override
            public String getSignUpId() {
                return "9hh5re3r5q-signup";
            }

            @Override
            public String getSignUpSecret() {
                return "3be4d7bf59c109e76a3619a33c1da9a8";
            }

            @Override
            public String getSignInId() {
                return "9hh5re3r5q-signin";
            }

            @Override
            public String getSignInSecret() {
                return "ffcfaaf6e6e78c2f654791d9d6cb7f09";
            }
        },
        PRODUCTION {
            @Override
            public String getBillUrl() {
                return "https://salty-plateau-1529.herokuapp.com/billGenerator.production.php";
            }

            @Override
            public String getVanity() {
                return "testing";
            }

            @Override
            public String getSignUpId() {
                return "kkizp9tsqg-signup";
            }

            @Override
            public String getSignUpSecret() {
                return "39c50a32eaabaf382223fdd05f331e1c";
            }

            @Override
            public String getSignInId() {
                return "kkizp9tsqg-signin";
            }

            @Override
            public String getSignInSecret() {
                return "1fc1f57639ec87cf4d49920f6b3a2c9d";
            }
        };

        public abstract String getBillUrl();

        public abstract String getVanity();

        public abstract String getSignUpId();

        public abstract String getSignUpSecret();

        public abstract String getSignInId();

        public abstract String getSignInSecret();


    }

}
