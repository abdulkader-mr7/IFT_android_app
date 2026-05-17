package com.tamilquran.ift.view.activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tamilquran.ift.AppContainer;
import com.tamilquran.ift.R;
import com.tamilquran.ift.controller.BooksController;
import com.tamilquran.ift.model.entity.BookItem;
import com.tamilquran.ift.model.repository.BookCatalogRepository;
import com.tamilquran.ift.view.adapter.BookListAdapter;
import com.tamilquran.ift.view.dialog.DialogUtils;

import java.io.File;
import java.util.List;

public abstract class BooksCatalogActivity extends BaseDrawerActivity implements BookListAdapter.BookActionListener {

    private BooksController booksController;
    private BookListAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;

    protected abstract BookCatalogRepository.CatalogType getCatalogType();

    protected abstract String getScreenTitle();

    protected abstract boolean isSamarasam();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflateContent(R.layout.activity_books);
        booksController = new BooksController(this, getCatalogType());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getScreenTitle());
        }

        progressBar = findViewById(R.id.booksProgress);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        RecyclerView recyclerView = findViewById(R.id.booksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int tamilSize = AppContainer.getInstance(this).getQuranRepository()
                .getPreferencesRepository().getDisplaySettings().tamilFontSize;
        adapter = new BookListAdapter(this, isSamarasam(), tamilSize);
        adapter.setPdfExistsChecker(filename -> booksController.getPdfFile(filename).exists());
        adapter.setCoverFileResolver(booksController::getCoverFile);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::syncCatalog);
        reloadBooks();

        if (adapter.getCurrentList().isEmpty()) {
            syncCatalog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.iftbooks_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            syncCatalog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void reloadBooks() {
        List<BookItem> items = booksController.loadItems();
        adapter.submitList(items);
    }

    private void syncCatalog() {
        showNetworkErrorIfOffline();
        if (!booksController.isOnline()) {
            swipeRefresh.setRefreshing(false);
            return;
        }
        swipeRefresh.setRefreshing(true);
        booksController.syncCatalog(new BooksController.SyncCallback() {
            @Override
            public void onSuccess(int newItems, String message) {
                runOnUiThread(() -> {
                    swipeRefresh.setRefreshing(false);
                    reloadBooks();
                    showLongToast(message);
                    if (newItems > 0) {
                        booksController.downloadCoverImages();
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    swipeRefresh.setRefreshing(false);
                    showLongToast(message);
                });
            }
        });
    }

    @Override
    public void onDownloadClick(BookItem item, int position) {
        File pdf = booksController.getPdfFile(item.bookFilename);
        if (pdf.exists()) {
            openPdf(pdf);
            return;
        }
        DialogUtils.showConfirmDialog(this, getString(R.string.download_confirm),
                item.bookTitle, () -> downloadPdf(item));
    }

    @Override
    public void onDeleteClick(BookItem item, int position) {
        File pdf = booksController.getPdfFile(item.bookFilename);
        if (!pdf.exists()) {
            return;
        }
        DialogUtils.showConfirmDialog(this, getString(R.string.delete_confirm),
                item.bookTitle, () -> {
                    booksController.deletePdf(item.bookFilename);
                    adapter.notifyItemChanged(position);
                });
    }

    private void downloadPdf(BookItem item) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        booksController.downloadPdf(item.bookFilename, new BooksController.DownloadCallback() {
            @Override
            public void onProgress(int percent) {
                runOnUiThread(() -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onComplete(File file) {
                runOnUiThread(() -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                    openPdf(file);
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    showLongToast(message);
                });
            }
        });
    }

    private void openPdf(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);
        android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
