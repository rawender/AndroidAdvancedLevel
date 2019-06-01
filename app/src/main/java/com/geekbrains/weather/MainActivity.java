package com.geekbrains.weather;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.geekbrains.weather.fragments.AboutDeveloperFragment;
import com.geekbrains.weather.fragments.CitiesFragments;
import com.geekbrains.weather.fragments.FeedbackFragment;
import com.geekbrains.weather.fragments.WeatherHistoryFragment;

import static com.geekbrains.weather.fragments.CitiesFragments.keyForIndex;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GetCurrentIndex {

    private int index;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initSideMenu(toolbar);

        fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentById(R.id.main_container);

        if (fragment == null) {
            fragment = new CitiesFragments();
            fragmentManager.beginTransaction()
                    .add(R.id.main_container, fragment)
                    .commit();
        }
    }

    private void initSideMenu(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        NavigationView navigationView = findViewById(R.id.nav_view_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_history) {
            Fragment fragment = fragmentManager.findFragmentById(R.id.weather_main);
            if (fragment != null) {
                fragment = new WeatherHistoryFragment();
                Bundle args = new Bundle();
                args.putInt(keyForIndex, index);
                fragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.weather_main, fragment)
                        .commit();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_about) {
            Fragment fragment = fragmentManager.findFragmentById(R.id.main_container);
            if (fragment != null) {
                fragment = new AboutDeveloperFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .addToBackStack("Some_Key")
                        .commit();
            }
        } else if (id == R.id.nav_feedback) {
            Fragment fragment = fragmentManager.findFragmentById(R.id.main_container);
            if (fragment != null) {
                fragment = new FeedbackFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.main_container, fragment)
                        .addToBackStack("Some_Key")
                        .commit();
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout_main);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void getCurrentIndex(int index) {
        this.index = index;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int countOfFragmentInManager = getSupportFragmentManager().getBackStackEntryCount();
        if(countOfFragmentInManager > 0) {
            getSupportFragmentManager().popBackStack();
            getSupportFragmentManager().popBackStack("Some_Key", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
