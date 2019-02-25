package com.example.ivani.schoolscheduleonline;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private SharedPreferences manager;
    private int tabsCount;

    public PagerAdapter(FragmentManager fm, int tabsCount, Context context) {
        super(fm);
        this.tabsCount = tabsCount;
        this.manager = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public Fragment getItem(int position) {
        String response = getResponse();
        switch (position) {
            case 0:
                return Monday.newInstance(response);
            case 1:
                return Tuesday.newInstance(response);
            case 2:
                return Wednesday.newInstance(response);
            case 3:
                return Thursday.newInstance(response);
            case 4:
                return Friday.newInstance(response);
        }
        return null;
    }

    private String getResponse() {
        //return teacher response String if the application is in teacher view
        if (!manager.getBoolean("studentView", true)) {
            return manager.getString("ResponseTeacher", "");
        }
        //else return student view response String
        return manager.getString("Response", "");
    }

    @Override
    public int getCount() {
        return this.tabsCount;
    }

}
