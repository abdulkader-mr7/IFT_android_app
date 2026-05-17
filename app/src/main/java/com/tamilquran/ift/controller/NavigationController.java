package com.tamilquran.ift.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.tamilquran.ift.R;
import com.tamilquran.ift.view.activity.BookmarksActivity;
import com.tamilquran.ift.view.activity.BooksActivity;
import com.tamilquran.ift.view.activity.MainActivity;
import com.tamilquran.ift.view.activity.SamarasamActivity;
import com.tamilquran.ift.view.activity.SearchActivity;
import com.tamilquran.ift.view.activity.SettingsActivity;
import com.tamilquran.ift.view.activity.SuraDetailActivity;

public class NavigationController {

    private final Activity activity;

    public NavigationController(Activity activity) {
        this.activity = activity;
    }

    public boolean onNavigationItemSelected(int itemId) {
        if (itemId == R.id.nav_suraindex) {
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
            return true;
        }
        if (itemId == R.id.nav_search) {
            activity.startActivity(new Intent(activity, SearchActivity.class));
            activity.finish();
            return true;
        }
        if (itemId == R.id.nav_favourite) {
            activity.startActivity(new Intent(activity, BookmarksActivity.class));
            activity.finish();
            return true;
        }
        if (itemId == R.id.nav_settings) {
            activity.startActivity(new Intent(activity, SettingsActivity.class));
            activity.finish();
            return true;
        }
        if (itemId == R.id.nav_site) {
            openUrl("https://iftchennai.in/");
            return true;
        }
        if (itemId == R.id.nav_rate) {
            openUrl("market://details?id=com.tamililquran.ift");
            return true;
        }
        if (itemId == R.id.nav_contact) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:iftmobileapps@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "");
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(intent);
            }
            return true;
        }
        if (itemId == R.id.nav_book1) {
            activity.startActivity(new Intent(activity, BooksActivity.class));
            activity.finish();
            return true;
        }
        if (itemId == R.id.nav_book2) {
            activity.startActivity(new Intent(activity, SamarasamActivity.class));
            activity.finish();
            return true;
        }
        if (itemId == R.id.nav_book_play) {
            openUrl("https://play.google.com/store/search?q=IFT%20CHENNAI&c=books");
            return true;
        }
        return false;
    }

    public void openSuraDetail(int sura, int ayah) {
        Intent intent = new Intent(activity, SuraDetailActivity.class);
        intent.putExtra(SuraDetailActivity.EXTRA_SURA_NO, sura);
        intent.putExtra(SuraDetailActivity.EXTRA_AYAH_NO, ayah);
        activity.startActivity(intent);
    }

    private void openUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        }
    }
}
