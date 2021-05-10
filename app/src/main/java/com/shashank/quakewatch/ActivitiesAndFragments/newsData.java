package com.shashank.quakewatch.ActivitiesAndFragments;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.shashank.quakewatch.customAdapterAndLoader.NewsAdapter;
import com.shashank.quakewatch.customAdapterAndLoader.NewsLoader;
import com.shashank.quakewatch.R;
import com.shashank.quakewatch.customAdapterAndLoader.news;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings( "deprecation" )
public class newsData extends Fragment implements LoaderManager.LoaderCallbacks<List<news>> {


    public newsData() {
        // Required empty public constructor
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);

    }
    private static final int NEWS_LOADER_ID = 2;


    private NewsAdapter nAdapter;
    SwipeRefreshLayout SR;
    ListView newsListView;
    TextView emptyView;
    ProgressBar loadingProgressBar;
    ConnectivityManager connectivityManager;
    ArrayList<news> newsArrayList = new ArrayList<>();
//    final String NEWS_REQUEST_URL = "https://newsdata.io/api/1/news?apikey=pub_API_KEY&q=earthquake";
    final String NEWS_REQUEST_URL = "https://gnews.io/api/v4/search?q=earthquake&token=API_KEY";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_data, container, false);
        newsListView = rootView.findViewById(R.id.lv1);
        SR = rootView.findViewById(R.id.swipe1);
        loadingProgressBar = rootView.findViewById(R.id.Loading1);
        loadingProgressBar.setVisibility(View.VISIBLE);
        emptyView = rootView.findViewById(R.id.emptyView1);
        newsListView.setEmptyView(emptyView);

        SR.setColorSchemeResources(R.color.magnitude3, R.color.magnitude5, R.color.magnitude10plus);
        nAdapter = new NewsAdapter(getContext(), newsArrayList);
        newsListView.setTextFilterEnabled(true);
        int nightModeFlags = getContext().getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        }

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        //  earthquakeListView.setAdapter(mAdapter);
        Configuration configuration = getResources().getConfiguration();
        int screenhtDp = configuration.screenHeightDp;
        //Toast.makeText(MainActivity.this,screenhtDp+" W: "+ScreenWidth,Toast.LENGTH_LONG).show();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT && screenhtDp < 550) {
            Toast.makeText(getContext()
                    , "this app is designed for BIG screen phones may not work properly."
                    , Toast.LENGTH_LONG).show();
        } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int ScreenWidth = configuration.screenWidthDp;
            if (ScreenWidth < 550) {
                Toast.makeText(getContext()
                        , "this app is designed for BIG screen phones may not work properly."
                        , Toast.LENGTH_LONG).show();
            }
        }
        // Set an item click listener on the ListView, which starts new activity with detailed data
        newsListView.setOnItemClickListener((adapterView, view, position, l) -> {
            news News = (news) newsListView.getAdapter().getItem(position);
            String url = News.getURL();
            Intent intent = new Intent(getContext(),WebView.class);
            intent.putExtra("url",url);
            intent.putExtra("request","NEWS");
            startActivity(intent);
            getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        SR.setOnRefreshListener(() -> {
            newsListView.animate().alpha(0.0f).setDuration(300);
            new Handler().postDelayed(() -> {
                // Get a reference to the LoaderManager, in order to interact with loaders.
                LoaderManager loaderManager = getActivity().getLoaderManager();
                loaderManager.initLoader(NEWS_LOADER_ID, null, newsData.this);
                getActivity().getLoaderManager().restartLoader(NEWS_LOADER_ID, null, newsData.this);
            }, 350);
        });

        newsListView.animate().alpha(0.0f);
        connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            emptyView.setText(R.string.no_int);
            loadingProgressBar.animate().alpha(0.0f);
        } else {
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getActivity().getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, newsData.this);
        }
        return rootView;
    }


    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<news>> onCreateLoader(int id, Bundle args) {
            return new NewsLoader(getContext(),NEWS_REQUEST_URL);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadFinished(Loader<List<news>> loader, List<news> data) {
        nAdapter.clear();
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            nAdapter.addAll(data);
            emptyView.setText("");
        } else if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            emptyView.setText(R.string.no_int);
        } else if (data == null) {
            emptyView.setText("server not responding.");
        }
        newsListView.setAdapter(nAdapter);
        new Handler().postDelayed(() -> {
            loadingProgressBar.setVisibility(View.GONE);
            newsListView.animate().alpha(1.0f).setDuration(1000);
            SR.setRefreshing(false);
        }, 2000);
    }
    @Override
    public void onLoaderReset(Loader<List<news>> loader) {
        nAdapter.clear();
    }

}
