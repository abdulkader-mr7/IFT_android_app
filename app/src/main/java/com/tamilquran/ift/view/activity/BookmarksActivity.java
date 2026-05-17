package com.tamilquran.ift.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tamilquran.ift.AppContainer;
import com.tamilquran.ift.R;
import com.tamilquran.ift.controller.BookmarkController;
import com.tamilquran.ift.controller.NavigationController;
import com.tamilquran.ift.controller.SuraController;
import com.tamilquran.ift.model.entity.VerseRow;
import com.tamilquran.ift.model.preference.PreferencesRepository;
import com.tamilquran.ift.view.adapter.VerseListAdapter;

import java.util.List;

public class BookmarksActivity extends BaseDrawerActivity {

    private BookmarkController bookmarkController;
    private NavigationController navigationController;
    private VerseListAdapter adapter;
    private List<VerseRow> favorites;
    private PreferencesRepository.DisplaySettings displaySettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateContent(R.layout.suradetail);

        AppContainer container = AppContainer.getInstance(this);
        bookmarkController = new BookmarkController(container.getQuranRepository());
        navigationController = new NavigationController(this);
        displaySettings = container.getQuranRepository().getPreferencesRepository().getDisplaySettings();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("பிடித்தவை");
        }

        RecyclerView recyclerView = findViewById(R.id.suradetail_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VerseListAdapter();
        adapter.setFontSizes(displaySettings.tamilFontSize, displaySettings.arabicFontSize);
        adapter.setOnVerseInteractionListener(new VerseListAdapter.OnVerseInteractionListener() {
            @Override
            public void onVerseClick(int position, VerseRow row) {
                Toast.makeText(BookmarksActivity.this, R.string.ayah_hint, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerseLongClick(int position, VerseRow row) {
                showFavoriteActions(row);
            }
        });
        recyclerView.setAdapter(adapter);
        reloadFavorites();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookmarksmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            showAddFavoriteDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadFavorites() {
        favorites = bookmarkController.loadFavorites();
        adapter.submitList(favorites);
        View empty = findViewById(R.id.empty_favorites);
        if (empty != null) {
            empty.setVisibility(favorites.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private void showAddFavoriteDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.alert_dialog_text_entry, null, false);
        EditText suraInput = dialogView.findViewById(R.id.surano);
        EditText ayahInput = dialogView.findViewById(R.id.ayahno);
        new AlertDialog.Builder(this)
                .setTitle("Enter Sura & Ayah No.")
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    SuraController.QuickGotoResult result = bookmarkController.validateManualFavorite(
                            suraInput.getText().toString(),
                            ayahInput.getText().toString()
                    );
                    if (!result.valid) {
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    bookmarkController.addFavorite(result.sura, result.ayah);
                    Toast.makeText(this, R.string.added_favourite, Toast.LENGTH_SHORT).show();
                    reloadFavorites();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showFavoriteActions(VerseRow row) {
        new AlertDialog.Builder(this)
                .setTitle("Action")
                .setItems(new String[]{"Delete", "Go To", "Share", "Copy"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            bookmarkController.removeFavorite(row.sura, row.ayah);
                            Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
                            reloadFavorites();
                            break;
                        case 1:
                            navigationController.openSuraDetail(row.sura, row.ayah);
                            break;
                        case 2:
                            shareText(bookmarkController.getCopyText(row.sura, row.ayah));
                            break;
                        case 3:
                            copyText(bookmarkController.getCopyText(row.sura, row.ayah));
                            break;
                        default:
                            break;
                    }
                })
                .show();
    }

    private void shareText(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, "Share with"));
    }

    private void copyText(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Quran Ayah", text));
        Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show();
    }
}
