package com.example.ivani.schoolscheduleonline;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import java.util.Calendar;

public class Timetable extends AppCompatActivity implements Monday.OnFragmentInteractionListener, Tuesday.OnFragmentInteractionListener,
        Wednesday.OnFragmentInteractionListener, Thursday.OnFragmentInteractionListener, Friday.OnFragmentInteractionListener {

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);
        loadCustomActionBar();

        tabLayout = findViewById(R.id.tablayout);
        setTabsTitles(tabLayout);
        final ViewPager viewPager = findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), getApplicationContext());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        loadCurrentDay(viewPager);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //return to previous activity (main)
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCustomActionBar() {
        Bitmap logo = getIntent().getParcelableExtra("BitmapImage");
        ImageView schoolLogo = findViewById(R.id.logoId);
        if (logo != null) {
            schoolLogo.setImageBitmap(logo);
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //display back button
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setTabsTitles(TabLayout tabLayout) {
        tabLayout.addTab(tabLayout.newTab().setText("Понеделник"));
        tabLayout.addTab(tabLayout.newTab().setText("Вторник"));
        tabLayout.addTab(tabLayout.newTab().setText("Сряда"));
        tabLayout.addTab(tabLayout.newTab().setText("Четвъртък"));
        tabLayout.addTab(tabLayout.newTab().setText("Петък"));
    }

    private void loadCurrentDay(ViewPager viewPager) {
        //start timetable activity with the current day view
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_WEEK);
        int index = 0;
        switch (day) {
            case Calendar.TUESDAY:
                index = 1;
                break;
            case Calendar.WEDNESDAY:
                index = 2;
                break;
            case Calendar.THURSDAY:
                index = 3;
                break;
            case Calendar.FRIDAY:
                index = 4;
                break;
        }
        viewPager.setCurrentItem(index);
    }
}
