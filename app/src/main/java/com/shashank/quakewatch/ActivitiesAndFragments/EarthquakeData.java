package com.shashank.quakewatch.ActivitiesAndFragments;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.shashank.quakewatch.customAdapterAndLoader.EarthquakeAdapter;
import com.shashank.quakewatch.customAdapterAndLoader.EarthquakeLoader;
import com.shashank.quakewatch.DialogsAndAnimation.FilterDialog;
import com.shashank.quakewatch.R;
import com.shashank.quakewatch.customAdapterAndLoader.earthquake;
import com.shashank.quakewatch.DialogsAndAnimation.settingsDialog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings( "deprecation" )
public class EarthquakeData extends Fragment implements LoaderManager.LoaderCallbacks<List<earthquake>>, FilterDialog.OnInputListener, settingsDialog.onSetListener {


    public EarthquakeData() {
        // Required empty public constructor
    }

    @Override
    public void sendTheme(String theme, String mapTheme) {
        mTheme = theme;
        mMapTheme = mapTheme;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", mTheme);
        editor.putString("mapTheme", mMapTheme);
        editor.apply();
    }

    @Override
    public void sendInput(String input, String num, String order) {
        earthquakeListView.animate().alpha(0.0f).setDuration(500);
        loadingProgressBar.setVisibility(View.VISIBLE);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("min", input);
        editor.putString("want", num);
        editor.putString("order", order);
        editor.apply();
        if (!num.equals("")) {
            if (Integer.parseInt(num) > 20000) {
                Toast.makeText(getContext(), "value greater than 20000 is not allowed", Toast.LENGTH_LONG).show();
            }
        }
        new Handler().postDelayed(() -> {
            LoaderManager loaderManager = Objects.requireNonNull(getActivity()).getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, EarthquakeData.this);
            getActivity().getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, EarthquakeData.this);
        }, 515);
    }


    /**
     * URL for earthquake data from the USGS dataset
     */
    String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson";

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */

    private static final int EARTHQUAKE_LOADER_ID = 1;

    private EarthquakeAdapter mAdapter;
    SwipeRefreshLayout SR;
    ListView earthquakeListView;
    TextView emptyView;
    ProgressBar loadingProgressBar;
    ConnectivityManager connectivityManager;
    ArrayList<earthquake> earthquakes = new ArrayList<>();
    String mTheme, mMapTheme;
    View rootView;


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_earthquake_data, container, false);
        LoadTheme();

        earthquakeListView = rootView.findViewById(R.id.lv);
        SR = rootView.findViewById(R.id.swipe);
        loadingProgressBar = rootView.findViewById(R.id.Loading);
        loadingProgressBar.setVisibility(View.VISIBLE);
        emptyView = rootView.findViewById(R.id.emptyView);
        earthquakeListView.setEmptyView(emptyView);

        SR.setColorSchemeResources(R.color.magnitude3, R.color.magnitude5, R.color.magnitude10plus);
        mAdapter = new EarthquakeAdapter(getContext(), earthquakes);
        earthquakeListView.setTextFilterEnabled(true);
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
        earthquakeListView.setOnItemClickListener((adapterView, view, position, l) -> {
            Intent intent = new Intent(getContext(), detailedData.class);
            earthquake e = (earthquake) earthquakeListView.getAdapter().getItem(position);
            String feltNo = e.getFeltNo();
            String title = e.getTitle();
            String mag = String.valueOf(e.getMagnitude());
            String coordinates = e.getCoordinates();
            String url = e.getURL();
            intent.putExtra("mapTheme", mMapTheme);
            intent.putExtra("title", title);
            intent.putExtra("feltNo", feltNo);
            intent.putExtra("mag", mag);
            intent.putExtra("coordinates", coordinates);
            intent.putExtra("url", url);
            startActivity(intent);
            getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        earthquakeListView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Find the current earthquake that was clicked on
            earthquake currentEarthquake = mAdapter.getItem(position);
            // Convert the String URL into a URI object (to pass into the Intent constructor)
            Intent intent1 = new Intent(getContext(),WebView.class);
            intent1.putExtra("url",currentEarthquake.getURL());
            intent1.putExtra("request","USGS");
            startActivity(intent1);
            Objects.requireNonNull(getActivity()).overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            return true;
        });

        SR.setOnRefreshListener(() -> {
            earthquakeListView.animate().alpha(0.0f).setDuration(300);
//            new Handler().postDelayed(() -> {
                // Get a reference to the LoaderManager, in order to interact with loaders.
                LoaderManager loaderManager = Objects.requireNonNull(getActivity()).getLoaderManager();
                loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, EarthquakeData.this);
                getActivity().getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, EarthquakeData.this);
