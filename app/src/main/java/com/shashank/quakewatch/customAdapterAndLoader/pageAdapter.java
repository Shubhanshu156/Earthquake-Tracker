package com.shashank.quakewatch.customAdapterAndLoader;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.shashank.quakewatch.ActivitiesAndFragments.EarthquakeData;
import com.shashank.quakewatch.ActivitiesAndFragments.newsData;

public class pageAdapter extends FragmentStatePagerAdapter {
    private final int numOfTabs;


    public pageAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm, numOfTabs);
        this.numOfTabs = numOfTabs;
    }



    @NonNull
    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new EarthquakeData();
            case 1:
                return new newsData();
        }
        return null;
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }

}

