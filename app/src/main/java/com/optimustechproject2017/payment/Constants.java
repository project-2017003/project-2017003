package com.optimustechproject2017.payment;

import com.citrus.sdk.Environment;

/**
 * Created by salil on 13/6/15.
 */
public final class Constants {

    public static final String AMOUNT_DOUBLE_PRECISION_FORMAT = "#.00";
    public static String BILL_URL = "https://salty-plateau-1529.herokuapp.com/billGenerator.sandbox.php";

    // Sand wallet PG
    public static String SIGNUP_ID = "9hh5re3r5q-signup";
    public static String SIGNUP_SECRET = "3be4d7bf59c109e76a3619a33c1da9a8";
    public static String SIGNIN_ID = "9hh5re3r5q-signin";
    public static String SIGNIN_SECRET = "ffcfaaf6e6e78c2f654791d9d6cb7f09";
    public static String VANITY = "nativeSDK";
    public static Environment environment = Environment.SANDBOX;

    public static boolean enableLogging = true;

    public static String colorPrimaryDark = "#E7961D";
    public static String colorPrimary = "#F9A323";
    public static String textColor = "#ffffff";
}
