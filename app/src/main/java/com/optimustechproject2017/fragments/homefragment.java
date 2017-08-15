package com.optimustechproject2017.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nshmura.snappysmoothscroller.LinearLayoutScrollVectorDetector;
import com.nshmura.snappysmoothscroller.SnapType;
import com.nshmura.snappysmoothscroller.SnappySmoothScroller;
import com.optimustechproject2017.Adapters.AdapterFavRest;
import com.optimustechproject2017.Adapters.Album;
import com.optimustechproject2017.Adapters.AlbumsAdapter;
import com.optimustechproject2017.Adapters.ClickListener;
import com.optimustechproject2017.Adapters.RecyclerTouchListener;
import com.optimustechproject2017.R;
import com.optimustechproject2017.RestarentActivity;
import com.optimustechproject2017.SettingsMy;
import com.optimustechproject2017.api.EndPoints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by HemanthKandula on 7/8/2017.
 */

public class homefragment extends Fragment {
    public static final int PLACE_PICKER_REQUEST = 1;
    static final boolean GRID_LAYOUT = false;
    private static final int ITEM_COUNT = 100;
     RecyclerView recyclerView;
    android.support.v7.widget.Toolbar toolbar;

    ArrayAdapter<String> stringAdapter;
    TextView yourfav, under_30, more_rest;
    ProgressDialog progressDialog;
    SwipeRefreshLayout mSwipeRefreshLayout;
    // private RecyclerView.Adapter mAdapter;
    // private RecyclerView.LayoutManager mLayoutManager;
    //private SliderLayout mDemoSlider;
    private CardView cardView;
    private AutoCompleteTextView autoComplete;
    // Content specific
    private TextView emptyContentView;
//    private ProductsRecyclerAdapter productsRecyclerAdapter;
//    private EndlessRecyclerScrollListener endlessRecyclerScrollListener;
    private RecyclerView productsRecycler;
    private GridLayoutManager productsRecyclerLayoutManager;
    private RecyclerView mRecyclerView,mRecyclerView_fav;
    private RecyclerView.Adapter mAdapter,mAdapter_fav;
    private List<Album> mContentItems = new ArrayList<>();
    private List<Album> mContentItems_fav = new ArrayList<>();
    private List<Album> mContentItems_under30 = new ArrayList<>();

    public static homefragment newInstance() {
        homefragment fragment = new homefragment();
        return fragment;
    }

    public static ProgressDialog generateProgressDialog(Context context, boolean cancelable) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.ProgressTheme);
        progressDialog.setMessage(context.getString(R.string.Loading));
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View myFragmentView =  inflater.inflate(R.layout.fragment_home, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) myFragmentView.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                mSwipeRefreshLayout.setRefreshing(true);
                getrests();
            }
        });


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
        progressDialog = generateProgressDialog(getActivity(), false);


        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        yourfav = (TextView) myFragmentView.findViewById(R.id.fav_rests_textview);

        under_30 = (TextView) myFragmentView.findViewById(R.id.under_30);
        more_rest = (TextView) myFragmentView.findViewById(R.id.more_rest);

        if (mContentItems.size() == 0 || mContentItems_fav.size() == 0 || mContentItems_under30.size() == 0) {
            progressDialog = new ProgressDialog(getActivity());
            yourfav.setVisibility(View.INVISIBLE);
            more_rest.setVisibility(View.INVISIBLE);
            under_30.setVisibility(View.INVISIBLE);
            progressDialog.setCancelable(false);
            getrests();

        } else {


            yourfav.setVisibility(View.VISIBLE);
            more_rest.setVisibility(View.VISIBLE);
            under_30.setVisibility(View.VISIBLE);


        }



        initContrls( myFragmentView);

        initrecycler(myFragmentView);


        initrecycler_fav(myFragmentView);


//        String[] colors = getResources().getStringArray(R.array.colorList);
//
//        stringAdapter = new ArrayAdapter<String>(getActivity(), R.layout.row, colors);
//
//
//
//

