package com.shashank.quakewatch.customAdapterAndLoader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.shashank.quakewatch.fetchData.fetchNews;

import java.util.ArrayList;
import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<news>> {

    List<news> mEarthquake = new ArrayList<>();
    private final String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    public void deliverResult(List<news> data) {
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
    public List<news> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        return fetchNews.fetchNewsData(mUrl);
    }
}