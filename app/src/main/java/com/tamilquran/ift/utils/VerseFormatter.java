package com.tamilquran.ift.utils;

import com.tamilquran.ift.model.entity.VerseEntity;
import com.tamilquran.ift.model.entity.VerseRow;
import com.tamilquran.ift.model.preference.PreferencesRepository;

import java.util.ArrayList;
import java.util.List;

public final class VerseFormatter {

    private static final String BISMILLAH_ARABIC = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ";
    private static final String BISMILLAH_TAMIL =
            "அளவற்ற அருளாளனும், நிகரற்ற அன்புடையோனுமாகிய அல்லாஹ்வின் திருப்பெயரால்(துவங்குகிறேன்)";

    private VerseFormatter() {
    }

    public static List<VerseRow> formatSuraVerses(
            List<VerseEntity> entities,
            int suraNo,
            PreferencesRepository.DisplaySettings settings
    ) {
        List<VerseRow> rows = new ArrayList<>();
        if (suraNo != 1 && suraNo != 9) {
            String tamil = settings.showTamil ? BISMILLAH_TAMIL : "";
            String arabic = settings.showArabic ? BISMILLAH_ARABIC : "";
            rows.add(new VerseRow(suraNo, 0, tamil, arabic, true));
        }

        for (VerseEntity entity : entities) {
            String tamil = "";
            if (settings.showTamil) {
                tamil = entity.sura + ":" + entity.ayah + ". " + entity.tamilContent;
            }
            String arabic = "";
            if (settings.showArabic) {
                arabic = ArabicTextHelper.cleanLegacyGlyphs(entity.arabicContent)
                        + "  ﴿" + entity.sura + ":" + entity.ayah + "﴾";
            }
            rows.add(new VerseRow(entity.sura, entity.ayah, tamil, arabic, false));
        }
        return rows;
    }

    public static String buildCopyText(
            VerseEntity entity,
            int suraNo,
            int ayahNo,
            PreferencesRepository.DisplaySettings settings
    ) {
        if (suraNo != 1 && suraNo != 9 && ayahNo == 0) {
            if (settings.showArabic && settings.showTamil) {
                return BISMILLAH_ARABIC + "\r\n" + BISMILLAH_TAMIL;
            }
            if (settings.showArabic) {
                return BISMILLAH_ARABIC;
            }
            if (settings.showTamil) {
                return BISMILLAH_TAMIL;
            }
            return "";
        }

        if (entity == null) {
            return "";
        }

        String reference = "\n(அல்குர்ஆன் : " + entity.sura + ":" + entity.ayah + ")";
        String arabic = ArabicTextHelper.cleanLegacyGlyphs(entity.arabicContent);
        if (settings.showArabic && settings.showTamil) {
            return arabic + "\r\n" + entity.tamilContent + reference;
        }
        if (settings.showArabic) {
            return arabic + "\r\n" + reference;
        }
        return entity.tamilContent + "\r\n" + reference;
    }
}