//
//        autoComplete = (AutoCompleteTextView)  myFragmentView.findViewById(R.id.autoComplete);
//
//        autoComplete.setAdapter(stringAdapter);
//
//        autoComplete.setTextColor(Color.BLACK);
//
//

//        mDemoSlider = (SliderLayout)myFragmentView.findViewById(R.id.slider);
//
//        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
//        file_maps.put("Healthy Food",R.drawable.b1);
//        file_maps.put("21 Tapas",R.drawable.b2);
//        file_maps.put("The Pizza Restarent",R.drawable.b3);
//        file_maps.put("In All Sizes", R.drawable.b4);
//
//
//        for(String name : file_maps.keySet()){
//            TextSliderView textSliderView = new TextSliderView(getActivity());
//            // initialize a SliderLayout
//            textSliderView
//                    .description(name)
//                    .image(file_maps.get(name))
//                    .setScaleType(BaseSliderView.ScaleType.Fit)
//                    .setOnSliderClickListener(this);
//
//
//            textSliderView.bundle(new Bundle());
//            // textSliderView.getBundle().putString("extra",name);
//
//            mDemoSlider.addSlider(textSliderView);
//        }
//        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
//        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
//        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
//        mDemoSlider.setDuration(4000);
//        mDemoSlider.addOnPageChangeListener(this);
//
//
//mDemoSlider.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        startActivity(new Intent(getActivity(), RestarentActivity.class));
//    }
//});





        return myFragmentView;



    }

    private void initrecycler(View myFragmentView) {

        mRecyclerView = (RecyclerView) myFragmentView.findViewById(R.id.category_rest_recycler);
//        RecyclerView.LayoutManager layoutManager;
//
//        if (GRID_LAYOUT) {
//            layoutManager = new GridLayoutManager(getActivity(), 2);
//        } else {
//            layoutManager = new LinearLayoutManager(getActivity());
//        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                SnappySmoothScroller scroller = new SnappySmoothScroller.Builder()
                        .setPosition(position)
                        .setSnapType(SnapType.CENTER)
                        .setScrollVectorDetector(new LinearLayoutScrollVectorDetector(this))
                        .build(recyclerView.getContext());

                startSmoothScroll(scroller);
            }
        };

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mAdapter = new AlbumsAdapter(getContext(), mContentItems);
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
                View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.fav_rest_card, null);


                Intent intent = new Intent(getActivity(), RestarentActivity.class);
// Pass data object in the bundle and populate details activity.
                Album a = mContentItems.get(position);

                ImageView image = (ImageView) inflatedView.findViewById(R.id.thumbnail);
                TextView name = (TextView) inflatedView.findViewById(R.id.title);
                intent.putExtra("URL", a.getURL());
                intent.putExtra("NAME", a.getName());

                Pair<View, String> p1 = Pair.create((View) image, "fragment_image_trans");
                Pair<View, String> p2 = Pair.create((View) name, "fragment_text_trans");

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1, p2);
                startActivity(intent, options.toBundle());


            }

            @Override
            public void onLongClick(View view, int position) {
                //Album album = albumList.get(position);

                // Toast.makeText(getApplicationContext(), album.getName() + " is selected!", Toast.LENGTH_SHORT).show();

            }
        }));

