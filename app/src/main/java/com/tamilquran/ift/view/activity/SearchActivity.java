package com.tamilquran.ift.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.tamilquran.ift.AppContainer;
import com.tamilquran.ift.R;
import com.tamilquran.ift.controller.SearchController;
import com.tamilquran.ift.utils.FontManager;

public class SearchActivity extends BaseDrawerActivity {

    private SearchController searchController;
    private EditText searchInput;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateContent(R.layout.search);
        searchController = new SearchController(AppContainer.getInstance(this).getQuranRepository());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("தேடல்");
        }

        searchInput = findViewById(R.id.isearch);
        searchInput.setTypeface(FontManager.getTamilTypeface(this));
        Button searchButton = findViewById(R.id.BSearch);
        searchButton.setTypeface(FontManager.getTamilTypeface(this));
        searchButton.setOnClickListener(v -> performSearch());
    }

    private void performSearch() {
        searchController.validateQueryAsync(searchInput.getText().toString(), validation -> {
            if (isFinishing()) {
                return;
            }
            if (!validation.valid) {
                if (validation.error.contains("?")) {
                    searchInput.setError(validation.error);
                } else {
                    showLongToast(validation.error);
                }
                return;
            }
            Intent intent = new Intent(this, SearchResultActivity.class);
            intent.putExtra(SearchResultActivity.EXTRA_QUERY, validation.query);
            intent.putExtra(SearchResultActivity.EXTRA_COUNT, validation.count);
            startActivity(intent);
        });
    }
}
