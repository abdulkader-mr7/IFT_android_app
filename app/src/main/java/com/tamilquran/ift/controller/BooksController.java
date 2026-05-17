package com.tamilquran.ift.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.tamilquran.ift.model.Callback;
import com.tamilquran.ift.model.entity.BookItem;
import com.tamilquran.ift.model.repository.BookCatalogRepository;
import com.tamilquran.ift.utils.NetworkUtils;

import java.io.File;
import java.util.List;

public class BooksController {

    public interface SyncCallback {
        void onSuccess(int newItems, String message);

        void onError(String message);
    }

    public interface DownloadCallback {
        void onProgress(int percent);

        void onComplete(File file);

        void onError(String message);
    }

    private final Context context;
    private final BookCatalogRepository repository;
    private final BookCatalogRepository.CatalogType catalogType;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public BooksController(Context context, BookCatalogRepository.CatalogType catalogType) {
        this.context = context.getApplicationContext();
        this.repository = new BookCatalogRepository(context);
        this.catalogType = catalogType;
    }

    /** Loads the catalog from the database off the main thread. */
    public void loadItemsAsync(Callback<List<BookItem>> callback) {
        new Thread(() -> {
            List<BookItem> items = repository.getBooks(catalogType);
            mainHandler.post(() -> callback.onResult(items));
        }).start();
    }

    public boolean isOnline() {
        return NetworkUtils.isNetworkAvailable(context);
    }

    public void syncCatalog(SyncCallback callback) {
        if (!isOnline()) {
            callback.onError(context.getString(com.tamilquran.ift.R.string.no_internet));
            return;
        }
        new Thread(() -> {
            try {
                int inserted = repository.syncCatalog(catalogType);
                String message = inserted > 0
                        ? (catalogType == BookCatalogRepository.CatalogType.IFT_BOOKS
                        ? context.getString(com.tamilquran.ift.R.string.new_books_available)
                        : context.getString(com.tamilquran.ift.R.string.new_issues_available))
                        : (catalogType == BookCatalogRepository.CatalogType.IFT_BOOKS
                        ? context.getString(com.tamilquran.ift.R.string.no_new_books)
                        : context.getString(com.tamilquran.ift.R.string.no_new_issues));
                callback.onSuccess(inserted, message);
            } catch (Exception e) {
                callback.onError(context.getString(com.tamilquran.ift.R.string.download_error));
            }
        }).start();
    }

    public void downloadPdf(String filename, DownloadCallback callback) {
        if (!isOnline()) {
            callback.onError(context.getString(com.tamilquran.ift.R.string.no_internet));
            return;
        }
        new Thread(() -> repository.downloadFile(
                catalogType, filename, ".pdf", new BookCatalogRepository.DownloadProgressListener() {
                    @Override
                    public void onProgress(int percent) {
                        callback.onProgress(percent);
                    }

                    @Override
                    public void onComplete(File file) {
                        callback.onComplete(file);
                    }

                    @Override
                    public void onError(String message) {
                        callback.onError(message);
                    }
                })).start();
    }

    public void downloadCoverImages() {
        new Thread(() -> {
            for (BookItem item : repository.getBooks(catalogType)) {
                File cover = repository.getCoverFile(catalogType, item.bookFilename);
                if (!cover.exists()) {
                    repository.downloadFile(catalogType, item.bookFilename, ".png",
                            new BookCatalogRepository.DownloadProgressListener() {
                                @Override
                                public void onProgress(int percent) {
                                }

                                @Override
                                public void onComplete(File file) {
                                }

                                @Override
                                public void onError(String message) {
                                }
                            });
                }
            }
        }).start();
    }

    public File getPdfFile(String filename) {
        return repository.getPdfFile(catalogType, filename);
    }

    public File getCoverFile(String filename) {
        return repository.getCoverFile(catalogType, filename);
    }

    public boolean deletePdf(String filename) {
        return repository.deletePdf(catalogType, filename);
    }
}
