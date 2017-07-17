package com.optimustechproject2017;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.optimustechproject2017.adapter.CardViewAdapter;
import com.optimustechproject2017.adapter.FeedProperties;
import com.optimustechproject2017.adapter.NavigationBaseAdapter;
import com.optimustechproject2017.adapter.SliderLayout;
import com.optimustechproject2017.fragments.Search_fragment;
import com.optimustechproject2017.fragments.homefragment;
import com.optimustechproject2017.fragments.ordersfragment;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    NavigationBaseAdapter adapter;
    ArrayAdapter<String> stringAdapter;
    private SliderLayout mDemoSlider;
    private RecyclerView recyclerView;
    private CardView cardView;
    private ArrayList<FeedProperties> os_versions;
   // private RecyclerView.Adapter mAdapter;
    // private RecyclerView.LayoutManager mLayoutManager;
    private AutoCompleteTextView autoComplete;
    private CardViewAdapter mAdapter;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();

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
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




//        com.roughike.bottombar.BottomBar bottomBar = (com.roughike.bottombar.BottomBar)
//                findViewById(R.id.bottomBar);

        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Home", R.drawable.home, R.color.black_transparent_70percent);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.search_menu_title, R.drawable.search, R.color.black_transparent_70percent);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Kart", R.drawable.cart, R.color.black_transparent_70percent);

        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Account", R.drawable.account, R.color.black_transparent_70percent);
        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item2);
        bottomNavigationItems.add(item3);
        bottomNavigationItems.add(item4);

        bottomNavigation.addItems(bottomNavigationItems);

        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));


        bottomNavigation.setForceTint(true);

        bottomNavigation.setTranslucentNavigationEnabled(true);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);


        bottomNavigation.setColored(true);

        bottomNavigation.setCurrentItem(1);









       //  Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                Fragment selectedFragment = null;

                // Do something cool here...
switch (position){

    case 0:          {           selectedFragment = homefragment.newInstance();break;}

    case 1:          {           selectedFragment = Search_fragment.newInstance();break;}

    case 2:          {           selectedFragment = ordersfragment.newInstance();break;}

    case 3:          {           selectedFragment = homefragment.newInstance();
        break;}





}




                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();



                return true;
            }



        });


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, homefragment.newInstance());
        transaction.commit();



        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override public void onPositionChange(int y) {
                // Manage the new y position
            }
        });





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }







}

