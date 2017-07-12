package com.optimustechproject2017;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.optimustechproject2017.adapter.CardViewAdapter;
import com.optimustechproject2017.adapter.FeedProperties;
import com.optimustechproject2017.adapter.NavigationBaseAdapter;
import com.optimustechproject2017.adapter.SliderLayout;
import com.optimustechproject2017.fragments.homefragment;
import com.optimustechproject2017.fragments.ordersfragment;
import com.roughike.bottombar.OnTabReselectListener;

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




        com.roughike.bottombar.BottomBar bottomBar = (com.roughike.bottombar.BottomBar)
                findViewById(R.id.bottomBar);



        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {

                Fragment selectedFragment = null;




                if (tabId == R.id.tab_home) {
                    // The tab with id R.id.tab_favorites was reselected,
                    // change your content accordingly.
                    selectedFragment = homefragment.newInstance();

                }
                if (tabId == R.id.tab_search) {
                    // The tab with id R.id.tab_favorites was reselected,
                    // change your content accordingly.
                    selectedFragment = homefragment.newInstance();

                }   if (tabId == R.id.tab_orders) {
                    // The tab with id R.id.tab_favorites was reselected,
                    // change your content accordingly.
                    selectedFragment = ordersfragment.newInstance();

                }   if (tabId == R.id.tab_Account) {
                    // The tab with id R.id.tab_favorites was reselected,
                    // change your content accordingly.
                    selectedFragment = homefragment.newInstance();


                }

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();



            }
        });




        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, homefragment.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        //bottomNavigationView.getMenu().getItem(2).setChecked(true);











    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }







}

