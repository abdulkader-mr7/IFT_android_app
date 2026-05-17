package com.tamilquran.ift.view.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tamilquran.ift.AppContainer;
import com.tamilquran.ift.R;
import com.tamilquran.ift.controller.SearchController;
import com.tamilquran.ift.controller.SuraController;
import com.tamilquran.ift.model.entity.VerseRow;
import com.tamilquran.ift.model.preference.PreferencesRepository;
import com.tamilquran.ift.view.adapter.VerseListAdapter;

public class SearchResultActivity extends BaseDrawerActivity {

    public static final String EXTRA_QUERY = "searchString";
    public static final String EXTRA_COUNT = "count";

    private SearchController searchController;
    private SuraController suraController;
    private VerseListAdapter adapter;
    private RecyclerView resultList;
    private String query;
    private int totalCount;
    private int currentPage = 1;
    private int totalPages;
    private Menu optionsMenu;
    private TextView pageText;
    private PreferencesRepository.DisplaySettings displaySettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAllowExitDialog(false);
        inflateContent(R.layout.searchresult);

        AppContainer container = AppContainer.getInstance(this);
        searchController = new SearchController(container.getQuranRepository());
        suraController = new SuraController(container.getQuranRepository());
        displaySettings = container.getQuranRepository().getPreferencesRepository().getDisplaySettings();

        query = getIntent().getStringExtra(EXTRA_QUERY);
        totalCount = getIntent().getIntExtra(EXTRA_COUNT, 0);
        totalPages = searchController.getTotalPages(totalCount);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("தேடல்");
        }

        pageText = findViewById(R.id.pagetext);
        updatePageLabel();

        resultList = findViewById(R.id.searchdeail_list);
        resultList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VerseListAdapter();
        adapter.setHighlightQuery(query);
        adapter.setFontSizes(displaySettings.tamilFontSize, displaySettings.arabicFontSize);
        adapter.setOnVerseInteractionListener(new VerseListAdapter.OnVerseInteractionListener() {
            @Override
            public void onVerseClick(int position, VerseRow row) {
                Toast.makeText(SearchResultActivity.this, R.string.ayah_hint, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerseLongClick(int position, VerseRow row) {
                showVerseActions(row);
            }
        });
        resultList.setAdapter(adapter);
        loadPage();
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
        if (latest.tamilFontSize != displaySettings.tamilFontSize
                || latest.arabicFontSize != displaySettings.arabicFontSize) {
            displaySettings = latest;
            adapter.setFontSizes(latest.tamilFontSize, latest.arabicFontSize);
            // Only the font size changed -> rebind in place, no requery.
            adapter.notifyItemRangeChanged(0, adapter.getItemCount());
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
        if (id == R.id.menu_prev && currentPage > 1) {
            currentPage--;
            loadPage();
            return true;
        }
        if (id == R.id.menu_next && currentPage < totalPages) {
            currentPage++;
            loadPage();
            return true;
        }
        if (id == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadPage() {
        searchController.loadPageAsync(query, currentPage, rows -> {
            if (isFinishing()) {
                return;
            }
            adapter.submitList(rows, () -> resultList.scrollToPosition(0));
            updatePageLabel();
            updateNavButtons();
        });
    }

    private void updatePageLabel() {
        pageText.setText("Found : " + totalCount + ". Page " + currentPage + " of " + totalPages);
    }

    private void updateNavButtons() {
        if (optionsMenu == null) {
            return;
        }
        MenuItem prev = optionsMenu.findItem(R.id.menu_prev);
        MenuItem next = optionsMenu.findItem(R.id.menu_next);
        if (prev != null) {
            prev.setEnabled(currentPage > 1);
        }
        if (next != null) {
            next.setEnabled(currentPage < totalPages);
        }
    }

    private void showVerseActions(VerseRow row) {
        new AlertDialog.Builder(this)
                .setTitle("Action")
                .setItems(new String[]{"Favourite", "Share", "Copy"}, (dialog, which) -> {
                    if (which == 0) {
                        suraController.addFavorite(row.sura, row.ayah);
                        Toast.makeText(this, R.string.added_favourite, Toast.LENGTH_SHORT).show();
                    } else if (which == 1) {
                        suraController.getCopyTextAsync(row.sura, row.ayah, this::shareText);
                    } else {
                        suraController.getCopyTextAsync(row.sura, row.ayah, this::copyText);
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
