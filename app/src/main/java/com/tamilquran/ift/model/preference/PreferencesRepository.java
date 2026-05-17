package com.tamilquran.ift.model.preference;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.tamilquran.ift.constants.PrefsKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class PreferencesRepository {

    public static class DisplaySettings {
        public final boolean showArabic;
        public final boolean showTamil;
        public final int tamilFontSize;
        public final int arabicFontSize;

        public DisplaySettings(boolean showArabic, boolean showTamil, int tamilFontSize, int arabicFontSize) {
            this.showArabic = showArabic;
            this.showTamil = showTamil;
            this.tamilFontSize = tamilFontSize;
            this.arabicFontSize = arabicFontSize;
        }
    }

    private final SharedPreferences preferences;

    public PreferencesRepository(Context context) {
        preferences = createPreferences(context);
    }

    private SharedPreferences createPreferences(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            return EncryptedSharedPreferences.create(
                    context,
                    PrefsKeys.PREFS_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            return context.getSharedPreferences(PrefsKeys.PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public DisplaySettings getDisplaySettings() {
        return new DisplaySettings(
                preferences.getBoolean(PrefsKeys.ARABIC_SWITCH, true),
                preferences.getBoolean(PrefsKeys.TAMIL_SWITCH, true),
                preferences.getInt(PrefsKeys.TAMIL_FONT_SIZE, 16),
                preferences.getInt(PrefsKeys.ARABIC_FONT_SIZE, 24)
        );
    }

    public void setArabicVisible(boolean visible) {
        preferences.edit().putBoolean(PrefsKeys.ARABIC_SWITCH, visible).apply();
    }

    public void setTamilVisible(boolean visible) {
        preferences.edit().putBoolean(PrefsKeys.TAMIL_SWITCH, visible).apply();
    }

    public void setTamilFontSize(int size) {
        preferences.edit().putInt(PrefsKeys.TAMIL_FONT_SIZE, Math.max(10, size)).apply();
    }

    public void setArabicFontSize(int size) {
        preferences.edit().putInt(PrefsKeys.ARABIC_FONT_SIZE, Math.max(10, size)).apply();
    }

    public void saveLastRead(int sura, int listPosition) {
        preferences.edit()
                .putInt(PrefsKeys.SURA_NO, sura)
                .putInt(PrefsKeys.AYAH_NO, listPosition)
                .apply();
    }

    public int getLastReadSura() {
        return preferences.getInt(PrefsKeys.SURA_NO, 0);
    }

    public int getLastReadPosition() {
        return preferences.getInt(PrefsKeys.AYAH_NO, 0);
    }
}
