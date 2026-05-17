package com.tamilquran.ift.controller;

import com.tamilquran.ift.model.Callback;
import com.tamilquran.ift.model.entity.SuraHeader;
import com.tamilquran.ift.model.entity.VerseRow;
import com.tamilquran.ift.model.repository.QuranRepository;

import java.util.List;

public class SuraController {

    private final QuranRepository repository;

    public SuraController(QuranRepository repository) {
        this.repository = repository;
    }

    public void loadSuraHeadersAsync(Callback<List<SuraHeader>> callback) {
        repository.runAsync(repository::getSuraHeaders, callback);
    }

    public void loadSuraVersesAsync(int suraNo, Callback<List<VerseRow>> callback) {
        repository.runAsync(() -> repository.getSuraVerses(suraNo), callback);
    }

    public void getSuraHeaderAsync(int suraNo, Callback<SuraHeader> callback) {
        repository.runAsync(() -> repository.getSuraHeader(suraNo), callback);
    }

    public void getCopyTextAsync(int sura, int ayah, Callback<String> callback) {
        repository.runAsync(() -> repository.getCopyText(sura, ayah), callback);
    }

    public void addFavorite(int sura, int ayah) {
        repository.runOnBackground(() -> repository.setFavorite(sura, ayah, true));
    }

    public void saveLastRead(int sura, int listPosition) {
        repository.getPreferencesRepository().saveLastRead(sura, listPosition);
    }

    public int[] getLastRead() {
        return new int[]{
                repository.getPreferencesRepository().getLastReadSura(),
                repository.getPreferencesRepository().getLastReadPosition()
        };
    }

    public QuickGotoResult validateQuickGoto(String suraText, String ayahText) {
        int sura = parseOrDefault(suraText, 0);
        int ayah = parseOrDefault(ayahText, 1);
        if (sura <= 0 || sura > 114) {
            return QuickGotoResult.error("Sura No. must be from 1 to 114.");
        }
        if (sura == 1 || sura == 9) {
            ayah = Math.max(0, ayah - 1);
        } else {
            ayah = Math.max(0, ayah);
        }
        return QuickGotoResult.success(sura, ayah);
    }

    private int parseOrDefault(String value, int defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return Integer.parseInt(value.trim());
    }

    public static final class QuickGotoResult {
        public final boolean valid;
        public final int sura;
        public final int ayah;
        public final String error;

        private QuickGotoResult(boolean valid, int sura, int ayah, String error) {
            this.valid = valid;
            this.sura = sura;
            this.ayah = ayah;
            this.error = error;
        }

        public static QuickGotoResult success(int sura, int ayah) {
            return new QuickGotoResult(true, sura, ayah, null);
        }

        public static QuickGotoResult error(String error) {
            return new QuickGotoResult(false, 0, 0, error);
        }
    }
}
