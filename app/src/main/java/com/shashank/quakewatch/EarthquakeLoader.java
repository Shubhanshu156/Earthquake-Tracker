package com.shashank.quakewatch;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
//@SuppressWarnings("ALL")
public class EarthquakeLoader extends AsyncTaskLoader<List<earthquake>> {

    /**
     * Tag for log messages
     */
    List<earthquake> mEarthquake = new ArrayList<>();
    /**
     * Query URL
     */
    private final String mUrl;

//    @Override
//    public void onContentChanged() {
//        forceLoad();
//    }


    /**
     * Constructs a new {@link EarthquakeLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
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
        if (mEarthquake.isEmpty()) {
            forceLoad();
        }else{
            deliverResult(mEarthquake);
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
        return QueryUtils.fetchEarthquakeData(mUrl);
    }
}
