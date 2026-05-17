package com.tamilquran.ift;

import android.content.Context;

import com.tamilquran.ift.model.repository.BookCatalogRepository;
import com.tamilquran.ift.model.repository.QuranRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AppContainer {

    private static AppContainer instance;

    private final ExecutorService ioExecutor;
    private final QuranRepository quranRepository;
    private final BookCatalogRepository bookCatalogRepository;

    private AppContainer(Context context) {
        // Single thread: serialises DB work so a write always completes before
        // a following read, and keeps SQLite access off the UI thread.
        ioExecutor = Executors.newSingleThreadExecutor();
        quranRepository = new QuranRepository(context, ioExecutor);
        bookCatalogRepository = new BookCatalogRepository(context);
    }

    public static synchronized AppContainer getInstance(Context context) {
        if (instance == null) {
            instance = new AppContainer(context.getApplicationContext());
        }
        return instance;
    }

    public ExecutorService getIoExecutor() {
        return ioExecutor;
    }

    public QuranRepository getQuranRepository() {
        return quranRepository;
    }

    public BookCatalogRepository getBookCatalogRepository() {
        return bookCatalogRepository;
    }
}
