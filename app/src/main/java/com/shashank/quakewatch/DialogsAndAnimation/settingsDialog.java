package com.shashank.quakewatch.DialogsAndAnimation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.shashank.quakewatch.R;

import java.util.Objects;

public class settingsDialog extends DialogFragment {
    private static final String TAG = "settings Dialogue: ";
    RadioButton selectedRadioButton, selectedRadioButton2, dark, light, sys_def, dark_map, silver_map, aubergine_map, satellite_map, night_map, simple_map, sys_def_map, retro_map;
    RadioGroup rg_1, rg_2;
    Button done;
    boolean mThemeReset;
    LinearLayout settings;

    public interface onSetListener {
        void sendTheme(String theme, String mapTheme);
    }
    public interface onSetTheme {
        void sendThemeToActivity(String theme, boolean themeReset);
    }
    public interface onClickListenerFragment{
        void detachFragment();
    }
    public onSetListener mOnSetListener;
    public onClickListenerFragment mOnClickListenerFragment;
    public onSetTheme mOnSetTheme;

    @SuppressLint("NonConstantResourceId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings_dialog, container, false);
        rootView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            public void onSwipeBottom() {
                getDialog().dismiss();
            }
        });
        getDialog().getWindow().setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        // p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        //p.x = 200;
        getDialog().getWindow().setAttributes(p);
        dark = rootView.findViewById(R.id.dark);
        light = rootView.findViewById(R.id.light);
        sys_def = rootView.findViewById(R.id.sys_def);
        dark_map = rootView.findViewById(R.id.dark_map);
        silver_map = rootView.findViewById(R.id.silver_map);
        aubergine_map = rootView.findViewById(R.id.Aubergine_map);
        satellite_map = rootView.findViewById(R.id.satellite_map);
        night_map = rootView.findViewById(R.id.night_map);
        simple_map = rootView.findViewById(R.id.simple_map);
        retro_map = rootView.findViewById(R.id.Retro_map);
        sys_def_map = rootView.findViewById(R.id.sys_def_map);
        settings = rootView.findViewById(R.id.settings);
        done = rootView.findViewById(R.id.Done);
        rg_1 = rootView.findViewById(R.id.rg_1);
        rg_2 = rootView.findViewById(R.id.rg_2);
        dark.setChecked(Update_radio("dark"));
        light.setChecked(Update_radio("light"));
        sys_def.setChecked(Update_radio("sys_def"));
        simple_map.setChecked(Update_radio("simple"));
        dark_map.setChecked(Update_radio("dark_map"));
        night_map.setChecked(Update_radio("night_map"));
        retro_map.setChecked(Update_radio("retro_map"));
        silver_map.setChecked(Update_radio("silver_map"));
        satellite_map.setChecked(Update_radio("satellite_map"));
        aubergine_map.setChecked(Update_radio("aubergine_map"));
        sys_def_map.setChecked(Update_radio("sys_def_map"));
        if (!dark.isChecked() && !light.isChecked() && !sys_def.isChecked()) {
            sys_def.setChecked(true);
        }
        if (!simple_map.isChecked() && !dark_map.isChecked() && !night_map.isChecked() && !retro_map.isChecked()
                && !silver_map.isChecked() && !satellite_map.isChecked() && !aubergine_map.isChecked() && !sys_def_map.isChecked()) {
            sys_def_map.setChecked(true);
        }
        if (Build.VERSION.SDK_INT <= 28) { // it causes app hanging, user can not interact with app
            for (int i = 0; i < rg_1.getChildCount(); i++) {
                rg_1.getChildAt(i).setEnabled(false);
            }
        }

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int nightModeFlags = getContext().getResources().getConfiguration().uiMode &
                    Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
                GradientDrawable settings_back = (GradientDrawable) settings.getBackground();
                settings_back.setColor(ContextCompat.getColor(getContext(), R.color.white));
            }
            // getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE); /*for older devices android 4.x*/
        }
        rg_1.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.dark:
                    selectedRadioButton = dark;
                    save_radio("dark", selectedRadioButton.isChecked());
                    save_radio("light", false);
                    save_radio("sys_def", false);
                    mThemeReset = true;
                    break;
                case R.id.light:
                    selectedRadioButton = light;
                    save_radio("light", selectedRadioButton.isChecked());
                    save_radio("sys_def", false);
                    mThemeReset = true;
                    break;
                case R.id.sys_def:
                    selectedRadioButton = sys_def;
                    save_radio("sys_def", selectedRadioButton.isChecked());
                    mThemeReset = true;
                    break;
            }
        });
        rg_2.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.simple_map:
                    selectedRadioButton2 = simple_map;
                    save_radio("simple", selectedRadioButton2.isChecked());
                    save_radio("dark_map", false);
                    save_radio("night_map", false);
                    save_radio("retro_map", false);
                    save_radio("silver_map", false);
                    save_radio("satellite_map", false);
                    save_radio("aubergine_map", false);
                    save_radio("sys_def_map", false);
                    break;
                case R.id.dark_map:
                    selectedRadioButton2 = dark_map;
                    save_radio("dark_map", selectedRadioButton2.isChecked());
                    save_radio("night_map", false);
                    save_radio("retro_map", false);
                    save_radio("silver_map", false);
                    save_radio("satellite_map", false);
                    save_radio("aubergine_map", false);
                    save_radio("sys_def_map", false);
                    break;
                case R.id.night_map:
                    selectedRadioButton2 = night_map;
                    save_radio("night_map", selectedRadioButton2.isChecked());
                    save_radio("retro_map", false);
                    save_radio("silver_map", false);
                    save_radio("satellite_map", false);
                    save_radio("aubergine_map", false);
                    save_radio("sys_def_map", false);
                    break;
                case R.id.Retro_map:
                    selectedRadioButton2 = retro_map;
                    save_radio("retro_map", selectedRadioButton2.isChecked());
                    save_radio("aubergine_map", false);
                    save_radio("silver_map", false);
                    save_radio("sys_def_map", false);
                    save_radio("satellite_map", false);
                    break;
                case R.id.silver_map:
                    selectedRadioButton2 = silver_map;
                    save_radio("silver_map", selectedRadioButton2.isChecked());
                    save_radio("satellite_map", false);
                    save_radio("aubergine_map", false);
                    save_radio("sys_def_map", false);
                    break;
                case R.id.satellite_map:
                    selectedRadioButton2 = satellite_map;
                    save_radio("satellite_map", selectedRadioButton2.isChecked());
                    save_radio("aubergine_map", false);
                    save_radio("sys_def_map", false);
                    break;
                case R.id.Aubergine_map:
                    selectedRadioButton2 = aubergine_map;
                    save_radio("aubergine_map", selectedRadioButton2.isChecked());
                    save_radio("sys_def_map", false);
                    break;
                case R.id.sys_def_map:
                    selectedRadioButton2 = sys_def_map;
                    save_radio("sys_def_map", selectedRadioButton2.isChecked());
                    break;
            }
        });
        done.setOnClickListener(v -> {
            int id = rg_1.getCheckedRadioButtonId();
            int id2 = rg_2.getCheckedRadioButtonId();
            RadioButton checked_button = rootView.findViewById(id);
            RadioButton checked_button_2 = rootView.findViewById(id2);
            mOnSetListener.sendTheme(checked_button.getText().toString(), checked_button_2.getText().toString());
            mOnClickListenerFragment.detachFragment();
            mOnSetTheme.sendThemeToActivity(checked_button.getText().toString(),mThemeReset);
            Objects.requireNonNull(getDialog()).dismiss();
        });
        return rootView;
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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnSetListener = (settingsDialog.onSetListener) getTargetFragment();
            mOnClickListenerFragment = (settingsDialog.onClickListenerFragment) getActivity();
            mOnSetTheme = (settingsDialog.onSetTheme) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }
}
