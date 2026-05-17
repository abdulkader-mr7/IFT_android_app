package com.tamilquran.ift.controller;

import com.tamilquran.ift.model.Callback;
import com.tamilquran.ift.model.entity.VerseRow;
import com.tamilquran.ift.model.repository.QuranRepository;

import java.util.List;

public class BookmarkController {

    private final QuranRepository repository;
    private final SuraController suraController;

    public BookmarkController(QuranRepository repository) {
        this.repository = repository;
        this.suraController = new SuraController(repository);
    }

    public void loadFavoritesAsync(Callback<List<VerseRow>> callback) {
        repository.runAsync(repository::getFavorites, callback);
    }

    public void removeFavorite(int sura, int ayah) {
        repository.runOnBackground(() -> repository.setFavorite(sura, ayah, false));
    }

    public void addFavorite(int sura, int ayah) {
        repository.runOnBackground(() -> repository.setFavorite(sura, ayah, true));
    }

    public SuraController.QuickGotoResult validateManualFavorite(String suraText, String ayahText) {
        return suraController.validateQuickGoto(suraText, ayahText);
    }

    public void getCopyTextAsync(int sura, int ayah, Callback<String> callback) {
        repository.runAsync(() -> repository.getCopyText(sura, ayah), callback);
    }
}
