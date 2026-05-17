package com.tamilquran.ift.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.tamilquran.ift.R;
import com.tamilquran.ift.controller.SplashController;
import com.tamilquran.ift.interfaces.SplashView;
import com.tamilquran.ift.utils.FontManager;

public class SplashActivity extends BaseActivity implements SplashView {

    private SplashController controller;
    private CircularProgressIndicator progressIndicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TextView greeting = findViewById(R.id.splashGreeting);
        greeting.setTypeface(FontManager.getTamilTypeface(this));
        progressIndicator = findViewById(R.id.splashProgress);

        controller = new SplashController(this, this);
        controller.startBootstrap();
    }

    @Override
    public void showLoading(boolean visible) {
        progressIndicator.setVisibility(visible ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    @Override
    public void showStorageError() {
        showLongToast(getString(R.string.storage_error));
    }

    @Override
    public void showError(String message) {
        showLongToast(message);
    }

    @Override
    public void navigateToMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void post(Runnable runnable) {
        runOnUiThread(runnable);
    }
}
