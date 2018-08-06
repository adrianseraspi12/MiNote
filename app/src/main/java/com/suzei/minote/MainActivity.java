package com.suzei.minote;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.suzei.minote.fragments.AllFragment;
import com.suzei.minote.fragments.EventsFragment;
import com.suzei.minote.fragments.LectureFragment;
import com.suzei.minote.fragments.ReminderFragment;
import com.suzei.minote.fragments.TodoFragment;
import com.suzei.minote.utils.AppRater;
import com.suzei.minote.utils.ListDialog;
import com.suzei.minote.utils.onBackPressedListener;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private AdView adView;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private onBackPressedListener onBackPressedListener;

    private FirebaseAnalytics mAnalytics;

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initObjects();
        setUpAd();
        setUpToolbar();
        setUpDrawer();
        setUpNavigationView();
        showUi(savedInstanceState);
    }

    private void initObjects() {
        MobileAds.initialize(MainActivity.this, getString(R.string.ad_app_id));
        Fabric.with(this, new Crashlytics());
        mAnalytics = FirebaseAnalytics.getInstance(this);
        AppRater.app_launched(this);
    }

    private void setUpAd() {
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setUpDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setUpNavigationView() {
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void showUi(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            showFragment(new AllFragment());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (onBackPressedListener != null) {
            Log.d(TAG, "onBackPressed: Listener= RUN");
            onBackPressedListener.doBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement

        switch (item.getItemId()) {

            case R.id.menu_add:
                showListDialog();
                break;
        }

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Bundle params = new Bundle();
        params.putInt("NavId", id);
        String navName = null;

        switch (id) {
            case R.id.nav_all:
                navName = "Nav_All Clicked";
                showFragment(new AllFragment());
                break;

            case R.id.nav_reminder:
                navName = "Nav_Reminder Clicked";
                showFragment(new ReminderFragment());
                break;

            case R.id.nav_todo:
                navName = "Nav_Todo Clicked";
                showFragment(new TodoFragment());
                break;

            case R.id.nav_lecture:
                navName = "Nav_Lecture Clicked";
                showFragment(new LectureFragment());
                break;

            case R.id.nav_events:
                navName = "Nav_Events Clicked";
                showFragment(new EventsFragment());
                break;
            case R.id.nav_instruction:
                String titleInstruction = getString(R.string.dialog_instruction_title);
                String messageInstruction = getString(R.string.dialog_instruction_message);
                showDialog(titleInstruction, messageInstruction);
                break;
            case R.id.nav_send_feedback:
                showSendFeedBack();
                break;
            case R.id.nav_rate_minote:
                showRateMiNote();
                break;
            case R.id.nav_about:
                navName = "Nav_About Clicked";
                String titleAbout = getString(R.string.dialog_about_title);
                String messageAbout = getString(R.string.dialog_about_message);
                showDialog(titleAbout, messageAbout + BuildConfig.VERSION_NAME);
                break;
        }

        mAnalytics.logEvent(navName, params);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showListDialog() {
        ListDialog dialog = new ListDialog(MainActivity.this, new ListDialog.DialogCallback() {

            @Override
            public void selectedDialog(Class<? extends Fragment> fragment, String str) {

                FullScreenDialogFragment dialogFragment = new FullScreenDialogFragment.Builder(
                        MainActivity.this)
                        .setTitle("Create " + str)
                        .setConfirmButton("Save")
                        .setContent(fragment, null)
                        .build();
                dialogFragment.show(getSupportFragmentManager(), "dialog");
            }
        });
        dialog.show();
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    private void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showSendFeedBack() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "adrianseraspi12@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "User Feedback");
        startActivity(Intent.createChooser(emailIntent, "Send Feedback:"));
    }

    private void showRateMiNote() {
        SharedPreferences prefs = getSharedPreferences("apprater", 0);
        SharedPreferences.Editor editor = prefs.edit();
        AppRater.showRateDialog(MainActivity.this, editor);
    }

    public void setOnBackPressedListener(onBackPressedListener listener) {
        this.onBackPressedListener = listener;
    }

    public void removeOnBackPressedListener() {
        this.onBackPressedListener = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        mAnalytics.setCurrentScreen(this, fragment.getClass().getSimpleName(),
                fragment.getClass().getSimpleName());
    }
}
