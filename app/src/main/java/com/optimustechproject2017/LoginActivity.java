package com.optimustechproject2017;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;

import custom_font.MyTextView;

public class LoginActivity extends AppCompatActivity {

    TextView zoo;
    MyTextView create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setStatusBarTranslucent(true);

        Typeface custom_fonts = Typeface.createFromAsset(getAssets(), "Swistblnk Duwhoers Brush.ttf");
        zoo = (TextView)findViewById(R.id.Title);
        zoo.setTypeface(custom_fonts);

        create = (MyTextView)findViewById(R.id.create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this,SplashActivity.class);
                startActivity(it);

            }
        });
    }




    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
