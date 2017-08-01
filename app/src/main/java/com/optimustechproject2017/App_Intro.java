package com.optimustechproject2017;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;


public class App_Intro extends AppIntro {

    int PLACE_PICKER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
setStatusBarTranslucent(true);









        addSlide(AppIntro2Fragment.newInstance("Welcome!" , "This is a demo 1", R.drawable.com_facebook_button_icon_blue, Color.TRANSPARENT));

        addSlide(AppIntro2Fragment.newInstance("2!" , "This is a demo 2", R.drawable.com_facebook_button_icon_white, Color.TRANSPARENT));
        addSlide(AppIntro2Fragment.newInstance("3!" , "This is a demo3", R.drawable.com_facebook_button_like_icon_selected, Color.TRANSPARENT));
        addSlide(AppIntro2Fragment.newInstance("4!" , "This is a demo 4", R.drawable.common_google_signin_btn_icon_light, Color.TRANSPARENT));

        setDepthAnimation();


    }
    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        setloc();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.

//        finish();
        setloc();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }



    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }






    public void setloc(){

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(App_Intro.this), PLACE_PICKER_REQUEST);


        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
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
                Log.d("tag",toastMsg+"   "+name+"    "+ address+ "    "+phone+"    "+placeId+ "    "+attribution);
//                addresslayout.getEditText().setText(address);
//                layoutContentcontinue.setVisibility(View.GONE);
//
//                layoutsetAdddress.setVisibility(View.VISIBLE);
                Double latitude = place.getLatLng().latitude;
                Double longitude = place.getLatLng().longitude;

                SettingsMy.setaddress(address.toString(), null, placeId, latitude.toString(), longitude.toString());

                startActivity(new Intent(App_Intro.this,MainActivity.class));
                finish();

            }
            else if (resultCode == PlacePicker.RESULT_ERROR) {
                Status status = PlacePicker.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {


            }

        }}




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransitionExit();
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransitionExit();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransitionEnter();
    }

    protected void overridePendingTransitionEnter() {
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    protected void overridePendingTransitionExit() {
    }




}

