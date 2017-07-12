package com.optimustechproject2017.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optimustechproject2017.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HemanthKandula on 7/12/2017.
 */

public class ordersfragment extends Fragment {

    private TabLayout tabLayout;

    //This is our viewPager
    private ViewPager viewPager;


    private Toolbar toolbar;



    public static ordersfragment newInstance() {
        ordersfragment fragment = new ordersfragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View myFragmentView =  inflater.inflate(R.layout.fragment_orders, container, false);

        toolbar = (Toolbar) myFragmentView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) myFragmentView.findViewById(R.id.viewpager);

        setupViewPager(viewPager);

        tabLayout = (TabLayout) myFragmentView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        return myFragmentView;

    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new orders_tab1(), "UPCOMING");
        adapter.addFragment(new orders_tab2(), "COMPLETED");
        viewPager.setAdapter(adapter);
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




