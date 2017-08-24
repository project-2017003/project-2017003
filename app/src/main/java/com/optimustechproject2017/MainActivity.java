package com.optimustechproject2017;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.firebase.auth.FirebaseAuth;
import com.optimustechproject2017.Dialogs.LoginDialogFragment;
import com.optimustechproject2017.Interfaces.LoginDialogInterface;
import com.optimustechproject2017.fragments.AccountsFragment;
import com.optimustechproject2017.fragments.Search_fragment;
import com.optimustechproject2017.fragments.homefragment;
import com.optimustechproject2017.fragments.ordersfragment;

import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    protected OnBackPressedListener onBackPressedListener;
    ArrayAdapter<String> stringAdapter;
    private RecyclerView recyclerView;
    private CardView cardView;
   // private RecyclerView.Adapter mAdapter;
    // private RecyclerView.LayoutManager mLayoutManager;
    private AutoCompleteTextView autoComplete;
    private FirebaseAuth auth;

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {


        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
        else {
        super.onBackPressed();
            overridePendingTransitionExit();
        }
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
        auth = FirebaseAuth.getInstance();


        setupbottom();

//
////        com.roughike.bottombar.BottomBar bottomBar = (com.roughike.bottombar.BottomBar)
////                findViewById(R.id.bottomBar);
//
//        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
//
//        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Home", R.drawable.home, R.color.black_transparent_70percent);
//        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.search_menu_title, R.drawable.search, R.color.black_transparent_70percent);
//        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Kart", R.drawable.cart, R.color.black_transparent_70percent);
//
//        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Account", R.drawable.account, R.color.black_transparent_70percent);
//        bottomNavigationItems.add(item1);
//        bottomNavigationItems.add(item2);
//        bottomNavigationItems.add(item3);
//        bottomNavigationItems.add(item4);
//
//        bottomNavigation.addItems(bottomNavigationItems);
//
//        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
//
//        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
//        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
//
//
//        bottomNavigation.setForceTint(true);
//
//        bottomNavigation.setTranslucentNavigationEnabled(true);
//        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
//
//
//        bottomNavigation.setColored(true);
//
//        bottomNavigation.setCurrentItem(1);
//
//
//
//
//
//
//
//
//
//       //  Set listeners
//        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
//            @Override
//            public boolean onTabSelected(int position, boolean wasSelected) {
//
//                Fragment selectedFragment = null;
//
//                // Do something cool here...
//switch (position){
//
//    case 0:          {           selectedFragment = homefragment.newInstance();break;}
//
//    case 1:          {           selectedFragment = Search_fragment.newInstance();break;}
//
//    case 2:          {           selectedFragment = ordersfragment.newInstance();break;}
//
//    case 3:          {           selectedFragment = homefragment.newInstance();
//        break;}
//
//
//
//
//
//}
//
//
//
//
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.frame_layout, selectedFragment);
//                transaction.commit();
//
//
//
//                return true;
//            }
//
//
//
//        });
//
//
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame_layout, homefragment.newInstance());
//        transaction.commit();
//
//
//
//        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
//            @Override public void onPositionChange(int y) {
//                // Manage the new y position
//            }
//        });


        getrestdetails();


    }

    private void getrestdetails() {


    }

    private void setupbottom() {


        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);


        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment current = null;
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.tab_home:
                                selectedFragment = homefragment.newInstance();
                                break;
                            case R.id.tab_search:
                                selectedFragment = Search_fragment.newInstance();
                                break;
                            case R.id.tab_orders:
                                selectedFragment = ordersfragment.newInstance();
                                break;
                            case R.id.tab_Account:
                                selectedFragment = AccountsFragment.newInstance();
                                break;
                        }
                        if (current == selectedFragment)
                            return true;

                        replaceFragment(selectedFragment);
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, homefragment.newInstance());
        transaction.commit();

    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getSupportFragmentManager();


        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.frame_layout, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    /**
     * Check if user is logged in. If so then start defined fragment, otherwise show signuppref dialog.
     *
     * @param fragment       fragment to launch.
     * @param transactionTag text identifying fragment transaction.
     * @param loginListener  listener on successful signuppref.
     */
    private void launchUserSpecificFragment(Fragment fragment, String transactionTag, LoginDialogInterface loginListener) {
        if (auth.getCurrentUser()!= null) {
            replaceFragment(fragment, transactionTag);
        } else {
            DialogFragment loginDialogFragment = LoginDialogFragment.newInstance(loginListener);
            loginDialogFragment.show(getSupportFragmentManager(), LoginDialogFragment.class.getSimpleName());
        }
    }

    /**
     * Method creates fragment transaction and replace current fragment with new one.
     *
     * @param newFragment    new fragment used for replacement.
     * @param transactionTag text identifying fragment transaction.
     */
    private void replaceFragment(Fragment newFragment, String transactionTag) {
        if (newFragment != null) {
            FragmentManager frgManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = frgManager.beginTransaction();
            fragmentTransaction.setAllowOptimization(false);
            fragmentTransaction.addToBackStack(transactionTag);
            fragmentTransaction.replace(R.id.frame_layout, newFragment).commit();
            frgManager.executePendingTransactions();
        } else {
            Timber.e(new RuntimeException(), "Replace fragments with null newFragment parameter.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    public interface OnBackPressedListener {
        void doBack();
    }


    /**
     * If user is logged in then {@link CartActivity} is launched . Otherwise is showed a signuppref dialog.
     */
//    public void onCartSelected() {
//        launchUserSpecificFragment(new CartActivity(), CartActivity.class.getSimpleName(), new LoginDialogInterface() {
//            @Override
//            public void successfulLoginOrRegistration(User user) {
//                // If signuppref was successful launch CartActivity.
//                onCartSelected();
//            }
//        });
//    }


}

