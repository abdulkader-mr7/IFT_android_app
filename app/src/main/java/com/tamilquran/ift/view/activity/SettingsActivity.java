package com.tamilquran.ift.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tamilquran.ift.AppContainer;
import com.tamilquran.ift.R;
import com.tamilquran.ift.controller.SettingsController;
import com.tamilquran.ift.model.preference.PreferencesRepository;
import com.tamilquran.ift.utils.FontManager;

public class SettingsActivity extends BaseDrawerActivity implements SeekBar.OnSeekBarChangeListener {

    private SettingsController settingsController;
    private Switch arabicSwitch;
    private Switch tamilSwitch;
    private SeekBar tamilSeekBar;
    private SeekBar arabicSeekBar;
    private TextView tamilPreview;
    private TextView arabicPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAllowExitDialog(false);
        inflateContent(R.layout.listabout);

        settingsController = new SettingsController(
                AppContainer.getInstance(this).getQuranRepository().getPreferencesRepository()
        );

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Settings");
        }

        arabicSwitch = findViewById(R.id.arabic_switch);
        tamilSwitch = findViewById(R.id.tamil_switch);
        tamilSeekBar = findViewById(R.id.seek);
        arabicSeekBar = findViewById(R.id.seekArabic);
        tamilPreview = findViewById(R.id.progress);
        arabicPreview = findViewById(R.id.progressArabic);

        PreferencesRepository.DisplaySettings settings = settingsController.loadSettings();
        arabicSwitch.setChecked(settings.showArabic);
        tamilSwitch.setChecked(settings.showTamil);
        tamilSeekBar.setProgress(settings.tamilFontSize);
        arabicSeekBar.setProgress(settings.arabicFontSize);

        tamilPreview.setTypeface(FontManager.getTamilTypeface(this));
        arabicPreview.setTypeface(FontManager.getArabicTypeface(this));
        tamilPreview.setTextSize(settings.tamilFontSize);
        arabicPreview.setTextSize(settings.arabicFontSize);

        tamilSeekBar.setOnSeekBarChangeListener(this);
        arabicSeekBar.setOnSeekBarChangeListener(this);

        arabicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                tamilSwitch.setChecked(true);
            }
            settingsController.setArabicVisible(isChecked);
        });
        tamilSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                arabicSwitch.setChecked(true);
            }
            settingsController.setTamilVisible(isChecked);
        });

        Button rateButton = findViewById(R.id.Button01);
        rateButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=com.tamililquran.ift"));
            startActivity(intent);
        });
    }

    @Override
    protected boolean shouldConfirmExit() {
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        int size = Math.max(10, progress);
        if (seekBar.getId() == R.id.seek) {
            if (progress < 10) {
                tamilSeekBar.setProgress(10);
                size = 10;
            }
            tamilPreview.setTextSize(size);
            settingsController.setTamilFontSize(size);
        } else if (seekBar.getId() == R.id.seekArabic) {
            if (progress < 10) {
                arabicSeekBar.setProgress(10);
                size = 10;
            }
            arabicPreview.setTextSize(size);
            settingsController.setArabicFontSize(size);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}
