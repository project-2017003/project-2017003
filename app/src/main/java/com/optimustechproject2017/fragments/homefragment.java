package com.optimustechproject2017.fragments;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
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
import com.optimustechproject2017.Adapters.Album;
import com.optimustechproject2017.Adapters.AlbumsAdapter;
import com.optimustechproject2017.Adapters.ClickListener;
import com.optimustechproject2017.Adapters.RecyclerTouchListener;
import com.optimustechproject2017.R;
import com.optimustechproject2017.RestarentActivity;
import com.optimustechproject2017.SettingsMy;
import com.optimustechproject2017.adapter.CardViewAdapter;
import com.optimustechproject2017.adapter.FeedProperties;
import com.optimustechproject2017.adapter.NavigationBaseAdapter;
import com.optimustechproject2017.adapter.SliderLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by HemanthKandula on 7/8/2017.
 */

public class homefragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{
    static final boolean GRID_LAYOUT = false;
    private static final int PLACE_PICKER_REQUEST = 1 ;
    private static final int ITEM_COUNT = 100;
     RecyclerView recyclerView;
    android.support.v7.widget.Toolbar toolbar;
    NavigationBaseAdapter adapter;
    ArrayAdapter<String> stringAdapter;

    // private RecyclerView.Adapter mAdapter;
    // private RecyclerView.LayoutManager mLayoutManager;
    private SliderLayout mDemoSlider;
    private CardView cardView;
    private ArrayList<FeedProperties> os_versions;
    private AutoCompleteTextView autoComplete;
    // Content specific
    private TextView emptyContentView;
//    private ProductsRecyclerAdapter productsRecyclerAdapter;
//    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;
    private RecyclerView productsRecycler;
    private GridLayoutManager productsRecyclerLayoutManager;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private List<Album> mContentItems = new ArrayList<>();

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

        initrecycler(myFragmentView);

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


mDemoSlider.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(getActivity(), RestarentActivity.class));
    }
});





        return myFragmentView;



    }

    private void initrecycler(View myFragmentView) {

        mRecyclerView = (RecyclerView) myFragmentView.findViewById(R.id.category_rest_recycler);
        RecyclerView.LayoutManager layoutManager;

        if (GRID_LAYOUT) {
            layoutManager = new GridLayoutManager(getActivity(), 2);
        } else {
            layoutManager = new LinearLayoutManager(getActivity());
        }
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

mAdapter = new AlbumsAdapter(getContext(),mContentItems);
        //mAdapter = new TestRecyclerViewAdapter(mContentItems);
        mRecyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));

        final AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(mAdapter);

        alphaAdapter.setFirstOnly(false);
        alphaAdapter.setDuration(1000);
        alphaAdapter.setInterpolator(new OvershootInterpolator(.5f));



        //mAdapter = new RecyclerViewMaterialAdapter();
        mRecyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter));


        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                startActivity(new Intent(getActivity(), RestarentActivity.class));

                // TestRecyclerViewAdapter album = mContentItems.get(position);


//                Intent Cl = new Intent(getContext(), ProductDetail.class);
//                Cl.putExtra("name","name");
//                Cl.putExtra("no",position);
//
//                startActivity(Cl);



            }

            @Override
            public void onLongClick(View view, int position) {
                //Album album = albumList.get(position);

                // Toast.makeText(getApplicationContext(), album.getName() + " is selected!", Toast.LENGTH_SHORT).show();

            }
        }));

        prepareAlbums();

    }


    private void prepareAlbums() {
        int[] covers = new int[]{
                R.drawable.a1 ,
                R.drawable.a2,
                R.drawable.a3,
                R.drawable.a4,
                R.drawable.a5,
                R.drawable.a6,
                R.drawable.a7,
                R.drawable.a8,
                R.drawable.a9,
                R.drawable.a10,
                R.drawable.a11
        };

//        for (int i = 0; i< mContentItems.size();i++) {
//            System.out.println(mContentItems);
//
//            Album k = new Album(i,getContext());
//            albumList.add(k);
//
//        }


        Album a = new Album("Chicken", ">Item1 . Item 2 .itme 3  .Item4 ", covers[0],"25-30 MIN");
        mContentItems.add(a);

        a = new Album("BarbequeNation",">Item1 . Item 2 .itme 3  .Item4 ", covers[1],"25-30 MIN");
        mContentItems.add(a);

        a = new Album("BarbequeNation2",">Item1 . Item 2 .itme 3  .Item4 ", covers[2],"25-30 MIN");
        mContentItems.add(a);

        a = new Album("BarbequeNation3",">Item1 . Item 2 .itme 3  .Item4 ", covers[3],"25-30 MIN");
        mContentItems.add(a);

        a = new Album("BindaasRasoi", ">Item1 . Item 2 .itme 3  .Item4 ", covers[4],"25-30 MIN");
        mContentItems.add(a);

        a = new Album("Chilis", ">Item1 . Item 2 .itme 3  .Item4 ", covers[5],"25-30 MIN");
        mContentItems.add(a);

        a = new Album("KobeSizzlers",">Item1 . Item 2 .itme 3  .Item4 ", covers[6],"25-30 MIN");
        mContentItems.add(a);

        a = new Album("LittleItaly",">Item1 . Item 2 .itme 3  .Item4 ", covers[7],"25-30 MIN");
        mContentItems.add(a);

        a = new Album("Mamagoto",">Item1 . Item 2 .itme 3  .Item4 ", covers[8],"25-30 MIN");
        mContentItems.add(a);

        a = new Album("PunjabGrill",">Item1 . Item 2 .itme 3  .Item4 ", covers[9],"25-30 MIN");
        mContentItems.add(a);
        a = new Album("SigreeGlobalGrillTheSpringHotel",">Item1 . Item 2 .itme 3  .Item4 ", covers[10],"25-30 MIN");
        mContentItems.add(a);

        mAdapter.notifyDataSetChanged();
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

recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {


        startActivity(new Intent(getActivity(), RestarentActivity.class));

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
});


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
