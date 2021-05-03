package com.shashank.quakewatch;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<earthquake>>, FilterDialog.OnInputListener, settingsDialog.onSetListener {
    @Override
    public void sendTheme(String theme, String mapTheme, boolean mThemeReset) {
        mTheme = theme;
        mMapTheme = mapTheme;
        themeReset = mThemeReset;
        Toast.makeText(MainActivity.this, "App Theme changing not work in android pir or older version", Toast.LENGTH_LONG).show();
        if (theme.equals("Dark")) {
            if (themeReset) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                themeReset = false;
            }
        } else if (theme.equals("Light")) {
            if (themeReset) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                themeReset = false;
            }
        } else {
            if (themeReset) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                themeReset = false;
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", mTheme);
        editor.putString("mapTheme", mMapTheme);
        editor.apply();
    }

    @Override
    public void sendInput(String input, String num, String order) {
        earthquakeListView.animate().alpha(0.0f).setDuration(500);
        loadingProgressBar.setVisibility(View.VISIBLE);
        SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("min", input);
        editor.putString("want", num);
        editor.putString("order", order);
        editor.apply();
        if (!num.equals("")) {
            if (Integer.parseInt(num) > 20000) {
                Toast.makeText(MainActivity.this, "value greater than 20000 is not allowed", Toast.LENGTH_LONG).show();
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, MainActivity.this);
                getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, MainActivity.this);
            }
        }, 515);
    }

    private EarthquakeAdapter mAdapter;
    SwipeRefreshLayout SR;
    ListView earthquakeListView;
    TextView emptyView;
    ProgressBar loadingProgressBar;
    ConnectivityManager connectivityManager;
    ArrayList<earthquake> earthquakes = new ArrayList<>();
    SearchView searchView;
    //  ImageView iv;

    String mTheme, mMapTheme;
    boolean themeReset;
    /**
     * URL for earthquake data from the USGS dataset
     */
    String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson";

    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */

    private static final int EARTHQUAKE_LOADER_ID = 1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoadTheme();
        if (Build.VERSION.SDK_INT >= 28) {
            if (Build.VERSION.SDK_INT == 28) { // it causes app hanging, user can not interact with app
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else if (mTheme.equals("Dark")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else if (mTheme.equals("Light")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }
        earthquakeListView = (ListView) findViewById(R.id.lv);
        SR = findViewById(R.id.swipe);
        loadingProgressBar = findViewById(R.id.Loading);
        loadingProgressBar.setVisibility(View.VISIBLE);
        emptyView = findViewById(R.id.emptyView);
        earthquakeListView.setEmptyView(emptyView);

        SR.setColorSchemeResources(R.color.magnitude3, R.color.magnitude5, R.color.magnitude10plus);
        mAdapter = new EarthquakeAdapter(MainActivity.this, earthquakes);
        earthquakeListView.setTextFilterEnabled(true);
        int nightModeFlags = MainActivity.this.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
                break;
            default:
                break;
        }

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        //  earthquakeListView.setAdapter(mAdapter);
        Configuration configuration = getResources().getConfiguration();
        int screenhtDp = configuration.screenHeightDp;
        //Toast.makeText(MainActivity.this,screenhtDp+" W: "+ScreenWidth,Toast.LENGTH_LONG).show();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT && screenhtDp < 550) {
            Toast.makeText(MainActivity.this
                    , "this app is designed for BIG screen phones may not work properly."
                    , Toast.LENGTH_LONG).show();
        } else if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int ScreenWidth = configuration.screenWidthDp;
            if (ScreenWidth < 550) {
                Toast.makeText(MainActivity.this
                        , "this app is designed for BIG screen phones may not work properly."
                        , Toast.LENGTH_LONG).show();
            }
        }
        // Set an item click listener on the ListView, which starts new activity with detailed data
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(MainActivity.this, detailedData.class);
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
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        earthquakeListView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Find the current earthquake that was clicked on
            earthquake currentEarthquake = mAdapter.getItem(position);
            // Convert the String URL into a URI object (to pass into the Intent constructor)
            Uri earthquakeUri = Uri.parse(currentEarthquake.getURL());
            // Create a new intent to view the earthquake URI
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
            // Send the intent to launch a new activity
            startActivity(websiteIntent);
            return true;
        });
        SR.setOnRefreshListener(() -> {
            earthquakeListView.animate().alpha(0.0f).setDuration(300);
            new Handler().postDelayed(() -> {
                // Get a reference to the LoaderManager, in order to interact with loaders.
                LoaderManager loaderManager = getLoaderManager();
                loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, MainActivity.this);
                getLoaderManager().restartLoader(EARTHQUAKE_LOADER_ID, null, MainActivity.this);
            }, 350);
        });
        earthquakeListView.animate().alpha(0.0f);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            emptyView.setText(R.string.no_int);
            loadingProgressBar.animate().alpha(0.0f);
        } else {
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<earthquake>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = getSharedPreferences("test", 0);
        String minMagnitude = sharedPrefs.getString("min", "");
        String OrderBy = sharedPrefs.getString("order", "");
        String minEarth = sharedPrefs.getString("want", "");
        String USGS_URL = USGS_REQUEST_URL;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String dateTime = dtf.format(now);
        String[] date = dateTime.split(" ");
        String[] time = date[1].split(":");

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("endtime", date[0].replace("/", "-") + "T" + time[0] + ":00:00"/*date[1]*/);
        uriBuilder.appendQueryParameter("orderby", OrderBy);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("limit", minEarth);
//        ClipboardManager clipboard = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clip = ClipData.newPlainText("coordinatesData", uriBuilder.toString());
//        clipboard.setPrimaryClip(clip);
//        Toast.makeText(MainActivity.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        return new EarthquakeLoader(MainActivity.this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<earthquake>> loader, List<earthquake> data) {
        mAdapter.clear();
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
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
        }, 1500);
    }

    @Override
    public void onLoaderReset(Loader<List<earthquake>> loader) {
        mAdapter.clear();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                FilterDialog filterDialogue = new FilterDialog();
                filterDialogue.show(getSupportFragmentManager(), "filter dialogue");
                return true;
            case R.id.settings:
                settingsDialog settings_Dialogue = new settingsDialog();
                settings_Dialogue.show(getSupportFragmentManager(), "settings");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        searchView = (SearchView) menuItem.getActionView();
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
                        EarthquakeAdapter adapter = new EarthquakeAdapter(MainActivity.this, arrayList);
                        earthquakeListView.setAdapter(adapter);
                    }

                }
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        earthquakeListView.animate().alpha(1.0f).setDuration(2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        earthquakeListView.animate().alpha(0.0f);
    }

    public void LoadTheme() {
        SharedPreferences preferences = getSharedPreferences("test", MODE_PRIVATE);
        mMapTheme = preferences.getString("mapTheme", "System default");
        mTheme = preferences.getString("theme", "");
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Are you sure you want to exit??");
            builder.setCancelable(true);
            builder.setNegativeButton("Yes", (dialog, which) -> {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
            builder.setPositiveButton("cancel", (dialog, which) -> dialog.cancel());
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}