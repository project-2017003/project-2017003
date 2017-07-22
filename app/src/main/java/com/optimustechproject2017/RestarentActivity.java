package com.optimustechproject2017;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.optimustechproject2017.fragments.RestaurantTimeFragment;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class RestarentActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int statusBarColor;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
       // RestaurantTimeFragment.onBackPressed();

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
        setContentView(R.layout.activity_restarent);
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.restlay);
       CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
collapsingToolbar.setTitle("Restaurant Name");
       collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent)); // transperent color = #00000000
        collapsingToolbar.setCollapsedTitleTextColor(Color.rgb(0, 0, 0)); //Color of your title


        Toolbar toolbar = (Toolbar) findViewById(R.id.AppBar);
        setSupportActionBar(toolbar);




        //setupFab();

        if (getSupportActionBar() != null){


            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           // getSupportActionBar().setDisplayShowHomeEnabled(true);
           }




        viewPager = (ViewPager) findViewById(R.id.viewpager);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }


      private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RestaurantTimeFragment(), "Breakfast");
        adapter.addFragment(new RestaurantTimeFragment(), "Lunch");
          adapter.addFragment(new RestaurantTimeFragment(), "Dinner");


          viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here

//                fa
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }

//    private void setupFab() {
//
//        Fab fab = (Fab) findViewById(R.id.fab);
//        View sheetView = findViewById(R.id.fab_sheet);
//        View overlay = findViewById(R.id.overlay);
//        int sheetColor = getResources().getColor(R.color.white);
//        int fabColor = getResources().getColor(R.color.black_transparent_70percent);
//
//        // Create material sheet FAB
//        MaterialSheetFab<Fab> materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);
//
//        // Set material sheet event listener
//        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
//            @Override
//            public void onShowSheet() {
//                // Save current status bar color
//                statusBarColor = getStatusBarColor();
//                // Set darker status bar color to match the dim overlay
//                setStatusBarColor(getResources().getColor(R.color.cardview_dark_background));
//            }
//
//            @Override
//            public void onHideSheet() {
//                // Restore status bar color
//                setStatusBarColor(statusBarColor);
//            }
//        });
//
//
//        ListView lv = (ListView) findViewById(R.id.fablist);
//
//        // Set material sheet item click listeners
////        findViewById(R.id.fab_sheet_item_recording).setOnClickListener(this);
////        findViewById(R.id.fab_sheet_item_reminder).setOnClickListener(this);
////        findViewById(R.id.fab_sheet_item_photo).setOnClickListener(this);
////        findViewById(R.id.fab_sheet_item_note).setOnClickListener(this);
//    }

    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
