package com.tamilquran.ift.view.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.tamilquran.ift.R;
import com.tamilquran.ift.utils.NetworkUtils;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    protected void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    protected void showNetworkErrorIfOffline() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Snackbar.make(findViewById(android.R.id.content),
                    R.string.no_internet, Snackbar.LENGTH_LONG).show();
        }
    }
}
