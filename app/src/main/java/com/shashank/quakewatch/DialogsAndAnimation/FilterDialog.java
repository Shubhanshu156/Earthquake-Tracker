package com.shashank.quakewatch.DialogsAndAnimation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.shashank.quakewatch.R;

import java.util.Objects;


public class FilterDialog extends DialogFragment {
    private static final String TAG = "Filter Dialogue: ";
    // SharedPreferences preferences;
    int min;
    RadioButton selectedRadioButton;
    String order, minEarth;
    LinearLayout filter;


    public interface OnInputListener {
        void sendInput(String min, String num, String order);
    }

    public interface onClickListenerFragment{
        void detachFragment();
    }

    public OnInputListener mOnInputListener;
    public onClickListenerFragment mOnClickListenerFragment;
    NumberPicker magnitude;
    RadioGroup rg;
    RadioButton time, time_asc, mag_dsc, mag_asc;
    TextView tv;
    EditText numOfEarthquake;
    Button done;




    @SuppressLint("NonConstantResourceId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.filter_dialog, container, false);
        rootView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeBottom() {
                getDialog().dismiss();
            }
        });
        getDialog().getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
       // p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        //p.x = 200;
        window.setAttributes(p);
        magnitude = rootView.findViewById(R.id.minimum);
        done = rootView.findViewById(R.id.done);
        tv = rootView.findViewById(R.id.textView2);
        numOfEarthquake = rootView.findViewById(R.id.numofEarthquake);
        rg = rootView.findViewById(R.id.rg);
        time = rootView.findViewById(R.id.time);
        time_asc = rootView.findViewById(R.id.time_asc);
        mag_dsc = rootView.findViewById(R.id.magnitude_);
        mag_asc = rootView.findViewById(R.id.magnitude_asc);
        filter = rootView.findViewById(R.id.filter);
        time.setChecked(Update_radio("time"));
        time_asc.setChecked(Update_radio("time_asc"));
        mag_dsc.setChecked(Update_radio("mag"));
        mag_asc.setChecked(Update_radio("mag_asc"));
        tv.setText("minimum \nmagnitude: ");
        magnitude.setMinValue(1);
        magnitude.setMaxValue(10);
        if (!time.isChecked() || !time_asc.isChecked() || !mag_asc.isChecked() || !mag_dsc.isChecked()){
            time.isChecked();
        }

        rg.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.time:
                    selectedRadioButton = time;
                    order = selectedRadioButton.getText().toString();
                    save_radio("time", selectedRadioButton.isChecked());
                    save_radio("mag", false);
                    save_radio("mag_asc", false);
                    save_radio("time_asc", false);
                    break;
                case R.id.time_asc:
                    selectedRadioButton = time_asc;
                    order = selectedRadioButton.getText().toString();
                    save_radio("time_asc", selectedRadioButton.isChecked());
                    save_radio("mag",false);
                    // Toast.makeText(getContext(),"running",Toast.LENGTH_LONG).show();
                    break;
                case R.id.magnitude_:
                    selectedRadioButton = mag_dsc;
                    order = selectedRadioButton.getText().toString();
                    save_radio("mag", selectedRadioButton.isChecked());
                    save_radio("mag_asc",false);
                    // Toast.makeText(getContext(),"running2",Toast.LENGTH_LONG).show();
                    break;
                case R.id.magnitude_asc:
                    selectedRadioButton = mag_asc;
                    order = selectedRadioButton.getText().toString();
                    save_radio("mag_asc", selectedRadioButton.isChecked());
                    //Toast.makeText(getContext(),"running3",Toast.LENGTH_LONG).show();
                    break;
            }
        });
        done.setOnClickListener(v -> {
            int id = rg.getCheckedRadioButtonId();
            RadioButton rb = rootView.findViewById(id);
            String minimum = String.valueOf(magnitude.getValue());
            mOnInputListener.sendInput(minimum, numOfEarthquake.getText().toString(), rb.getText().toString());
            mOnClickListenerFragment.detachFragment();

           // Toast.makeText(getContext(), order + " ", Toast.LENGTH_LONG).show();
            saveData();
            Objects.requireNonNull(getDialog()).dismiss();
        });

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int nightModeFlags = getContext().getResources().getConfiguration().uiMode &
                    Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
                GradientDrawable settings_back = (GradientDrawable) filter.getBackground();
                settings_back.setColor(ContextCompat.getColor(getContext(), R.color.white));
            }
            // getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        LoadData();
        UpdateData();
        return rootView;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnInputListener = (FilterDialog.OnInputListener) getTargetFragment();
            mOnClickListenerFragment = (FilterDialog.onClickListenerFragment) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

    public void saveData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("min", magnitude.getValue());
        editor.putString("want", numOfEarthquake.getText().toString());
        editor.apply();
    }

    public void LoadData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        min = preferences.getInt("min", 0);
        minEarth = preferences.getString("want", "");
    }

    public void UpdateData() {
        magnitude.setValue(min);
        numOfEarthquake.setText(minEarth);
    }

    public void save_radio(String key, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean Update_radio(String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        return preferences.getBoolean(key, false);
    }
}
