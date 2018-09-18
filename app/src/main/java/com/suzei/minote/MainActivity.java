package com.suzei.minote;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.franmontiel.fullscreendialog.FullScreenDialogFragment;
import com.suzei.minote.fragments.AllFragment;
import com.suzei.minote.fragments.EventsFragment;
import com.suzei.minote.fragments.LectureFragment;
import com.suzei.minote.fragments.ReminderFragment;
import com.suzei.minote.fragments.TodoFragment;
import com.suzei.minote.utils.AppRater;
import com.suzei.minote.utils.FullscreenDialog;
import com.suzei.minote.utils.ListDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nav_view) NavigationView navigationView;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.main_fab) FloatingActionButton fabView;

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
        setUpToolbar();
        setUpNavigationView();
        setUpFab();
        setUpDrawer();
        showUi(savedInstanceState);
    }

    private void initObjects() {
        AppRater.app_launched(this);
        ButterKnife.bind(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpFab() {
        fabView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FullscreenDialog dialog = new FullscreenDialog(MainActivity.this, R.style.FullscreenDialog);
                dialog.show();
            }

        });
    }

    private void setUpDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void showUi(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            showFragment(new AllFragment());
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        super.onBackPressed();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_all:
                showFragment(new AllFragment());
                break;

            case R.id.nav_reminder:
                showFragment(new ReminderFragment());
                break;

            case R.id.nav_todo:
                showFragment(new TodoFragment());
                break;

            case R.id.nav_lecture:
                showFragment(new LectureFragment());
                break;

            case R.id.nav_events:
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
                String titleAbout = getString(R.string.dialog_about_title);
                String messageAbout = getString(R.string.dialog_about_message);
                showDialog(titleAbout, messageAbout + BuildConfig.VERSION_NAME);
                break;
        }

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
}
