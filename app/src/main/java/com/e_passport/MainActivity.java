package com.e_passport;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.e_passport.activities.CountriesActivity;
import com.e_passport.activities.LoginActivity;
import com.e_passport.adapters.ViewPagerAdapter;
import com.e_passport.utilities.AppUtilities;
import com.e_passport.utilities.FirebaseUtilities;
import com.e_passport.utilities.PrefsUtilities;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle = null;
    private NavigationView navigationView;
    private BottomNavigationView bottomNav;
    private ViewPager viewPager;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!AppUtilities.isInternetAvailable(this)){
            AppUtilities.showNoInternetDialog(this);
        }
        drawerLayout = findViewById(R.id.drawer_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open_hint, R.string.drawer_close_hint) {
                public void onDrawerClosed(View view) {
                    supportInvalidateOptionsMenu();
                }
                public void onDrawerOpened(View drawerView) {
                    supportInvalidateOptionsMenu();
                }
            };
            toggle.setDrawerIndicatorEnabled(true);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.drawer_home){
                    viewPager.setCurrentItem(0);
                    bottomNav.setSelectedItemId(bottomNav.getMenu().getItem(0).getItemId());
                } else if(item.getItemId() == R.id.drawer_user){
                    viewPager.setCurrentItem(1);
                    bottomNav.setSelectedItemId(bottomNav.getMenu().getItem(1).getItemId());
                } else if(item.getItemId() == R.id.drawer_countries) {
                    startActivity(new Intent(MainActivity.this, CountriesActivity.class).putExtra("payload", "load_data"));
                    drawerLayout.close();
                    return false;
                } else if(item.getItemId() == R.id.drawer_logout) {
                    logout();
                    return false;
                } else if(item.getItemId() == R.id.drawer_about){
                    showAboutDialog();
                }
                drawerLayout.close();
                return true;
            }
        });

        bottomNav = findViewById(R.id.bottom_nav);
        bottomNav.setOnApplyWindowInsetsListener(null);

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_home){
                    viewPager.setCurrentItem(0);
                    navigationView.getMenu().findItem(R.id.drawer_home).setChecked(true);
                } else if(item.getItemId() == R.id.nav_user){
                    viewPager.setCurrentItem(1);
                    navigationView.getMenu().findItem(R.id.drawer_user).setChecked(true);
                } else if(item.getItemId() == R.id.nav_logout){
                    logout();
                    return false;
                }
                return true;
            }
        });

        viewPager = findViewById(R.id.view_pager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    navigationView.getMenu().findItem(R.id.drawer_home).setChecked(true);
                } else if(position == 1){
                    navigationView.getMenu().findItem(R.id.drawer_user).setChecked(true);
                }
                bottomNav.setSelectedItemId(bottomNav.getMenu().getItem(position).getItemId());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(0);
        navigationView.getMenu().findItem(R.id.drawer_home).setChecked(true);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if(drawerLayout.isOpen()){
            drawerLayout.close();
        } else if(viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0);
            navigationView.getMenu().findItem(R.id.drawer_home).setChecked(true);
            bottomNav.setSelectedItemId(bottomNav.getMenu().getItem(0).getItemId());
        } else {
            showExitDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.main_menu_about){
            showAboutDialog();
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(toggle != null) {
            toggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (toggle != null) {
            toggle.onConfigurationChanged(newConfig);
        }
    }

    private void logout(){
        FirebaseUtilities.getAuth().signOut();
        PrefsUtilities.clearPrefs();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void showAboutDialog(){
        final Dialog aboutDialog = new Dialog(this);
        aboutDialog.setContentView(R.layout.about_dialog);
        aboutDialog.setTitle(getResources().getString(R.string.about_dialog_title));
        Button closeButton = aboutDialog.findViewById(R.id.about_dialog_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog.dismiss();
            }
        });
        aboutDialog.show();
    }

    private void showExitDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.exit_dialog_title));
        builder.setMessage(getResources().getString(R.string.exit_dialog_descriptions));
        builder.setPositiveButton(getResources().getString(R.string.yes_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.no_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}