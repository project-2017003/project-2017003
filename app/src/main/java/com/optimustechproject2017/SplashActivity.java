/*******************************************************************************
 * Copyright (C) 2016 Business Factory, s.r.o.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.optimustechproject2017;


import android.animation.Animator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;


import mehdi.sakout.fancybuttons.FancyButton;
import timber.log.Timber;

public class SplashActivity extends AppCompatActivity   {
    public static final String REFERRER = "referrer";
    private static final String TAG = SplashActivity.class.getSimpleName();
    private VideoView mVV;
    private FirebaseAuth auth;

    int PLACE_PICKER_REQUEST = 1;

    private Activity activity;
    private ProgressDialog progressDialog;
   /**
     * Indicates if layout has been already created.
     */
    private boolean layoutCreated = false;

private boolean appintro = true;
    /**
     * Button allowing selection of shop during fresh start.
     */
    private FancyButton continuebutton;

    /**
     * Indicates that window has been already detached.
     */
    private boolean windowDetached = false;

    // Possible layouts
    private View layoutIntroScreen;
    private View layoutContent;
    private View layoutContentNoConnection;
    private View layoutContentcontinue;
    private View layoutsetAdddress;
    TextInputLayout addresslayout,houselayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG);
        activity = this;
        setStatusBarTranslucent(true);

        // init loading dialog
        progressDialog = generateProgressDialog(this, false);
        init();
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }




    /**
     * Generate top layer progress indicator.
     *
     * @param context    activity context
     * @param cancelable can be progress layer canceled
     * @return dialog
     */
    public static ProgressDialog generateProgressDialog(Context context, boolean cancelable) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.ProgressTheme);
        progressDialog.setMessage(context.getString(R.string.Loading));
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }

    /**
     * Prepares activity view and handles incoming intent(Notification, utm data).
     */




    private void init() {
        // Check if data connected.
        if (!MyApplication.getInstance().isDataConnected()) {
            progressDialog.hide();
            Timber.d("No network connection.");

            initSplashLayout();

            // Skip intro screen.
            layoutContent.setVisibility(View.VISIBLE);
            layoutIntroScreen.setVisibility(View.GONE);

            // Show retry button.
            layoutContentNoConnection.setVisibility(View.VISIBLE);
            layoutContentcontinue.setVisibility(View.GONE);
            layoutsetAdddress.setVisibility(View.GONE);
        } else {
            progressDialog.hide();

startMainActivity(null);
        }
    }



    /**
     * SetContentView to activity and prepare layout views.
     */
    private void initSplashLayout() {
        if (!layoutCreated) {
            setContentView(R.layout.activity_splash);


            layoutContent = findViewById(R.id.splash_content);
            layoutIntroScreen = findViewById(R.id.splash_intro_screen);
            layoutContentNoConnection = findViewById(R.id.splash_content_no_connection);
            layoutContentcontinue= findViewById(R.id.splash_content_continue);
            layoutsetAdddress =findViewById(R.id.splash_set_address);

            final Button Save_proceed =(Button)findViewById(R.id.save_and_proceed) ;

             addresslayout = (TextInputLayout)findViewById(R.id.got_address) ;
             houselayout = (TextInputLayout)findViewById(R.id.house_flat) ;

            if(houselayout.getEditText() !=null)
                houselayout.getEditText().addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.toString().equals("")) {
                            Save_proceed.setEnabled(false);
                        } else {
                            Save_proceed.setEnabled(true);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });




            ImageButton imageButton = (ImageButton)findViewById(R.id.close_button);


            Save_proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String addres,hou;
                    if(addresslayout.getEditText()!=null && houselayout.getEditText()!=null){

                    addres= addresslayout.getEditText().getText().toString();
                    hou = houselayout.getEditText().getText().toString();
SettingsMy.setaddress(addres,hou);
                        startActivity(new Intent(SplashActivity.this,MainActivity.class));
                       finish();


                    }

                }
            });





            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Slow to display ripple effect
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
layoutsetAdddress.setVisibility(View.GONE);
                            layoutContentcontinue.setVisibility(View.VISIBLE);
                        }
                    }, 200);
                }
            });






            continuebutton = (FancyButton) findViewById(R.id.splash_continue);


            continuebutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if (!SettingsMy.appintro) {
//                        startActivity(new Intent(SplashActivity.this,App_Intro.class));
//
//                    }else  startActivity(new Intent(SplashActivity.this,LoginActivity.class));


     boolean  firstTime=SettingsMy.getappintro();

                    if(firstTime) {
                        SettingsMy.setAppintro(false);

                        //For commit the changes, Use either editor.commit(); or  editor.apply();.
                        Intent intent = new Intent(SplashActivity.this, App_Intro.class);

                        startActivity(intent);
                        finish();

                    }else {
//                        Intent intent = new Intent(SplashActivity.this, com.optimustechproject2017.location.address.class);
//                        startActivity(intent);
//                        finish();




                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                        try {
                            startActivityForResult(builder.build(SplashActivity.this), PLACE_PICKER_REQUEST);


                        } catch (GooglePlayServicesRepairableException e) {
                            e.printStackTrace();
                        } catch (GooglePlayServicesNotAvailableException e) {
                            e.printStackTrace();
                        }
                    }










                }
            });
            mVV = (VideoView)findViewById(R.id.videoView1);
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

            mVV.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splashvid));







            Button reRunButton = (Button) findViewById(R.id.splash_re_run_btn);
            if (reRunButton != null) {
                reRunButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                init();
                            }
                        }, 600);
                    }
                });
            } else {
                Timber.e(new RuntimeException(), "ReRunButton didn't found");
            }
            layoutCreated = true;
        } else {
            Timber.d("%s screen is already created.", this.getClass().getSimpleName());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {




        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this,data);
                //Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                final CharSequence phone = place.getPhoneNumber();
                final String placeId = place.getId();
                String attribution = PlacePicker.getLatLngBounds(data).toString();
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                Log.d(TAG,toastMsg+"   "+name+"    "+ address+ "    "+phone+"    "+placeId+ "    "+attribution);
addresslayout.getEditText().setText(address);
                layoutContentcontinue.setVisibility(View.GONE);

                layoutsetAdddress.setVisibility(View.VISIBLE);



            }
            else if (resultCode == PlacePicker.RESULT_ERROR) {
                Status status = PlacePicker.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                layoutContentNoConnection.setVisibility(View.VISIBLE);
                layoutContentcontinue.setVisibility(View.GONE);
                layoutsetAdddress.setVisibility(View.GONE);


            }

    }}

    private void startMainActivity(Bundle bundle) {
        if (appintro) {

            initSplashLayout();
            layoutContentNoConnection.setVisibility(View.GONE);
            layoutContentcontinue.setVisibility(View.VISIBLE);


            animateContentVisible();



        } else {


            Intent mainIntent = new Intent(SplashActivity.this, com.optimustechproject2017.auth.LoginActivity.class);
            if (bundle != null) {
                Timber.d("Pass bundle to main activity");
                mainIntent.putExtras(bundle);
            }
            startActivity(mainIntent);
            finish();
        }
    }


    public void splashScreen (final int x)
    {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(x);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).run();
    }



    /**
     * Hide intro screen and display content layout with animation.
     */
    private void animateContentVisible() {
        if (layoutIntroScreen != null && layoutContent != null && layoutIntroScreen.getVisibility() == View.VISIBLE) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (windowDetached) {
                                if (layoutContent != null) layoutContent.setVisibility(View.VISIBLE);
                            } else {
//                            // If lollipop use reveal animation. On older phones use fade animation.
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                                    Timber.d("Circular animation.");
                                    // get the center for the animation circle
                                    final int cx = (layoutContent.getLeft() + layoutContent.getRight()) / 2;
                                    final int cy = (layoutContent.getTop() + layoutContent.getBottom()) / 2;

                                    // get the final radius for the animation circle
                                    int dx = Math.max(cx, layoutContent.getWidth() - cx);
                                    int dy = Math.max(cy, layoutContent.getHeight() - cy);
                                    float finalRadius = (float) Math.hypot(dx, dy);

                                    Animator animator = ViewAnimationUtils.createCircularReveal(layoutContent, cx, cy, 0, finalRadius);
                                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                                    animator.setDuration(1250);
                                    layoutContent.setVisibility(View.VISIBLE);
                                    animator.start();
                                } else {
                                    Timber.d("Alpha animation.");
                                    layoutContent.setAlpha(0f);
                                    layoutContent.setVisibility(View.VISIBLE);
                                    layoutContent.animate()
                                            .alpha(1f)
                                            .setDuration(1000)
                                            .setListener(null);
                                }
                            }
                        }
                    }, 330);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    public void onAttachedToWindow() {
        windowDetached = false;
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        windowDetached = true;
        super.onDetachedFromWindow();
    }


}
