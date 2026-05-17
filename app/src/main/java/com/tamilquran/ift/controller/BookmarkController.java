package com.tamilquran.ift.controller;

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

    public List<VerseRow> loadFavorites() {
        return repository.getFavorites();
    }

    public void removeFavorite(int sura, int ayah) {
        repository.setFavorite(sura, ayah, false);
    }

    public void addFavorite(int sura, int ayah) {
        repository.setFavorite(sura, ayah, true);
    }

    public SuraController.QuickGotoResult validateManualFavorite(String suraText, String ayahText) {
        return suraController.validateQuickGoto(suraText, ayahText);
    }

    public String getCopyText(int sura, int ayah) {
        return repository.getCopyText(sura, ayah);
    }
}