//        prepareAlbums();

    }

    private void initrecycler_fav(View myFragmentView) {

        mRecyclerView_fav = (RecyclerView) myFragmentView.findViewById(R.id.my_recycler_view_fav);
//        RecyclerView.LayoutManager layoutManager;
//
//        if (GRID_LAYOUT) {
//            layoutManager = new GridLayoutManager(getActivity(), 2);
//        } else {
//            layoutManager = new LinearLayoutManager(getActivity());
//        }
//



// Attach layout manager to the RecyclerView:
       // mRecyclerView_fav.setLayoutManager(layoutManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                SnappySmoothScroller scroller = new SnappySmoothScroller.Builder()
                        .setPosition(position)
                        .setSnapType(SnapType.CENTER)
                        .setScrollVectorDetector(new LinearLayoutScrollVectorDetector(this))
                        .build(recyclerView.getContext());

                startSmoothScroll(scroller);
            }
        };

        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);


        mRecyclerView_fav.setLayoutManager(linearLayoutManager);
        mRecyclerView_fav.setHasFixedSize(true);

        mAdapter_fav = new AdapterFavRest(getContext(), mContentItems_fav);
        //mAdapter = new TestRecyclerViewAdapter(mContentItems);
        mRecyclerView_fav.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));

        final AlphaInAnimationAdapter alphaAdapter_fav = new AlphaInAnimationAdapter(mAdapter_fav);

        alphaAdapter_fav.setFirstOnly(false);
        alphaAdapter_fav.setDuration(1000);
        alphaAdapter_fav.setInterpolator(new OvershootInterpolator(.5f));



        //mAdapter = new RecyclerViewMaterialAdapter();
        mRecyclerView_fav.setAdapter(new ScaleInAnimationAdapter(alphaAdapter_fav));


        mRecyclerView_fav.addOnItemTouchListener(new RecyclerTouchListener(getContext(), mRecyclerView_fav, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.fav_rest_card, null);


                Intent intent = new Intent(getActivity(), RestarentActivity.class);
                // Pass data object in the bundle and populate details activity.
                Album a = mContentItems_fav.get(position);

                ImageView image = (ImageView) inflatedView.findViewById(R.id.thumbnail);
                TextView name = (TextView) inflatedView.findViewById(R.id.title);
                intent.putExtra("URL", a.getURL());
                intent.putExtra("NAME", a.getName());

                Pair<View, String> p1 = Pair.create((View) image, "fragment_image_trans");
                Pair<View, String> p2 = Pair.create((View) name, "fragment_text_trans");

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1, p2);
                startActivity(intent, options.toBundle());

            }

            @Override
            public void onLongClick(View view, int position) {
                //Album album = albumList.get(position);

                // Toast.makeText(getApplicationContext(), album.getName() + " is selected!", Toast.LENGTH_SHORT).show();

            }
        }));

        //prepareAlbums_fav();

    }

