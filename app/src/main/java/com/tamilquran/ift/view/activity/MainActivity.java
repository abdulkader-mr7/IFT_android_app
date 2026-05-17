package com.tamilquran.ift.view.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

import com.tamilquran.ift.R;
import com.tamilquran.ift.view.fragment.SuraIndexFragment;

public class MainActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.activity_frame, new SuraIndexFragment());
            transaction.commit();
        }
        setToolbarTitle("ஸூரா அட்டவணை");
    }

    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }
}