//            }, 350);
        });
        earthquakeListView.animate().alpha(0.0f);
        connectivityManager = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            emptyView.setText(R.string.no_int);
            loadingProgressBar.animate().alpha(0.0f);
        } else {
            LoaderManager loaderManager = getActivity().getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, EarthquakeData.this);
        }
        return rootView;
    }
    @Override
    public Loader<List<earthquake>> onCreateLoader(int id, Bundle args) {
        int minMagnitude;
        String OrderBy;
        String minEarth;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        minMagnitude = sharedPrefs.getInt("min", 0);
        OrderBy = sharedPrefs.getString("order", "");
        minEarth = sharedPrefs.getString("want", "");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String dateTime = dtf.format(now);
        String[] date = dateTime.split(" ");
        String[] time = date[1].split(":");

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("endtime", date[0].replace("/", "-") + "T" + time[0] + ":00:00"/*date[1]*/);
        if (OrderBy.equals("time-dsc") || OrderBy.equals("magnitude-dsc")){
            String[] dsc = OrderBy.split("-");
            uriBuilder.appendQueryParameter("orderby", dsc[0]);
        }else{
            uriBuilder.appendQueryParameter("orderby", OrderBy);
        }
        uriBuilder.appendQueryParameter("minmag", minMagnitude+"");
        uriBuilder.appendQueryParameter("limit", minEarth);
//        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clip = ClipData.newPlainText("coordinatesData", uriBuilder.toString());
//        clipboard.setPrimaryClip(clip);
//        Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
        return new EarthquakeLoader(getContext(),uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(Loader<List<earthquake>> loader, List<earthquake> data) {
        mAdapter.clear();

        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
            emptyView.setText("");
        } else if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            emptyView.setText(R.string.no_int);
        } else if (data == null) {
            emptyView.setText("server not responding.\ntry changing total earthquake value");
        }
        earthquakeListView.setAdapter(mAdapter);
        new Handler().postDelayed(() -> {
            loadingProgressBar.setVisibility(View.GONE);
            earthquakeListView.animate().alpha(1.0f).setDuration(1000);
            SR.setRefreshing(false);
        }, 1000);
    }

    @Override
    public void onLoaderReset(Loader<List<earthquake>> loader) {
        mAdapter.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(isVisible());
    }

    public void LoadTheme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mMapTheme = preferences.getString("mapTheme", "System default");
        mTheme = preferences.getString("theme", "");
    }




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.filter:
                    FilterDialog filterDialogue = new FilterDialog();
                    filterDialogue.show(getActivity().getSupportFragmentManager(), "filter dialog");
                    filterDialogue.setTargetFragment(EarthquakeData.this, 1);
                    return true;
                case R.id.settings:
                    settingsDialog settings_Dialogue = new settingsDialog();
                    settings_Dialogue.show(getActivity().getSupportFragmentManager(), "settings");
                    settings_Dialogue.setTargetFragment(EarthquakeData.this,0);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<earthquake> arrayList = new ArrayList<>();
                for (earthquake e : earthquakes) {
                    if (newText.length() > 0) {
                        if (Character.isDigit(newText.charAt(0))) {
                            if (String.valueOf(e.getMagnitude()).startsWith(newText)) {
                                arrayList.add(e);
                            }
                        } else if (e.getLocation().toLowerCase().contains(newText.toLowerCase())) {
                            arrayList.add(e);
                        }
                    }
                    if (newText.equals("")) {
                        earthquakeListView.setAdapter(mAdapter);
                    } else {
                        EarthquakeAdapter adapter = new EarthquakeAdapter(getContext(), arrayList);
                        earthquakeListView.setAdapter(adapter);
                    }
                }
                return false;
            }
        });
    }
}