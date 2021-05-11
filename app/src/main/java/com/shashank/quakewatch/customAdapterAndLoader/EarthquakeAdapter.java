package com.shashank.quakewatch.customAdapterAndLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.shashank.quakewatch.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EarthquakeAdapter extends ArrayAdapter<earthquake> {
//    ArrayList<earthquake> earthquakes;
    public EarthquakeAdapter(@NonNull Context context, @NonNull List<earthquake> objects) {
        super(context, 0, objects);
    }
    private static class ViewHolder {
        TextView magnitude;
        TextView location;
        TextView SimpleDate;
        TextView SimpleTime;
        TextView primaryLoc;
    }

    @SuppressLint({"ViewHolder", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String mag = String.valueOf(getItem(position).getMagnitude());
        long date = getItem(position).getTimeInMilliseconds();
        String loc = getItem(position).getLocation();
        String primaryLocation, locationOffSet;
        ViewHolder holder;
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.earthquake_listitem, parent, false);
            holder = new ViewHolder();
            holder.magnitude = convertView.findViewById(R.id.magnitude);
            holder.location = convertView.findViewById(R.id.location);
            holder.SimpleDate = convertView.findViewById(R.id.date);
            holder.SimpleTime = convertView.findViewById(R.id.time);
            holder.primaryLoc = convertView.findViewById(R.id.priLoc);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if (loc.contains("of")) {
            String[] parts = loc.split("of ");
            primaryLocation = parts[1];
            locationOffSet = parts[0] + " of ";
        } else {
            locationOffSet = "Near the ";
            primaryLocation = loc;
        }
        Date UnixTime = new Date(date);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");
        String displayDate = dateFormatter.format(UnixTime);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm a");
        String displayTime = timeFormatter.format(UnixTime);
        GradientDrawable magnitudeCircle = (GradientDrawable) holder.magnitude.getBackground();
        int magnitudeColor = getMagnitudeColor(getItem(position).getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);
        String[] NewMag = mag.split("\\.");
        if (NewMag[1].length()>=2){
            String new_mag = NewMag[1].substring(0,2);
            holder.magnitude.setText(NewMag[0]+"."+new_mag);
        }else {
            holder.magnitude.setText(mag);
        }
        holder.location.setText(locationOffSet);
        holder.primaryLoc.setText(primaryLocation);
        holder.SimpleDate.setText(displayDate);
        holder.SimpleTime.setText(displayTime);
        return convertView;
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
        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }
}

