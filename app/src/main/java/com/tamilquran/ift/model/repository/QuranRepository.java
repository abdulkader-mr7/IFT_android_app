package com.tamilquran.ift.model.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tamilquran.ift.model.Callback;
import com.tamilquran.ift.model.database.AppDatabase;
import com.tamilquran.ift.model.entity.SuraHeader;
import com.tamilquran.ift.model.entity.SuraHeaderEntity;
import com.tamilquran.ift.model.entity.VerseEntity;
import com.tamilquran.ift.model.entity.VerseRow;
import com.tamilquran.ift.model.preference.PreferencesRepository;
import com.tamilquran.ift.utils.VerseFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public class QuranRepository {

    private static final String TAG = "QuranRepository";

    private final AppDatabase database;
    private final PreferencesRepository preferencesRepository;
    private final ExecutorService executor;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public QuranRepository(Context context, ExecutorService executor) {
        this.database = AppDatabase.getInstance(context);
        this.preferencesRepository = new PreferencesRepository(context);
        this.executor = executor;
    }

    public PreferencesRepository getPreferencesRepository() {
        return preferencesRepository;
    }

    public List<SuraHeader> getSuraHeaders() {
        List<SuraHeaderEntity> entities = database.suraDao().getAllSuraHeaders();
        List<SuraHeader> headers = new ArrayList<>();
        for (SuraHeaderEntity entity : entities) {
            headers.add(new SuraHeader(entity.suraNo, entity.name, entity.suraType, entity.verseCount));
        }
        return headers;
    }

    public SuraHeader getSuraHeader(int suraNo) {
        SuraHeaderEntity entity = database.suraDao().getSuraHeader(suraNo);
        if (entity == null) {
            return null;
        }
        return new SuraHeader(entity.suraNo, entity.name, entity.suraType, entity.verseCount);
    }

    public List<VerseRow> getSuraVerses(int suraNo) {
        PreferencesRepository.DisplaySettings settings = preferencesRepository.getDisplaySettings();
        List<VerseEntity> entities = database.verseDao().getVersesForSura(suraNo);
        return VerseFormatter.formatSuraVerses(entities, suraNo, settings);
    }

    public List<VerseRow> getFavorites() {
        PreferencesRepository.DisplaySettings settings = preferencesRepository.getDisplaySettings();
        List<VerseEntity> entities = database.verseDao().getFavorites();
        List<VerseRow> rows = new ArrayList<>();
        for (VerseEntity entity : entities) {
            String tamil = settings.showTamil
                    ? entity.sura + ":" + entity.ayah + ". " + entity.tamilContent : "";
            String arabic = settings.showArabic
                    ? entity.arabicContent + "  ﴿" + entity.sura + ":" + entity.ayah + "﴾" : "";
            rows.add(new VerseRow(entity.sura, entity.ayah, tamil, arabic, false));
        }
        return rows;
    }

    public int countSearchResults(String query) {
        return database.verseDao().countSearch(query);
    }

    public List<VerseRow> searchVerses(String query, int offset, int limit) {
        PreferencesRepository.DisplaySettings settings = preferencesRepository.getDisplaySettings();
        List<VerseEntity> entities = database.verseDao().searchVerses(query, offset, limit);
        List<VerseRow> rows = new ArrayList<>();
        for (VerseEntity entity : entities) {
            String tamil = entity.sura + ":" + entity.ayah + ". " + entity.tamilContent;
            String arabic = settings.showArabic
                    ? entity.arabicContent + "  ﴿" + entity.sura + ":" + entity.ayah + "﴾" : "";
            rows.add(new VerseRow(entity.sura, entity.ayah, tamil, arabic, false));
        }
        return rows;
    }

    public void setFavorite(int sura, int ayah, boolean liked) {
        database.verseDao().updateLiked(sura, ayah, liked ? 1 : 0);
    }

    public String getCopyText(int sura, int ayah) {
        PreferencesRepository.DisplaySettings settings = preferencesRepository.getDisplaySettings();
        VerseEntity entity = database.verseDao().getVerse(sura, ayah);
        return VerseFormatter.buildCopyText(entity, sura, ayah, settings);
    }

    public void runOnBackground(Runnable runnable) {
        executor.execute(runnable);
    }

    /**
     * Runs {@code task} on the IO executor and delivers the result to
     * {@code callback} on the main thread. If the task fails the error is
     * logged and the callback is not invoked.
     */
    public <T> void runAsync(Callable<T> task, Callback<T> callback) {
        executor.execute(() -> {
            final T result;
            try {
                result = task.call();
            } catch (Exception e) {
                Log.e(TAG, "Background query failed", e);
                return;
            }
            mainHandler.post(() -> callback.onResult(result));
        });
    }
}
