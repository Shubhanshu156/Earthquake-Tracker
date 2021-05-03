package com.shashank.quakewatch;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;


public class MapFragment extends Fragment {
    double lat,lng;
    String title,mMapTheme;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        int nightModeFlags =
                Objects.requireNonNull(getContext()).getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        Bundle b = getArguments();
        if (b != null) {
            lat = Double.parseDouble(b.getString("lat"));
            lng = Double.parseDouble(b.getString("lng"));
            mMapTheme = b.getString("mapTheme");
            title = b.getString("title");
        }
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(googleMap -> {
            try {
            switch (mMapTheme) {
                case "Simple":
                    googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    Objects.requireNonNull(getContext()), R.raw.default_style));
                    break;
                case "Dark":
                    googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    Objects.requireNonNull(getContext()), R.raw.style_dark_json));
                    break;
                case "Night":
                    googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    Objects.requireNonNull(getContext()), R.raw.style_night_json));
                    break;
                case "Retro":
                    googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    Objects.requireNonNull(getContext()), R.raw.style_retro_json));
                    break;
                case "Silver":
                    googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    Objects.requireNonNull(getContext()), R.raw.style_silver_json));
                    break;
                case "Satellite":
                    googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    break;
                case "Aubergine":
                    googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    Objects.requireNonNull(getContext()), R.raw.style_aubergine_json));
                    break;
                case "System default":
                    switch (nightModeFlags) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            Objects.requireNonNull(getContext()), R.raw.style_aubergine_json));
                            break;

                        case Configuration.UI_MODE_NIGHT_NO:
                            googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            Objects.requireNonNull(getContext()), R.raw.style_silver_json));
                            break;
                        case Configuration.UI_MODE_NIGHT_UNDEFINED:
                            googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            Objects.requireNonNull(getContext()), R.raw.default_style));
                            break;
                    }
                default:
                    switch (nightModeFlags) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            Objects.requireNonNull(getContext()), R.raw.style_aubergine_json));
                            break;

                        case Configuration.UI_MODE_NIGHT_NO:
                            googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            Objects.requireNonNull(getContext()), R.raw.style_silver_json));
                            break;
                        case Configuration.UI_MODE_NIGHT_UNDEFINED:
                            googleMap.setMapStyle(
                                    MapStyleOptions.loadRawResourceStyle(
                                            Objects.requireNonNull(getContext()), R.raw.default_style));
                            break;
                    }
            }
            }catch(Exception e){
                Toast.makeText(getContext(),"Error occurred",Toast.LENGTH_LONG).show();
                googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                Objects.requireNonNull(getContext()), R.raw.default_style));
            }
                LatLng latLng = new LatLng(lat, lng);
              //  Toast.makeText(getContext(),lat + ","+lng,Toast.LENGTH_SHORT).show();
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title).icon(BitmapDescriptorFactory.fromResource(R.drawable.earthquake_4));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4));
                googleMap.addMarker(markerOptions);
        });
        return rootView;
    }

}

