package com.tamilquran.ift.controller;

import com.tamilquran.ift.model.preference.PreferencesRepository;

public class SettingsController {

    private final PreferencesRepository repository;

    public SettingsController(PreferencesRepository repository) {
        this.repository = repository;
    }

    public PreferencesRepository.DisplaySettings loadSettings() {
        return repository.getDisplaySettings();
    }

    public void setArabicVisible(boolean visible) {
        PreferencesRepository.DisplaySettings current = repository.getDisplaySettings();
        if (!visible && !current.showTamil) {
            repository.setTamilVisible(true);
        }
        repository.setArabicVisible(visible);
    }

    public void setTamilVisible(boolean visible) {
        PreferencesRepository.DisplaySettings current = repository.getDisplaySettings();
        if (!visible && !current.showArabic) {
            repository.setArabicVisible(true);
        }
        repository.setTamilVisible(visible);
    }

    public void setTamilFontSize(int size) {
        repository.setTamilFontSize(Math.max(10, size));
    }

    public void setArabicFontSize(int size) {
        repository.setArabicFontSize(Math.max(10, size));
    }
}
