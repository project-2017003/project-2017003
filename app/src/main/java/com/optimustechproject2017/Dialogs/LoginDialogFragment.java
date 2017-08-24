package com.optimustechproject2017.Dialogs;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.BuildConfig;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.optimustechproject2017.Interfaces.LoginDialogInterface;
import com.optimustechproject2017.MainActivity;
import com.optimustechproject2017.R;
import com.optimustechproject2017.SettingsMy;
import com.optimustechproject2017.User;
import com.optimustechproject2017.listeners.OnSingleClickListener;
import com.optimustechproject2017.listeners.OnTouchPasswordListener;
import com.optimustechproject2017.utils.MsgUtils;
import com.optimustechproject2017.utils.Utils;

import java.util.Arrays;

import timber.log.Timber;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.facebook.login.widget.ProfilePictureView.TAG;


/**
 * Dialog handles user signuppref, registration and forgotten password function.
 */
public class LoginDialogFragment extends DialogFragment  {

    public static final String MSG_RESPONSE = "response: %s";
    public static final int PLACE_PICKER_REQUEST = 1;
    private static final int RC_SIGN_IN = 0;
    private static FirebaseAuth auth;
    public ImageView fb_login, googele_login, twittte_login;
    private CallbackManager callbackManager;
    private LoginDialogInterface loginDialogInterface;
    private ProgressDialog progressDialog;
    private FormState actualFormState = FormState.BASE;
    private LinearLayout loginBaseForm;
    private LinearLayout loginRegistrationForm;
    private LinearLayout loginEmailForm;
    private LinearLayout loginEmailForgottenForm;
    private TextInputLayout loginRegistrationEmailWrapper;
    private TextInputLayout loginRegistrationPasswordWrapper;
    private TextInputLayout loginEmailEmailWrapper;
    private TextInputLayout loginEmailPasswordWrapper;
    private TextInputLayout loginEmailForgottenEmailWrapper;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleApiClient mGoogleApiClient;



    //sign out method
    public static void signOut() {
        auth.signOut();
    }


    public static LoginDialogFragment newInstance(

            LoginDialogInterface loginDialogInterface) {
        LoginDialogFragment frag = new LoginDialogFragment();
        frag.loginDialogInterface = loginDialogInterface;
        return frag;
    }

