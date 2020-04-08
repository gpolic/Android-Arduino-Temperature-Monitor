package com.gpolic.hometemp;
// TODO  test connectivity issues. Wifi DOWN. Wifi Up server DOWN. 3G up but slow?.

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.gpolic.hometemp.adapters.MyFragmentAdapter;
import com.gpolic.hometemp.data.TemperaturesDBController;
import com.gpolic.hometemp.logger.LLog;
import com.gpolic.hometemp.sync.GetTempFromServerUpdater;
import com.gpolic.hometemp.ui.GenericFragment;
import com.gpolic.hometemp.ui.TempsChartFragment;
import com.gpolic.hometemp.util.DateUtils;
import com.gpolic.hometemp.util.MyHelper;

import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CURRENT_TEMP_FRAG = 0;
    private static final int PAST_TEMP_FRAG = 1;
    private static final int ANALYTICS_FRAG = 2;
    private static final String EXTRA_FRAG_TYPE = "extraFragType";
    private DrawerLayout drawerLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LLog.EnableLog();
        LLog.d(TAG, "Logging enabled");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar myActionBar = getSupportActionBar();
        if (myActionBar != null) {
            myActionBar.setHomeAsUpIndicator(R.drawable.ic_menu);   // initialize the top bar
            myActionBar.setDisplayHomeAsUpEnabled(true);
        }
        drawerLayout = findViewById(R.id.drawer_layout);


        NavigationView myNavigationView = findViewById(R.id.nav_view);
        if (myNavigationView != null) {
            setupDrawerContent(myNavigationView);   // implemented below
        }

        viewPager = findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);              // implemented below - add fragments in MyFragmentAdapter
            viewPager.setCurrentItem(getIntent().getIntExtra(EXTRA_FRAG_TYPE, CURRENT_TEMP_FRAG));
        }


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        Boolean networkActive = MyHelper.isNetworkAvailable(getApplicationContext());

        switch (MyHelper.checkFirstRun(getApplicationContext())) {
            case 0:
                if (BuildConfig.DEBUG)
                    LLog.d(TAG, "normal run");

                final TemperaturesDBController databaseController = new TemperaturesDBController(getApplicationContext());
                final Date date = databaseController.getLatestTempRecordDate();

                final String dateString = date != null ? DateUtils.dateToDBString(date) : "1/1/1900";  // if DB returns null date then use 1/1/1900
                if (networkActive)
                    new GetTempFromServerUpdater().getJSON(MainActivity.this, MyHelper.PULL_AFTER_DATE_URL, getString(R.string.load_server_data_msg), dateString);
                else
                    Toast.makeText(this, R.string.network_disabled_message, Toast.LENGTH_SHORT).show();

                break;

            case 1:
                if (BuildConfig.DEBUG)
                    LLog.d(TAG, "First run");

                if (networkActive)
                    new GetTempFromServerUpdater().getJSON(MainActivity.this, MyHelper.PULL_AFTER_DATE_URL, getString(R.string.first_run_msg), "1/1/1900");
                else
                    Toast.makeText(this, R.string.network_disabled_message, Toast.LENGTH_SHORT).show();

                break;

            case 2:
                if (BuildConfig.DEBUG) {
                    LLog.d(TAG, "Run after upgrade");
//                    Toast.makeText(this, "Run after upgrade", Toast.LENGTH_SHORT).show();
                }
                if (networkActive) {
                    final TemperaturesDBController dbController = new TemperaturesDBController(getApplicationContext());
                    dbController.deleteAllTemperatureRecords();   // clear all records to perform a new download from the server and refresh data
                    new GetTempFromServerUpdater().getJSON(getApplicationContext(), MyHelper.PULL_AFTER_DATE_URL, getString(R.string.first_run_msg), "1/1/1900");
                } else
                    Toast.makeText(this, R.string.network_disabled_message, Toast.LENGTH_SHORT).show();

                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_actions, menu);  // inflate settings menu
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Setup the adapter for the ViewPager.
     * Creates the FragmentAdapter and the Fragments and insert parameters (bundle)
     *
     * @param viewPager
     */
    private void setupViewPager(ViewPager viewPager) {
        // use a customized fragment adapter - extends FragmentPagerAdapter
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager());

        GenericFragment pastTempsFragment = new GenericFragment();
        adapter.addFragment(pastTempsFragment, "Current Temps");
        LLog.d(TAG, "Created the Fragment : Current Temps");

        TempsChartFragment chartTempFragment = new TempsChartFragment();
        adapter.addFragment(chartTempFragment, "Analytics");
        LLog.d(TAG, "Created the Fragment : Analytics");

        viewPager.setAdapter(adapter);
    }


    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.nav_home:
                                viewPager.setCurrentItem(CURRENT_TEMP_FRAG);
                                drawerLayout.closeDrawers();
                                return true;
                            case R.id.nav_temperatures:
                                viewPager.setCurrentItem(PAST_TEMP_FRAG);
                                drawerLayout.closeDrawers();
                                return true;
                            case R.id.nav_analytics:
                                viewPager.setCurrentItem(ANALYTICS_FRAG);
                                drawerLayout.closeDrawers();
                                return true;
                            case R.id.nav_link1:
                                Intent browser1 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.github.com/gpolic"));
                                startActivity(browser1);
                                return true;
                            default:
                                return true;
                        }
                    }
                });
    }
}