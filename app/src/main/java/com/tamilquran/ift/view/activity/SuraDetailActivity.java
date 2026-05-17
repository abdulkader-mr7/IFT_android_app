package com.tamilquran.ift.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tamilquran.ift.AppContainer;
import com.tamilquran.ift.R;
import com.tamilquran.ift.controller.SuraController;
import com.tamilquran.ift.model.entity.VerseRow;
import com.tamilquran.ift.model.preference.PreferencesRepository;
import com.tamilquran.ift.model.repository.QuranRepository;
import com.tamilquran.ift.view.adapter.VerseListAdapter;

import java.util.List;

public class SuraDetailActivity extends BaseDrawerActivity {

    public static final String EXTRA_SURA_NO = "surano";
    public static final String EXTRA_AYAH_NO = "ayahno";

    private SuraController suraController;
    private VerseListAdapter adapter;
    private RecyclerView verseList;
    private List<VerseRow> verses;
    private int suraNo;
    private int scrollPosition;
    private PreferencesRepository.DisplaySettings displaySettings;
    private Menu optionsMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAllowExitDialog(false);
        inflateContent(R.layout.suradetail);

        QuranRepository repository = AppContainer.getInstance(this).getQuranRepository();
        suraController = new SuraController(repository);
        displaySettings = repository.getPreferencesRepository().getDisplaySettings();

        suraNo = getIntent().getIntExtra(EXTRA_SURA_NO, 1);
        scrollPosition = getIntent().getIntExtra(EXTRA_AYAH_NO, 0);
        if (suraNo < 1 || suraNo > 114) {
            suraNo = 1;
        }

        verseList = findViewById(R.id.suradetail_list);
        verseList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VerseListAdapter();
        adapter.setFontSizes(displaySettings.tamilFontSize, displaySettings.arabicFontSize);
        adapter.setOnVerseInteractionListener(new VerseListAdapter.OnVerseInteractionListener() {
            @Override
            public void onVerseClick(int position, VerseRow row) {
                Toast.makeText(SuraDetailActivity.this, R.string.ayah_hint, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerseLongClick(int position, VerseRow row) {
                showVerseActionDialog(position, row);
            }
        });
        verseList.setAdapter(adapter);

        loadVerses(scrollPosition);
        updateTitle();
        updateNavButtons();
    }

    @Override
    protected boolean shouldConfirmExit() {
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesRepository.DisplaySettings latest =
                AppContainer.getInstance(this).getQuranRepository()
                        .getPreferencesRepository().getDisplaySettings();
        boolean visibilityChanged = latest.showArabic != displaySettings.showArabic
                || latest.showTamil != displaySettings.showTamil;
        boolean fontChanged = latest.tamilFontSize != displaySettings.tamilFontSize
                || latest.arabicFontSize != displaySettings.arabicFontSize;
        if (visibilityChanged || fontChanged) {
            displaySettings = latest;
            adapter.setFontSizes(latest.tamilFontSize, latest.arabicFontSize);
            if (visibilityChanged) {
                // Verse text content differs -> reload from the database.
                loadVerses(0);
            } else {
                // Only the font size changed -> rebind in place, no requery.
                adapter.notifyItemRangeChanged(0, adapter.getItemCount());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.suraactionmenu, menu);
        optionsMenu = menu;
        updateNavButtons();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        if (id == R.id.menu_prev) {
            suraNo--;
            loadVerses(0);
            updateTitle();
            updateNavButtons();
            return true;
        }
        if (id == R.id.menu_next) {
            suraNo++;
            loadVerses(0);
            updateTitle();
            updateNavButtons();
            return true;
        }
        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showVerseActionDialog(int position, VerseRow row) {
        new AlertDialog.Builder(this)
                .setTitle("Action")
                .setItems(new String[]{"Bookmark", "Favourite", "Share", "Copy"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            suraController.saveLastRead(suraNo, position);
                            Toast.makeText(this, R.string.bookmark_saved, Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            suraController.addFavorite(suraNo, row.ayah);
                            Toast.makeText(this, R.string.added_favourite, Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            suraController.getCopyTextAsync(suraNo, row.ayah, this::shareText);
                            break;
                        case 3:
                            suraController.getCopyTextAsync(suraNo, row.ayah, this::copyText);
                            break;
                        default:
                            break;
                    }
                })
                .show();
    }

    private void shareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(shareIntent, "Share with"));
    }

    private void copyText(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("Quran Ayah", text));
        Toast.makeText(this, R.string.copied, Toast.LENGTH_SHORT).show();
    }

    private void loadVerses(int scrollTo) {
        suraController.loadSuraVersesAsync(suraNo, rows -> {
            if (isFinishing()) {
                return;
            }
            verses = rows;
            adapter.submitList(rows, () -> verseList.scrollToPosition(scrollTo));
        });
    }

    private void updateTitle() {
        suraController.getSuraHeaderAsync(suraNo, header -> {
            if (!isFinishing() && header != null && getSupportActionBar() != null) {
                getSupportActionBar().setTitle(header.name);
            }
        });
    }

    private void updateNavButtons() {
        if (optionsMenu == null) {
            return;
        }
        MenuItem prev = optionsMenu.findItem(R.id.menu_prev);
        MenuItem next = optionsMenu.findItem(R.id.menu_next);
        if (prev != null) {
            prev.setEnabled(suraNo > 1);
        }
        if (next != null) {
            next.setEnabled(suraNo < 114);
        }
    }
}