    public static void logoutUser() {
        signOut();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogFullscreen);
        progressDialog = Utils.generateProgressDialog(getActivity(), false);


    }

    @Override
    public void onStart() {
        super.onStart();
        auth = FirebaseAuth.getInstance();

        auth.addAuthStateListener(mAuthListener);

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch signuppref activity

                } else {

                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());
                    if (user.getDisplayName() != null)
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    dismiss();

                }


            }
        };

        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Window window = d.getWindow();
            window.setLayout(width, height);
            window.setWindowAnimations(R.style.dialogFragmentAnimation);
            d.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (BuildConfig.DEBUG)
                        Timber.d("onKey: %d (Back=%d). Event:%d (Down:%d, Up:%d)", keyCode, KeyEvent.KEYCODE_BACK, event.getAction(),
                                KeyEvent.ACTION_DOWN, KeyEvent.ACTION_UP);
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                        switch (actualFormState) {
                            case REGISTRATION:
                                if (event.getAction() == KeyEvent.ACTION_UP) {
                                    setVisibilityOfRegistrationForm(false);
                                }
                                return true;
                            case FORGOTTEN_PASSWORD:
                                if (event.getAction() == KeyEvent.ACTION_UP) {
                                    setVisibilityOfEmailForgottenForm(false);
                                }
                                return true;
                            case EMAIL:
                                if (event.getAction() == KeyEvent.ACTION_UP) {
                                    setVisibilityOfEmailForm(false);
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                    return false;
                }
            });
        }



        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);

                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_LONG).show();

                // ...
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("%s - OnCreateView", this.getClass().getSimpleName());
        View view = inflater.inflate(R.layout.dialog_login, container, false);
        callbackManager = CallbackManager.Factory.create();

        loginBaseForm = (LinearLayout) view.findViewById(R.id.login_base_form);
        loginRegistrationForm = (LinearLayout) view.findViewById(R.id.login_registration_form);
        loginEmailForm = (LinearLayout) view.findViewById(R.id.login_email_form);
        loginEmailForgottenForm = (LinearLayout) view.findViewById(R.id.login_email_forgotten_form);


        prepareLoginFormNavigation(view);
        prepareInputBoxes(view);
        prepareActionButtons(view);
        return view;
    }

    private void prepareInputBoxes(View view) {
        // Registration form
        loginRegistrationEmailWrapper = (TextInputLayout) view.findViewById(R.id.login_registration_email_wrapper);
        loginRegistrationPasswordWrapper = (TextInputLayout) view.findViewById(R.id.login_registration_password_wrapper);
        EditText registrationPassword = loginRegistrationPasswordWrapper.getEditText();
        if (registrationPassword != null) {
            registrationPassword.setOnTouchListener(new OnTouchPasswordListener(registrationPassword));
        }


        // Login email form
        loginEmailEmailWrapper = (TextInputLayout) view.findViewById(R.id.login_email_email_wrapper);
        EditText loginEmail = loginEmailEmailWrapper.getEditText();
        if (loginEmail != null) loginEmail.setText(SettingsMy.getUserEmailHint());
        loginEmailPasswordWrapper = (TextInputLayout) view.findViewById(R.id.login_email_password_wrapper);
        EditText emailPassword = loginEmailPasswordWrapper.getEditText();
        if (emailPassword != null) {
            emailPassword.setOnTouchListener(new OnTouchPasswordListener(emailPassword));
            emailPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND || actionId == 124) {
                        invokeLoginWithEmail();
                        return true;
                    }
                    return false;
                }
            });
        }

        loginEmailForgottenEmailWrapper = (TextInputLayout) view.findViewById(R.id.login_email_forgotten_email_wrapper);
        EditText emailForgottenPassword = loginEmailForgottenEmailWrapper.getEditText();
        if (emailForgottenPassword != null)
            emailForgottenPassword.setText(SettingsMy.getUserEmailHint());

        // Simple accounts whisperer.
        Account[] accounts = AccountManager.get(getActivity()).getAccounts();
        String[] addresses = new String[accounts.length];
        for (int i = 0; i < accounts.length; i++) {
            addresses[i] = accounts[i].name;
            Timber.e("Sets autocompleteEmails: %s", accounts[i].name);
        }

        ArrayAdapter<String> emails = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, addresses);
        AutoCompleteTextView textView = (AutoCompleteTextView) view.findViewById(R.id.login_registration_email_text_auto);
        textView.setAdapter(emails);
    }

    private void prepareLoginFormNavigation(View view) {

        VideoView mVV = (VideoView) view.findViewById(R.id.videoView1);
        mVV.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });
        mVV.setOnPreparedListener(new MediaPlayer.OnPreparedListener(){

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mVV.setVideoURI(Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.splashvid));


        googele_login = (ImageView)view.findViewById(R.id.google_login);




        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        auth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //signInButton.setVisibility(View.GONE);
                //signOutButton.setVisibility(View.VISIBLE);
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());



//                    if(user.getDisplayName() != null)
//                        nameTextView.setText("HI " + user.getDisplayName().toString());
//                    emailTextView.setText(user.getEmail().toString());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        googele_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



