package com.tamilquran.ift.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.tamilquran.ift.R;
import com.tamilquran.ift.controller.NavigationController;
import com.tamilquran.ift.view.dialog.DialogUtils;

public abstract class BaseDrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout drawerLayout;
    protected FrameLayout contentFrame;
    private NavigationController navigationController;
    private boolean allowExitDialog = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationController = new NavigationController(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        contentFrame = findViewById(R.id.activity_frame);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void inflateContent(int layoutResId) {
        getLayoutInflater().inflate(layoutResId, contentFrame, true);
    }

    protected void setAllowExitDialog(boolean allowExitDialog) {
        this.allowExitDialog = allowExitDialog;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        boolean handled = navigationController.onNavigationItemSelected(item.getItemId());
        drawerLayout.closeDrawer(GravityCompat.START);
        return handled;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (allowExitDialog && shouldConfirmExit()) {
            DialogUtils.showExitDialog(this, this::finishAffinity);
        } else {
            super.onBackPressed();
        }
    }

    protected boolean shouldConfirmExit() {
        return true;
    }

    protected void openExternalUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
