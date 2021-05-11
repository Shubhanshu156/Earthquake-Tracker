package com.shashank.quakewatch.customAdapterAndLoader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.shashank.quakewatch.fetchData.fetchEarthquake;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<earthquake>> {

    List<earthquake> mEarthquake = new ArrayList<>();
    private final String mUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public void deliverResult(List<earthquake> data) {
        mEarthquake = data;
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mEarthquake != null) {
            if (mEarthquake.isEmpty()) {
                forceLoad();
            } else {
                deliverResult(mEarthquake);
            }
        }
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<earthquake> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        return fetchEarthquake.fetchEarthquakeData(mUrl);
    }
}
