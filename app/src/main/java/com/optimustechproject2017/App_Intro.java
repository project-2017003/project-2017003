package com.optimustechproject2017;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2Fragment;


public class App_Intro extends AppIntro {

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

        startActivity(new Intent( App_Intro.this, com.optimustechproject2017.auth.LoginActivity.class ));
finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.

        startActivity(new Intent( App_Intro.this, com.optimustechproject2017.auth.LoginActivity.class ));
        finish();

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


}