//    private void prepareAlbums_fav() {
//        int[] covers = new int[]{
//                R.drawable.a1 ,
//                R.drawable.a2,
//                R.drawable.a3,
//                R.drawable.a4,
//                R.drawable.a5,
//                R.drawable.a6,
//                R.drawable.a7,
//                R.drawable.a8,
//                R.drawable.a9,
//                R.drawable.a10,
//                R.drawable.a11
//        };
//
////        for (int i = 0; i< mContentItems.size();i++) {
////            System.out.println(mContentItems);
////
////            Album k = new Album(i,getContext());
////            albumList.add(k);
////
////        }
//
//
//        Album a = new Album("Chicken", ">Item1 . Item 2 .itme 3  .Item4 ", covers[0],"25-30 MIN");
//        mContentItems_fav.add(a);
//
//        a = new Album("BarbequeNation",">Item1 . Item 2 .itme 3  .Item4 ", covers[1],"25-30 MIN");
//        mContentItems_fav.add(a);
//
//        a = new Album("BarbequeNation2",">Item1 . Item 2 .itme 3  .Item4 ", covers[2],"25-30 MIN");
//        mContentItems_fav.add(a);
//
//        a = new Album("BarbequeNation3",">Item1 . Item 2 .itme 3  .Item4 ", covers[3],"25-30 MIN");
//        mContentItems_fav.add(a);
//
//        a = new Album("BindaasRasoi", ">Item1 . Item 2 .itme 3  .Item4 ", covers[4],"25-30 MIN");
//        mContentItems_fav.add(a);
//
//        a = new Album("Chilis", ">Item1 . Item 2 .itme 3  .Item4 ", covers[5],"25-30 MIN");
//        mContentItems_fav.add(a);
//
//        a = new Album("KobeSizzlers",">Item1 . Item 2 .itme 3  .Item4 ", covers[6],"25-30 MIN");
//        mContentItems_fav.add(a);
//
//        a = new Album("LittleItaly",">Item1 . Item 2 .itme 3  .Item4 ", covers[7],"25-30 MIN");
//        mContentItems_fav.add(a);
//
//        a = new Album("Mamagoto",">Item1 . Item 2 .itme 3  .Item4 ", covers[8],"25-30 MIN");
//        mContentItems_fav.add(a);
//
//        a = new Album("PunjabGrill",">Item1 . Item 2 .itme 3  .Item4 ", covers[9],"25-30 MIN");
//        mContentItems_fav.add(a);
//        a = new Album("SigreeGlobalGrillTheSpringHotel",">Item1 . Item 2 .itme 3  .Item4 ", covers[10],"25-30 MIN");
//        mContentItems_fav.add(a);
//
//        mAdapter_fav.notifyDataSetChanged();
//    }
//
//
//    private void prepareAlbums() {
//        int[] covers = new int[]{
//                R.drawable.a1 ,
//                R.drawable.a2,
//                R.drawable.a3,
//                R.drawable.a4,
//                R.drawable.a5,
//                R.drawable.a6,
//                R.drawable.a7,
//                R.drawable.a8,
//                R.drawable.a9,
//                R.drawable.a10,
//                R.drawable.a11
//        };
//
////        for (int i = 0; i< mContentItems.size();i++) {
////            System.out.println(mContentItems);
////
////            Album k = new Album(i,getContext());
////            albumList.add(k);
////
////        }
//
//
//        Album a = new Album("Chicken", ">Item1 . Item 2 .itme 3  .Item4 ", covers[0],"25-30 MIN");
//        mContentItems.add(a);
//
//        a = new Album("BarbequeNation",">Item1 . Item 2 .itme 3  .Item4 ", covers[1],"25-30 MIN");
//        mContentItems.add(a);
//
//        a = new Album("BarbequeNation2",">Item1 . Item 2 .itme 3  .Item4 ", covers[2],"25-30 MIN");
//        mContentItems.add(a);
//
//        a = new Album("BarbequeNation3",">Item1 . Item 2 .itme 3  .Item4 ", covers[3],"25-30 MIN");
//        mContentItems.add(a);
//
//        a = new Album("BindaasRasoi", ">Item1 . Item 2 .itme 3  .Item4 ", covers[4],"25-30 MIN");
//        mContentItems.add(a);
//
//        a = new Album("Chilis", ">Item1 . Item 2 .itme 3  .Item4 ", covers[5],"25-30 MIN");
//        mContentItems.add(a);
//
//        a = new Album("KobeSizzlers",">Item1 . Item 2 .itme 3  .Item4 ", covers[6],"25-30 MIN");
//        mContentItems.add(a);
//
//        a = new Album("LittleItaly",">Item1 . Item 2 .itme 3  .Item4 ", covers[7],"25-30 MIN");
//        mContentItems.add(a);
//
//        a = new Album("Mamagoto",">Item1 . Item 2 .itme 3  .Item4 ", covers[8],"25-30 MIN");
//        mContentItems.add(a);
//
//        a = new Album("PunjabGrill",">Item1 . Item 2 .itme 3  .Item4 ", covers[9],"25-30 MIN");
//        mContentItems.add(a);
//        a = new Album("SigreeGlobalGrillTheSpringHotel",">Item1 . Item 2 .itme 3  .Item4 ", covers[10],"25-30 MIN");
//        mContentItems.add(a);
//
//        mAdapter.notifyDataSetChanged();
//    }



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
                Toast.makeText(getActivity(), place.getLatLng().toString(), Toast.LENGTH_LONG).show();
                Log.d("TAG", toastMsg + "   " + name + "    " + address + "    " + phone + "    " + placeId + "    " + place.getLatLng());
                TextView tv = (TextView) toolbar.findViewById(R.id.addre);
                tv.setText(SettingsMy.getadd());

                Double latitude = place.getLatLng().latitude;
                Double longitude = place.getLatLng().longitude;
                SettingsMy.setaddress(address.toString(), null, placeId, latitude.toString(), longitude.toString());


            }
            else if (resultCode == PlacePicker.RESULT_ERROR) {
                Status status = PlacePicker.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {



            }

        }}


    private void initContrls(final View myFragmentView) {


        recyclerView = (RecyclerView) myFragmentView.findViewById(R.id.my_recycler_view);



        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                SnappySmoothScroller scroller = new SnappySmoothScroller.Builder()
                        .setPosition(position)
                        .setSnapType(SnapType.CENTER)
                        .setScrollVectorDetector(new LinearLayoutScrollVectorDetector(this))
                        .build(recyclerView.getContext());

                startSmoothScroll(scroller);
            }
        };

        linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);


        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        mAdapter_fav = new AdapterFavRest(getContext(), mContentItems_fav);
        //mAdapter = new TestRecyclerViewAdapter(mContentItems);
        recyclerView.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));

        final AlphaInAnimationAdapter alphaAdapter_fav = new AlphaInAnimationAdapter(mAdapter_fav);

        alphaAdapter_fav.setFirstOnly(false);
        alphaAdapter_fav.setDuration(1000);
        alphaAdapter_fav.setInterpolator(new OvershootInterpolator(.5f));


        //mAdapter = new RecyclerViewMaterialAdapter();
        recyclerView.setAdapter(new ScaleInAnimationAdapter(alphaAdapter_fav));


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                View inflatedView = getActivity().getLayoutInflater().inflate(R.layout.fav_rest_card, null);


                Intent intent = new Intent(getActivity(), RestarentActivity.class);
