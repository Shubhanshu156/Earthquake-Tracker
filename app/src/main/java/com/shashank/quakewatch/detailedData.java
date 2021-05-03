package com.shashank.quakewatch;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Objects;

public class detailedData extends AppCompatActivity {
    TextView Title, magnitude, felt, coordinates,submit;
    Button web,share;
    String[] titleArray;
    ScrollView sv;
    View customView;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_data);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.hide();
        }
        web = findViewById(R.id.web);
        share = findViewById(R.id.share);
        Title = findViewById(R.id.title);
        submit = findViewById(R.id.submit);
        magnitude = findViewById(R.id.perceived_magnitude);
        felt = findViewById(R.id.FeltNo);
        coordinates = findViewById(R.id.coordinates);
        customView = findViewById(R.id.customView);
        sv = findViewById(R.id.sv);
        MapFragment fragment = new MapFragment();
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String feltNo = intent.getStringExtra("feltNo");
        String mMapTheme = intent.getStringExtra("mapTheme");
        String mag = intent.getStringExtra("mag");
        String coordinates1 = intent.getStringExtra("coordinates");
        String URL = intent.getStringExtra("url");
        String coordinates2 = coordinates1.replace("[", "");
        String coorNew = coordinates2.replace("]", "");
        String[] coordinatesArray = coorNew.split(",");
        titleArray = title.split("-");
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitude.getBackground();
        int magnitudeColor = getMagnitudeColor(Double.parseDouble(mag));
        magnitudeCircle.setColor(magnitudeColor);
        if (title.split(",").length>1) {
            titleArray = title.split(",");
        }
        Bundle bundle = new Bundle();
        bundle.putString("lat",coordinatesArray[1]);
        bundle.putString("lng",coordinatesArray[0]);
        bundle.putString("mapTheme",mMapTheme);
        if (titleArray != null) {
            bundle.putString("title", titleArray[1]);
        }
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.map,fragment).commit();
        Title.setText(title);
        magnitude.setText(mag);
        if (feltNo.equals("null")){
            felt.setText("0 RESPONSES RECEIVED");
        }else {
            felt.setText(feltNo);
        }
        coordinates.setText(coordinatesArray[1] + "," + coordinatesArray[0]);

        coordinates.setOnLongClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) detailedData.this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("coordinatesData", coorNew);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(detailedData.this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            return true;
        });

        coordinates.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("geo:" + coordinatesArray[1] + "," + coordinatesArray[0]);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            startActivity(mapIntent);

        });

        web.setOnClickListener(v -> {
            Uri earthquakeUri = Uri.parse(intent.getStringExtra("url"));
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
            startActivity(websiteIntent);
        });

        share.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, mag + "- Magnitude Earthquake at" + titleArray[1] + "\nfor more info visit official USGS Website: \n" + URL );
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });

        submit.setOnClickListener(v -> {
            Uri DYFI = Uri.parse(URL + "/tellus");
            Intent websiteIntent = new Intent(Intent.ACTION_VIEW, DYFI);
            startActivity(websiteIntent);
        });


        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            customView.setOnTouchListener((v, event) -> {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.performClick();
                    case MotionEvent.ACTION_MOVE:
                        // Disallow ScrollView to intercept touch events.
                        sv.requestDisallowInterceptTouchEvent(true);
                        v.performClick();
                        // Disable touch on transparent view
                        return false;
                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        sv.requestDisallowInterceptTouchEvent(false);
                        v.performClick();
                        return true;

                    default:
                        return true;

                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }

        return ContextCompat.getColor(this, magnitudeColorResourceId);
    }
}