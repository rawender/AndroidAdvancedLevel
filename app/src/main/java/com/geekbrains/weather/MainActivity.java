package com.geekbrains.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
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
import android.widget.Toast;

import com.geekbrains.weather.fragments.AboutDeveloperFragment;
import com.geekbrains.weather.fragments.CitiesFragments;
import com.geekbrains.weather.fragments.FeedbackFragment;
import com.geekbrains.weather.fragments.SensorsFragment;
import com.geekbrains.weather.fragments.WeatherHistoryFragment;
import com.geekbrains.weather.support.BackgroundService;
import com.geekbrains.weather.support.GetCurrentIndex;

import static com.geekbrains.weather.fragments.CitiesFragments.keyForIndex;
import static com.geekbrains.weather.fragments.WeatherHistoryFragment.keyForCityName;
import static com.geekbrains.weather.fragments.WeatherHistoryFragment.keyForCityTempHistory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GetCurrentIndex {

    private ServiceFinishedReceiver receiver = new ServiceFinishedReceiver();

    private FragmentManager fragmentManager;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initSideMenu(toolbar);
        fragmentManager = getSupportFragmentManager();
        showFragment();
    }

    private void showFragment() {
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
        switch (id) {
            case R.id.menu_history:
                Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
                intent.putExtra(keyForIndex, index);
                startService(intent);
                break;
            case R.id.menu_sensors:
                Fragment fragment = fragmentManager.findFragmentById(R.id.main_container_two);
                if (fragment != null) {
                    fragment = new SensorsFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container_two, fragment)
                            .commit();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.nav_about) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Fragment fragment = fragmentManager.findFragmentById(R.id.main_container_two);
                if (fragment != null) {
                    fragment = new AboutDeveloperFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container_two, fragment)
                            .commit();
                }
            } else {
                Fragment fragment = fragmentManager.findFragmentById(R.id.main_container);
                if (fragment != null) {
                    fragment = new AboutDeveloperFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container, fragment)
                            .addToBackStack("Some_Key")
                            .commit();
                }
            }

        } else if (id == R.id.nav_feedback) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Fragment fragment = fragmentManager.findFragmentById(R.id.main_container_two);
                if (fragment != null) {
                    fragment = new FeedbackFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container_two, fragment)
                            .commit();
                }
            } else {
                Fragment fragment = fragmentManager.findFragmentById(R.id.main_container);
                if (fragment != null) {
                    fragment = new FeedbackFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.main_container, fragment)
                            .addToBackStack("Some_Key")
                            .commit();
                }
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

    private class ServiceFinishedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String cityName = intent.getStringExtra(keyForCityName);
                    String[] tempHistory = intent.getStringArrayExtra(keyForCityTempHistory);
                    Fragment fragment = fragmentManager.findFragmentById(R.id.main_container_two);
                    if (fragment != null) {
                        fragment = new WeatherHistoryFragment();
                        Bundle args = new Bundle();
                        args.putString(keyForCityName, cityName);
                        args.putStringArray(keyForCityTempHistory, tempHistory);
                        fragment.setArguments(args);
                        fragmentManager.beginTransaction()
                                .replace(R.id.main_container_two, fragment)
                                .commit();
                    }
                    Toast.makeText(getApplicationContext(), "From IntentService",
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(receiver, new IntentFilter(BackgroundService.BROADCAST_ACTION));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int countOfFragmentInManager = getSupportFragmentManager().getBackStackEntryCount();
        if(countOfFragmentInManager > 0) {
            getSupportFragmentManager().popBackStack("Some_Key", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
}