// Pass data object in the bundle and populate details activity.
                Album a = mContentItems_fav.get(position);

                ImageView image = (ImageView) inflatedView.findViewById(R.id.thumbnail);
                TextView name = (TextView) inflatedView.findViewById(R.id.title);
                intent.putExtra("URL", a.getURL());
                intent.putExtra("NAME", a.getName());

                Pair<View, String> p1 = Pair.create((View) image, "fragment_image_trans");
                Pair<View, String> p2 = Pair.create((View) name, "fragment_text_trans");

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1, p2);
                startActivity(intent, options.toBundle());


            }

            @Override
            public void onLongClick(View view, int position) {
                //Album album = albumList.get(position);

                // Toast.makeText(getApplicationContext(), album.getName() + " is selected!", Toast.LENGTH_SHORT).show();

            }
        }));

        // prepareAlbums_fav();



    }

    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        //mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    public void getrests() {


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        //progressDialog.show();

        params.put("Longitude", SettingsMy.getLongitude());
        params.put("Latitude", SettingsMy.getLatitude());
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            params.put("Email", auth.getCurrentUser().getEmail());

            params.put("PhoneNo", auth.getCurrentUser().getPhoneNumber());
        }

        // Log.d("event",Clustername);
        client.post(EndPoints.GET_RESTAURANTS, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //progressDialog.hide();
                System.out.println(response);

                setrest(response);
                mSwipeRefreshLayout.setRefreshing(false);
                // System.out.println(response);

            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                mSwipeRefreshLayout.setRefreshing(false);

                // TODO Auto-generated method stub
                //progressDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getActivity(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getActivity(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), " Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void setrest(String response) {

        try {
            JSONArray arr = new JSONArray(response);
            System.out.println(arr.length());
            if (arr.length() != 0) {
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = (JSONObject) arr.get(i);


                    Album a = new Album(obj.get("Name").toString(),
                            ">Item1 . Item 2 .itme 3  .Item4 ", obj.get("URL").toString(), obj.get("Time").toString());
                    mContentItems_fav.add(a);
                    mContentItems.add(a);
                    mContentItems_under30.add(a);


                }
                titlevisible();
                mAdapter_fav.notifyDataSetChanged();

//                updateMySQLSyncSts(gson.toJson(Eventsynclist));

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    void titlevisible() {
        yourfav.setVisibility(View.VISIBLE);
        more_rest.setVisibility(View.VISIBLE);
        under_30.setVisibility(View.VISIBLE);
    }

}
