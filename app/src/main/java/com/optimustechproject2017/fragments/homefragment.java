package com.optimustechproject2017.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.optimustechproject2017.MainActivity;
import com.optimustechproject2017.R;
import com.optimustechproject2017.SettingsMy;
import com.optimustechproject2017.SplashActivity;
import com.optimustechproject2017.adapters.CardViewAdapter;
import com.optimustechproject2017.adapters.FeedProperties;
import com.optimustechproject2017.adapters.NavigationBaseAdapter;
import com.optimustechproject2017.adapters.SliderLayout;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by HemanthKandula on 7/8/2017.
 */

public class homefragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{
    private static final int PLACE_PICKER_REQUEST = 1 ;
    private SliderLayout mDemoSlider;
     RecyclerView recyclerView;
    private CardView cardView;
    android.support.v7.widget.Toolbar toolbar;
    private ArrayList<FeedProperties> os_versions;
    private AutoCompleteTextView autoComplete;

    private CardViewAdapter mAdapter;
    // private RecyclerView.Adapter mAdapter;
    // private RecyclerView.LayoutManager mLayoutManager;

    NavigationBaseAdapter adapter;

    ArrayAdapter<String> stringAdapter;




    public static homefragment newInstance() {
        homefragment fragment = new homefragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View myFragmentView =  inflater.inflate(R.layout.fragment_home, container, false);

         toolbar = (android.support.v7.widget.Toolbar) myFragmentView.findViewById(R.id.toolbar);

        TextView tv = (TextView) toolbar.findViewById(R.id.addre);
        tv.setText(SettingsMy.getadd());
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);


                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initContrls( myFragmentView);

        String[] colors = getResources().getStringArray(R.array.colorList);

        stringAdapter = new ArrayAdapter<String>(getActivity(), R.layout.row, colors);






        autoComplete = (AutoCompleteTextView)  myFragmentView.findViewById(R.id.autoComplete);

        autoComplete.setAdapter(stringAdapter);

        autoComplete.setTextColor(Color.BLACK);



        mDemoSlider = (SliderLayout)myFragmentView.findViewById(R.id.slider);

        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Healthy Food",R.drawable.b1);
        file_maps.put("21 Tapas",R.drawable.b2);
        file_maps.put("The Pizza Restarent",R.drawable.b3);
        file_maps.put("In All Sizes", R.drawable.b4);


        for(String name : file_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(getActivity());
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);


            textSliderView.bundle(new Bundle());
            // textSliderView.getBundle().putString("extra",name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);








        return myFragmentView;



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {




        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity(),data);
                //Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                final CharSequence phone = place.getPhoneNumber();
                final String placeId = place.getId();
                String attribution = PlacePicker.getLatLngBounds(data).toString();
                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
                Log.d("TAG",toastMsg+"   "+name+"    "+ address+ "    "+phone+"    "+placeId+ "    "+attribution);

                SettingsMy.setaddress(address.toString(),"");
                TextView tv = (TextView) toolbar.findViewById(R.id.addre);
                tv.setText(SettingsMy.getadd());


            }
            else if (resultCode == PlacePicker.RESULT_ERROR) {
                Status status = PlacePicker.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {



            }

        }}


    private void initContrls(View myFragmentView) {

        // toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        //  cardView = (CardView) findViewById(R.id.cardList);
        recyclerView = (RecyclerView) myFragmentView.findViewById(R.id.my_recycler_view);

//        if (toolbar != null) {
//            setSupportActionBar(toolbar);
//            getSupportActionBar().setTitle("");
//
//        }

        final String[] versions = {"A1", "A2", "A3", "A4", "A5", "A6"};
        final int[] icons = {R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5, R.drawable.a6, R.drawable.a7, R.drawable.a2};


        os_versions = new ArrayList<FeedProperties>();

        for (int i = 0; i < versions.length; i++) {
            FeedProperties feed = new FeedProperties();

            feed.setTitle(versions[i]);
            feed.setThumbnail(icons[i]);
            os_versions.add(feed);
        }

        recyclerView.setHasFixedSize(true);

        // ListView
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Grid View
        // recyclerView.setLayoutManager(new GridLayoutManager(this,2,1,false));

        //StaggeredGridView
        // recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,1));

        // create an Object for Adapter
        mAdapter = new CardViewAdapter(os_versions);

        // set the adapter object to the Recyclerview
        recyclerView.setAdapter(mAdapter);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        }
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());




    }

    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }
    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