//        signOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
//                        new ResultCallback<Status>() {
//                            @Override
//                            public void onResult(Status status) {
//                                signInButton.setVisibility(View.VISIBLE);
//                                signOutButton.setVisibility(View.GONE);
//                                emailTextView.setText(" ".toString());
//                                nameTextView.setText(" ".toString());
//                            }
//                        });
//            }
//            // ..
//        });



        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();







        fb_login = (ImageView)view.findViewById(R.id.fb_login);


        fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("email", "public_profile"));
            }
        });




        // Login email
        Button loginFormEmailButton = (Button) view.findViewById(R.id.login_form_email_btn);
        loginFormEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilityOfEmailForm(true);
            }
        });
        ImageButton closeEmailBtn = (ImageButton) view.findViewById(R.id.login_email_close_button);
        closeEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Slow to display ripple effect
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setVisibilityOfEmailForm(false);
                    }
                }, 200);
            }
        });

        // Registration
        TextView loginFormRegistrationButton = (TextView) view.findViewById(R.id.login_form_registration_btn);

        //if(!SettingsMy.hidesignup()){ loginFormEmailButton.setVisibility(View.INVISIBLE);}
        loginFormRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilityOfRegistrationForm(true);
            }
        });
        ImageButton closeRegistrationBtn = (ImageButton) view.findViewById(R.id.login_registration_close_button);
        closeRegistrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Slow to display ripple effect
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setVisibilityOfRegistrationForm(false);
                    }
                }, 200);
            }
        });

        // Email forgotten password
        TextView loginEmailFormForgottenButton = (TextView) view.findViewById(R.id.login_email_forgotten_password);
        loginEmailFormForgottenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisibilityOfEmailForgottenForm(true);
            }
        });
        ImageButton closeEmailForgottenFormBtn = (ImageButton) view.findViewById(R.id.login_email_forgotten_back_button);
        closeEmailForgottenFormBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Slow to display ripple effect
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setVisibilityOfEmailForgottenForm(false);
                    }
                }, 200);
            }
        });
    }

    private void prepareActionButtons(View view) {
        TextView loginBaseSkip = (TextView) view.findViewById(R.id.login_form_skip);
        loginBaseSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (loginDialogInterface != null) loginDialogInterface.skipLogin();
                dismiss();
            }
        });



        Button emailLogin = (Button) view.findViewById(R.id.login_email_confirm);
        emailLogin.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                invokeLoginWithEmail();
            }
        });

        Button registerBtn = (Button) view.findViewById(R.id.login_registration_confirm);
        registerBtn.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                invokeRegisterNewUser();
            }
        });

        Button resetPassword = (Button) view.findViewById(R.id.login_email_forgotten_confirm);
        resetPassword.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
               invokeResetPassword();
            }
        });
    }

    private void invokeFacebookLogin() {
        LoginManager.getInstance().registerCallback(callbackManager, (FacebookCallback<LoginResult>) this);
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    private void invokeRegisterNewUser() {
        hideSoftKeyboard();
        if (isRequiredFields(loginRegistrationEmailWrapper, loginRegistrationPasswordWrapper)) {
//            SettingsMy.setUserEmailHint(etRegistrationEmail.getText().toString());
            registerNewUser(loginRegistrationEmailWrapper.getEditText(), loginRegistrationPasswordWrapper.getEditText());
        }
    }

    private void registerNewUser(EditText inputEmail, EditText inputPassword) {


        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }

        //create user
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(getActivity(), "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressDialog.hide();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {

                        }
                    }
                });



    }

    private void invokeLoginWithEmail() {
        hideSoftKeyboard();
        if (isRequiredFields(loginEmailEmailWrapper, loginEmailPasswordWrapper)) {
            logInWithEmail(loginEmailEmailWrapper.getEditText(), loginEmailPasswordWrapper.getEditText());
        }
    }

    private void logInWithEmail(EditText inputEmail, final EditText inputPassword) {


        String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressDialog.hide();
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show();

                            }
                        } else {
                            dismiss();
                        }
                    }
                });



