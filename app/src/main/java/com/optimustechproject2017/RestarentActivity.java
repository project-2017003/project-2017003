package com.optimustechproject2017;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.optimustechproject2017.Databases.SqliteDB;
import com.optimustechproject2017.Dialogs.LoginDialogFragment;
import com.optimustechproject2017.Interfaces.LoginDialogInterface;
import com.optimustechproject2017.api.EndPoints;
import com.optimustechproject2017.fragments.RestaurantTimeFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * Created by HemanthKandula on 7/18/2017.
 */

public class RestarentActivity extends AppCompatActivity {

    public String Response;
    ProgressDialog prgDialog;
    SqliteDB sql;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int statusBarColor;
    private FirebaseAuth auth;

    // SwipeRefreshLayout mSwipeRefreshLayout;
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

//    @Override
//    public void startActivity(Intent intent) {
//        super.startActivity(intent);
//        overridePendingTransitionEnter();
//    }

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

        sql = new SqliteDB(this);

        auth = FirebaseAuth.getInstance();


        prgDialog = new ProgressDialog(this);
        prgDialog.setMessage(" Please wait...");
        prgDialog.setCancelable(false);



        Intent intent = getIntent();
        String URL = intent.getStringExtra("URL");
        String Name = intent.getStringExtra("NAME");

        final String RestID = intent.getStringExtra("RestID");
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.restlay);
       CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        collapsingToolbar.setTitle(Name);
       collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.transparent)); // transperent color = #00000000
        collapsingToolbar.setCollapsedTitleTextColor(Color.rgb(0, 0, 0)); //Color of your title


        Toolbar toolbar = (Toolbar) findViewById(R.id.AppBar);
        setSupportActionBar(toolbar);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.gotocart);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCartSelected();


                System.out.print("I came ");

            }
        });


        int TotalPrice = sql.totalprice();
        int quantcount = sql.quantcount();


        if (TotalPrice > 0 || quantcount > 0) {
            TextView total = (TextView) findViewById(R.id.total_price);

            Log.d("TotalPrice", TotalPrice + "");
            total.setText("₹" + TotalPrice + "");

            TextView count = (TextView) findViewById(R.id.cart_count);

            count.setText("" + quantcount + "");
        } else {
            relativeLayout.setVisibility(View.INVISIBLE);
        }


        //setupFab();
        getrestitems(RestID);

//        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // Refresh items
//                mSwipeRefreshLayout.setRefreshing(true);
//getrestitems(RestID);            }
//        });




        if (getSupportActionBar() != null){


            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           // getSupportActionBar().setDisplayShowHomeEnabled(true);
           }
        TextView Restname = (TextView) findViewById(R.id.tvVersionName);
        Restname.setText(Name);
        ImageView imageView = (ImageView) findViewById(R.id.thumbnail);


        Glide.with(getApplicationContext()).load(URL).into(imageView);


    }


    public void getrestitems(String RestID) {


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        //progressDialog.show();

        params.put("RestID", RestID);

        // Log.d("event",Clustername);
        client.post(EndPoints.GET_RESTAURANTITEMS, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                //progressDialog.hide();
                System.out.println(response);

                Response = response;
                // mSwipeRefreshLayout.setRefreshing(false);
                // System.out.println(response);
                viewPager = (ViewPager) findViewById(R.id.viewpager);

                setupViewPager(viewPager);

                tabLayout = (TabLayout) findViewById(R.id.tabs);
                tabLayout.setupWithViewPager(viewPager);


            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
//                mSwipeRefreshLayout.setRefreshing(false);

                // TODO Auto-generated method stub
                //progressDialog.hide();
                if (statusCode == 404) {
                    Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                } else if (statusCode == 500) {
                    Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), " Device might not be connected to Internet]",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        RestaurantTimeFragment r1 = new RestaurantTimeFragment();
        RestaurantTimeFragment r2 = new RestaurantTimeFragment();
        RestaurantTimeFragment r3 = new RestaurantTimeFragment();

        Bundle myBundle = new Bundle();
        myBundle.putString("response", Response);
        r1.setArguments(myBundle);
        r2.setArguments(myBundle);
        r3.setArguments(myBundle);

        adapter.addFragment(r1, "Breakfast");
        adapter.addFragment(r2, "Lunch");
        adapter.addFragment(r3, "Dinner");


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

    private void launchUserSpecificFragment(LoginDialogInterface loginListener) {
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {

            updatecart();



        } else {
            DialogFragment loginDialogFragment = LoginDialogFragment.newInstance(loginListener);
            loginDialogFragment.show(getSupportFragmentManager(), LoginDialogFragment.class.getSimpleName());
        }
    }

    private void updatecart() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        Gson gson = new GsonBuilder().create();


        ArrayList<HashMap<String, String>> userList = sql.getAllcartItems();
        if (userList.size() != 0) {
            prgDialog.show();
            params.put("Cart", gson.toJson(sql.getAllcartItems()));
            params.put("Email", auth.getCurrentUser().getEmail());
            System.out.println(gson.toJson(sql.getAllcartItems()));
            client.post(EndPoints.UpdateCart, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(String response) {
                    System.out.println(response);
                    prgDialog.hide();


                    Intent I = new Intent(RestarentActivity.this, CartActivity.class);

                    I.putExtra("response", response);
                    Log.d("cartresp", response);

                    startActivity(I);


                }

                @Override
                public void onFailure(int statusCode, Throwable error,
                                      String content) {
                    // TODO Auto-generated method stub
                    prgDialog.hide();
                    if (statusCode == 404) {
                        Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                    } else if (statusCode == 500) {
                        Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please connect  to Internet", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "No Food in cart ", Toast.LENGTH_LONG).show();
        }

    }


    public void onCartSelected() {
        launchUserSpecificFragment(new LoginDialogInterface() {
            @Override
            public void successfulLoginOrRegistration(User user) {
                // If signuppref was successful launch CartActivity.
                onCartSelected();
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

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
//        // Set material sheet item click listeners
//        findViewById(R.id.fab_sheet_item_recording).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RestaurantTimeFragment restaurantTimeFragment = new RestaurantTimeFragment();
//                ((RestaurantTimeFragment) restaurantTimeFragment).Scroll(0);
//
//            }
//        });
//        findViewById(R.id.fab_sheet_item_reminder).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                RestaurantTimeFragment restaurantTimeFragment = new RestaurantTimeFragment();
//                ((RestaurantTimeFragment) restaurantTimeFragment).Scroll(1);
//
//
//            }
//        });
//        findViewById(R.id.fab_sheet_item_photo).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                RestaurantTimeFragment restaurantTimeFragment = new RestaurantTimeFragment();
//                ((RestaurantTimeFragment) restaurantTimeFragment).Scroll(2);
//
//            }
//        });
//
//
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
