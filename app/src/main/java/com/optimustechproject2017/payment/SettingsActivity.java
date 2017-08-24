package com.optimustechproject2017.payment;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.Environment;
import com.optimustechproject2017.R;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String KEY_SETTINGS_CHANGED = "SettingsChanged";
    /**
     * Determines whether to always show the simplified settings UI, where
     * settings are presented in a single list. When false, settings are shown
     * as a master/detail two-pane view on tablets. When true, a single pane is
     * shown on tablets.
     */
    private static final boolean ALWAYS_SIMPLE_PREFS = false;
    private static CitrusClient citrusClient = null;
    private Context context = SettingsActivity.this;
    private boolean settingsChanged = false;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

                if ("Production".equalsIgnoreCase(stringValue)) {
                    Constants.environment = Environment.PRODUCTION;
                } else if ("Sandbox".equalsIgnoreCase(stringValue)) {
                    Constants.environment = Environment.SANDBOX;
                }
//                citrusClient.signOut(null);
                toggleEnvironments(preference, Constants.environment);
            } else if (preference instanceof EditTextPreference) {
                if ("signup_id".equals(preference.getKey())) {
                    Constants.SIGNUP_ID = stringValue;
                } else if ("signup_secret".equals(preference.getKey())) {
                    Constants.SIGNUP_SECRET = stringValue;
                } else if ("signin_id".equals(preference.getKey())) {
                    Constants.SIGNIN_ID = stringValue;
                } else if ("signin_secret".equals(preference.getKey())) {
                    Constants.SIGNIN_SECRET = stringValue;
                } else if ("vanity".equals(preference.getKey())) {
                    Constants.VANITY = stringValue;
                }

                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }

            switch (preference.getKey()) {
                case "use_default_signatures":
                    toggleSignatures(preference, !(boolean) value);
                    if ((boolean) value)
                        setDefaultMerchantValues(preference);
                    break;
                case "use_default_urls":
                    toggleUrls(preference, !(boolean) value);
                    if ((boolean) value)
                        setDefaultUrls(preference);
                    break;
            }

            return true;
        }
    };

    private static void toggleEnvironments(Preference preference, Environment environment) {
        if (environment == Environment.SANDBOX || environment == Environment.PRODUCTION) {
            toggleEnvUrls(preference, false);
        }
    }

    private static void toggleEnvUrls(Preference preference, boolean toggleValue) {
        preference.getPreferenceManager().findPreference("base_url").setEnabled(toggleValue);
        preference.getPreferenceManager().findPreference("citrus_base_url").setEnabled(toggleValue);
        preference.getPreferenceManager().findPreference("dynamic_pricing_url").setEnabled(toggleValue);
        preference.getPreferenceManager().findPreference("dynamic_pricing_path").setEnabled(toggleValue);
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Determines whether the simplified settings UI should be shown. This is
     * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
     * doesn't have newer APIs like {@link PreferenceFragment}, or the device
     * doesn't have an extra-large screen. In these cases, a single-pane
     * "simplified" settings UI should be shown.
     */
    private static boolean isSimplePreferences(Context context) {
        return ALWAYS_SIMPLE_PREFS
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
                || !isXLargeTablet(context);
    }

    private void toggleUrls(Preference preference, boolean toggleValue) {
        preference.getPreferenceManager().findPreference("bill_url").setEnabled(toggleValue);
    }

    private void toggleSignatures(Preference preference, boolean toggleValue) {
        preference.getPreferenceManager().findPreference("signup_id").setEnabled(toggleValue);
        preference.getPreferenceManager().findPreference("signup_secret").setEnabled(toggleValue);
        preference.getPreferenceManager().findPreference("signin_id").setEnabled(toggleValue);
        preference.getPreferenceManager().findPreference("signin_secret").setEnabled(toggleValue);
        preference.getPreferenceManager().findPreference("vanity").setEnabled(toggleValue);
    }

    private void setDefaultMerchantValues(Preference preference) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        //this returns whatever preference was set for myPrefKey in PreferencesPage or the default value if nothing was set
        preferences.getString("signup_id", getResources().getString(R.string.prefs_signup_id_default_value));
        preferences.getString("signup_secret", getResources().getString(R.string.prefs_signup_id_default_value));
        preferences.getString("signin_id", getResources().getString(R.string.prefs_signup_id_default_value));
        preferences.getString("signin_secret", getResources().getString(R.string.prefs_signup_id_default_value));
        preferences.getString("vanity", getResources().getString(R.string.prefs_signup_id_default_value));

        //this effectively resets the myPrefKey to its default value
        editor.remove("signup_id");
        editor.remove("signup_secret");
        editor.remove("signin_id");
        editor.remove("signin_secret");
        editor.remove("vanity");
        editor.apply();

        preference.getPreferenceManager().findPreference("signup_id").setSummary(R.string.prefs_signup_id_default_value);
        preference.getPreferenceManager().findPreference("signup_secret").setDefaultValue(R.string.prefs_signup_id_default_value);
        preference.getPreferenceManager().findPreference("signin_id").setDefaultValue(R.string.prefs_signup_id_default_value);
        preference.getPreferenceManager().findPreference("signin_secret").setDefaultValue(R.string.prefs_signup_id_default_value);
        preference.getPreferenceManager().findPreference("vanity").setDefaultValue(R.string.prefs_signup_id_default_value);
    }

    private void setDefaultUrls(Preference preference) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        //this returns whatever preference was set for myPrefKey in PreferencesPage or the default value if nothing was set
        preferences.getString("bill_url", getResources().getString(R.string.prefs_bill_url_default_value));

        //this effectively resets the myPrefKey to its default value
        editor.remove("bill_url");
        editor.remove("load_money_return_url");
        editor.apply();

        preference.getPreferenceManager().findPreference("bill_url").setSummary(R.string.prefs_bill_url_default_value);
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private void bindBooleanPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        citrusClient = CitrusClient.getInstance(context);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Set up the {@link ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            actionBar = getActionBar();

            if (actionBar != null) {
                // Show the Up button in the action bar.
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        setupSimplePreferencesScreen();
    }

    /**
     * Shows the simplified settings UI if the device configuration if the
     * device configuration dictates that a simplified, single-pane UI should be
     * shown.
     */
    private void setupSimplePreferencesScreen() {
        if (!isSimplePreferences(this)) {
            return;
        }

        // In the simplified UI, fragments are not used at all and we instead
        // use the older PreferenceActivity APIs.

        // Add 'general' preferences.
        addPreferencesFromResource(R.xml.dummy_prefs);

        // Add 'Login Credentials' preferences, and a corresponding header.
        PreferenceCategory fakeHeader0 = new PreferenceCategory(this);
        fakeHeader0.setTitle(R.string.login_credentials_text);
        getPreferenceScreen().addPreference(fakeHeader0);
        addPreferencesFromResource(R.xml.login_credentials);


        // Add 'SDK Environment' preferences, and a corresponding header.
        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle(R.string.sdk_environment_settings);
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.sdk_environment_settings);

        // Add 'Merchant Settings' preferences, and a corresponding header.
        PreferenceCategory fakeHeader1 = new PreferenceCategory(this);
        fakeHeader1.setTitle(R.string.merchant_settings);
        getPreferenceScreen().addPreference(fakeHeader1);
        addPreferencesFromResource(R.xml.merchant_settings);

        // Bind the summaries of EditText/List/Dialog/Ringtone preferences to
        // their values. When their values change, their summaries are updated
        // to reflect the new value, per the Android Design guidelines.
        bindPreferenceSummaryToValue(findPreference("pref_email_id_key"));
        bindPreferenceSummaryToValue(findPreference("pref_mobile_num_key"));
        bindBooleanPreferenceSummaryToValue(findPreference(getString(R.string.pref_show_citrus_login_screen)));

        bindPreferenceSummaryToValue(findPreference("pref_environment"));
        bindPreferenceSummaryToValue(findPreference("base_url"));
        bindPreferenceSummaryToValue(findPreference("citrus_base_url"));
        bindPreferenceSummaryToValue(findPreference("dynamic_pricing_url"));
        bindPreferenceSummaryToValue(findPreference("dynamic_pricing_path"));

        bindPreferenceSummaryToValue(findPreference("signup_id"));
        bindPreferenceSummaryToValue(findPreference("signup_secret"));
        bindPreferenceSummaryToValue(findPreference("signin_id"));
        bindPreferenceSummaryToValue(findPreference("signin_secret"));
        bindPreferenceSummaryToValue(findPreference("vanity"));
        bindPreferenceSummaryToValue(findPreference("bill_url"));
        bindBooleanPreferenceSummaryToValue(findPreference("use_default_signatures"));
        bindBooleanPreferenceSummaryToValue(findPreference("use_default_urls"));
        bindBooleanPreferenceSummaryToValue(findPreference("enable_logs"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this) && !isSimplePreferences(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        if (!isSimplePreferences(this)) {
            loadHeadersFromResource(R.xml.pref_headers, target);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (!key.equalsIgnoreCase(Utils.getResourceString(context, R.string.prefs_load_money_return_url_key))
                && !key.equalsIgnoreCase(Utils.getResourceString(context, R.string.pref_bill_url_key))) {
            settingsChanged = true;
            setResult(RESULT_OK, new Intent().putExtra(KEY_SETTINGS_CHANGED, true));
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        settingsChanged = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= 11) {
            UIActivity.activity.recreate();
        }
    }
}