//        SettingsMy.setUserEmailHint(editTextEmail.getText().toString());
//        String url = String.format(EndPoints.USER_LOGIN_EMAIL, SettingsMy.getActualNonNullShop(getActivity()).getId());
//        progressDialog.show();
//
//        JSONObject jo;
//        try {
//            jo = JsonUtils.createUserAuthentication(editTextEmail.getText().toString().trim(), editTextPassword.getText().toString().trim());
//            editTextPassword.setText("");
//        } catch (JSONException e) {
//            Timber.e(e, "Parse logInWithEmail exception");
//            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
//            return;
//        }
//        if (BuildConfig.DEBUG) Timber.d("Login user: %s", jo.toString());
//
//        GsonRequest<User> userLoginEmailRequest = new GsonRequest<>(Request.Method.POST, url, jo.toString(), User.class,
//                new Response.Listener<User>() {
//                    @Override
//                    public void onResponse(@NonNull User response) {
//                        Timber.d(MSG_RESPONSE, response.toString());
//                        handleUserLogin(response);
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (progressDialog != null) progressDialog.cancel();
//                MsgUtils.logAndShowErrorMessage(getActivity(), error);
//            }
//        });
//        userLoginEmailRequest.setRetryPolicy(MyApplication.getDefaultRetryPolice());
//        userLoginEmailRequest.setShouldCache(false);
//        MyApplication.getInstance().addToRequestQueue(userLoginEmailRequest, CONST.LOGIN_DIALOG_REQUESTS_TAG);
    }

    private void handleUserLogin(User user) {
        if (progressDialog != null) progressDialog.cancel();
      //  SettingsMy.setActiveUser(user);

        // Invalidate GCM token for new registration with authorized user.
        SettingsMy.setTokenSentToServer(false);
        if (getActivity() instanceof MainActivity)
            ///((MainActivity) getActivity()).registerGcmOnServer();

       // MainActivity.invalidateDrawerMenuHeader();

        if (loginDialogInterface != null) {
            loginDialogInterface.successfulLoginOrRegistration(user);
        } else {
            Timber.e("Interface is null");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.SHORT);
        }
        dismiss();
    }

    private void invokeResetPassword() {
        EditText emailForgottenPasswordEmail = loginEmailForgottenEmailWrapper.getEditText();
        if (emailForgottenPasswordEmail == null || emailForgottenPasswordEmail.getText().toString().equalsIgnoreCase("")) {
            loginEmailForgottenEmailWrapper.setErrorEnabled(true);
            loginEmailForgottenEmailWrapper.setError(getString(R.string.Required_field));
        } else {
            loginEmailForgottenEmailWrapper.setErrorEnabled(false);
            resetPassword(emailForgottenPasswordEmail);
        }
    }

    private void resetPassword(EditText oldEmail) {
//        String url = String.format(EndPoints.USER_RESET_PASSWORD, SettingsMy.getActualNonNullShop(getActivity()).getId());
        progressDialog.show();
        if (!oldEmail.getText().toString().trim().equals("")) {
            auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                progressDialog.hide();
                            } else {
                                Toast.makeText(getActivity(), "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                progressDialog.hide();
                            }
                        }
                    });
        } else {
            oldEmail.setError("Enter email");
            progressDialog.hide();
        }
