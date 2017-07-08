package com.optimustechproject2017;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.optimustechproject2017.adapters.CardViewAdapter;
import com.optimustechproject2017.adapters.FeedProperties;
import com.optimustechproject2017.adapters.NavigationBaseAdapter;
import com.optimustechproject2017.adapters.SliderLayout;
import com.optimustechproject2017.fragments.homefragment;
import com.roughike.bottombar.OnTabReselectListener;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private SliderLayout mDemoSlider;
    private RecyclerView recyclerView;
    private CardView cardView;
    private ArrayList<FeedProperties> os_versions;
    private AutoCompleteTextView autoComplete;

    private CardViewAdapter mAdapter;
   // private RecyclerView.Adapter mAdapter;
    // private RecyclerView.LayoutManager mLayoutManager;

    NavigationBaseAdapter adapter;

    ArrayAdapter<String> stringAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       //android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);

        com.roughike.bottombar.BottomBar bottomBar = (com.roughike.bottombar.BottomBar)
                findViewById(R.id.bottomBar);

//      TextView tv = (TextView) toolbar.findViewById(R.id.addre);
//        tv.setText(SettingsMy.getadd());
//        toolbar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//            }
//        });


        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {

                Fragment selectedFragment = null;




                if (tabId == R.id.tab_home) {
                    // The tab with id R.id.tab_favorites was reselected,
                    // change your content accordingly.
                    selectedFragment = homefragment.newInstance();

                }
                if (tabId == R.id.tab_home) {
                    // The tab with id R.id.tab_favorites was reselected,
                    // change your content accordingly.
                    selectedFragment = homefragment.newInstance();

                }   if (tabId == R.id.tab_home) {
                    // The tab with id R.id.tab_favorites was reselected,
                    // change your content accordingly.
                    selectedFragment = homefragment.newInstance();

                }   if (tabId == R.id.tab_home) {
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