//
    }

    private void hideSoftKeyboard() {
        if (getActivity() != null && getView() != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    private void setVisibilityOfRegistrationForm(boolean setVisible) {
        if (setVisible) {
            actualFormState = FormState.REGISTRATION;
            loginBaseForm.setVisibility(View.INVISIBLE);
            loginRegistrationForm.setVisibility(View.VISIBLE);
        } else {
            actualFormState = FormState.BASE;
            loginBaseForm.setVisibility(View.VISIBLE);
            loginRegistrationForm.setVisibility(View.INVISIBLE);
            hideSoftKeyboard();
        }
    }

    private void setVisibilityOfEmailForm(boolean setVisible) {
        if (setVisible) {
            actualFormState = FormState.EMAIL;
            loginBaseForm.setVisibility(View.INVISIBLE);
            loginEmailForm.setVisibility(View.VISIBLE);
        } else {
            actualFormState = FormState.BASE;
            loginBaseForm.setVisibility(View.VISIBLE);
            loginEmailForm.setVisibility(View.INVISIBLE);
            hideSoftKeyboard();
        }
    }

    private void setVisibilityOfEmailForgottenForm(boolean setVisible) {
        if (setVisible) {
            actualFormState = FormState.FORGOTTEN_PASSWORD;
            loginEmailForm.setVisibility(View.INVISIBLE);
            loginEmailForgottenForm.setVisibility(View.VISIBLE);
        } else {
            actualFormState = FormState.EMAIL;
            loginEmailForm.setVisibility(View.VISIBLE);
            loginEmailForgottenForm.setVisibility(View.INVISIBLE);
        }
        hideSoftKeyboard();
    }

    /**
     * Check if editTexts are valid view and if user set all required fields.
     *
     * @return true if ok.
     */
    private boolean isRequiredFields(TextInputLayout emailWrapper, TextInputLayout passwordWrapper) {
        if (emailWrapper == null || passwordWrapper == null) {
            Timber.e(new RuntimeException(), "Called isRequiredFields with null parameters.");
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.LONG);
            return false;
        } else {
            EditText email = emailWrapper.getEditText();
            EditText password = passwordWrapper.getEditText();
            if (email == null || password == null) {
                Timber.e(new RuntimeException(), "Called isRequiredFields with null editTexts in wrappers.");
                MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_INTERNAL_ERROR, null, MsgUtils.ToastLength.LONG);
                return false;
            } else {
                boolean isEmail = false;
                boolean isPassword = false;

                if (email.getText().toString().equalsIgnoreCase("")) {
                    emailWrapper.setErrorEnabled(true);
                    emailWrapper.setError(getString(R.string.Required_field));
                } else {
                    emailWrapper.setErrorEnabled(false);
                    isEmail = true;
                }

                if (password.getText().toString().equalsIgnoreCase("")) {
                    passwordWrapper.setErrorEnabled(true);
                    passwordWrapper.setError(getString(R.string.Required_field));
                } else {
                    passwordWrapper.setErrorEnabled(false);
                    isPassword = true;
                }

                if (isEmail && isPassword) {
                    return true;
                } else {
                    Timber.e("Some fields are required.");
                    return false;
                }
            }
        }
    }



    @Override
    public void onDetach() {
        loginDialogInterface = null;
        super.onDetach();
    }


    /**
     * Handle errors, when user have identity at least.
     * Show error message to user.
     */
    private void handleNonFatalError(String message, boolean logoutFromFb) {
        if (logoutFromFb) {
            LoginDialogFragment.logoutUser();
        }
        if (getActivity() != null)
            MsgUtils.showToast(getActivity(), MsgUtils.TOAST_TYPE_MESSAGE, message, MsgUtils.ToastLength.LONG);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener( getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();

                            if (user != null) {
                                dismiss();

                            }

                            //gotomainact();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            dismiss();
                        }

                        // ...
                    }
                });
    }

    public void signIn( ) {


        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("requestCode1", String.valueOf(requestCode));
        Log.d("resultCode1", String.valueOf(resultCode));
        Log.d("data1", String.valueOf(data));

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d("resultgoogle", result.isSuccess() + "");

            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                // ...


            }





        }
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener( getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            dismiss();
//                            Toast.makeText(Ta, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        } else {

                            Toast.makeText(getActivity(), "Retry login ", Toast.LENGTH_LONG).show();
                            dismiss();


                        }


                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }


//    public void getplaceid(String Email) {
//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//progressDialog.show();
//        params.put("Email",Email);
//        // Log.d("event",Clustername);
//        client.post(getplaceid , params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(String response) {
//                progressDialog.hide();
//                System.out.println(response);
//if(response!=null)
//{                final String placeId = response;
//
//
//        Places.GeoDataApi.getPlaceById(mGoogleApiClient1, placeId)
//                        .setResultCallback(new ResultCallback<PlaceBuffer>() {
//                            @Override
//                            public void onResult(PlaceBuffer places) {
//                                if (places.getStatus().isSuccess() && places.getCount() > 0) {
//                                    final Place myPlace = places.get(0);
//
//                                    SettingsMy.setaddress(myPlace.getAddress().toString(),null,placeId);
//                                    SettingsMy.sethidesignup(true);
//                                    progressDialog.hide();
//dismiss();
//
//
//                                    Log.i(TAG, "Place found: " + myPlace.getName() +"  "+ myPlace.getAddress());
//                                } else {
//                                    sedelivery();
//                                    Log.e(TAG, "Place not found");
//                                }
//                                places.release();
//                            }
//                        });
//
//
//
//                System.out.println(response);
//               }
//            }
//            @Override
//            public void onFailure(int statusCode, Throwable error, String content) {
//
//
//                // TODO Auto-generated method stub
//                progressDialog.hide();
//                if (statusCode == 404) {
//                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
//                } else if (statusCode == 500) {
//                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getActivity(), "Device might not be connected to Internet]",
//                            Toast.LENGTH_LONG).show();
//                }
//
//
//                sedelivery();
//            }
//        });
//    }

//    private void sedelivery() {
//
//
//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//        try {
//            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
//
//
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }
//
//    }


    private enum FormState {
        BASE, REGISTRATION, EMAIL, FORGOTTEN_PASSWORD
    }

}
